package com.inmobix.backend.service;

import com.inmobix.backend.dto.*;
import com.inmobix.backend.exception.AuthenticationException;
import com.inmobix.backend.exception.BadRequestException;
import com.inmobix.backend.exception.DuplicateResourceException;
import com.inmobix.backend.exception.ResourceNotFoundException;
import com.inmobix.backend.model.Role;
import com.inmobix.backend.model.User;
import com.inmobix.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// iText para PDF
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

// Apache POI para Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.url.backend}")
    private String backendUrl;

    @Value("${app.url.frontend}")
    private String frontendUrl;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public UserResponse register(UserRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("El email " + request.getEmail() + " ya está registrado");
        }

        if (repository.findByDocumento(request.getDocumento()).isPresent()) {
            throw new DuplicateResourceException("El documento " + request.getDocumento() + " ya está registrado");
        }

        User entity = new User();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setUsername(request.getUsername());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setDocumento(request.getDocumento());
        entity.setPhone(request.getPhone());
        entity.setBirthDate(request.getBirthDate());
        entity.setRole(Role.USER);
        entity.setVerified(false);

        // Generar código de 6 dígitos y token único
        entity.setVerificationCode(generateSixDigitCode());
        entity.setVerificationToken(generateUniqueToken());
        entity.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));

        User saved = repository.save(entity);
        sendVerificationEmail(saved);

        return mapToResponseWithToken(saved);
    }

    @Transactional
    public UserResponse login(String email, String rawPassword) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthenticationException("Credenciales incorrectas");
        }

        if (!user.isVerified()) {
            throw new AuthenticationException(
                    "Debes verificar tu correo antes de iniciar sesión. Revisa tu bandeja de entrada.");
        }

        return mapToResponse(user);
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una cuenta con el email " + email));

        // Rate limiting - verificar si hay código activo
        if (user.getResetTokenExpiry() != null &&
                user.getResetTokenExpiry().isAfter(LocalDateTime.now())) {

            long secondsRemaining = java.time.Duration.between(
                    LocalDateTime.now(),
                    user.getResetTokenExpiry()).getSeconds();

            long minutes = secondsRemaining / 60;
            long seconds = secondsRemaining % 60;

            throw new BadRequestException(
                    String.format("Ya hay un código activo. Podrás solicitar uno nuevo en %d:%02d", minutes, seconds));
        }

        // Generar código de 6 dígitos y token único
        user.setResetToken(generateSixDigitCode());
        user.setResetPasswordToken(generateUniqueToken());
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(5));
        repository.save(user);

        sendPasswordResetEmail(user);

        return new ForgotPasswordResponse(
                user.getResetPasswordToken(),
                "Se ha enviado un código de recuperación a tu correo. Válido por 5 minutos.");
    }

    @Transactional
    public void verifyEmail(String verificationToken, String code) {
        User user = repository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new BadRequestException("Token de verificación inválido"));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            throw new BadRequestException("Código de verificación inválido");
        }

        if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El código ha expirado. Solicita uno nuevo.");
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationToken(null);
        user.setVerificationCodeExpiry(null);
        User saved = repository.save(user);

        sendVerificationSuccessEmail(saved);
    }

    @Transactional
    public void resetPassword(String resetPasswordToken, String code, String newPassword) {
        User user = repository.findByResetPasswordToken(resetPasswordToken)
                .orElseThrow(() -> new BadRequestException("Token de recuperación inválido"));

        if (user.getResetToken() == null || !user.getResetToken().equals(code)) {
            throw new BadRequestException("Código inválido");
        }

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El código ha expirado. Solicita uno nuevo.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetPasswordToken(null);
        user.setResetTokenExpiry(null);
        User saved = repository.save(user);

        sendPasswordResetSuccessEmail(saved);
    }

    @Transactional
    public UserResponse resendVerificationEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email " + email));

        if (user.isVerified()) {
            throw new BadRequestException("Este usuario ya está verificado");
        }

        if (user.getVerificationCodeExpiry() != null &&
                user.getVerificationCodeExpiry().isAfter(LocalDateTime.now())) {

            long secondsRemaining = java.time.Duration.between(
                    LocalDateTime.now(),
                    user.getVerificationCodeExpiry()).getSeconds();

            long minutes = secondsRemaining / 60;
            long seconds = secondsRemaining % 60;

            throw new BadRequestException(
                    String.format("Ya hay un código activo. Podrás solicitar uno nuevo en %d:%02d", minutes, seconds));
        }

        user.setVerificationCode(generateSixDigitCode());
        user.setVerificationToken(generateUniqueToken());
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));
        User saved = repository.save(user);

        sendResendVerificationEmail(saved);

        return mapToResponseWithToken(saved);
    }

    public UserResponse getByDocumento(String documento, UUID requesterId, Role requesterRole) {
        User user = repository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con documento " + documento));

        if (requesterRole != Role.ADMIN && !user.getId().equals(requesterId)) {
            throw new AuthenticationException("No tienes permisos para ver este usuario");
        }

        return mapToResponse(user);
    }

    public List<UserResponse> getAll(Role requesterRole) {
        if (requesterRole != Role.ADMIN) {
            throw new AuthenticationException("Solo administradores pueden listar todos los usuarios");
        }

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void requestEditToken(UUID userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + userId));

        user.setEditToken(UUID.randomUUID().toString());
        user.setEditTokenExpiry(LocalDateTime.now().plusMinutes(15));
        repository.save(user);

        sendEditConfirmationEmail(user);
    }

    @Transactional
    public UserResponse confirmUpdate(String token, UserUpdateRequest request) {
        User user = repository.findByEditToken(token)
                .orElseThrow(() -> new BadRequestException("Token inválido o expirado"));

        if (user.getEditTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El token ha expirado. Solicita uno nuevo.");
        }

        if (!user.getEmail().equals(request.getEmail())) {
            if (repository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicateResourceException("El email " + request.getEmail() + " ya está en uso");
            }
            user.setEmail(request.getEmail());
            user.setVerified(false);
            user.setVerificationCode(generateSixDigitCode());
            user.setVerificationToken(generateUniqueToken());
            user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));
            sendVerificationEmail(user);
        }

        if (request.getDocumento() != null && !request.getDocumento().equals(user.getDocumento())) {
            if (repository.findByDocumento(request.getDocumento()).isPresent()) {
                throw new DuplicateResourceException("El documento " + request.getDocumento() + " ya está en uso");
            }
            user.setDocumento(request.getDocumento());
        }

        user.setName(request.getName());
        user.setUsername(request.getUsername());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setPhone(request.getPhone());
        user.setBirthDate(request.getBirthDate());

        user.setEditToken(null);
        user.setEditTokenExpiry(null);

        User updated = repository.save(user);
        return mapToResponse(updated);
    }

    @Transactional
    public void requestDeleteToken(UUID userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + userId));

        user.setDeleteToken(UUID.randomUUID().toString());
        user.setDeleteTokenExpiry(LocalDateTime.now().plusMinutes(15));
        repository.save(user);

        sendDeleteConfirmationEmail(user);
    }

    @Transactional
    public void confirmDelete(String token) {
        User user = repository.findByDeleteToken(token)
                .orElseThrow(() -> new BadRequestException("Token inválido o expirado"));

        if (user.getDeleteTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El token ha expirado. Solicita uno nuevo.");
        }

        repository.deleteById(user.getId());
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private String generateSixDigitCode() {
        int code = new Random().nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private String generateUniqueToken() {
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
    }

    private void sendVerificationEmail(User user) {
        String html = String
                .format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        </head>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                                <h2 style="color: #2E86C1;">¡Bienvenido a Inmobix, %s!</h2>
                                <p>Gracias por registrarte. Para activar tu cuenta, utiliza el siguiente código de verificación:</p>
                                <div style="text-align: center; margin: 30px 0;">
                                    <div style="background:#f0f0f0; padding:20px; border-radius:8px; display:inline-block;">
                                        <h1 style="margin:0; color:#2E86C1; font-size:48px; letter-spacing:8px;">%s</h1>
                                    </div>
                                </div>
                                <p style="color: #666; font-size: 14px; text-align: center;">Este código expira en <strong>5 minutos</strong></p>
                                <p style="color: #666; font-size: 14px;">Si no creaste esta cuenta, ignora este correo.</p>
                            </div>
                        </body>
                        </html>
                        """,
                        user.getName(), user.getVerificationCode())
                .stripIndent().trim();

        emailService.sendHtmlEmail(user.getEmail(), "Verifica tu cuenta de Inmobix", html);
    }

    private void sendResendVerificationEmail(User user) {
        String html = String
                .format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        </head>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                                <h2 style="color: #F39C12;">Verifica tu cuenta</h2>
                                <p>Hola %s,</p>
                                <p>Has solicitado un nuevo código de verificación. Utiliza el siguiente código para activar tu cuenta:</p>
                                <div style="text-align: center; margin: 30px 0;">
                                    <div style="background:#f0f0f0; padding:20px; border-radius:8px; display:inline-block;">
                                        <h1 style="margin:0; color:#F39C12; font-size:48px; letter-spacing:8px;">%s</h1>
                                    </div>
                                </div>
                                <p style="color: #666; font-size: 14px; text-align: center;">Este código expira en <strong>5 minutos</strong></p>
                                <p style="color: #666; font-size: 14px;">Si no solicitaste este código, ignora este correo.</p>
                            </div>
                        </body>
                        </html>
                        """,
                        user.getName(), user.getVerificationCode())
                .stripIndent().trim();

        emailService.sendHtmlEmail(user.getEmail(), "Verifica tu cuenta de Inmobix", html);
    }

    private void sendVerificationSuccessEmail(User user) {
        String html = String
                .format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        </head>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                                <h2 style="color: #27AE60; text-align: center;">¡Verificación Exitosa!</h2>
                                <p>Hola %s,</p>
                                <p>¡Excelentes noticias! Tu cuenta ha sido verificada exitosamente.</p>
                                <div style="background:#f0f0f0; padding:20px; border-radius:8px; margin:20px 0;">
                                    <p style="margin:0; color:#555;"><strong>✅ Tu email está confirmado</strong></p>
                                    <p style="margin:5px 0 0 0; color:#555;"><strong>✅ Ya puedes iniciar sesión</strong></p>
                                    <p style="margin:5px 0 0 0; color:#555;"><strong>✅ Tu cuenta está activa</strong></p>
                                </div>
                                <p>Ahora puedes acceder a todas las funcionalidades de Inmobix:</p>
                                <ul style="color:#555;">
                                    <li>Publicar propiedades</li>
                                    <li>Buscar inmuebles</li>
                                    <li>Contactar vendedores</li>
                                    <li>Gestionar tu perfil</li>
                                </ul>
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="%s" style="background:#2E86C1; color:white; padding:12px 30px; text-decoration:none; border-radius:6px; display:inline-block; font-weight: bold;">
                                        Ir a Inmobix
                                    </a>
                                </div>
                                <p style="color: #666; font-size: 14px; text-align: center;">¡Bienvenido a la comunidad Inmobix!</p>
                            </div>
                        </body>
                        </html>
                        """,
                        user.getName(), frontendUrl)
                .stripIndent().trim();

        emailService.sendHtmlEmail(user.getEmail(), "✅ Cuenta verificada - Inmobix", html);
    }

    private void sendPasswordResetEmail(User user) {
        String html = String
                .format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        </head>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                                <h2 style="color: #E74C3C;">Recuperar contraseña</h2>
                                <p>Hola %s,</p>
                                <p>Recibimos una solicitud para restablecer tu contraseña. Utiliza el siguiente código:</p>
                                <div style="text-align: center; margin: 30px 0;">
                                    <div style="background:#f0f0f0; padding:20px; border-radius:8px; display:inline-block;">
                                        <h1 style="margin:0; color:#E74C3C; font-size:48px; letter-spacing:8px;">%s</h1>
                                    </div>
                                </div>
                                <p style="color: #666; font-size: 14px; text-align: center;">Este código expira en <strong>5 minutos</strong></p>
                                <p style="color: #666; font-size: 14px;">Si no solicitaste restablecer tu contraseña, ignora este correo.</p>
                            </div>
                        </body>
                        </html>
                        """,
                        user.getName(), user.getResetToken())
                .stripIndent().trim();

        emailService.sendHtmlEmail(user.getEmail(), "Restablecer contraseña - Inmobix", html);
    }

    private void sendPasswordResetSuccessEmail(User user) {
        String dateTime = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String html = String
                .format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        </head>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;">
                            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                                <h2 style="color: #27AE60; text-align: center;">Contraseña Actualizada</h2>
                                <p>Hola %s,</p>
                                <p>Tu contraseña ha sido restablecida exitosamente.</p>
                                <div style="background:#f0f0f0; padding:20px; border-radius:8px; margin:20px 0;">
                                    <p style="margin:0; color:#555;"><strong>✅ Contraseña actualizada</strong></p>
                                    <p style="margin:5px 0 0 0; color:#555;"><strong>✅ Tu cuenta está segura</strong></p>
                                    <p style="margin:5px 0 0 0; color:#555;"><strong>🕐 Fecha: %s</strong></p>
                                </div>
                                <p style="color:#666;">Ya puedes iniciar sesión con tu nueva contraseña.</p>
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="%s" style="background:#2E86C1; color:white; padding:12px 30px; text-decoration:none; border-radius:6px; display:inline-block; font-weight: bold;">
                                        Iniciar Sesión
                                    </a>
                                </div>
                                <div style="background:#fff3cd; border-left:4px solid #ffc107; padding:15px; margin:20px 0;">
                                    <p style="margin:0; color:#856404;"><strong>⚠️ Aviso de Seguridad</strong></p>
                                    <p style="margin:5px 0 0 0; color:#856404;">Si no solicitaste este cambio, tu cuenta podría estar comprometida. Por favor, contacta a soporte inmediatamente.</p>
                                </div>
                                <p style="color: #666; font-size: 14px; text-align: center;">Equipo de Inmobix</p>
                            </div>
                        </body>
                        </html>
                        """,
                        user.getName(), dateTime, frontendUrl)
                .stripIndent().trim();

        emailService.sendHtmlEmail(user.getEmail(), "✅ Contraseña actualizada - Inmobix", html);
    }

    void sendEditConfirmationEmail(User user) {
        String TOKEN = user.getEditToken();

        String html = String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;">
            <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                <h2 style="color: #F39C12;">Confirmar edición de cuenta</h2>
                <p>Hola %s,</p>
                <p>Has solicitado editar tu información. Copia y pega este token:</p>
                <div style="text-align: center; margin: 30px 0;">
                    <div style="background:#f5f5f5; border:2px dashed #F39C12; padding:15px; border-radius:6px; display:inline-block;">
                        <code style="font-size:20px; font-weight:bold; color:#F39C12; letter-spacing:2px; user-select:all;">%s</code>
                    </div>
                </div>
                <p style="color: #666; font-size: 14px;">Este token expira en 15 minutos.</p>
                <p style="color: #666; font-size: 14px;">Si no solicitaste editar tu cuenta, ignora este correo.</p>
            </div>
        </body>
        </html>
        """, user.getName(), TOKEN).stripIndent().trim();

        emailService.sendHtmlEmail(user.getEmail(), "Confirmar edición - Inmobix", html);
    }

    private void sendDeleteConfirmationEmail(User user) {
        String TOKEN = user.getDeleteToken();

        String html = String
                .format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    </head>
                    <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;">
                        <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                            <h2 style="color: #C0392B;">⚠️ Confirmar eliminación de cuenta</h2>
                            <p>Hola %s,</p>
                            <p>Has solicitado eliminar tu cuenta de Inmobix. Esta acción es <strong>irreversible</strong>.</p>
                            <p>Si estás seguro, copia y pega este token:</p>
                            <div style="text-align: center; margin: 30px 0;">
                                <div style="background:#f5f5f5; border:2px dashed #C0392B; padding:15px; border-radius:6px; display:inline-block;">
                                    <code style="font-size:20px; font-weight:bold; color:#C0392B; letter-spacing:2px; user-select:all;">%s</code>
                                </div>
                            </div>
                            <p style="color: #666; font-size: 14px;">Este token expira en 15 minutos.</p>
                            <p style="color: #666; font-size: 14px;">Si no solicitaste eliminar tu cuenta, ignora este correo y cambia tu contraseña inmediatamente.</p>
                        </div>
                    </body>
                    </html>
                    """,
                        user.getName(), TOKEN)
                .stripIndent().trim();

        emailService.sendHtmlEmail(user.getEmail(), "⚠️ Confirmar eliminación - Inmobix", html);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setDocumento(user.getDocumento());
        response.setPhone(user.getPhone());
        response.setBirthDate(user.getBirthDate());
        response.setRole(user.getRole().name());
        response.setVerificationToken(null);
        response.setResetPasswordToken(null);
        return response;
    }

    private UserResponse mapToResponseWithToken(User user) {
        UserResponse response = mapToResponse(user);
        response.setVerificationToken(user.getVerificationToken());
        return response;
    }

    @Transactional(readOnly = true)
    public byte[] generatePdfReport() {
        List<User> users = repository.findAll();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph title = new Paragraph("Reporte de Usuarios - Inmobix")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(new DeviceRgb(46, 134, 193))
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Fecha
            String dateStr = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            Paragraph date = new Paragraph("Generado el: " + dateStr)
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(date);

            // Resumen
            Paragraph summary = new Paragraph("Total de usuarios registrados: " + users.size())
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(15);
            document.add(summary);

            // Tabla
            float[] columnWidths = {3, 4, 2.5f, 2.5f, 1.5f};
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(
                    UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            // Encabezados
            String[] headers = {"Nombre", "Email", "Documento", "Teléfono", "Rol"};
            for (String header : headers) {
                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(header).setBold())
                        .setBackgroundColor(new DeviceRgb(46, 134, 193))
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }

            // Datos
            for (User user : users) {
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(user.getName())));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(user.getEmail())));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(user.getDocumento() != null ? user.getDocumento() : "N/A")));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(user.getPhone() != null ? user.getPhone() : "N/A")));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(user.getRole().name()))
                        .setTextAlignment(TextAlignment.CENTER));
            }

            document.add(table);

            // Footer
            Paragraph footer = new Paragraph("Inmobix - Sistema de Gestión Inmobiliaria")
                    .setFontSize(8)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar reporte PDF: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateExcelReport() {
        List<User> users = repository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Usuarios");

            // Estilos
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle alternateStyle = workbook.createCellStyle();
            alternateStyle.cloneStyleFrom(dataStyle);
            alternateStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            alternateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Título
            Row titleRow = sheet.createRow(0);
            titleRow.setHeight((short) 600);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Reporte de Usuarios - Inmobix");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            // Fecha
            Row dateRow = sheet.createRow(1);
            org.apache.poi.ss.usermodel.Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generado el: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));

            // Resumen
            Row summaryRow = sheet.createRow(2);
            summaryRow.setHeight((short) 400);
            org.apache.poi.ss.usermodel.Cell summaryCell = summaryRow.createCell(0);
            summaryCell.setCellValue("Total de usuarios registrados: " + users.size());
            CellStyle summaryStyle = workbook.createCellStyle();
            Font summaryFont = workbook.createFont();
            summaryFont.setBold(true);
            summaryStyle.setFont(summaryFont);
            summaryCell.setCellStyle(summaryStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));

            // Encabezados
            Row headerRow = sheet.createRow(4);
            String[] headers = {"Nombre", "Email", "Username", "Documento", "Teléfono", "Rol"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowNum = 5;
            for (User user : users) {
                Row row = sheet.createRow(rowNum);
                CellStyle style = (rowNum % 2 == 0) ? dataStyle : alternateStyle;

                org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0);
                cell0.setCellValue(user.getName());
                cell0.setCellStyle(style);

                org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1);
                cell1.setCellValue(user.getEmail());
                cell1.setCellStyle(style);

                org.apache.poi.ss.usermodel.Cell cell2 = row.createCell(2);
                cell2.setCellValue(user.getUsername());
                cell2.setCellStyle(style);

                org.apache.poi.ss.usermodel.Cell cell3 = row.createCell(3);
                cell3.setCellValue(user.getDocumento() != null ? user.getDocumento() : "N/A");
                cell3.setCellStyle(style);

                org.apache.poi.ss.usermodel.Cell cell4 = row.createCell(4);
                cell4.setCellValue(user.getPhone() != null ? user.getPhone() : "N/A");
                cell4.setCellStyle(style);

                org.apache.poi.ss.usermodel.Cell cell5 = row.createCell(5);
                cell5.setCellValue(user.getRole().name());
                cell5.setCellStyle(style);

                rowNum++;
            }

            // Ajustar anchos
            sheet.setColumnWidth(0, 6000);  // Nombre
            sheet.setColumnWidth(1, 8000);  // Email
            sheet.setColumnWidth(2, 5000);  // Username
            sheet.setColumnWidth(3, 4500);  // Documento
            sheet.setColumnWidth(4, 4500);  // Teléfono
            sheet.setColumnWidth(5, 3000);  // Rol

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar reporte Excel: " + e.getMessage(), e);
        }
    }

    public byte[] generateUserPdfReport(UUID userId) {
        return null;
    }

    public byte[] generateUserExcelReport(UUID userId) {
        return null;
    }
}