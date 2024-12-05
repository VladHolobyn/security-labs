package com.holobyn.security.service;

import com.holobyn.security.domain.User;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FailedLoginAttemptSercvice {
    private static final Logger logger = LoggerFactory.getLogger(FailedLoginAttemptSercvice.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;


    private final int MAX_ATTEMPTS = 3;

    public void failedLoginAttempt(Authentication authentication) {
        String key = "loginAttempts:" + authentication.getPrincipal();

        String attempts = redisTemplate.opsForValue().get(key);
        System.out.println(attempts);
        attempts = (attempts == null) ? "1" : String.valueOf(Integer.parseInt(attempts) + 1);

        logger.warn("User %s attempted to login: %s".formatted(authentication.getPrincipal(), attempts));
        redisTemplate.opsForValue().set(key, attempts, 5, TimeUnit.MINUTES);
    }

    public boolean isBlocked(Long userId) {
        User user = userService.loadUserById(userId);

        String attempts = redisTemplate.opsForValue().get("loginAttempts:" + user.getEmail());

        return attempts != null && Integer.parseInt(attempts) >= MAX_ATTEMPTS;
    }
}
