package org.example.boardback.security.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.boardback.common.enums.ErrorCode;
import org.example.boardback.exception.BusinessException;
import org.example.boardback.security.user.UserPrincipal;

/**
 * === PrincipalUtils ===
 *  - 인증된 UserPrincipal의 상태 검사 유틸리티
 *  - SecurityContext 인증 객체의 상태를 서비스/컨트롤러에서 통합 검증
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrincipalUtils {

    /**
     * principal 상태 검증:
     * 1) null → INVALID_AUTH
     * 2) disabled → ACCESS_DENIED
     * 3) locked → ACCESS_DENIED
     * 4) account expired → ACCESS_DENIED
     * 5) credentials expired → ACCESS_DENIED
     */
    public static void validateActive(UserPrincipal principal) {

        // 1) 로그인 안 한 상태 (null principal)
        if (principal == null) {
            throw new BusinessException(ErrorCode.INVALID_AUTH);
        }

        // 2) 비활성화된 계정
        if (!principal.isEnabled()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 3) 잠긴 계정
        if (!principal.isAccountNonLocked()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 4) 계정 기간 만료
        if (!principal.isAccountNonExpired()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 5) 비밀번호(자격 증명) 만료
        if (!principal.isCredentialsNonExpired()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }
}