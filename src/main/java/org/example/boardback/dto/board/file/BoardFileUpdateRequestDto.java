
package org.example.boardback.dto.board.file;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BoardFileUpdateRequestDto {
    private List<Long> keepFileIds;         // 유지할 기존 파일 리스트 (file_info의 id값)
    private List<MultipartFile> newFiles;   // 새로 추가되는 파일
}
