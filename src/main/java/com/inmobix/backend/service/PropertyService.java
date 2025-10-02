package com.inmobix.backend.service;

import com.inmobix.backend.dto.PropertyRequest;
import com.inmobix.backend.dto.PropertyResponse;
import com.inmobix.backend.model.Property;
import com.inmobix.backend.model.User;
import com.inmobix.backend.repository.PropertyRepository;
import com.inmobix.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    // Crear una nueva propiedad
    @Transactional
    public PropertyResponse create(PropertyRequest request) {
        Property property = new Property();
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setPrice(request.getPrice());
        property.setArea(request.getArea());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setGarages(request.getGarages());
        property.setPropertyType(request.getPropertyType());
        property.setTransactionType(request.getTransactionType());
        property.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);
        property.setImageUrl(request.getImageUrl());

        // Asociar usuario si se proporciona
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + request.getUserId()));
            property.setUser(user);
        }

        Property saved = propertyRepository.save(property);
        return mapToResponse(saved);
    }

    // Obtener propiedad por ID
    public PropertyResponse getById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada con id " + id));
        return mapToResponse(property);
    }

    // Listar todas las propiedades
    public List<PropertyResponse> getAll() {
        return propertyRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Actualizar una propiedad
    @Transactional
    public PropertyResponse update(Long id, PropertyRequest request) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada con id " + id));

        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setPrice(request.getPrice());
        property.setArea(request.getArea());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setGarages(request.getGarages());
        property.setPropertyType(request.getPropertyType());
        property.setTransactionType(request.getTransactionType());
        property.setAvailable(request.getAvailable());
        property.setImageUrl(request.getImageUrl());

        Property updated = propertyRepository.save(property);
        return mapToResponse(updated);
    }

    // Eliminar una propiedad
    @Transactional
    public void delete(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Propiedad no encontrada con id " + id);
        }
        propertyRepository.deleteById(id);
    }

    // Buscar propiedades disponibles
    public List<PropertyResponse> getAvailableProperties() {
        return propertyRepository.findByAvailableTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Buscar por ciudad
    public List<PropertyResponse> getByCity(String city) {
        return propertyRepository.findByCity(city)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Buscar por tipo de propiedad
    public List<PropertyResponse> getByPropertyType(String propertyType) {
        return propertyRepository.findByPropertyType(propertyType)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Buscar por tipo de transacción
    public List<PropertyResponse> getByTransactionType(String transactionType) {
        return propertyRepository.findByTransactionType(transactionType)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Buscar por rango de precio
    public List<PropertyResponse> getByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return propertyRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Buscar propiedades de un usuario
    public List<PropertyResponse> getByUserId(Long userId) {
        return propertyRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Método auxiliar para mapear Property a PropertyResponse
    private PropertyResponse mapToResponse(Property property) {
        PropertyResponse response = new PropertyResponse();
        response.setId(property.getId());
        response.setTitle(property.getTitle());
        response.setDescription(property.getDescription());
        response.setAddress(property.getAddress());
        response.setCity(property.getCity());
        response.setState(property.getState());
        response.setPrice(property.getPrice());
        response.setArea(property.getArea());
        response.setBedrooms(property.getBedrooms());
        response.setBathrooms(property.getBathrooms());
        response.setGarages(property.getGarages());
        response.setPropertyType(property.getPropertyType());
        response.setTransactionType(property.getTransactionType());
        response.setAvailable(property.getAvailable());
        response.setImageUrl(property.getImageUrl());
        response.setCreatedAt(property.getCreatedAt());
        response.setUpdatedAt(property.getUpdatedAt());

        // Incluir información del usuario si existe
        if (property.getUser() != null) {
            response.setUserId(property.getUser().getId());
            response.setUserName(property.getUser().getName());
            response.setUserEmail(property.getUser().getEmail());
            response.setUserPhone(property.getUser().getPhone());
        }

        return response;
    }
}