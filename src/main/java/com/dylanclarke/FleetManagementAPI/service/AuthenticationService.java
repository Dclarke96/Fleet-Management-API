package com.dylanclarke.FleetManagementAPI.service;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dylanclarke.FleetManagementAPI.api.ApiResponse;
import com.dylanclarke.FleetManagementAPI.dto.AuthRequest;
import com.dylanclarke.FleetManagementAPI.dto.RegisterRequest;
import com.dylanclarke.FleetManagementAPI.model.Company;
import com.dylanclarke.FleetManagementAPI.model.Role;
import com.dylanclarke.FleetManagementAPI.model.User;
import com.dylanclarke.FleetManagementAPI.repository.CompanyRepository;
import com.dylanclarke.FleetManagementAPI.repository.UserRepository;
import com.dylanclarke.FleetManagementAPI.security.JwtService;

@Service
public class AuthenticationService {

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
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user.setCompany(company);

        userRepository.save(user);

        return new ApiResponse<>(
                true,
                "Registration successful",
                "User registered successfully"
        );
    }

    public ApiResponse<String> login(AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new ApiResponse<>(
                    false,
                    null,
                    "Invalid credentials"
            );
        }

        String token = jwtService.generateToken(user);

        return new ApiResponse<>(
                true,
                token,
                "Login successful"
        );
    }
}