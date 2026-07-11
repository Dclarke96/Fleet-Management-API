package com.dylanclarke.FleetManagementAPI.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dylanclarke.FleetManagementAPI.model.User;
import com.dylanclarke.FleetManagementAPI.repository.UserRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthFilter(
            JwtService jwtService,
            UserRepository userRepository,
            AuthenticationEntryPoint authenticationEntryPoint) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No Authorization header or not Bearer token
        if (!StringUtils.hasText(authHeader)
                || !authHeader.startsWith("Bearer ")) {

            log.debug(
                    "No JWT provided for request: {} {}",
                    request.getMethod(),
                    request.getRequestURI()
            );

            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {

            final String username =
                    jwtService.extractUsername(token);

            if (username != null
                    && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                User user = userRepository
                        .findByUsername(username)
                        .orElse(null);

                if (user == null) {

                    log.warn(
                            "JWT authentication failed: user not found username={}",
                            username
                    );

                    authenticationEntryPoint.commence(
                            request,
                            response,
                            new BadCredentialsException(
                                    "User not found"
                            )
                    );

                    return;
                }

                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        )
                );

                CustomUserDetails principal =
                        new CustomUserDetails(
                                user.getId(),
                                user.getCompany().getId(),
                                user.getEmail(),
                                user.getPassword(),
                                authorities
                        );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                authorities
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                log.debug(
                        "JWT authentication successful: userId={}, companyId={}, uri={}",
                        user.getId(),
                        user.getCompany().getId(),
                        request.getRequestURI()
                );
            }

        } catch (JwtException | IllegalArgumentException ex) {

            log.warn(
                    "JWT authentication failed: invalid token for request {} {}",
                    request.getMethod(),
                    request.getRequestURI()
            );

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException(
                            "Invalid JWT token",
                            ex
                    )
            );

            return;
        }

        filterChain.doFilter(request, response);
    }
}