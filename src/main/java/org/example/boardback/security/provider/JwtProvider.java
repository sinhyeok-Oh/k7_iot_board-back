package org.example.boardback.security.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

/**
 * JwtProvider - 실무용 리팩토링 버전
 *
 * - Access / Refresh / Email 토큰 생성
 * - 토큰 검증(서명 + 만료 + clock-skew 허용)
 * - Claims 기반 정보 추출 (username / roles / email)
 *
 * 설정:
 *  - jwt.secret (Base64 인코딩된 시크릿, 최소 256bit)
 *  - jwt.expiration (access token ms)
 *  - jwt.refresh-expiration (refresh token ms)
 *  - jwt.email-expiration (email token ms)
 *  - jwt.clock-skew-seconds (optional, default 0)
 */
@Component
public class JwtProvider {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_TYPE = "type";

    private final SecretKey key;
    private final long accessExpMs;
    private final long refreshExpMs;
    private final long emailExpMs;
    private final int clockSkewSeconds;

    private final JwtParser parser;

    public JwtProvider(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.expiration}") long accessExpMs,
            @Value("${jwt.refresh-expiration}") long refreshExpMs,
            @Value("${jwt.email-expiration}") long emailExpMs,
            @Value("${jwt.clock-skew-seconds:0}") int clockSkewSeconds
    ) {
        byte[] secretBytes = Decoders.BASE64.decode(base64Secret);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException("jwt.secret must be at least 256 bits (32 bytes) when Base64-decoded.");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);

        this.accessExpMs = accessExpMs;
        this.refreshExpMs = refreshExpMs;
        this.emailExpMs = emailExpMs;
        this.clockSkewSeconds = Math.max(clockSkewSeconds, 0);

        this.parser = Jwts.parser()
                .setSigningKey(this.key)
                .setAllowedClockSkewSeconds(this.clockSkewSeconds)
                .build();
    }

    /* ============================
     * Token 생성 API
     * ============================ */

    /**
     * Access 토큰 생성 (subject = username)
     */
    public String generateAccessToken(String username, Set<String> roles) {
        return buildToken(username, roles, accessExpMs);
    }

    /**
     * Refresh 토큰 생성 (subject = username)
     */
    public String generateRefreshToken(String username, Set<String> roles) {
        return buildToken(username, roles, refreshExpMs);
    }

    /**
     * Email 목적의 토큰 생성 (claim 'email' 및 'type' 포함)
     * type 예: "VERIFY_EMAIL", "RESET_PASSWORD"
     */
    public String generateEmailJwtToken(String email, String type) {
        long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + emailExpMs);

        return Jwts.builder()
                .setSubject(email) // subject에도 email 저장 (편의성)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_TYPE, type)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* ============================
     * Token 검증 & Claims 조회
     * ============================ */

    /**
     * 토큰이 유효한지(서명/만료 포함) 확인.
     * 예외는 내부에서 잡아 boolean 으로 변환.
     */
    public boolean isValidToken(String token) {
        try {
            parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Email 토큰이 기대하는 type과 일치하고 유효한지 확인.
     * (예: expectedType = "RESET_PASSWORD")
     */
    public boolean isValidEmailToken(String token, String expectedType) {
        try {
            Claims claims = parseClaimsJws(token);
            String type = claims.get(CLAIM_TYPE, String.class);
            return expectedType == null ? type != null : expectedType.equals(type);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * 토큰에서 Claims 반환 (서명 및 만료 검증 포함).
     * 유효하지 않으면 예외(ExpiredJwtException 등)를 던짐.
     */
    public Claims getClaims(String token) {
        return parseClaimsJws(token);
    }

    /**
     * JWT subject (username/email) 조회
     */
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Access/Refresh token 용 username 조회 (subject)
     */
    public String getUsernameFromJwt(String token) {
        return getSubject(token);
    }

    /**
     * Email 토큰에서 email 추출 (claim 'email' 또는 subject)
     */
    public String getEmailFromEmailToken(String token) {
        Claims c = getClaims(token);
        String email = c.get(CLAIM_EMAIL, String.class);
        return (email != null) ? email : c.getSubject();
    }

    /**
     * 토큰의 roles 클레임을 Set<String>으로 반환 (없으면 빈 집합)
     */
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromJwt(String token) {
        Claims c = getClaims(token);
        Object raw = c.get(CLAIM_ROLES);
        if (raw == null) return Set.of();

        if (raw instanceof Collection<?> coll) {
            Set<String> result = new HashSet<>();
            for (Object o : coll) if (o != null) result.add(o.toString());
            return result;
        }
        return Set.of(raw.toString());
    }

    /**
     * 토큰의 남은 만료 시간 (ms). 만료된 경우 음수 반환.
     */
    public long getRemainingMillis(String token) {
        Claims c = getClaims(token);
        Date exp = c.getExpiration();
        return (exp == null) ? -1L : (exp.getTime() - System.currentTimeMillis());
    }

    /* ============================
     * 유틸(헤더 처리 등)
     * ============================ */

    /**
     * "Bearer ..." 문자열에서 실제 토큰 부분 제거
     */
    public String removeBearer(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Authorization 형식이 유효하지 않습니다.");
        }
        return bearerToken.substring(BEARER_PREFIX.length()).trim();
    }

    /* ============================
     * Private helpers
     * ============================ */

    private Claims parseClaimsJws(String token) {
        JwtParser p = this.parser;
        // parser.parseClaimsJws throws various JwtException (ExpiredJwtException, MalformedJwtException, etc.)
        Jws<Claims> jws = p.parseClaimsJws(token);
        return jws.getBody();
    }

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
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}