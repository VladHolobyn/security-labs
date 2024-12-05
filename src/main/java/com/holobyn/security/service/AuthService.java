package com.holobyn.security.service;

import com.holobyn.security.domain.User;
import com.holobyn.security.dto.AuthenticationRequestDto;
import com.holobyn.security.dto.AuthenticationResponseDto;
import com.holobyn.security.dto.PasswordRestoreDto;
import com.holobyn.security.dto.ReqistrationRequestDto;
import com.holobyn.security.dto.UserDto;
import com.holobyn.security.exception.ApiException;
import com.holobyn.security.mapper.UserMapper;
import com.holobyn.security.security.JwtUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final UserMapper userMapper;

    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    private final GoogleAuthService googleAuthService;

    private final AESUtil aesUtil;


    public AuthenticationResponseDto login(AuthenticationRequestDto loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        if (user.is2FAEnabled()) {
            String userSecretKey;
            try {
                userSecretKey = aesUtil.decrypt(user.getTotpSecret());
            } catch (Exception e) {
                throw new ApiException(e.getMessage());
            }

            if (!googleAuthService.isValid(userSecretKey, loginDTO.getCode())) {
                throw new ApiException("Invalid code");
            }
        }

        String jwt = jwtUtils.generateToken(user.getUsername(), user.getId(), user.getRole());
        return new AuthenticationResponseDto(jwt);
    }

    public UserDto register(ReqistrationRequestDto reqistrationRequestDto) {

        if (userService.userExistsByEmail(reqistrationRequestDto.getEmail())) {
            throw new ApiException("User with this email already exists");
        }

        if (!isValidPassword(reqistrationRequestDto.getPassword())) {
            throw new ApiException("Your password is weak");
        }

        User createdUser = userService.save(reqistrationRequestDto);
        sendVerificationCode(createdUser);

        return userMapper.toDto(createdUser);
    }

    private void sendVerificationCode(User user) {
        String token = jwtUtils.generateVerificationToken(user.getEmail(), user.getId());

        try {
            emailService.sendEmail(user.getEmail(), "Verification Code", "Your token: " + token);
        } catch (MessagingException e) {
            throw new ApiException(e.getMessage());
        }
    }


    public UserDto verifyUser(String token) {
        try {
            Long userId = jwtUtils.extractVerificationUserDetails(token);
            return userMapper.toDto(userService.activate(userId));
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    public String passwordRestoreRequest(String email) {
        User user = (User) userService.loadUserByUsername(email);
        String token = jwtUtils.generateRestoreToken(user.getEmail(), user.getId());

        try {
            emailService.sendEmail(user.getEmail(), "Reset Password Code", "Your token: " + token);
        } catch (MessagingException e) {
            throw new ApiException(e.getMessage());
        }

        return "Ok";
    }

    public UserDto restorePassword(PasswordRestoreDto passwordRestoreDto) {
        try {
            Long userId = jwtUtils.extractRestoreUserDetails(passwordRestoreDto.getRestoreToken());

            if (!isValidPassword(passwordRestoreDto.getNewPassword())) {
                throw new ApiException("Your password is weak");
            }

            return userMapper.toDto(userService.changePassword(userId, passwordRestoreDto.getNewPassword()));
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }


    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }

            if (hasUppercase && hasLowercase && hasDigit && hasSpecialChar) {
                return true;
            }
        }

        return false;
    }


    public byte[] enable2FA(Long userId) {
        String key = googleAuthService.generateKey();
        User user = userService.changeToptToken(userId, key);

        return googleAuthService.generateQRImage(key, user.getEmail());
    }

    public UserDto disable2FA(Long userId) {
        User user = userService.changeToptToken(userId, null);
        return userMapper.toDto(user);
    }

    public byte[] getQRCode(Long userId) {
        User user = userService.loadUserById(userId);
        return googleAuthService.generateQRImage(user.getTotpSecret(), user.getEmail());
    }

}
