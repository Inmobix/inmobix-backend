package com.inmobix.backend.controller;

import com.inmobix.backend.dto.UserRequest;
import com.inmobix.backend.dto.UserResponse;
import com.inmobix.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest request) {
        String result = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(result);
    }

    // POST /forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody UserRequest request) {
        String result = userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(result);
    }

    // GET /user/{id}
    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        UserResponse response = userService.getById(id);
        return ResponseEntity.ok(response);
    }

    // GET /users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

}