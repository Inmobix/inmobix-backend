package com.inmobix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyRequest {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "El departamento es obligatorio")
    private String state;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal price;

    @Positive(message = "El área debe ser positiva")
    private BigDecimal area;

    private Integer bedrooms = 0;

    private Integer bathrooms = 0;

    private Integer garages = 0;

    @NotBlank(message = "El tipo de propiedad es obligatorio")
    private String propertyType; // casa, apartamento, local, lote, finca

    @NotBlank(message = "El tipo de transacción es obligatorio")
    private String transactionType; // venta, arriendo

    private Boolean available = true;

    private String imageUrl;

    private Long userId; // ID del usuario propietario/agente
}