package com.holobyn.security.controller;

import com.holobyn.security.dto.AuthenticationRequestDto;
import com.holobyn.security.dto.AuthenticationResponseDto;
import com.holobyn.security.dto.PasswordRestoreDto;
import com.holobyn.security.dto.PasswordRestoreRequestDto;
import com.holobyn.security.dto.ReqistrationRequestDto;
import com.holobyn.security.dto.UserDto;
import com.holobyn.security.dto.VerificationDto;
import com.holobyn.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthenticationResponseDto login(
        @RequestBody AuthenticationRequestDto authenticationRequestDto
    ) {
        return authService.login(authenticationRequestDto);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(
        @Valid @RequestBody ReqistrationRequestDto reqistrationRequestDto
    ) {
        return authService.register(reqistrationRequestDto);
    }


    @PostMapping("/verify")
    public UserDto verify(
        @Valid @RequestBody VerificationDto token
    ) {
        return authService.verifyUser(token.getToken());
    }

    @PostMapping("/password-restore-request")
    public String requestPasswordRestore(
        @RequestBody PasswordRestoreRequestDto email
    ) {
        return authService.passwordRestoreRequest(email.getEmail());
    }

    @PostMapping("/restore-password")
    public UserDto restorePassword(
        @RequestBody PasswordRestoreDto passwordRestoreDto
        ) {
        return authService.restorePassword(passwordRestoreDto);
    }

}
