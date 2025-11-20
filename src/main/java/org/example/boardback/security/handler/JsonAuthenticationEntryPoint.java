package org.example.boardback.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.boardback.common.enums.ErrorCode;
import org.example.boardback.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// AuthenticationEntryPoint 인터페이스
// : 인증되지 않은 사용자가 인증이 필요한 엔드포인트로 접근할 때 발생

// cf) 엔드포인트(endpoint)
// : 웹 애플리케이션에서 클라이언트가 서버에 요청을 보내는 특정 URL
// EX) /api/v1/boards, /api/v1/users

// 401 전용 Entry Point
@Slf4j
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        log.warn("[AuthenticationEntryPoint] 인증 실패: {}", authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");

        ResponseDto<?> body = ResponseDto.failure(
                "인증이 필요합니다. (로그인이 필요하거나 토큰이 만료됨)",
                HttpStatus.UNAUTHORIZED.value(),
                ErrorCode.INVALID_AUTH.name()
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}