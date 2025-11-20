package org.example.boardback.security.user;

import lombok.RequiredArgsConstructor;
import org.example.boardback.entity.user.User;
import org.example.boardback.repository.user.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserPrincipalMapper {

    private final UserRepository userRepository;

    /** === JWT Filter에서 username만 가지고 principal 생성용 === */
    public UserPrincipal toPrincipal(@NonNull String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return map(user);
    }

    /** === DB User 엔티티 → UserPrincipal 변환 === */
    public UserPrincipal map(@NonNull User user) {

        List<SimpleGrantedAuthority> authorities =
                (user.getUserRoles() == null || user.getUserRoles().isEmpty())
                        ? List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        : user.getUserRoles().stream()
                        .map(role -> {
                            String r = role.getRole().getName().name();
                            String name = r.startsWith("ROLE_") ? r : "ROLE_" + r;
                            return new SimpleGrantedAuthority(name);
                        })
                        .toList();

        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }
}