package org.example.boardback.repository.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class EmailVerifyRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "email:verify:";

    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(
                PREFIX + email,
                code,
                Duration.ofMinutes(10)     // 인증코드 10분 유효
        );
    }

    public String getCode(String email) {
        return redisTemplate.opsForValue().get(PREFIX + email);
    }

    public void deleteCode(String email) {
        redisTemplate.delete(PREFIX + email);
    }
}