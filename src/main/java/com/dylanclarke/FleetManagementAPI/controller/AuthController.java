package com.dylanclarke.FleetManagementAPI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.dylanclarke.FleetManagementAPI.dto.RegisterRequest;
import com.dylanclarke.FleetManagementAPI.dto.AuthRequest;
import com.dylanclarke.FleetManagementAPI.model.Company;
import com.dylanclarke.FleetManagementAPI.model.Role;
import com.dylanclarke.FleetManagementAPI.model.User;
import com.dylanclarke.FleetManagementAPI.repository.CompanyRepository;
import com.dylanclarke.FleetManagementAPI.repository.UserRepository;
import com.dylanclarke.FleetManagementAPI.util.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          CompanyRepository companyRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ---------------- REGISTER ----------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        Company company = new Company();
        company.setName(request.getCompanyName());
        companyRepository.save(company);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user.setCompany(company);

        userRepository.save(user);

        return ResponseEntity.ok("User and company registered");
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        User user = userRepository.findByUsername(request.username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(token);
    }
}