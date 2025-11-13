package com.inmobix.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String username;
    private String documento;
    private String phone;
    private LocalDate birthDate;
    private String role;

    // NUEVO - Token para verificación (solo se devuelve en registro)
    private String verificationToken;

    // NUEVO - Token para reset password (solo se devuelve en forgot-password)
    private String resetPasswordToken;
}