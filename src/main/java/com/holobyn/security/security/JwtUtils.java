package com.holobyn.security.security;

import com.holobyn.security.domain.User;
import com.holobyn.security.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


//@UtilityClass
@Component
public class JwtUtils {

    @Value("${spring.security.secret-key}")
    private String secret;
    @Value("${spring.security.token-expiration-time}")
    private int expirationTime;


    public String generateToken(String email, Long userId, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);

        System.out.println(secret);

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(email)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                   .signWith(SignatureAlgorithm.HS256, secret)
                   .compact();
    }

    public String generateVerificationToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", userId);

        System.out.println(secret);

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject("verification")
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + 300_000)) //5m
                   .signWith(SignatureAlgorithm.HS256, secret)
                   .compact();
    }

    public String generateRestoreToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", userId);

        System.out.println(secret);

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject("restore")
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + 300_000)) //5m
                   .signWith(SignatureAlgorithm.HS256, secret)
                   .compact();
    }

    public UserDetails extractUserDetails(String token) {
        Claims claims = Jwts.parser()
                            .setSigningKey(secret)
                            .parseClaimsJws(token)
                            .getBody();

        return User.builder()
                   .id(claims.get("userId", Long.class))
                   .email(claims.getSubject())
                   .role(UserRole.valueOf(claims.get("role", String.class)))
                   .build();
    }

    public Long extractVerificationUserDetails(String token) {
        Claims claims = Jwts.parser()
                            .setSigningKey(secret)
                            .parseClaimsJws(token)
                            .getBody();

        if (!claims.getSubject().equals("verification")) {
            throw new RuntimeException("Not a verification token");
        }

        return claims.get("userId", Long.class);

//        return User.builder()
//                   .id(claims.get("userId", Long.class))
//                   .email(claims.get("email", String.class))
//                   .build();
    }

    public Long extractRestoreUserDetails(String token) {
        Claims claims = Jwts.parser()
                            .setSigningKey(secret)
                            .parseClaimsJws(token)
                            .getBody();

        if (!claims.getSubject().equals("restore")) {
            throw new RuntimeException("Not a restore token");
        }

        return claims.get("userId", Long.class);
    }

}
