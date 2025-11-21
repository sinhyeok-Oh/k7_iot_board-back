package org.example.boardback.service.auth;

import org.example.boardback.dto.ResponseDto;
import org.example.boardback.dto.auth.request.*;
import org.example.boardback.dto.auth.response.LoginResponseDto;
import org.example.boardback.dto.auth.response.PasswordVerifyResponseDto;
import org.example.boardback.dto.auth.response.SignupResponseDto;

public interface AuthService {
    ResponseDto<LoginResponseDto> login(LoginRequestDto request);
    ResponseDto<LoginResponseDto> refresh(RefreshRequestDto request);
    ResponseDto<Void> logout(LogoutRequestDto request);
    ResponseDto<SignupResponseDto> signup(SignupRequestDto request);
    ResponseDto<PasswordVerifyResponseDto> verifyPasswordToken(String token);
    ResponseDto<Void> resetPassword(PasswordResetRequestDto request);
    ResponseDto<Void> sendPasswordResetEmail(String email);
    ResponseDto<Void> sendVerifyCode(String email);
    ResponseDto<Void> verifyEmailCode(String email, String code);
}