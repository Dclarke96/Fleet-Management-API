package com.dylanclarke.FleetManagementAPI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dylanclarke.FleetManagementAPI.dto.RegisterRequest;
import com.dylanclarke.FleetManagementAPI.model.Company;
import com.dylanclarke.FleetManagementAPI.model.Role;
import com.dylanclarke.FleetManagementAPI.model.User;
import com.dylanclarke.FleetManagementAPI.repository.CompanyRepository;
import com.dylanclarke.FleetManagementAPI.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          CompanyRepository companyRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        // Create company
        Company company = new Company();
        company.setName(request.getCompanyName());
        companyRepository.save(company);

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user.setCompany(company);

        userRepository.save(user);

        return ResponseEntity.ok("User and company registered");
    }
}
