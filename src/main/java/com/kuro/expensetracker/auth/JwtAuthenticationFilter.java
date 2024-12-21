package com.kuro.expensetracker.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;
    private final HandlerExceptionResolver handlerExceptionResolver;
    @Value("/${api.prefix}")
    private String apiPrefix;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   HandlerExceptionResolver handlerExceptionResolver) {
        this.authenticationManager = authenticationManager;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = extractJwtFromHeader(request);
            JwtAuthenticationToken authRequest = new JwtAuthenticationToken(jwt);
            logger.debug("Attempting to authenticate JWT token");

            Authentication authResult = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.equals(apiPrefix + "/users/login") || requestUri.equals(apiPrefix + "/users/register");
    }

    private String extractJwtFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BadCredentialsException("A valid JWT token is required");
        }
        return token.substring(7);
    }
}
