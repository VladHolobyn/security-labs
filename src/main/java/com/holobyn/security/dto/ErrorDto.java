package com.holobyn.security.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorDto {
    private List<String> errors;

    public ErrorDto(List<String> errors) {
        this.errors = errors;
    }

    public ErrorDto(String error) {
        this.errors = List.of(error);
    }
}
