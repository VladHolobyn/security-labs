package com.holobyn.security.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.holobyn.security.dto.AuthenticationRequestDto;
import com.holobyn.security.dto.ErrorDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EntryPoint implements AuthenticationEntryPoint {


    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        ErrorDto errorDTO;

        if (authException instanceof BadCredentialsException) {
            errorDTO = new ErrorDto("Wrong credentials");
//            System.out.println(objectMapper.readValue(request, AuthenticationRequestDto.class));
        } else {
            errorDTO = new ErrorDto(authException.getMessage());
        }

        System.out.println("hello");


        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorDTO));
    }

}
