package com.holobyn.security.security;

import com.holobyn.security.domain.User;
import com.holobyn.security.domain.UserRole;
import com.holobyn.security.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
public class JwtUtils {

    @Value("${spring.security.secret-key}")
    private String secret;

    @Value("${spring.security.token-expiration-time}")
    private int expirationTime;

    @Value("${spring.security.reset-pass-token-expiration-time}")
    private int resetPasswordTokenexpirationTime;

    @Value("${spring.security.verification-token-expiration-time}")
    private int verificationTokenexpirationTime;


    public String generateToken(String email, Long userId, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);

        return generateJwtToken(email, claims, expirationTime);
    }

    public String generateVerificationToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", userId);

        return generateJwtToken("Verification Token", claims, verificationTokenexpirationTime);
    }

    public String generateRestoreToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", userId);

        return generateJwtToken("Reset Token", claims, resetPasswordTokenexpirationTime);
    }

    private String generateJwtToken(String subject, Map<String, Object> claims, int expiration) {
        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(subject)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + expiration))
                   .signWith(SignatureAlgorithm.HS256, secret)
                   .compact();
    }

    private Claims parse(String token) {
        return Jwts.parser()
                   .setSigningKey(secret)
                   .parseClaimsJws(token)
                   .getBody();
    }


    public UserDetails extractUserDetails(String token) {
        Claims claims = parse(token);

        return User.builder()
                   .id(claims.get("userId", Long.class))
                   .email(claims.getSubject())
                   .role(UserRole.valueOf(claims.get("role", String.class)))
                   .build();
    }

    public Long extractVerificationUserDetails(String token) {
        Claims claims = parse(token);

        if (!claims.getSubject().equals("Verification Token")) {
            throw new ApiException("Not a verification token");
        }

        return claims.get("userId", Long.class);
    }

    public Long extractRestoreUserDetails(String token) {
        Claims claims = parse(token);

        if (!claims.getSubject().equals("Reset Token")) {
            throw new ApiException("Not a reset token");
        }

        return claims.get("userId", Long.class);
    }

}
