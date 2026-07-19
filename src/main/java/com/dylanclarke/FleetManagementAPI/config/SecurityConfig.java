package com.dylanclarke.FleetManagementAPI.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.dylanclarke.FleetManagementAPI.logging.RequestLoggingFilter;
import com.dylanclarke.FleetManagementAPI.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String AUTH_ENDPOINT = "/api/auth/**";

    private static final String[] SWAGGER_ENDPOINTS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    private static final String[] ACTUATOR_ENDPOINTS = {
        "/actuator/health",
        "/actuator/health/**"
    };

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
    // SECURITY FILTER CHAIN
    // =========================
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            RequestLoggingFilter requestLoggingFilter,
            AuthenticationEntryPoint authenticationEntryPoint) throws Exception {

        http

                // =========================
                // CORS
                // =========================
                .cors(Customizer.withDefaults())

                // =========================
                // CSRF
                // =========================
                .csrf(csrf -> csrf.disable())

                // =========================
                // SESSION MANAGEMENT
                // =========================
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // =========================
                // REST API ERROR HANDLING
                // =========================
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )

                // =========================
                // AUTHORIZATION
                // =========================
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                AUTH_ENDPOINT
                        ).permitAll()

                        .requestMatchers(
                                SWAGGER_ENDPOINTS
                        ).permitAll()

                        .requestMatchers(
                                ACTUATOR_ENDPOINTS
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                // =========================
                // DISABLE DEFAULT LOGIN
                // =========================
                .formLogin(form -> form.disable())

                .httpBasic(httpBasic -> httpBasic.disable())

                // =========================
                // SECURITY HEADERS
                // =========================
                .headers(headers -> headers
                        // Allows embedded H2 console during local development.
                        // Safe for production because the H2 console is not enabled.
                        .frameOptions(frame -> frame.sameOrigin())
                )

                // =========================
                // REQUEST LOGGING
                // =========================
                .addFilterBefore(
                        requestLoggingFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // =========================
                // JWT AUTHENTICATION
                // =========================
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // =========================
    // CORS CONFIGURATION
    // =========================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setExposedHeaders(List.of(
                "Authorization"
        ));

        configuration.setAllowCredentials(true);

        // Cache preflight requests for one hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // =========================
    // PASSWORD ENCODER
    // =========================
    @Bean
    public PasswordEncoder passwordEncoder() {

        // BCrypt strength of 12 provides strong password hashing
        // while remaining performant for authentication workloads.
        return new BCryptPasswordEncoder(12);
    }
}