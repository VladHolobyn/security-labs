package com.holobyn.security.service;

import com.holobyn.security.domain.User;
import com.holobyn.security.domain.UserRole;
import com.holobyn.security.dto.AuthenticationRequestDto;
import com.holobyn.security.dto.AuthenticationResponseDto;
import com.holobyn.security.dto.PasswordRestoreDto;
import com.holobyn.security.dto.ReqistrationRequestDto;
import com.holobyn.security.dto.UserDto;
import com.holobyn.security.dto.VerificationDto;
import com.holobyn.security.exception.BlockeException;
import com.holobyn.security.mapper.UserMapper;
import com.holobyn.security.security.JwtUtils;
import jakarta.mail.MessagingException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final FailedLoginAttemptSercvice failedLoginAttemptSercvice;
    private final GoogleAuthService googleAuthService;
    private final PasswordEncoder encoder;



    public AuthenticationResponseDto login(AuthenticationRequestDto loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        User userDetails = (User) authentication.getPrincipal();

        System.out.println(failedLoginAttemptSercvice.isBlocked(userDetails.getId()));
        if (failedLoginAttemptSercvice.isBlocked(userDetails.getId())) {
            throw new BlockeException("Your account is blocked wait 5 minutes to try again");
        }

        if (userDetails.is2FAEnabled()) {
            if (!googleAuthService.isValid(userDetails.getTotpSecret(), loginDTO.getCode())) {
                throw new BlockeException("Wrong code");
            }
        }

        String email = userDetails.getUsername();
        Long userID = userDetails.getId();
        UserRole role = UserRole.valueOf(
            userDetails.getAuthorities().stream().map(Object::toString).toList().get(0)
        );

        String jwt = jwtUtils.generateToken(email, userID, role);

        return new AuthenticationResponseDto(jwt, false);
    }

    public AuthenticationResponseDto verify(Long userId, Integer code) {
        User user = userService.loadUserById(userId);

        if (!googleAuthService.isValid(user.getTotpSecret(), code)) {
            throw new BlockeException("Wrong code");
        }

        String jwt = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole());

        return new AuthenticationResponseDto(jwt, false);
    }

    public UserDto register(ReqistrationRequestDto reqistrationRequestDto) {

        if (userService.userExistsByEmail(reqistrationRequestDto.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        if(!isValidPassword(reqistrationRequestDto.getPassword())) {
            throw new RuntimeException("Weak password");
        }

        User createdUser = userService.save(reqistrationRequestDto);
//        sendVerificationCode(createdUser);

        return userMapper.toDto(createdUser);
    }

    private void sendVerificationCode(User user) {

        String token = jwtUtils.generateVerificationToken(user.getEmail(), user.getId());

        try {
            emailService.sendEmail(user.getEmail(), "Verification Code", "Your token: "+ token);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    public UserDto verifyUser(String token) {
        try {
            Long userId = jwtUtils.extractVerificationUserDetails(token);
            return userMapper.toDto(userService.activate(userId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String passwordRestoreRequest(String email) {
        User user = (User) userService.loadUserByUsername(email);

        String token = jwtUtils.generateRestoreToken(user.getEmail(), user.getId());

        try {
            emailService.sendEmail(user.getEmail(), "Restore Password Code", "Your token: "+ token);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return "Ok";
    }

    public UserDto restorePassword(PasswordRestoreDto passwordRestoreDto) {
        try {
            Long userId = jwtUtils.extractRestoreUserDetails(passwordRestoreDto.getRestoreToken());

            if(!isValidPassword(passwordRestoreDto.getNewPassword())) {
                throw new RuntimeException("Weak password");
            }

            return userMapper.toDto(userService.changePassword(userId, passwordRestoreDto.getNewPassword()));
        } catch (Exception e) {
            throw new RuntimeException(e);
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

        return googleAuthService.generateQRUrl(key, user.getEmail());
    }
    public UserDto disable2FA(Long userId) {
        User user = userService.changeToptToken(userId, null);
        return userMapper.toDto(user);
    }

    public byte[] getQRcode(Long userId) {
        User user = userService.loadUserById(userId);
        return googleAuthService.generateQRUrl(user.getTotpSecret(), user.getEmail());
    }




}
