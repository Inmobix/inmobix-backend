package com.inmobix.backend.playground.andres.controller;

import com.inmobix.backend.playground.andres.dto.UserClassRequest;
import com.inmobix.backend.playground.andres.dto.UserClassResponse;
import com.inmobix.backend.playground.andres.service.UserClassService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userclass")
public class UserClassController {

    private final UserClassService service;

    public UserClassController(UserClassService service) {
        this.service = service;
    }

    @PostMapping
    public UserClassResponse crear(@RequestBody UserClassRequest request) {
        return service.crear(request);
    }

    @GetMapping
    public List<UserClassResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public UserClassResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public UserClassResponse actualizar(@PathVariable Long id, @RequestBody UserClassRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
