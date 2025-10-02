package com.inmobix.backend.playgraundJordy.service;

import com.inmobix.backend.playgraundJordy.dto.UserClassRequest;
import com.inmobix.backend.playgraundJordy.dto.UserClassResponse;
import com.inmobix.backend.playgraundJordy.model.UserClass;
import com.inmobix.backend.playgraundJordy.repository.UserClassRepository;
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
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());

        UserClass saved = repository.save(entity);
        return new UserClassResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getPhone());
    }
    //Listar todos los usuarios
    public List<UserClassResponse> listar() {
        return repository.findAll().stream()
                .map(u-> new UserClassResponse(u.getId(),u.getName(),u.getEmail(),u.getPhone()))
                        .collect(Collectors.toList());
    }
    //Listar por Id
    public UserClassResponse buscarPorId(Long id) {
        UserClass entity = repository.findById(id).orElseThrow(() -> new RuntimeException(("Usuario no encontrado con id" + id)));
        return new UserClassResponse(entity.getId(), entity.getName(), entity.getEmail(), entity.getPhone());
    }

    //Actualizar usuario por Id
    public UserClassResponse actualizar(Long id, UserClassRequest request) {
        UserClass entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id" + id));
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        UserClass actualizate = repository.save(entity);

        return new UserClassResponse(actualizate.getId(), actualizate.getName(), actualizate.getEmail(), actualizate.getPhone());
    }

    //Eliminar usuario

    public void eliminar(Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException(("Usuario no encontrado con id" + id));
        }
        repository.deleteById(id);
    }

}
