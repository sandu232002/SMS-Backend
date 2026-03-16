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
import java.nio.charset.StandardCharsets;
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
            log.debug("Found Bearer token, validating...");
            try {
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();

                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                Object rawAdminId = claims.get("adminId");
                Long adminId = rawAdminId instanceof Number ? ((Number) rawAdminId).longValue() : null;

                log.debug("JWT validated. User: {}, Role: {}, AdminId: {}", username, role, adminId);

                request.setAttribute("adminId", adminId);
                request.setAttribute("username", username);

                String roleWithPrefix = (role != null && role.startsWith("ROLE_")) ? role : "ROLE_" + role;

                var auth = new UsernamePasswordAuthenticationToken(
                    username, token, List.of(new SimpleGrantedAuthority(roleWithPrefix))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Security context set for user: {}", username);

            } catch (JwtException e) {
                log.error("JWT validation failed for token: {}. Error: {}", token.substring(0, Math.min(token.length(), 10)) + "...", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                log.error("Unexpected error during JWT validation: {}", e.getMessage(), e);
                SecurityContextHolder.clearContext();
            }
        } else {
            log.warn("No Bearer token found in Authorization header");
        }

        filterChain.doFilter(request, response);
    }
}
