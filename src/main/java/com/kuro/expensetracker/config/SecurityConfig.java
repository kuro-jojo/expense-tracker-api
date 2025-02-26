package com.kuro.expensetracker.config;

import com.kuro.expensetracker.auth.JwtAuthenticationFilter;
import com.kuro.expensetracker.filters.RequestLoggingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AuthenticationManager authenticationManager;
    @Value("/${api.prefix}")
    private String apiPrefix;
    @Value("${cors.allowedOrigins}")
    private List<String> allowedOrigins;

    public SecurityConfig(HandlerExceptionResolver handlerExceptionResolver,
                          AuthenticationManager authenticationManager) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.authenticationManager = authenticationManager;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(
                        authorize -> {
                            authorize.requestMatchers(apiPrefix + "/auth/login").permitAll();
                            authorize.requestMatchers(apiPrefix + "/auth/register").permitAll();
                            authorize.requestMatchers(apiPrefix + "/auth/confirm-email").permitAll();
                            authorize.requestMatchers(apiPrefix + "/auth/verify-otp").permitAll();
                            authorize.requestMatchers(apiPrefix + "/auth/resend-confirmation-email").permitAll();
                            authorize.anyRequest().authenticated();
                        })
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter(), AuthorizationFilter.class)
                .addFilterBefore(new RequestLoggingFilter(), AuthorizationFilter.class)
                .build();
    }


    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authenticationManager, handlerExceptionResolver);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
