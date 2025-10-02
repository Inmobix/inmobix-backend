package com.inmobix.backend.playgraundJordy.repository;

import com.inmobix.backend.playgraundJordy.model.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserClassRepository extends JpaRepository<UserClass, Long> {
    Optional<UserClass> findByEmail(String email);
}
