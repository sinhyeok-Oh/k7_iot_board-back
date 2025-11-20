package org.example.boardback.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boardback.security.provider.JwtProvider;
import org.example.boardback.security.user.UserPrincipal;
import org.example.boardback.security.user.UserPrincipalMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * === JwtAuthenticationFilter ===
 * - 매 요청마다 JWT 검증 후 SecurityContext에 인증 정보 주입
 * - Bearer Token 파싱, 검증(jwtProvider), userPrincipal 매핑
 * - 인증 성공 시 UsernamePasswordAuthenticationToken 구성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserPrincipalMapper userPrincipalMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        try {
            if (token != null && jwtProvider.isValidToken(token)) {

                String username = jwtProvider.getUsernameFromJwt(token);
                UserPrincipal principal = userPrincipalMapper.toPrincipal(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.warn("[JwtAuthenticationFilter] 토큰 검증 실패: {}", ex.getMessage());
            // Exception은 JsonAuthenticationEntryPoint 로 전달됨
        }

        chain.doFilter(request, response);
    }

    /** Authorization Header → Bearer 토큰 추출 */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith(JwtProvider.BEARER_PREFIX)) {
            return header.substring(JwtProvider.BEARER_PREFIX.length()).trim();
        }
        return null;
    }
}