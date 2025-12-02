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

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = userService.forgotPassword(request.getEmail());
        return ResponseEntity
                .ok(ApiResponse.success(response.getMessage(), response));
    }

    // NUEVO - Verificación con token único
    @PostMapping("/user/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody VerifyWithTokenRequest request) {
        userService.verifyEmail(request.getVerificationToken(), request.getCode());
        return ResponseEntity
                .ok(ApiResponse.success("¡Correo verificado exitosamente! Ya puedes iniciar sesión.", null));
    }

    // ACTUALIZADO - Reset con token único
    @PostMapping("/user/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordWithTokenRequest request) {
        userService.resetPassword(request.getResetPasswordToken(), request.getCode(), request.getNewPassword());
        return ResponseEntity
                .ok(ApiResponse.success("Contraseña restablecida correctamente", null));
    }

    @PostMapping("/user/resend-verification")
    public ResponseEntity<ApiResponse<UserResponse>> resendVerificationEmail(@Valid @RequestBody ForgotPasswordRequest request) {
        UserResponse response = userService.resendVerificationEmail(request.getEmail());
        return ResponseEntity
                .ok(ApiResponse.success("Código de verificación reenviado. Válido por 5 minutos.", response));
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

    // Solo ADMIN
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

    // Generar reporte PDF de usuarios (Solo ADMIN)
    @GetMapping("/users/report/pdf")
    public ResponseEntity<byte[]> generateUsersPdfReport(@RequestHeader("X-User-Role") Role requesterRole) {
        if (requesterRole != Role.ADMIN) {
            throw new com.inmobix.backend.exception.AuthenticationException(
                    "Solo administradores pueden generar reportes");
        }

        byte[] pdfBytes = userService.generatePdfReport();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "reporte_usuarios_" + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // Generar reporte Excel de usuarios (Solo ADMIN)
    @GetMapping("/users/report/excel")
    public ResponseEntity<byte[]> generateUsersExcelReport(@RequestHeader("X-User-Role") Role requesterRole) {
        if (requesterRole != Role.ADMIN) {
            throw new com.inmobix.backend.exception.AuthenticationException(
                    "Solo administradores pueden generar reportes");
        }

        byte[] excelBytes = userService.generateExcelReport();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment",
                "reporte_usuarios_" + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    // Generar reporte PDF de un usuario específico con sus propiedades
    @GetMapping("/user/{userId}/report/pdf")
    public ResponseEntity<byte[]> generateUserPdfReport(
            @PathVariable UUID userId,
            @RequestHeader("X-User-Id") UUID requesterId,
            @RequestHeader("X-User-Role") Role requesterRole) {

        // Validar permisos: solo el mismo usuario o un admin
        if (requesterRole != Role.ADMIN && !requesterId.equals(userId)) {
            throw new com.inmobix.backend.exception.AuthenticationException(
                    "No tienes permisos para ver el reporte de este usuario");
        }

        byte[] pdfBytes = userService.generateUserPdfReport(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "reporte_usuario_" + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // Generar reporte Excel de un usuario específico con sus propiedades
    @GetMapping("/user/{userId}/report/excel")
    public ResponseEntity<byte[]> generateUserExcelReport(
            @PathVariable UUID userId,
            @RequestHeader("X-User-Id") UUID requesterId,
            @RequestHeader("X-User-Role") Role requesterRole) {

        // Validar permisos: solo el mismo usuario o un admin
        if (requesterRole != Role.ADMIN && !requesterId.equals(userId)) {
            throw new com.inmobix.backend.exception.AuthenticationException(
                    "No tienes permisos para ver el reporte de este usuario");
        }

        byte[] excelBytes = userService.generateUserExcelReport(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment",
                "reporte_usuario_" + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}