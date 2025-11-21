package org.example.boardback.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.boardback.common.enums.Gender;
import org.example.boardback.entity.file.FileInfo;
import org.example.boardback.entity.user.User;

public record SignupRequestDto (
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 4, max = 50, message = "아이디는 4~50자 사이여야 합니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 100, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        String password,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
        String nickname,

        Gender gender
) {
    /**
     * DTO → Entity 변환 (비밀번호 암호화 전 상태)
     * 암호화는 Service에서 PasswordEncoder로 처리
     */
    // to) DTO -> Entity 변환할 떄
    //     : DTO를 Entity로 만든다
    public User toEntity(String encodedPassword, FileInfo profileFile) {
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .nickname(nickname)
                .gender(gender)
                .profileFile(profileFile)
                .build();
    }
}