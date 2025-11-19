package org.example.boardback.controller;

import lombok.RequiredArgsConstructor;
import org.example.boardback.common.apis.BoardApi;
import org.example.boardback.dto.board.file.BoardFileListDto;
import org.example.boardback.dto.board.file.BoardFileUpdateRequestDto;
import org.example.boardback.entity.file.FileInfo;
import org.example.boardback.service.impl.BoardFileServiceImpl;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping(BoardApi.ROOT)
@RequiredArgsConstructor
public class BoardFileController {
    private final BoardFileServiceImpl boardFileService;

    @PostMapping(BoardApi.ID_ONLY + "/files")
    public ResponseEntity<?> uploadBoardFiles(
            @PathVariable Long boardId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        boardFileService.uploadBoardFiles(boardId, files);
        return ResponseEntity.ok("업로드 성공");
    }

    @GetMapping(BoardApi.ID_ONLY + "/files")
    public ResponseEntity<List<BoardFileListDto>> getFilesByBoard(
            @PathVariable Long boardId
    ) {
        List<BoardFileListDto> files = boardFileService.getFilesByBoard(boardId);

        if (files == null || files.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(files); // 200 OK
    }

    @GetMapping("/file/download/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) {
        // service 에서 파일 정보 조회
        FileInfo info = boardFileService.getFileInfo(fileId);

        // 파일 경로 획득 + 존재 여부 검증
        Path path = boardFileService.loadFile(fileId);

        // Resource 변환
        Resource resource = new PathResource(path);

        // 다운로드에 필요한 헤더 생성 (파일명 인코딩, MIME 타입 등)
        HttpHeaders headers = boardFileService.createDownloadHeaders(info, path);

        // 응답 반환
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<Void> deleteBoardFile(@PathVariable Long fileId) {
        boardFileService.deleteBoardFile(fileId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(
            value = BoardApi.ID_ONLY + "/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateBoardFiles(
            @PathVariable Long boardId,
            // ModelAttribute
            // : 쿼리 파라미터나 HTML 폼 데이터를 Java 객체로 바인딩할 때 사용
            // MultipartFile + List<Long> 조합 시 JSON + 파일 혼합이 불가능
            //      >> FormData 기반 요청에는 @RequestBody 사용 불가
            //      >> @ModelAttribute가 가장 안정적인 방식
            @ModelAttribute BoardFileUpdateRequestDto dto) {
        boardFileService.updateBoardFiles(boardId, dto);
        return ResponseEntity.noContent().build();
    }
}