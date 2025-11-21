package org.example.boardback.dto.auth.response;

import org.example.boardback.entity.user.User;

public record SignupResponseDto (
        Long userId,
        String username,
        String email,
        String nickname
) {
    // from) Entity -> DTO 변환할 때
    //       : Entity로부터 DTO를 만든다
    public static SignupResponseDto from(User user) {
        return new SignupResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname()
        );
    }
}