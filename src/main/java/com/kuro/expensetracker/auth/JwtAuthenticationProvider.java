package com.kuro.expensetracker.auth;

import com.kuro.expensetracker.exceptions.JwtAuthenticationException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationProvider(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        UserDetails userDetails;

        if (token == null) {
            throw new JwtAuthenticationException("No token has been provided");
        }
        if (jwtService.isTokenExpired(token)) {
            throw new JwtAuthenticationException("Token has expired");
        }

        String userID = jwtService.extractUserID(token);
        userDetails = userDetailsService.loadUserByUsername(userID);
        if (userDetails == null) {
            throw new JwtAuthenticationException("No user found with the uuid found in the token #[" + userID + "]");
        }

        return new JwtAuthenticationToken(userDetails, token, List.of());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
