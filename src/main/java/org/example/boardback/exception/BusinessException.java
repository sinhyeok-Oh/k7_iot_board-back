package org.example.boardback.exception;

import lombok.Getter;
import org.example.boardback.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String detailMessage; // 추가 메시지(Optional)

    /**
     * 기본 BusinessException
     * - ErrorCode.message 클라이언트 노출
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    /**
     * 실무 사용 빈도 높음
     * - 에러 상세 메시지를 서버 로깅용으로 활용
     * - 클라이언트 메시지는 ErrorCode 그대로 유지
     */
    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    /**
     * HttpStatus 대신 정수로 반환
     * ResponseDto 생성 시 편리함
     */
    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    @Override
    public String toString() {
        return "[BusinessException] " + errorCode +
                (detailMessage != null ? " / detail: " + detailMessage : "");
    }
}