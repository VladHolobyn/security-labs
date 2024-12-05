package com.holobyn.security.controller;

import com.holobyn.security.dto.ErrorDto;
import com.holobyn.security.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationError(ApiException e) {
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationError(AuthenticationException e) {
        System.out.println("hello 2");
        return new ErrorDto(e.getMessage());
    }

}
