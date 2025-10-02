package com.inmobix.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyResponse {

    private Long id;
    private String title;
    private String description;
    private String address;
    private String city;
    private String state;
    private BigDecimal price;
    private BigDecimal area;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer garages;
    private String propertyType;
    private String transactionType;
    private Boolean available;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Información básica del usuario propietario
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
}