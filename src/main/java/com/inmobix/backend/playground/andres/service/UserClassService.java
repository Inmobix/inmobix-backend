package com.inmobix.backend.playground.andres.service;

import com.inmobix.backend.playground.andres.dto.UserClassRequest;
import com.inmobix.backend.playground.andres.dto.UserClassResponse;
import com.inmobix.backend.playground.andres.model.UserClass;
import com.inmobix.backend.playground.andres.repository.UserClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserClassService {

    private final UserClassRepository repository;
    public UserClassService(UserClassRepository repository) {
        this.repository = repository;
    }

    //crear un nuevo usuario
    public UserClassResponse crear(UserClassRequest request) {
        UserClass entity = new UserClass();
        entity.setEmail(request.getEmail());
        entity.setNombre(request.getNombre());
        entity.setTelefono(request.getTelefono());

        UserClass guardado = repository.save(entity);
        return new UserClassResponse(guardado.getId(), guardado.getNombre(), guardado.getEmail(), guardado.getTelefono());
    }
    //Listar todos los usuarios
    public List<UserClassResponse> listar() {
        return repository.findAll()
                .stream()
                .map(u-> new UserClassResponse(u.getId(),u.getNombre(), u.getEmail(), u.getTelefono()))
                .collect(Collectors.toList());
    }
    //Listar por id
    public UserClassResponse buscarPorId(Long id) {
        UserClass entity = repository.findById(id).
                orElseThrow(()-> new RuntimeException("Usuario no encontrado con id "+id));
        return new UserClassResponse(entity.getId(),entity.getNombre(),entity.getEmail(),entity.getTelefono());
    }
    //Actualizar
    public UserClassResponse actualizar(Long id, UserClassRequest request) {
        UserClass entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id "+id));

        entity.setNombre(request.getNombre());
        entity.setEmail(request.getEmail());
        entity.setTelefono(request.getTelefono());

        UserClass actualizado = repository.save(entity);
        return new UserClassResponse(actualizado.getId(), actualizado.getNombre(), actualizado.getEmail(), actualizado.getTelefono());
    }
    //eliminar
    public void eliminar(Long id) {
        if(!repository.existsById(id)){
            throw new RuntimeException("Usuario no encontrado con id "+id);
        }
        repository.deleteById(id);
    }
}
