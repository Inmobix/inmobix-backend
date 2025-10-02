package com.inmobix.backend.playground.jordy.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("ejemplo/lenguajes")
public class LenguajesController {

    private List<String> lenguajesProgramacion = new ArrayList<>(List.of("Java","Python","JavaScript","TypeScript"));

    //Get- Sirve para listar todos los lenguajes
    //Endpoint: get/ejemplo/lenguajes
    @GetMapping
    public List<String> getLenguajesProgramacion(){
        System.out.println("Jordy");
        return lenguajesProgramacion;
    }

    //Post-Agregar nuevo lenguaje
    //Endpoint: Post /ejemplo/lenguajes?lenguaje=Go
    @PostMapping
    public String addLenguajeProgramacion(@RequestParam String lenguaje){
        lenguajesProgramacion.add(lenguaje);
        System.out.println("Lenguaje de programación agregado");
        return "Lenguaje agregado: " + lenguaje;
    }

    //PUT-Actualizar un lenguaje existente
    //Endpoint: Put /ejemplo/lenguajes/3?nuevo=C++
    @PutMapping("/{index}")
    public String updateLenguajeProgramacion(@PathVariable int index, @RequestParam String nuevo){
        lenguajesProgramacion.set(index, nuevo);
        return "Lenguaje actualizado en posición " + index + ": " + nuevo;
    }

    //DELETE - Eliminar lenguaje
    //Endpoint: DELETE /ejemplo/lenguajes/1
    @DeleteMapping("/{index}")
    public String deleteLenguajeProgramacion(@PathVariable int index){
        String eliminado = lenguajesProgramacion.remove(index);
        return "Lenguaje eliminado en posición " + index + ": " + eliminado;
    }

    // GET - Obtener un lenguaje por su posición en la lista
    // Endpoint: GET /ejemplo/lenguajes/2
    @GetMapping("/{index}")
    public String getLenguajeProgramacionPorPosicion(@PathVariable int index) {
        if (index >= 0 && index < lenguajesProgramacion.size()) {
            return "El lenguaje en la posición " + index + " es: " + lenguajesProgramacion.get(index);
        } else {
            return "Índice fuera de rango. La lista solo tiene " + lenguajesProgramacion.size() + " elementos.";
        }
    }
}
