package com.inmobix.backend.playgraundJordy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserClassRepositoryJDPY extends JpaRepository<com.inmobix.backend.playgraundJordy.model.UserClassJDPY, Long> {
    Optional<com.inmobix.backend.playgraundJordy.model.UserClassJDPY> findByEmail(String email);
}
