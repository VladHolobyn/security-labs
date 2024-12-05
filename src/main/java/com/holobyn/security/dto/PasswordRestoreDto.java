package com.holobyn.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordRestoreDto {

    @NotBlank
    private String restoreToken;

    @NotBlank
    private String newPassword;

}
