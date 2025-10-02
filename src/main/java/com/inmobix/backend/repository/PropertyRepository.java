package com.inmobix.backend.repository;

import com.inmobix.backend.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Buscar por ciudad
    List<Property> findByCity(String city);

    // Buscar por tipo de propiedad
    List<Property> findByPropertyType(String propertyType);

    // Buscar por tipo de transacción
    List<Property> findByTransactionType(String transactionType);

    // Buscar propiedades disponibles
    List<Property> findByAvailableTrue();

    // Buscar por rango de precio
    List<Property> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Buscar por ciudad y tipo de transacción
    List<Property> findByCityAndTransactionType(String city, String transactionType);

    // Buscar por número mínimo de habitaciones
    List<Property> findByBedroomsGreaterThanEqual(Integer bedrooms);

    // Buscar propiedades por usuario
    List<Property> findByUserId(Long userId);
}