package com.kuro.expensetracker.config;

import com.kuro.expensetracker.auth.JwtAuthenticationProvider;
import com.kuro.expensetracker.auth.JwtService;
import com.kuro.expensetracker.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ApplicationConfig(UserRepository userRepository,
                             JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return uuid -> userRepository.findByUuid(uuid)
                .orElseThrow(() -> new BadCredentialsException(String.format("User with uuid [%s] not found", uuid)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(email ->
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new BadCredentialsException(String.format("User with email [%s] not found", email))));
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager() {
        return new ProviderManager(new JwtAuthenticationProvider(jwtService, userDetailsService()),
                daoAuthenticationProvider());
    }
}
