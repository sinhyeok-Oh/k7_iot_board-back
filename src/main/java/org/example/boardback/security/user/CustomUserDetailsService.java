package org.example.boardback.security.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boardback.entity.user.User;
import org.example.boardback.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * === CustomUserDetailsService ===
 * Spring Security의 DaoAuthenticationProvider가
 * "username"으로 사용자를 찾는 과정에서 호출하는 공식 확장 포인트.
 *
 * 🔥 인증 흐름
 * UsernamePasswordAuthenticationFilter
 * → DaoAuthenticationProvider
 * → loadUserByUsername()
 * → UserPrincipal 반환
 * → PasswordEncoder 검사
 * → SecurityContext 저장
 *
 * 실무 팁:
 * - findByUsername / findByEmail / findByLoginId 등 정책에 따라 변경 가능
 * - 엔티티를 직접 SecurityContext에 저장하면 안 되므로
 *   반드시 UserPrincipalMapper를 통해 보안 VO로 변환해야 한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserPrincipalMapper userPrincipalMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Invalid username");
        }

        String loginId = username.trim();

        // 🔥 실무 기준: username 기반 조회
        // 이메일 로그인 정책이면: userRepository.findByEmail(loginId)
        User user = userRepository.findByUsername(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        // 🔥 엔티티 → UserPrincipal 변환
        return userPrincipalMapper.map(user);
    }
}