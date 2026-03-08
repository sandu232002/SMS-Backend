package com.sms.auth.config;

import com.sms.auth.model.Administrator;
import com.sms.auth.repository.AdministratorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_EMAIL = "admin@sms.com";
    private static final String DEFAULT_ROLE = "ADMIN";
    private static final String DEFAULT_PASSWORD = "Admin@1234";

    // Legacy hash currently inserted by init-db.sql (hash for "password").
    private static final String LEGACY_PASSWORD_HASH =
        "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        administratorRepository.findByUsername(DEFAULT_USERNAME)
            .ifPresentOrElse(this::repairLegacySeedPassword, this::createDefaultAdmin);
    }

    private void repairLegacySeedPassword(Administrator admin) {
        if (LEGACY_PASSWORD_HASH.equals(admin.getPasswordHash())) {
            admin.setPasswordHash(passwordEncoder.encode(DEFAULT_PASSWORD));
            administratorRepository.save(admin);
            log.warn("Updated legacy admin seed password hash for user '{}'", DEFAULT_USERNAME);
        }
    }

    private void createDefaultAdmin() {
        Administrator admin = Administrator.builder()
            .username(DEFAULT_USERNAME)
            .email(DEFAULT_EMAIL)
            .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
            .role(DEFAULT_ROLE)
            .build();

        administratorRepository.save(admin);
        log.info("Created default admin user '{}'", DEFAULT_USERNAME);
    }
}
