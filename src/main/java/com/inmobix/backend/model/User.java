package com.inmobix.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "El username es obligatorio")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String documento;

    private String phone;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    // Sistema de verificación de email
    private boolean verified = false;
    private String verificationCode;
    private String verificationToken;
    private LocalDateTime verificationCodeExpiry;

    // Sistema de recuperación de contraseña
    private String resetToken;
    private String resetPasswordToken;
    private LocalDateTime resetTokenExpiry;

    // Tokens para editar y eliminar cuenta
    private String editToken;
    private LocalDateTime editTokenExpiry;
    private String deleteToken;
    private LocalDateTime deleteTokenExpiry;
}