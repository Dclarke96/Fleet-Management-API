package com.dylanclarke.FleetManagementAPI.logging;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dylanclarke.FleetManagementAPI.security.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();

        log.info(
                "REQUEST_START method={} uri={}",
                method,
                uri
        );

        try {

            filterChain.doFilter(request, response);

        } finally {

            String userInfo = "anonymous";
            String companyInfo = "unknown";

            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null &&
                    authentication.getPrincipal() instanceof CustomUserDetails user) {

                userInfo = String.valueOf(user.getId());
                companyInfo = String.valueOf(user.getCompanyId());
            }

            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "REQUEST_COMPLETE method={} uri={} status={} durationMs={} userId={} companyId={}",
                    method,
                    uri,
                    response.getStatus(),
                    duration,
                    userInfo,
                    companyInfo
            );
        }
    }
}