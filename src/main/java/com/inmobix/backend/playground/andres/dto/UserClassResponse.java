package com.inmobix.backend.playground.andres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserClassResponse {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;

}
