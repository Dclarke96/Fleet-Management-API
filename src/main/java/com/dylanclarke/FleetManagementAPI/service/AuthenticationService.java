package com.dylanclarke.FleetManagementAPI.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dylanclarke.FleetManagementAPI.api.ApiResponse;
import com.dylanclarke.FleetManagementAPI.dto.AuthRequest;
import com.dylanclarke.FleetManagementAPI.dto.RegisterRequest;
import com.dylanclarke.FleetManagementAPI.exception.AuthenticationException;
import com.dylanclarke.FleetManagementAPI.model.Company;
import com.dylanclarke.FleetManagementAPI.model.Role;
import com.dylanclarke.FleetManagementAPI.model.User;
import com.dylanclarke.FleetManagementAPI.repository.CompanyRepository;
import com.dylanclarke.FleetManagementAPI.repository.UserRepository;
import com.dylanclarke.FleetManagementAPI.security.JwtService;

import jakarta.transaction.Transactional;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, CompanyRepository companyRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {

    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
}

    @Transactional
    public ApiResponse<String> register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new ApiResponse<>(
                    false,
                    null,
                    "Username already exists"
            );
        }

        Company company = new Company();
        company.setName(request.getCompanyName());
        companyRepository.save(company);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user.setCompany(company);

        userRepository.save(user);

        log.info(
                "User registered: userId={}, companyId={}, role={}",
                user.getId(),
                company.getId(),
                user.getRole()
        );

        return new ApiResponse<>(
                true,
                "User registered successfully",
                null
        );
    }

    public ApiResponse<String> login(AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn(
                            "Login failed: unknown username={}",
                            request.getUsername()
                    );
                    return new AuthenticationException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            log.warn(
                    "Login failed: invalid password userId={}",
                    user.getId()
            );

            throw new AuthenticationException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        log.info(
                "Login successful: userId={}, companyId={}",
                user.getId(),
                user.getCompany().getId()
        );

        return new ApiResponse<>(
                true,
                token,
                "Login successful"
        );
    }
}