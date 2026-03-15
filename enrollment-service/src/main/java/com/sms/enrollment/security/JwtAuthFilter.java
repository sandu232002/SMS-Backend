package com.sms.enrollment.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // Using raw bytes to match the plain-text secret in YAML
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));

                Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();

                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                
                Object rawAdminId = claims.get("adminId");
                Long adminId = rawAdminId instanceof Number ? ((Number) rawAdminId).longValue() : null;

                request.setAttribute("adminId", adminId);
                request.setAttribute("username", username);

                // Ensure role has ROLE_ prefix for Spring Security
                String roleWithPrefix = (role != null && role.startsWith("ROLE_")) ? role : "ROLE_" + role;

                var auth = new UsernamePasswordAuthenticationToken(
                    username, token, List.of(new SimpleGrantedAuthority(roleWithPrefix))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException e) {
                log.error("JWT validation failed: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
