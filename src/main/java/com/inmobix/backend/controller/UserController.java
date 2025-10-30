package com.inmobix.backend.controller;

import com.inmobix.backend.dto.UserRequest;
import com.inmobix.backend.dto.UserResponse;
import com.inmobix.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
