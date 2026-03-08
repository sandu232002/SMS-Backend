package com.sms.auth.repository;

import com.sms.auth.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Optional<Administrator> findByUsername(String username);
    Optional<Administrator> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
