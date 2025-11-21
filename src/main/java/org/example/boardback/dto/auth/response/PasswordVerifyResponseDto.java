package org.example.boardback.dto.auth.response;

public record PasswordVerifyResponseDto(
        boolean valid,
        String email
) {

    public static PasswordVerifyResponseDto success(String email) {
        return new PasswordVerifyResponseDto(true, email);
    }

    public static PasswordVerifyResponseDto failure() {
        return new PasswordVerifyResponseDto(false, null);
    }
}