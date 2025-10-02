package com.inmobix.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "properties")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "El departamento es obligatorio")
    @Column(nullable = false)
    private String state;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Positive(message = "El área debe ser positiva")
    @Column(precision = 10, scale = 2)
    private BigDecimal area;

    @Column(nullable = false)
    private Integer bedrooms = 0;

    @Column(nullable = false)
    private Integer bathrooms = 0;

    @Column(nullable = false)
    private Integer garages = 0;

    @NotBlank(message = "El tipo de propiedad es obligatorio")
    @Column(nullable = false)
    private String propertyType; // casa, apartamento, local, lote, finca

    @NotBlank(message = "El tipo de transacción es obligatorio")
    @Column(nullable = false)
    private String transactionType; // venta, arriendo

    @Column(nullable = false)
    private Boolean available = true;

    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación con User (propietario o agente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}