package com.holobyn.security.security;

import com.holobyn.security.service.AuthService;
import com.holobyn.security.service.FailedLoginAttemptSercvice;
import com.holobyn.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("authenticationProvider")
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private FailedLoginAttemptSercvice failedLoginAttemptService;

    @Autowired
    @Qualifier("userService")
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Autowired
    @Qualifier("passwordEncoder")
    @Override
    public void setPasswordEncoder(PasswordEncoder encoder) {
        super.setPasswordEncoder(encoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        try {
            return super.authenticate(authentication);
        } catch (BadCredentialsException e) {
            System.out.println("Failed attempt " + authentication.getPrincipal());
            failedLoginAttemptService.failedLoginAttempt(authentication);
            throw e;
        }
    }

}
