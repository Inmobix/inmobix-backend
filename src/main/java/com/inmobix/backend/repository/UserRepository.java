package com.inmobix.backend.repository;

import com.inmobix.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByDocumento(String documento);

    // NUEVO - Búsqueda por tokens únicos
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByResetPasswordToken(String resetPasswordToken);

    // Mantener para compatibilidad
    Optional<User> findByVerificationCode(String code);
    Optional<User> findByResetToken(String token);
    Optional<User> findByEditToken(String token);
    Optional<User> findByDeleteToken(String token);
}