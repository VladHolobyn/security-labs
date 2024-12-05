package com.holobyn.security.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FailedLoginAttemptService {

    private static final Logger logger = LoggerFactory.getLogger(FailedLoginAttemptService.class);

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.security.invalid-login-attempts}")
    private int maxAttempts;

    @Value("${spring.security.wait-time-m}")
    private int waitTimeInMinutes;

    public void failedLoginAttempt(Authentication authentication) {
        String key = "loginAttempts:%s".formatted(authentication.getPrincipal());

        String attempts = redisTemplate.opsForValue().get(key);
        attempts = (attempts == null) ? "1" : String.valueOf(Integer.parseInt(attempts) + 1);

        logger.warn("User %s attempted to login: %s times".formatted(authentication.getPrincipal(), attempts));
        redisTemplate.opsForValue().set(key, attempts, waitTimeInMinutes, TimeUnit.MINUTES);
    }


    public boolean isBlocked(String email) {
        String attempts = redisTemplate.opsForValue().get("loginAttempts:%s".formatted(email));

        return attempts != null && Integer.parseInt(attempts) >= maxAttempts;
    }

}
