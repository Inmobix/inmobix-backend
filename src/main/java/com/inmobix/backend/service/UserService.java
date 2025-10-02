package com.inmobix.backend.service;

import com.inmobix.backend.dto.UserRequest;
import com.inmobix.backend.dto.UserResponse;
import com.inmobix.backend.model.User;
import com.inmobix.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar un nuevo usuario
    public UserResponse register(UserRequest request) {
        if (request.getEmail() == null || request.getPassword() == null || request.getName() == null || request.getUsername() == null) {
            throw new RuntimeException("Faltan campos obligatorios");
        }

        User entity = new User();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setUsername(request.getUsername());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setPhone(request.getPhone());
        entity.setBirthDate(request.getBirthDate());

        User saved = repository.save(entity);
        return new UserResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getUsername(),
                saved.getPhone(),
                saved.getBirthDate()
        );
    }

    // Login
    public String login(String email, String rawPassword) {
        Optional<User> userOpt = repository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con el email: " + email);
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta para el usuario: " + email);
        }

        return "Login exitoso para el usuario " + user.getUsername();
    }

    // Recuperar contraseña
    public String forgotPassword(String email) {
        Optional<User> userOpt = repository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con el email: " + email);
        }
        return "Se ha enviado un enlace de recuperación al correo " + email;
    }

    // Buscar usuario por id
    public UserResponse getById(Long id) {
        User entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + id));
        return new UserResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getUsername(),
                entity.getPhone(),
                entity.getBirthDate()
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
                        user.getBirthDate()
                ))
                .collect(Collectors.toList());
    }
}