package com.sms.auth.controller;

import com.sms.auth.dto.AuthDto;
import com.sms.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDto.ApiResponse<AuthDto.LoginResponse>> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(AuthDto.ApiResponse.success("Login successful", response));
    }

    @PostMapping("/validate")
    public ResponseEntity<AuthDto.TokenValidationResponse> validateToken(
            @RequestBody AuthDto.TokenValidationRequest request) {
        return ResponseEntity.ok(authService.validateToken(request.getToken()));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
}
