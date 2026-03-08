package com.sms.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDto {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String tokenType = "Bearer";
        private Long adminId;
        private String username;
        private String email;
        private String role;
        private long expiresIn;

        public LoginResponse(String token, Long adminId, String username, String email, String role, long expiresIn) {
            this.token = token;
            this.adminId = adminId;
            this.username = username;
            this.email = email;
            this.role = role;
            this.expiresIn = expiresIn;
        }
    }

    @Data
    public static class TokenValidationRequest {
        @NotBlank
        private String token;
    }

    @Data
    public static class TokenValidationResponse {
        private boolean valid;
        private String username;
        private String role;
        private Long adminId;

        public TokenValidationResponse(boolean valid, String username, String role, Long adminId) {
            this.valid = valid;
            this.username = username;
            this.role = role;
            this.adminId = adminId;
        }
    }

    @Data
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }
    }
}
