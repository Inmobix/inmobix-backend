package com.inmobix.backend.playgraundJordy.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class UserClassServiceJDPY {
    private final com.inmobix.backend.playgraundJordy.repository.UserClassRepositoryJDPY repository;
    public UserClassServiceJDPY(com.inmobix.backend.playgraundJordy.repository.UserClassRepositoryJDPY repository) {
        this.repository = repository;
    }

    //crear un nuevo usuario
    public com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY crear(com.inmobix.backend.playgraundJordy.dto.UserClassRequestJDPY request) {
        com.inmobix.backend.playgraundJordy.model.UserClassJDPY entity = new com.inmobix.backend.playgraundJordy.model.UserClassJDPY();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());

        com.inmobix.backend.playgraundJordy.model.UserClassJDPY saved = repository.save(entity);
        return new com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY(saved.getId(), saved.getName(), saved.getEmail(), saved.getPhone());
    }
    //Listar todos los usuarios
    public List<com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY> listar() {
        return repository.findAll().stream()
                .map(u-> new com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY(u.getId(),u.getName(),u.getEmail(),u.getPhone()))
                        .collect(Collectors.toList());
    }
    //Listar por Id
    public com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY buscarPorId(Long id) {
        com.inmobix.backend.playgraundJordy.model.UserClassJDPY entity = repository.findById(id).orElseThrow(() -> new RuntimeException(("Usuario no encontrado con id" + id)));
        return new com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY(entity.getId(), entity.getName(), entity.getEmail(), entity.getPhone());
    }

    //Actualizar usuario por Id
    public com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY actualizar(Long id, com.inmobix.backend.playgraundJordy.dto.UserClassRequestJDPY request) {
        com.inmobix.backend.playgraundJordy.model.UserClassJDPY entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id" + id));
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        com.inmobix.backend.playgraundJordy.model.UserClassJDPY actualizate = repository.save(entity);

        return new com.inmobix.backend.playgraundJordy.dto.UserClassResponseJDPY(actualizate.getId(), actualizate.getName(), actualizate.getEmail(), actualizate.getPhone());
    }

    //Eliminar usuario

    public void eliminar(Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException(("Usuario no encontrado con id" + id));
        }
        repository.deleteById(id);
    }

}
