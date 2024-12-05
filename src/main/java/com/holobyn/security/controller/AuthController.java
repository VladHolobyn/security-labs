package com.holobyn.security.controller;

import com.holobyn.security.dto.AuthenticationRequestDto;
import com.holobyn.security.dto.AuthenticationResponseDto;
import com.holobyn.security.dto.PasswordRestoreDto;
import com.holobyn.security.dto.PasswordRestoreRequestDto;
import com.holobyn.security.dto.ReqistrationRequestDto;
import com.holobyn.security.dto.UserDto;
import com.holobyn.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public AuthenticationResponseDto login(@Valid @RequestBody AuthenticationRequestDto authenticationRequestDto) {
        return authService.login(authenticationRequestDto);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody ReqistrationRequestDto reqistrationRequestDto) {
        return authService.register(reqistrationRequestDto);
    }


    @PostMapping("/verify/{token}")
    public UserDto verifyUser(@PathVariable String token) {
        return authService.verifyUser(token);
    }

    @PostMapping("/password-reset/request")
    public String requestPasswordReset(@Valid @RequestBody PasswordRestoreRequestDto resetRequestDto) {
        return authService.passwordRestoreRequest(resetRequestDto.getEmail());
    }

    @PostMapping("/reset-password")
    public UserDto resetPassword(@Valid @RequestBody PasswordRestoreDto passwordRestoreDto) {
        return authService.restorePassword(passwordRestoreDto);
    }

    @PostMapping("/2fa/{userId}/enable")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<byte[]> enable2fa(@PathVariable Long userId) {
        return sendImage(authService.enable2FA(userId));
    }

    @PostMapping("/2fa/{userId}/disable")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public UserDto disable2fa(@PathVariable Long userId) {
        return authService.disable2FA(userId);
    }

    @GetMapping("/2fa/qr/{userId}")
    public ResponseEntity<byte[]> getQRCode(@PathVariable Long userId) {
        return sendImage(authService.getQRcode(userId));
    }


    private ResponseEntity<byte[]> sendImage(byte[] image) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

}
