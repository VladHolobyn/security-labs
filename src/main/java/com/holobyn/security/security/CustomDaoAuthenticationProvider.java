package com.holobyn.security.security;

import com.holobyn.security.domain.User;
import com.holobyn.security.exception.ApiException;
import com.holobyn.security.service.FailedLoginAttemptService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private final FailedLoginAttemptService failedLoginAttemptService;

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
            Authentication auth = super.authenticate(authentication);
            User user = (User) auth.getPrincipal();

            if (failedLoginAttemptService.isBlocked(user.getEmail())) {
                throw new ApiException("Your account is blocked wait 5 minutes to try again");
            }
            return auth;

        } catch (BadCredentialsException e) {
            failedLoginAttemptService.failedLoginAttempt(authentication);
            throw e;
        }
    }

}
