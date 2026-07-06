package com.dylanclarke.FleetManagementAPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dylanclarke.FleetManagementAPI.logging.RequestLoggingFilter;
import com.dylanclarke.FleetManagementAPI.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // =========================
    // AUTH MANAGER
    // =========================
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    // =========================
    // USER DETAILS SERVICE
    // (Prevents Spring Boot from creating a default user)
    // =========================
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UnsupportedOperationException(
                    "JWT authentication only. No in-memory login.");
        };
    }

    // =========================
    // PRODUCTION SECURITY
    // =========================
    @Bean
    @Profile("!test")
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            RequestLoggingFilter requestLoggingFilter) throws Exception {

        configureSecurity(http, jwtAuthFilter, requestLoggingFilter);

        return http.build();
    }

    // =========================
    // TEST SECURITY
    // =========================
    @Bean
    @Profile("test")
    public SecurityFilterChain testSecurityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            RequestLoggingFilter requestLoggingFilter) throws Exception {

        configureSecurity(http, jwtAuthFilter, requestLoggingFilter);

        return http.build();
    }

    // =========================
    // SHARED SECURITY CONFIGURATION
    // =========================
    private void configureSecurity(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            RequestLoggingFilter requestLoggingFilter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())

                .formLogin(form -> form.disable())

                .httpBasic(httpBasic -> httpBasic.disable())

                // Log every request before authentication
                .addFilterBefore(
                        requestLoggingFilter,
                        JwtAuthFilter.class
                )

                // Authenticate JWT before Spring Security username/password auth
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
    }

    // =========================
    // PASSWORD ENCODER
    // =========================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}