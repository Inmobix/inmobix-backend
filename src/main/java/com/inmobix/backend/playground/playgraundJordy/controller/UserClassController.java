package com.inmobix.backend.playgraundJordy.controller;

import org.springframework.web.bind.annotation.*;
import com.inmobix.backend.playgraundJordy.dto.UserClassRequest;
import com.inmobix.backend.playgraundJordy.dto.UserClassResponse;
import com.inmobix.backend.playgraundJordy.service.UserClassService;

import java.util.List;

@RestController
@RequestMapping("/userclass")
public class UserClassController {
    private final UserClassService service;

    public UserClassController(UserClassService service){
        this.service = service;
    }

    @PostMapping
    public UserClassResponse crear(@RequestBody UserClassRequest request){
        return service.crear(request);
    }

    // Http://localhost:8080/userclass
    @GetMapping
    public List<UserClassResponse> listar(){
        return service.listar();
    }

    // Http://localhost:8080/userclass/id
    @GetMapping("/{id}")
    public UserClassResponse buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    // Http://localhost:8080/userclass
    @PutMapping("/{id}")
    public UserClassResponse actualizar(@PathVariable Long id, @RequestBody UserClassRequest request){
        return service.actualizar(id,request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id){
        service.eliminar(id);
    }

}
