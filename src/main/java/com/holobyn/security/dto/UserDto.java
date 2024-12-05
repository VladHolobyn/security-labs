package com.holobyn.security.dto;

import com.holobyn.security.domain.UserRole;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private boolean isEnabled;

    private UserRole role;

    private boolean is2FAEnabled;
}
