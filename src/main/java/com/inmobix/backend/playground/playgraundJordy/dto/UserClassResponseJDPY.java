package com.inmobix.backend.playgraundJordy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserClassResponseJDPY {
    private Long id;
    private String name;
    private String email;
    private String phone;
}
