package com.inmobix.backend.controller;

import com.inmobix.backend.dto.PropertyRequest;
import com.inmobix.backend.dto.PropertyResponse;
import com.inmobix.backend.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // POST /api/properties - Crear nueva propiedad
    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(
            @Valid @RequestBody PropertyRequest request,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        if (userId != null) {
            request.setUserId(userId);
        }
        PropertyResponse response = propertyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/properties - Listar todas las propiedades
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAll());
    }

    // GET /api/properties/{id} - Obtener propiedad por ID
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable UUID id) {
        PropertyResponse response = propertyService.getById(id);
        return ResponseEntity.ok(response);
    }

    // PUT /api/properties/{id} - Actualizar propiedad
    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable UUID id,
            @Valid @RequestBody PropertyRequest request) {
        PropertyResponse response = propertyService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/properties/{id} - Eliminar propiedad
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/properties/available - Listar propiedades disponibles
    @GetMapping("/available")
    public ResponseEntity<List<PropertyResponse>> getAvailableProperties() {
        return ResponseEntity.ok(propertyService.getAvailableProperties());
    }

    // GET /api/properties/city/{city} - Buscar por ciudad
    @GetMapping("/city/{city}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByCity(@PathVariable String city) {
        return ResponseEntity.ok(propertyService.getByCity(city));
    }

    // GET /api/properties/type/{propertyType} - Buscar por tipo de propiedad
    @GetMapping("/type/{propertyType}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByType(@PathVariable String propertyType) {
        return ResponseEntity.ok(propertyService.getByPropertyType(propertyType));
    }

    // GET /api/properties/transaction/{transactionType} - Buscar por tipo de
    // transacción
    @GetMapping("/transaction/{transactionType}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByTransaction(@PathVariable String transactionType) {
        return ResponseEntity.ok(propertyService.getByTransactionType(transactionType));
    }

    // GET /api/properties/price-range - Buscar por rango de precio
    @GetMapping("/price-range")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(propertyService.getByPriceRange(minPrice, maxPrice));
    }

    // GET /api/properties/user/{userId} - Buscar propiedades de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(propertyService.getByUserId(userId));
    }

    // GET /api/properties/report/pdf - Generar reporte PDF de propiedades
    @GetMapping("/report/pdf")
    public ResponseEntity<byte[]> generatePropertiesPdfReport() {
        byte[] pdfBytes = propertyService.generatePdfReport();

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "reporte_propiedades_" + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // GET /api/properties/report/excel - Generar reporte Excel de propiedades
    @GetMapping("/report/excel")
    public ResponseEntity<byte[]> generatePropertiesExcelReport() {
        byte[] excelBytes = propertyService.generateExcelReport();

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment",
                "reporte_propiedades_" + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

}