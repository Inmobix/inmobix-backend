package com.inmobix.backend.service;

import com.inmobix.backend.dto.UserRequest;
import com.inmobix.backend.dto.UserResponse;
import com.inmobix.backend.dto.UserUpdateRequest;
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
import jakarta.mail.MessagingException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // NUEVAS VARIABLES INYECTADAS
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
        entity.setVerificationCode(generateVerificationCode());

        User saved = repository.save(entity);
        sendVerificationEmail(saved);

        return mapToResponse(saved);
    }

    @Transactional
    public UserResponse login(String email, String rawPassword) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthenticationException("Credenciales incorrectas");
        }

        if (!user.isVerified()) {
            throw new AuthenticationException("Debes verificar tu correo antes de iniciar sesión. Revisa tu bandeja de entrada.");
        }

        return mapToResponse(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una cuenta con el email " + email));

        user.setResetToken(UUID.randomUUID().toString());
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        repository.save(user);

        sendPasswordResetEmail(user);
    }

    @Transactional
    public void verifyEmail(String code) {
        User user = repository.findByVerificationCode(code)
                .orElseThrow(() -> new BadRequestException("Código de verificación inválido o expirado"));

        user.setVerified(true);
        user.setVerificationCode(null);
        repository.save(user);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = repository.findByResetToken(token)
                .orElseThrow(() -> new BadRequestException("Token inválido o expirado"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El token ha expirado. Solicita uno nuevo.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        repository.save(user);
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
            user.setVerificationCode(generateVerificationCode());
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

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email " + email));

        if (user.isVerified()) {
            throw new BadRequestException("Este usuario ya está verificado");
        }

        user.setVerificationCode(generateVerificationCode());
        repository.save(user);
        sendVerificationEmail(user);
    }

    // EMAILS CON URLs DINÁMICAS
    private void sendVerificationEmail(User user) {
        String verifyUrl = backendUrl + "/api/user/verify?code=" + user.getVerificationCode();
        String html = """
            <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                        <h2 style="color: #2E86C1;">¡Bienvenido a Inmobix, %s!</h2>
                        <p>Gracias por registrarte. Para activar tu cuenta, verifica tu correo electrónico haciendo clic en el botón:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background:#2E86C1; color:white; padding:12px 30px; text-decoration:none; border-radius:6px; display:inline-block; font-weight: bold;">
                                Verificar mi cuenta
                            </a>
                        </div>
                        <p style="color: #666; font-size: 14px;">Si no creaste esta cuenta, ignora este correo.</p>
                        <p style="color: #666; font-size: 14px;">Código de verificación: <strong>%s</strong></p>
                    </div>
                </body>
            </html>
        """.formatted(user.getName(), verifyUrl, user.getVerificationCode());

        try {
            emailService.sendHtmlEmail(user.getEmail(), "Verifica tu cuenta de Inmobix", html);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo de verificación: " + e.getMessage());
        }
    }

    private void sendPasswordResetEmail(User user) {
        String resetUrl = frontendUrl + "/reset-password?token=" + user.getResetToken();
        String html = """
            <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                        <h2 style="color: #E74C3C;">Recuperar contraseña</h2>
                        <p>Hola %s,</p>
                        <p>Recibimos una solicitud para restablecer tu contraseña. Haz clic en el botón para crear una nueva:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background:#E74C3C; color:white; padding:12px 30px; text-decoration:none; border-radius:6px; display:inline-block; font-weight: bold;">
                                Restablecer contraseña
                            </a>
                        </div>
                        <p style="color: #666; font-size: 14px;">Este enlace expira en 30 minutos.</p>
                        <p style="color: #666; font-size: 14px;">Si no solicitaste restablecer tu contraseña, ignora este correo.</p>
                    </div>
                </body>
            </html>
        """.formatted(user.getName(), resetUrl);

        try {
            emailService.sendHtmlEmail(user.getEmail(), "Restablecer contraseña - Inmobix", html);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo de recuperación: " + e.getMessage());
        }
    }

    private void sendEditConfirmationEmail(User user) {
        String confirmUrl = frontendUrl + "/confirm-edit?token=" + user.getEditToken();
        String html = """
            <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                        <h2 style="color: #F39C12;">Confirmar edición de cuenta</h2>
                        <p>Hola %s,</p>
                        <p>Has solicitado editar tu información. Para confirmar los cambios, haz clic en el botón:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background:#F39C12; color:white; padding:12px 30px; text-decoration:none; border-radius:6px; display:inline-block; font-weight: bold;">
                                Confirmar cambios
                            </a>
                        </div>
                        <p style="color: #666; font-size: 14px;">Este enlace expira en 15 minutos.</p>
                        <p style="color: #666; font-size: 14px;">Si no solicitaste editar tu cuenta, ignora este correo.</p>
                    </div>
                </body>
            </html>
        """.formatted(user.getName(), confirmUrl);

        try {
            emailService.sendHtmlEmail(user.getEmail(), "Confirmar edición - Inmobix", html);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo de confirmación: " + e.getMessage());
        }
    }

    private void sendDeleteConfirmationEmail(User user) {
        String confirmUrl = backendUrl + "/api/user/confirm-delete?token=" + user.getDeleteToken();
        String html = """
            <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                        <h2 style="color: #C0392B;">⚠️ Confirmar eliminación de cuenta</h2>
                        <p>Hola %s,</p>
                        <p>Has solicitado eliminar tu cuenta de Inmobix. Esta acción es <strong>irreversible</strong>.</p>
                        <p>Si estás seguro, haz clic en el botón:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background:#C0392B; color:white; padding:12px 30px; text-decoration:none; border-radius:6px; display:inline-block; font-weight: bold;">
                                Eliminar mi cuenta
                            </a>
                        </div>
                        <p style="color: #666; font-size: 14px;">Este enlace expira en 15 minutos.</p>
                        <p style="color: #666; font-size: 14px;">Si no solicitaste eliminar tu cuenta, ignora este correo y cambia tu contraseña inmediatamente.</p>
                    </div>
                </body>
            </html>
        """.formatted(user.getName(), confirmUrl);

        try {
            emailService.sendHtmlEmail(user.getEmail(), "⚠️ Confirmar eliminación - Inmobix", html);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo de confirmación: " + e.getMessage());
        }
    }

    private String generateVerificationCode() {
        int code = new Random().nextInt(900000) + 100000;
        return String.valueOf(code);
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
        return response;
    }
}