package com.holobyn.security.controller;

import com.holobyn.security.dto.ErrorDto;
import com.holobyn.security.exception.BlockeException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(BlockeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationError(BlockeException e) {

//        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        return new ErrorDto(e.getMessage());
    }


}
