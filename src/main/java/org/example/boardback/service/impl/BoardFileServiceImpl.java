package org.example.boardback.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.boardback.dto.board.file.BoardFileListDto;
import org.example.boardback.dto.board.file.BoardFileUpdateRequestDto;
import org.example.boardback.entity.board.Board;
import org.example.boardback.entity.file.BoardFile;
import org.example.boardback.entity.file.FileInfo;
import org.example.boardback.exception.FileStorageException;
import org.example.boardback.repository.board.BoardRepository;
import org.example.boardback.repository.file.BoardFileRepository;
import org.example.boardback.repository.file.FileInfoRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardFileServiceImpl {
    private final FileServiceImpl fileService;
    private final BoardFileRepository boardFileRepository;
    private final FileInfoRepository fileInfoRepository;
    private final BoardRepository boardRepository;

    private final int MAX_ATTACH = 5;

    @Transactional
    public void uploadBoardFiles(Long boardId, List<MultipartFile> files) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));

        if (files.size() > MAX_ATTACH) throw new IllegalArgumentException("최대 " + MAX_ATTACH + "개까지 업로드 가능");

        int order = 0;

        for (MultipartFile mf : files) {
            FileInfo info = fileService.saveBoardFile(boardId, mf);

            BoardFile boardFile = BoardFile.of(board, info, order++);

            boardFileRepository.save(boardFile);
        }
    }

    public List<BoardFileListDto> getFilesByBoard(Long boardId) {
        final String baseURL = "/api/file/download/";

        List<BoardFile> boardFiles = boardFileRepository.findByBoardIdOrderByDisplayOrderAsc(boardId);

        return boardFiles.stream()
                .map(BoardFile::getFileInfo)
                .filter(Objects::nonNull) // FileInfo가 null일 가능성이 있는 경우 (안정성 강화)
                .map(fileInfo -> BoardFileListDto.fromEntity(fileInfo, baseURL))
                .toList();
    }

    /** 파일 정보를 DB에서 조회 */
    public FileInfo getFileInfo(Long fileId) {
        return fileInfoRepository.findById(fileId)
                .orElseThrow(() -> new FileStorageException("파일 정보를 찾을 수 없습니다."));
    }

    public Path loadFile(Long fileId) {
        FileInfo fileInfo = getFileInfo(fileId);

        Path path = Paths.get(fileInfo.getFilePath());

        if (!Files.exists(path) || !Files.isReadable(path)) {
            throw new FileStorageException("파일이 존재하지 않거나 읽을 수 없습니다.");
        }

        return path;
    }

    /** 파일 다운로드에 필요한 헤더 생성 (파일명 인코딩, MIME 타입 결정 포함) */
    public HttpHeaders createDownloadHeaders(FileInfo info, Path path) {
        HttpHeaders headers = new HttpHeaders();

        // Content-Type 결정 (DB에 없으면 자동 검사)
        String contentType = info.getContentType();
        if (contentType == null) {
            try {
                contentType = Files.probeContentType(path);
            } catch (Exception ignored) {}
        }
        headers.setContentType(MediaType.parseMediaType(
                // octet-stream: 웹 서버에서 특정 파일 형식을 알 수 없을 때 사용하는 기본 MIME
                // MIME: Multipurpose Internet Mail Extensions 타입
                contentType != null ? contentType : "application/octet-stream"
        ));

        // 파일명 인코딩
        String encodedName = URLEncoder.encode(
                info.getOriginalName(), StandardCharsets.UTF_8
                // %20: 웹사이트에서 띄어쓰기를 의미하는 퍼센트 인코딩 문자
        ).replace("+", "%20");

        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                        + info.getOriginalName().replace("\"", "")
                        + "\"; "
                        + "filename*=UTF-8''" + encodedName
        );

        // Content-Length 포함 (다운로드 진행률 표시용)
        headers.setContentLength(info.getFileSize());

        return headers;
    }

    @Transactional
    public void deleteBoardFile(Long fileId) {
        BoardFile boardFile = boardFileRepository.findByFileInfoId(fileId)
                .orElseThrow(() -> new FileStorageException("해당 파일은 게시글에 존재하지 않습니다."));

        FileInfo fileInfo = boardFile.getFileInfo();

        boardFileRepository.delete(boardFile);

        fileService.deleteFile(fileInfo);
    }

    /**
     * 수정 요청 시 전달 데이터
     *  boardId
     *  keepFileIds: List<Long> - 유지할 기존 파일 ID 목록
     *  newFiles: List<MultipartFile> - 새로 업로드한 파일 목록
     *
     * [ 서버 처리 순서 ]
     * 1. 기존 파일 목록 조회
     * 2. 삭제 대상 선정      > 디스크/DB 삭제
     * 3. 신규 파일 저장      > 디스크/DB board_files 추가
     * 4. display_order 다시 정렬
     * */
    @Transactional
    public void updateBoardFiles(Long boardId, BoardFileUpdateRequestDto dto) {
        List<Long> keepIds = dto.getKeepFileIds() == null
                ? List.of()
                : dto.getKeepFileIds();

        List<MultipartFile> newFiles = dto.getNewFiles();

        // 1. 현재 DB에 저장된 파일 목록 조회
        List<BoardFile> currentFiles = boardFileRepository.findByBoardIdOrderByDisplayOrderAsc(boardId);

        // 2. 삭제 대상 선정
        List<BoardFile> deleteTargets = currentFiles.stream()
                // 현재 파일 목록을 순회하여
                // 유지할 기존 File 목록(id)에 해당 파일의 info id 값이 포함되어 있지 않을 경우
                // 해당(포함되어 있지 않은) 파일을 새로운 배열에 담기!
                .filter(boardFile -> {
                    FileInfo info = boardFile.getFileInfo();
                    return info == null || !keepIds.contains(info.getId());
                })
                .toList();

        // 3. 삭제 처리
        for (BoardFile bf : deleteTargets) {
            boardFileRepository.delete(bf);             // board_files 삭제
            FileInfo info = bf.getFileInfo();
            if (info != null) {
                fileService.deleteFile(info);           // 디스크 + file_infos 삭제
            }
        }

        // 4. 신규 파일 추가
        if (newFiles != null && !newFiles.isEmpty()) {
            System.out.println(newFiles);
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 id의 게시글이 없습니다."));

            int maxOrder = boardFileRepository.findByBoardIdOrderByDisplayOrderAsc(boardId)
                    .stream() // List<BoardFile>
                    .mapToInt(b -> b.getDisplayOrder())
                    .max()
                    .orElse(-1);

            System.out.println(maxOrder);
            for (MultipartFile mf : newFiles) {
                FileInfo info = fileService.saveBoardFile(boardId, mf);

                BoardFile bf = BoardFile.of(board, info, ++maxOrder);

                boardFileRepository.save(bf);
            }
        }
    }
}