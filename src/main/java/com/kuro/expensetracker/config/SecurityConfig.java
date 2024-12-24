package com.kuro.expensetracker.config;

import com.kuro.expensetracker.auth.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AuthenticationManager authenticationManager;
    @Value("/${api.prefix}")
    private String apiPrefix;

    public SecurityConfig(HandlerExceptionResolver handlerExceptionResolver,
                          AuthenticationManager authenticationManager) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.authenticationManager = authenticationManager;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize -> {
                            authorize.requestMatchers(apiPrefix + "/users/login").permitAll();
                            authorize.requestMatchers(apiPrefix + "/users/register").permitAll();
                            authorize.requestMatchers(apiPrefix + "/users/confirm-email").permitAll();
                            authorize.anyRequest().authenticated();
                        })
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter(), AuthorizationFilter.class)
                .build();
    }


    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authenticationManager, handlerExceptionResolver);
    }


}
