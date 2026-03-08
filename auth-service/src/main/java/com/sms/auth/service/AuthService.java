package com.sms.auth.service;

import com.sms.auth.dto.AuthDto;
import com.sms.auth.model.Administrator;
import com.sms.auth.repository.AdministratorRepository;
import com.sms.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdministratorRepository adminRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        log.debug("Login attempt for username: {}", request.getUsername());

        Administrator admin = adminRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> {
                log.error("User not found: {}", request.getUsername());
                return new UsernameNotFoundException("Invalid username or password");
            });

        log.debug("Found admin: {}", admin.getUsername());
        log.debug("Password hash from DB: {}", admin.getPasswordHash());
        log.debug("Password hash is null: {}", admin.getPasswordHash() == null);

        boolean passwordMatches = passwordEncoder.matches(
            request.getPassword(), 
            admin.getPasswordHash()
        );
        log.debug("Password matches: {}", passwordMatches);

        if (!passwordMatches) {
            log.error("Password mismatch for user: {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(
            admin.getUsername(), 
            admin.getRole(), 
            admin.getAdminId()
        );
        log.info("Admin logged in successfully: {}", admin.getUsername());

        return new AuthDto.LoginResponse(
            token,
            admin.getAdminId(),
            admin.getUsername(),
            admin.getEmail(),
            admin.getRole(),
            jwtUtil.getExpiration()
        );
    }

    public AuthDto.TokenValidationResponse validateToken(String token) {
        if (!jwtUtil.isTokenValid(token)) {
            return new AuthDto.TokenValidationResponse(false, null, null, null);
        }
        return new AuthDto.TokenValidationResponse(
            true,
            jwtUtil.extractUsername(token),
            jwtUtil.extractRole(token),
            jwtUtil.extractAdminId(token)
        );
    }
}