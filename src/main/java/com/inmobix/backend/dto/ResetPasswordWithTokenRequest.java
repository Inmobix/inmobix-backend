package com.inmobix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordWithTokenRequest {

    @NotBlank(message = "El token de recuperación es obligatorio")
    private String resetPasswordToken;

    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "^[0-9]{6}$", message = "El código debe tener 6 dígitos")
    private String code;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String newPassword;
}