package com.inmobix.backend.service;

import com.inmobix.backend.dto.UserRequest;
import com.inmobix.backend.dto.UserResponse;
import com.inmobix.backend.model.Role;
import com.inmobix.backend.model.User;
import com.inmobix.backend.repository.UserRepository;
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

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // Registrar un nuevo usuario
    public UserResponse register(UserRequest request) {
        if (request.getEmail() == null || request.getPassword() == null
                || request.getName() == null || request.getUsername() == null) {
            throw new RuntimeException("Faltan campos obligatorios");
        }

        User entity = new User();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setUsername(request.getUsername());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setPhone(request.getPhone());
        entity.setBirthDate(request.getBirthDate());
        entity.setRole(Role.USER);

        // Generar código de verificación
        entity.setVerified(false);
        entity.setVerificationCode(generateVerificationCode());

        User saved = repository.save(entity);

        // Enviar correo de verificación
        String verifyUrl = "http://localhost:8080/api/user/verify?code=" + saved.getVerificationCode();
        String html = """
            <html>
                <body>
                    <h2>¡Bienvenido a Inmobix!</h2>
                    <p>Por favor verifica tu correo haciendo clic en el enlace:</p>
                    <a href="%s" style="background:#2E86C1; color:white; padding:10px 15px; text-decoration:none; border-radius:6px;">
                        Verificar mi cuenta
                    </a>
                </body>
            </html>
        """.formatted(verifyUrl);

        try {
            emailService.sendHtmlEmail(entity.getEmail(), "Verifica tu cuenta Inmobix", html);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }

        return new UserResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getUsername(),
                saved.getPhone(),
                saved.getBirthDate(),
                saved.getRole().name()
        );
    }

    public String login(String email, String rawPassword) {
        Optional<User> userOpt = repository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con el email: " + email);
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta para el usuario: " + email);
        }

        if (!user.isVerified()) {
            throw new RuntimeException("Debes verificar tu correo antes de iniciar sesión");
        }

        return "Login exitoso para el usuario " + user.getUsername();
    }

    public String forgotPassword(String email) {
        Optional<User> userOpt = repository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con el email: " + email);
        }

        // 🔹 Nueva lógica: generar token temporal
        User user = userOpt.get();
        user.setResetToken(UUID.randomUUID().toString());
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        repository.save(user);

        String resetUrl = "http://localhost:4200/reset-password?token=" + user.getResetToken();
        String html = """
            <html>
                <body>
                    <h2>Recuperar contraseña</h2>
                    <p>Haz clic en el siguiente enlace para restablecer tu contraseña (válido 30 minutos):</p>
                    <a href="%s" style="background:#E74C3C; color:white; padding:10px 15px; text-decoration:none; border-radius:6px;">
                        Restablecer contraseña
                    </a>
                </body>
            </html>
        """.formatted(resetUrl);

        try {
            emailService.sendHtmlEmail(email, "Recuperar contraseña", html);
        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando correo: " + e.getMessage());
        }

        return "Se ha enviado un enlace de recuperación al correo " + email;
    }

    // Buscar usuario por id
    public UserResponse getById(UUID id) {
        User entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + id));

        return new UserResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getUsername(),
                entity.getPhone(),
                entity.getBirthDate(),
                entity.getRole().name()
        );
    }

    // Listar todos los usuarios
    public List<UserResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getUsername(),
                        user.getPhone(),
                        user.getBirthDate(),
                        user.getRole().name()
                ))
                .collect(Collectors.toList());
    }

    // Actualizar usuario
    @Transactional
    public UserResponse update(UUID id, UserRequest request) {
        User entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + id));

        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setUsername(request.getUsername());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        entity.setPhone(request.getPhone());
        entity.setBirthDate(request.getBirthDate());

        User updated = repository.save(entity);

        return new UserResponse(
                updated.getId(),
                updated.getName(),
                updated.getEmail(),
                updated.getUsername(),
                updated.getPhone(),
                updated.getBirthDate(),
                updated.getRole().name()
        );
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public String verifyEmail(String code) {
        User user = repository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Código de verificación inválido"));

        user.setVerified(true);
        user.setVerificationCode(null);
        repository.save(user);
        return "Correo verificado exitosamente";
    }

    @Transactional
    public String resetPassword(String token, String newPassword) {
        User user = repository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        repository.save(user);

        return "Contraseña restablecida correctamente";
    }

    private String generateVerificationCode() {
        int code = new Random().nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
