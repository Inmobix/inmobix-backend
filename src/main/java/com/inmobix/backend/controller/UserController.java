package com.inmobix.backend.controller;

import com.inmobix.backend.dto.UserRequest;
import com.inmobix.backend.dto.UserResponse;
import com.inmobix.backend.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inmobix.backend.service.EmailService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    // POST /register
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email y contraseña son obligatorios");
        }
        String result = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(result);
    }

    // POST /forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody UserRequest request) {
        if (request.getEmail() == null) {
            return ResponseEntity.badRequest().body("El email es obligatorio");
        }
        String result = userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(result);
    }

    // GET /user/{id}
    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        UserResponse response = userService.getById(id);
        return ResponseEntity.ok(response);
    }

    // GET /users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    // PUT /user/{id} - Actualizar usuario
    @PutMapping("/user/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE /user/{id} - Eliminar usuario
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send-test-html")
    public ResponseEntity<String> sendTestHtmlEmail(@RequestParam String email) {
        String htmlContent = """
        <html>
            <body>
                <h1 style="color: #2E86C1;">¡Hola desde Inmobix!</h1>
                <p>Este es un correo de prueba con <b>HTML</b> y estilo.</p>
                <a href="https://inmobix.com">Visita nuestro sitio</a>
            </body>
        </html>
    """;

        try {
            emailService.sendHtmlEmail(email, "Correo de prueba Inmobix (HTML)", htmlContent);
            return ResponseEntity.ok("Correo HTML enviado a " + email);
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error enviando correo: " + e.getMessage());
        }
    }


}
