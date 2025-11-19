package org.example.boardback.repository.file;

import org.example.boardback.entity.file.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {
    List<BoardFile> findByBoardIdOrderByDisplayOrderAsc(Long boardId);

    Optional<BoardFile> findByFileInfoId(Long fileId);
}