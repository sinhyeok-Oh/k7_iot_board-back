package org.example.boardback.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.boardback.common.enums.ErrorCode;
import org.example.boardback.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// AccessDeniedHandler 인터페이스
// : 인증은 완료되었으나 요청에 대한 권한을 가지고 있지 않은 사용자가 엔드포인트에 접근 할 때 발생

// 403 전용 AccessDeniedHandler
@Slf4j
@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        log.warn("[AccessDeniedHandler] 권한 부족: {}", accessDeniedException.getMessage());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");

        ResponseDto<?> body = ResponseDto.failure(
                "접근 권한이 없습니다.",
                HttpStatus.FORBIDDEN.value(),
                ErrorCode.ACCESS_DENIED.name()
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}