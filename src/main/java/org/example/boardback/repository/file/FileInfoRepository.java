package org.example.boardback.repository.file;

import org.example.boardback.entity.file.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
}