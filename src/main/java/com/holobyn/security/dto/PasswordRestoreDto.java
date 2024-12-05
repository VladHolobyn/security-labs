package com.holobyn.security.dto;

import lombok.Data;

@Data
public class PasswordRestoreDto {

    private String restoreToken;

    private String newPassword;

}
