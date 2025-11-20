package org.example.boardback.security.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

/**
 * === JwtProvider (실무 표준 리팩토링 버전) ===
 * - Access / Refresh / Email 인증 토큰 발급
 * - 토큰 파싱 & 검증 (서명/만료 + clock-skew 허용)
 * - Claims 기반 사용자 정보 추출
 */
@Component
public class JwtProvider {

    /** HTTP Authorization: "Bearer " */
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CLAIM_ROLES = "roles";

    private final SecretKey key;
    private final long accessExpMs;
    private final long refreshExpMs;
    private final long emailExpMs;
    private final int clockSkewSeconds;

    private final JwtParser parser;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long accessExpMs,
            @Value("${jwt.refresh-expiration}") long refreshExpMs,
            @Value("${jwt.email-expiration}") long emailExpMs,
            @Value("${jwt.clock-skew-seconds:0}") int clockSkewSeconds
    ) {

        // Base64 decode → 최소 256 bit 이상 강제
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException("jwt.secret은 최소 256비트(32byte) 이상이어야 합니다.");
        }

        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.accessExpMs = accessExpMs;
        this.refreshExpMs = refreshExpMs;
        this.emailExpMs = emailExpMs;
        this.clockSkewSeconds = Math.max(clockSkewSeconds, 0);

        this.parser = Jwts.parser()
                .verifyWith(this.key)
                .build();
    }

    /* =======================================================================
     *  PUBLIC API — ACCESS / REFRESH / EMAIL 토큰 발급
     * ======================================================================= */

    /** Access Token 생성 */
    public String generateAccessToken(String username, Set<String> roles) {
        return buildToken(username, roles, accessExpMs);
    }

    /** Refresh Token 생성 */
    public String generateRefreshToken(String username, Set<String> roles) {
        return buildToken(username, roles, refreshExpMs);
    }

    /** Email 인증용 Token 생성 */
    public String generateEmailJwtToken(String email) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim("email", email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + emailExpMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* =======================================================================
     *  PUBLIC API — 토큰 검증 & Claims 조회
     * ======================================================================= */

    /** 유효한 토큰인지 (서명/만료 포함) */
    public boolean isValidToken(String token) {
        try {
            parseClaimsInternal(token, true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Claims 반환 (검증 포함) */
    public Claims getClaims(String token) {
        return parseClaimsInternal(token, true);
    }

    /** Username(sub) 조회 */
    public String getUsernameFromJwt(String token) {
        return getClaims(token).getSubject();
    }

    /** roles 조회(Set으로 표준화) */
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromJwt(String token) {
        Object raw = getClaims(token).get(CLAIM_ROLES);

        if (raw == null) return Set.of();

        if (raw instanceof List<?> list) {
            Set<String> result = new HashSet<>();
            for (Object o : list) {
                if (o != null) result.add(o.toString());
            }
            return result;
        }

        return Set.of(raw.toString());
    }

    /** Email token에서 email 조회 */
    public String getEmailFromJwt(String token) {
        return getClaims(token).get("email", String.class);
    }

    /** 남은 만료시간(ms) */
    public long getRemainingMillis(String token) {
        Claims c = parseClaimsInternal(token, true);
        return c.getExpiration().getTime() - System.currentTimeMillis();
    }

    /* =======================================================================
     *  PRIVATE — Bearer 처리
     * ======================================================================= */

    /** Authorization Header → "Bearer " 제거 */
    public String removeBearer(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Authorization 형식이 유효하지 않습니다.");
        }
        return bearerToken.substring(BEARER_PREFIX.length()).trim();
    }

    /* =======================================================================
     *  PRIVATE — 핵심 Claims 파싱 로직 (서명 + 만료 + clock-skew 허용)
     * ======================================================================= */

    private Claims parseClaimsInternal(String token, boolean allowClockSkewOnExpiry) {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException ex) {

            // 만료 + clock skew 허용
            if (allowClockSkewOnExpiry && clockSkewSeconds > 0 && ex.getClaims() != null) {
                Date exp = ex.getClaims().getExpiration();

                long skewMs = clockSkewSeconds * 1000L;
                long now = System.currentTimeMillis();

                if (exp != null && now - exp.getTime() <= skewMs) {
                    return ex.getClaims();
                }
            }

            throw ex;
        }
    }

    /* =======================================================================
     *  PRIVATE — 공통 빌드(Token Signing)
     * ======================================================================= */

    private String buildToken(String username, Set<String> roles, long expMs) {
        long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + expMs);

        List<String> roleList = (roles == null) ? List.of() : new ArrayList<>(roles);

        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIM_ROLES, roleList)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }
}