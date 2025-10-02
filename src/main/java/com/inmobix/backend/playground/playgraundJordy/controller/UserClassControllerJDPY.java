package com.inmobix.backend.playground.playgraundJordy.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jordy/userclass")
public class UserClassControllerJDPY {
    private final com.inmobix.backend.playgraundJordy.service.UserClassServiceJDPY service;

    public UserClassControllerJDPY(com.inmobix.backend.playgraundJordy.service.UserClassServiceJDPY service){
        this.service = service;
    }

    @PostMapping
    public com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY crear(@RequestBody com.inmobix.backend.playgraundJordy.dto.UserClassRequestJDPY request){
        return service.crear(request);
    }

    // Http://localhost:8080/userclass
    @GetMapping
    public List<com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY> listar(){
        return service.listar();
    }

    // Http://localhost:8080/userclass/id
    @GetMapping("/{id}")
    public com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    // Http://localhost:8080/userclass
    @PutMapping("/{id}")
    public com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY actualizar(@PathVariable Long id, @RequestBody com.inmobix.backend.playgraundJordy.dto.UserClassRequestJDPY request){
        return service.actualizar(id,request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id){
        service.eliminar(id);
    }

}
