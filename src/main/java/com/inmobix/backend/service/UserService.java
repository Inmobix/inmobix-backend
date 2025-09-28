package com.inmobix.backend.service;

import com.inmobix.backend.dto.UserRequest;
import com.inmobix.backend.dto.UserResponse;
import com.inmobix.backend.model.User;
import com.inmobix.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
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
        entity.setPassword(request.getPassword());
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
    public UserResponse login(String email, String password) {
        Optional<User> userOpt = repository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            throw new RuntimeException("Credenciales inválidas");
        }
        User user = userOpt.get();
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUsername(),
                user.getPhone(),
                user.getBirthDate()
        );
    }

    // Recuperar contraseña
    public void forgotPassword(String email) {
        Optional<User> userOpt = repository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con el email: " + email);
        }
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
}