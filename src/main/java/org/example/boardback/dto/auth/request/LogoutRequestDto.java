package org.example.boardback.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequestDto (
        @NotBlank(message = "리프레시 토큰은 필수입니다.")
        String refreshToken
) {}