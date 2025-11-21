package org.example.boardback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boardback.common.apis.AuthApi;
import org.example.boardback.dto.ResponseDto;
import org.example.boardback.dto.auth.request.*;
import org.example.boardback.dto.auth.response.LoginResponseDto;
import org.example.boardback.dto.auth.response.PasswordVerifyResponseDto;
import org.example.boardback.dto.auth.response.SignupResponseDto;
import org.example.boardback.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @PostMapping(AuthApi.LOGIN)
    public ResponseEntity<ResponseDto<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request
    ) {
        ResponseDto<LoginResponseDto> response = authService.login(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * 토큰 재발급
     */
    @PostMapping(AuthApi.REFRESH)
    public ResponseEntity<ResponseDto<LoginResponseDto>> refresh(
            @Valid @RequestBody RefreshRequestDto request
    ) {
        ResponseDto<LoginResponseDto> response = authService.refresh(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * 로그아웃
     */
    @PostMapping(AuthApi.LOGOUT)
    public ResponseEntity<ResponseDto<Void>> logout(
            @Valid @RequestBody LogoutRequestDto request
    ) {
        ResponseDto<Void> response = authService.logout(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * 회원가입
     */
    @PostMapping(AuthApi.SIGNUP)
    public ResponseEntity<ResponseDto<SignupResponseDto>> signup(
            @Valid @RequestBody SignupRequestDto request
    ) {
        ResponseDto<SignupResponseDto> response = authService.signup(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * 비밀번호 재설정 토큰 유효성 확인
     */
    @GetMapping(AuthApi.PASSWORD_VERIFY)
    public ResponseEntity<ResponseDto<PasswordVerifyResponseDto>> verifyPasswordToken(
            @RequestParam("token") String token
    ) {
        ResponseDto<PasswordVerifyResponseDto> response = authService.verifyPasswordToken(token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * 비밀번호 재설정
     */
    @PostMapping(AuthApi.PASSWORD_RESET)
    public ResponseEntity<ResponseDto<Void>> resetPassword(
            @Valid @RequestBody PasswordResetRequestDto request
    ) {
        ResponseDto<Void> response = authService.resetPassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}