package com.inmobix.backend.playground.andres.controller;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/andresgomez/tareas")
public class AndresGomezController {

    // Lista en memoria con tareas iniciales
    private List<String> tareas = new ArrayList<>(Arrays.asList("Estudiar", "Practicar Spring"));

    // GET -> listar todas las tareas
    // Endpoint: GET /andresgomez/tareas
    @GetMapping
    public List<String> getTareas() {
        return tareas;
    }

    // POST -> agregar nueva tarea
    // Endpoint: POST /andresgomez/tareas?nombre=AprenderGit
    @PostMapping
    public String addTarea(@RequestParam String nombre) {
        tareas.add(nombre);
        return "Tarea agregada: " + nombre;
    }

    // PUT -> actualizar una tarea existente
    // Endpoint: PUT /andresgomez/tareas/0?nombre=NuevaTarea
    @PutMapping("/{index}")
    public String updateTarea(@PathVariable int index, @RequestParam String nombre) {
        tareas.set(index, nombre);
        return "Tarea actualizada en " + index + ": " + nombre;
    }

    // DELETE -> eliminar una tarea
    // Endpoint: DELETE /andresgomez/tareas/0
    @DeleteMapping("/{index}")
    public String deleteTarea(@PathVariable int index) {
        String eliminada = tareas.remove(index);
        return "Tarea eliminada: " + eliminada;
    }
}
