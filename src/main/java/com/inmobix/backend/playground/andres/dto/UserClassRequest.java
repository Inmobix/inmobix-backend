package com.inmobix.backend.playground.andres.dto;

import lombok.Data;

@Data
public class UserClassRequest {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
}
