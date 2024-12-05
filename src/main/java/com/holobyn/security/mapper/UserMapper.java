package com.holobyn.security.mapper;

import com.holobyn.security.domain.User;
import com.holobyn.security.domain.UserRole;
import com.holobyn.security.dto.ReqistrationRequestDto;
import com.holobyn.security.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;


    public User toEntity(ReqistrationRequestDto reqistrationRequestDto) {
        return User.builder()
                   .firstName(reqistrationRequestDto.getFirstname())
                   .lastName(reqistrationRequestDto.getLastname())
                   .email(reqistrationRequestDto.getEmail())
                   .password(passwordEncoder.encode(reqistrationRequestDto.getPassword()))
                   .role(UserRole.USER)
                   .build();
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                      .firstName(user.getFirstName())
                      .lastName(user.getLastName())
                      .email(user.getEmail())
                      .isEnabled(user.isEnabled())
                      .role(user.getRole())
                      .id(user.getId())
                      .is2FAEnabled(user.is2FAEnabled())
                      .build();
    }

}
