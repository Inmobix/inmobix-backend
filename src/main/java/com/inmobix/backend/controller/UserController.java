package com.inmobix.backend.controller;

import com.inmobix.backend.dto.*;
import com.inmobix.backend.model.Role;
import com.inmobix.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario registrado exitosamente. Verifica tu correo para activar tu cuenta.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request) {
        UserResponse response = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity
                .ok(ApiResponse.success("Login exitoso", response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity
                .ok(ApiResponse.success("Se ha enviado un enlace de recuperación a tu correo", null));
    }

    @GetMapping("/user/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String code) {
        userService.verifyEmail(code);
        return ResponseEntity
                .ok(ApiResponse.success("¡Correo verificado exitosamente! Ya puedes iniciar sesión.", null));
    }

    @PostMapping("/user/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity
                .ok(ApiResponse.success("Contraseña restablecida correctamente", null));
    }

    @PostMapping("/user/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.resendVerificationEmail(request.getEmail());
        return ResponseEntity
                .ok(ApiResponse.success("Correo de verificación reenviado", null));
    }

    // Buscar por documento (requiere autenticación)
    @GetMapping("/user/documento/{documento}")
    public ResponseEntity<ApiResponse<UserResponse>> getByDocumento(
            @PathVariable String documento,
            @RequestHeader("X-User-Id") UUID requesterId,
            @RequestHeader("X-User-Role") Role requesterRole) {
        UserResponse response = userService.getByDocumento(documento, requesterId, requesterRole);
        return ResponseEntity
                .ok(ApiResponse.success("Usuario encontrado", response));
    }

    // MODIFICADO - Solo ADMIN
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestHeader("X-User-Role") Role requesterRole) {
        List<UserResponse> users = userService.getAll(requesterRole);
        return ResponseEntity
                .ok(ApiResponse.success("Usuarios obtenidos exitosamente", users));
    }

    // Solicitar token para editar
    @PostMapping("/user/request-edit/{id}")
    public ResponseEntity<ApiResponse<Void>> requestEditToken(@PathVariable UUID id) {
        userService.requestEditToken(id);
        return ResponseEntity
                .ok(ApiResponse.success("Se ha enviado un correo de confirmación para editar tu cuenta", null));
    }

    // Confirmar edición con token
    @PutMapping("/user/confirm-edit")
    public ResponseEntity<ApiResponse<UserResponse>> confirmUpdate(
            @RequestParam String token,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.confirmUpdate(token, request);
        return ResponseEntity
                .ok(ApiResponse.success("Usuario actualizado exitosamente", response));
    }

    // Solicitar token para eliminar
    @PostMapping("/user/request-delete/{id}")
    public ResponseEntity<ApiResponse<Void>> requestDeleteToken(@PathVariable UUID id) {
        userService.requestDeleteToken(id);
        return ResponseEntity
                .ok(ApiResponse.success("Se ha enviado un correo de confirmación para eliminar tu cuenta", null));
    }

    // Confirmar eliminación con token
    @DeleteMapping("/user/confirm-delete")
    public ResponseEntity<ApiResponse<Void>> confirmDelete(@RequestParam String token) {
        userService.confirmDelete(token);
        return ResponseEntity
                .ok(ApiResponse.success("Usuario eliminado exitosamente", null));
    }
}