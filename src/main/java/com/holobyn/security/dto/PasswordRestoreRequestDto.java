package com.holobyn.security.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class PasswordRestoreRequestDto {

    @Email
    private String email;

}
