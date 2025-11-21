package org.example.boardback.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestDto(
        // 이미지 링크에 있던 비밀번호 재설정 토큰
        @NotBlank(message = "토큰은 필수입니다.")
        String token,

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        String newPassword,

        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        String confirmPassword
) {}