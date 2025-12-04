package com.inmobix.backend.service;

import com.inmobix.backend.dto.PropertyRequest;
import com.inmobix.backend.dto.PropertyResponse;
import com.inmobix.backend.model.Property;
import com.inmobix.backend.model.User;
import com.inmobix.backend.repository.PropertyRepository;
import com.inmobix.backend.repository.UserRepository;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.itextpdf.kernel.geom.PageSize;

@Service
public class PropertyService {

        private final PropertyRepository propertyRepository;
        private final UserRepository userRepository;

        public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository) {
                this.propertyRepository = propertyRepository;
                this.userRepository = userRepository;
        }

        // Crear una nueva propiedad
        @SuppressWarnings("null")
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
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Usuario no encontrado con id " + request.getUserId()));
                        property.setUser(user);
                }

                Property saved = propertyRepository.save(property);
                return mapToResponse(saved);
        }

        @SuppressWarnings("null")
        @Transactional(readOnly = true)
        public PropertyResponse getById(UUID id) {
                Property property = propertyRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada con id " + id));
                return mapToResponse(property);
        }

        @Transactional(readOnly = true)
        public List<PropertyResponse> getAll() {
                return propertyRepository.findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Transactional
        public PropertyResponse update(UUID id, PropertyRequest request) {
                @SuppressWarnings("null")
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

        @SuppressWarnings("null")
        @Transactional
        public void delete(UUID id) {
                if (!propertyRepository.existsById(id)) {
                        throw new RuntimeException("Propiedad no encontrada con id " + id);
                }
                propertyRepository.deleteById(id);
        }

        @Transactional(readOnly = true)
        public List<PropertyResponse> getAvailableProperties() {
                return propertyRepository.findByAvailableTrue()
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<PropertyResponse> getByCity(String city) {
                return propertyRepository.findByCity(city)
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<PropertyResponse> getByPropertyType(String propertyType) {
                return propertyRepository.findByPropertyType(propertyType)
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<PropertyResponse> getByTransactionType(String transactionType) {
                return propertyRepository.findByTransactionType(transactionType)
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<PropertyResponse> getByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
                return propertyRepository.findByPriceBetween(minPrice, maxPrice)
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        // Buscar propiedades de un usuario usando UUID
        @Transactional(readOnly = true)
        public List<PropertyResponse> getByUserId(UUID userId) {
                return propertyRepository.findByUserId(userId)
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

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

                if (property.getUser() != null) {
                        response.setUserId(property.getUser().getId()); // UUID
                        response.setUserName(property.getUser().getName());
                        response.setUserEmail(property.getUser().getEmail());
                        response.setUserPhone(property.getUser().getPhone());
                }

                return response;
        }

        // ==================== MÉTODOS DE GENERACIÓN DE REPORTES ====================

        @Transactional(readOnly = true)
        public byte[] generatePdfReport() {
                List<Property> properties = propertyRepository.findAll();

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(baos);
                        com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
                        com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf, PageSize.A4);
                        document.setMargins(36, 36, 36, 36);

                        // Título principal
                        com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph(
                                        "Reporte de Propiedades - Inmobix")
                                        .setFontSize(24)
                                        .setBold()
                                        .setFontColor(new com.itextpdf.kernel.colors.DeviceRgb(46, 134, 193))
                                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
                        document.add(title);

                        // Fecha
                        String dateStr = java.time.LocalDateTime.now().format(
                                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                        com.itextpdf.layout.element.Paragraph date = new com.itextpdf.layout.element.Paragraph(
                                        "Generado el: " + dateStr)
                                        .setFontSize(10)
                                        .setItalic()
                                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                                        .setMarginBottom(20);
                        document.add(date);

                        // Resumen
                        com.itextpdf.layout.element.Paragraph summary = new com.itextpdf.layout.element.Paragraph(
                                        "Total de propiedades registradas: " + properties.size())
                                        .setFontSize(12)
                                        .setBold()
                                        .setMarginBottom(20);
                        document.add(summary);

                        // Crear una tabla separada para cada propiedad
                        for (int i = 0; i < properties.size(); i++) {
                                Property property = properties.get(i);

                                // Título de la propiedad
                                com.itextpdf.layout.element.Paragraph propertyTitle = new com.itextpdf.layout.element.Paragraph(
                                                "Propiedad #" + (i + 1))
                                                .setFontSize(14)
                                                .setBold()
                                                .setFontColor(new com.itextpdf.kernel.colors.DeviceRgb(46, 134, 193))
                                                .setMarginTop(15)
                                                .setMarginBottom(10);
                                document.add(propertyTitle);

                                float[] columnWidths = { 100f, 420f };
                                com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(
                                                com.itextpdf.layout.properties.UnitValue.createPointArray(columnWidths));
                                table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
                                table.setFixedLayout();

                                // Agregar filas de información
                                addPdfPropertyRow(table, "Título", property.getTitle());

                                String description = property.getDescription() != null
                                                && !property.getDescription().isEmpty()
                                                                ? property.getDescription()
                                                                : "N/A";
                                addPdfPropertyRow(table, "Descripción", description);

                                addPdfPropertyRow(table, "Tipo", property.getPropertyType());
                                addPdfPropertyRow(table, "Precio", "$" + String.format("%,.2f", property.getPrice()));

                                String area = property.getArea() != null ? property.getArea() + " m²" : "N/A";
                                addPdfPropertyRow(table, "Área", area);

                                String address = property.getAddress() != null && !property.getAddress().isEmpty()
                                                ? property.getAddress()
                                                : "N/A";
                                addPdfPropertyRow(table, "Dirección", address);

                                addPdfPropertyRow(table, "Ciudad", property.getCity());

                                String state = property.getState() != null && !property.getState().isEmpty()
                                                ? property.getState()
                                                : "N/A";
                                addPdfPropertyRow(table, "Departamento", state);

                                addPdfPropertyRow(table, "Habitaciones", property.getBedrooms().toString());
                                addPdfPropertyRow(table, "Baños", property.getBathrooms().toString());
                                addPdfPropertyRow(table, "Garajes", property.getGarages().toString());

                                // Tipo de Propiedad
                                String propertyType = property.getPropertyType();
                                String propertyTypeLabel = switch (propertyType) {
                                        case "HOUSE" -> "Casa";
                                        case "APARTMENT" -> "Apartamento";
                                        case "LAND" -> "Terreno";
                                        case "COMMERCIAL" -> "Comercial";
                                        default -> propertyType;
                                };
                                addPdfPropertyRow(table, "Tipo de Propiedad", propertyTypeLabel);

                                // Tipo de Transacción
                                String transactionType = property.getTransactionType();
                                String transactionLabel = transactionType.equals("SALE") ? "Venta" : "Alquiler";
                                addPdfPropertyRow(table, "Tipo de Transacción", transactionLabel);

                                addPdfPropertyRow(table, "Disponible", property.getAvailable() ? "Sí" : "No");

                                // Fecha de Creación
                                if (property.getCreatedAt() != null) {
                                        String createdAt = property.getCreatedAt().format(
                                                        java.time.format.DateTimeFormatter
                                                                        .ofPattern("dd/MM/yyyy HH:mm"));
                                        addPdfPropertyRow(table, "Fecha de Creación", createdAt);
                                }

                                document.add(table);
                        }

                        // Footer
                        com.itextpdf.layout.element.Paragraph footer = new com.itextpdf.layout.element.Paragraph(
                                        "Inmobix - Sistema de Gestión Inmobiliaria")
                                        .setFontSize(8)
                                        .setItalic()
                                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                                        .setMarginTop(20);
                        document.add(footer);

                        document.close();
                        return baos.toByteArray();

                } catch (Exception e) {
                        throw new RuntimeException("Error al generar reporte PDF: " + e.getMessage(), e);
                }
        }

        private void addPdfPropertyRow(com.itextpdf.layout.element.Table table, String field, String value) {
                // Celda de campo (encabezado)
                com.itextpdf.layout.element.Cell fieldCell = new com.itextpdf.layout.element.Cell()
                                .add(new com.itextpdf.layout.element.Paragraph(field).setBold().setFontSize(9))
                                .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(240, 248, 255))
                                .setPadding(5);
                table.addCell(fieldCell);

                // Celda de valor - con word wrap habilitado
                com.itextpdf.layout.element.Paragraph valueParagraph = new com.itextpdf.layout.element.Paragraph(value)
                                .setFontSize(9);
                
                com.itextpdf.layout.element.Cell valueCell = new com.itextpdf.layout.element.Cell()
                                .add(valueParagraph)
                                .setPadding(5);
                table.addCell(valueCell);
        }

        @Transactional(readOnly = true)
        public byte[] generateExcelReport() {
                List<Property> properties = propertyRepository.findAll();

                try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Propiedades");

                        // Estilos
                        org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
                        titleFont.setBold(true);
                        titleFont.setFontHeightInPoints((short) 16);
                        titleFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
                        titleStyle.setFont(titleFont);
                        titleStyle.setFillForegroundColor(
                                        org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
                        titleStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
                        titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
                        titleStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

                        // Estilo para títulos de propiedad
                        org.apache.poi.ss.usermodel.CellStyle propertyTitleStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font propertyTitleFont = workbook.createFont();
                        propertyTitleFont.setBold(true);
                        propertyTitleFont.setFontHeightInPoints((short) 12);
                        propertyTitleFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
                        propertyTitleStyle.setFont(propertyTitleFont);
                        propertyTitleStyle.setFillForegroundColor(
                                        org.apache.poi.ss.usermodel.IndexedColors.LIGHT_BLUE.getIndex());
                        propertyTitleStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
                        propertyTitleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

                        org.apache.poi.ss.usermodel.CellStyle fieldStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font fieldFont = workbook.createFont();
                        fieldFont.setBold(true);
                        fieldStyle.setFont(fieldFont);
                        fieldStyle.setFillForegroundColor(
                                        org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
                        fieldStyle.setWrapText(true);
                        fieldStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.TOP);
                        fieldStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
                        fieldStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                        fieldStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                        fieldStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                        fieldStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                        fieldStyle.setWrapText(true);
                        fieldStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.TOP);

                        org.apache.poi.ss.usermodel.CellStyle valueStyle = workbook.createCellStyle();
                        valueStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                        valueStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                        valueStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                        valueStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                        valueStyle.setWrapText(true);
                        valueStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.TOP);

                        // Título principal
                        org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
                        titleRow.setHeight((short) 600);
                        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
                        titleCell.setCellValue("Reporte de Propiedades - Inmobix");
                        titleCell.setCellStyle(titleStyle);
                        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));

                        // Fecha
                        org.apache.poi.ss.usermodel.Row dateRow = sheet.createRow(1);
                        org.apache.poi.ss.usermodel.Cell dateCell = dateRow.createCell(0);
                        dateCell.setCellValue("Generado el: " + java.time.LocalDateTime.now().format(
                                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 1));

                        // Resumen
                        org.apache.poi.ss.usermodel.Row summaryRow = sheet.createRow(2);
                        summaryRow.setHeight((short) 400);
                        org.apache.poi.ss.usermodel.Cell summaryCell = summaryRow.createCell(0);
                        summaryCell.setCellValue("Total de propiedades registradas: " + properties.size());
                        org.apache.poi.ss.usermodel.CellStyle summaryStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font summaryFont = workbook.createFont();
                        summaryFont.setBold(true);
                        summaryStyle.setFont(summaryFont);
                        summaryCell.setCellStyle(summaryStyle);
                        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, 0, 1));

                        int currentRow = 4;

                        // Crear sección para cada propiedad
                        for (int i = 0; i < properties.size(); i++) {
                                Property property = properties.get(i);

                                // Título de la propiedad
                                org.apache.poi.ss.usermodel.Row propTitleRow = sheet.createRow(currentRow);
                                propTitleRow.setHeight((short) 400);
                                org.apache.poi.ss.usermodel.Cell propTitleCell = propTitleRow.createCell(0);
                                propTitleCell.setCellValue("Propiedad #" + (i + 1));
                                propTitleCell.setCellStyle(propertyTitleStyle);
                                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow,
                                                currentRow, 0, 1));
                                currentRow++;

                                // Agregar campos de la propiedad
                                currentRow = addExcelPropertyRow(sheet, currentRow, "Título", property.getTitle(),
                                                fieldStyle,
                                                valueStyle);

                                String description = property.getDescription() != null
                                                && !property.getDescription().isEmpty()
                                                                ? property.getDescription()
                                                                : "N/A";
                                currentRow = addExcelPropertyRow(sheet, currentRow, "Descripción", description,
                                                fieldStyle, valueStyle);

                                currentRow = addExcelPropertyRow(sheet, currentRow, "Tipo", property.getPropertyType(),
                                                fieldStyle,
                                                valueStyle);
                                currentRow = addExcelPropertyRow(sheet, currentRow, "Precio",
                                                "$" + String.format("%,.2f", property.getPrice()), fieldStyle,
                                                valueStyle);

                                String area = property.getArea() != null ? property.getArea() + " m²" : "N/A";
                                currentRow = addExcelPropertyRow(sheet, currentRow, "Área", area, fieldStyle,
                                                valueStyle);

                                String address = property.getAddress() != null && !property.getAddress().isEmpty()
                                                ? property.getAddress()
                                                : "N/A";
                                currentRow = addExcelPropertyRow(sheet, currentRow, "Dirección", address, fieldStyle,
                                                valueStyle);

                                currentRow = addExcelPropertyRow(sheet, currentRow, "Ciudad", property.getCity(),
                                                fieldStyle,
                                                valueStyle);

                                String state = property.getState() != null && !property.getState().isEmpty()
                                                ? property.getState()
                                                : "N/A";
                                currentRow = addExcelPropertyRow(sheet, currentRow, "Departamento", state, fieldStyle,
                                                valueStyle);

                                currentRow = addExcelPropertyRow(sheet, currentRow, "Habitaciones",
                                                property.getBedrooms().toString(), fieldStyle, valueStyle);

                                currentRow = addExcelPropertyRow(sheet, currentRow, "Baños",
                                                property.getBathrooms().toString(), fieldStyle, valueStyle);

                                currentRow = addExcelPropertyRow(sheet, currentRow, "Garajes",
                                                property.getGarages().toString(), fieldStyle, valueStyle);

                                // Tipo de Propiedad
                                String propertyType = property.getPropertyType();
                                String propertyTypeLabel = switch (propertyType) {
                                        case "HOUSE" -> "Casa";
                                        case "APARTMENT" -> "Apartamento";
                                        case "LAND" -> "Terreno";
                                        case "COMMERCIAL" -> "Comercial";
                                        default -> propertyType;
                                };
                                currentRow = addExcelPropertyRow(sheet, currentRow, "Tipo de Propiedad",
                                                propertyTypeLabel, fieldStyle,
                                                valueStyle);

                                // Tipo de Transacción
                                String transactionType = property.getTransactionType();
                                String transactionLabel = transactionType.equals("SALE") ? "Venta" : "Alquiler";
                                currentRow = addExcelPropertyRow(sheet, currentRow, "Tipo de Transacción",
                                                transactionLabel, fieldStyle,
                                                valueStyle);

                                currentRow = addExcelPropertyRow(sheet, currentRow, "Disponible",
                                                property.getAvailable() ? "Sí" : "No", fieldStyle, valueStyle);

                                // Información del Propietario
                                if (property.getUser() != null) {
                                        currentRow = addExcelPropertyRow(sheet, currentRow, "Propietario",
                                                        property.getUser().getName(), fieldStyle, valueStyle);

                                        String email = property.getUser().getEmail() != null
                                                        ? property.getUser().getEmail()
                                                        : "N/A";
                                        currentRow = addExcelPropertyRow(sheet, currentRow, "Email Propietario", email,
                                                        fieldStyle,
                                                        valueStyle);

                                        String phone = property.getUser().getPhone() != null
                                                        ? property.getUser().getPhone()
                                                        : "N/A";
                                        currentRow = addExcelPropertyRow(sheet, currentRow, "Teléfono Propietario",
                                                        phone, fieldStyle,
                                                        valueStyle);
                                } else {
                                        currentRow = addExcelPropertyRow(sheet, currentRow, "Propietario", "N/A",
                                                        fieldStyle, valueStyle);
                                }

                                // Fecha de Creación
                                if (property.getCreatedAt() != null) {
                                        String createdAt = property.getCreatedAt().format(
                                                        java.time.format.DateTimeFormatter
                                                                        .ofPattern("dd/MM/yyyy HH:mm"));
                                        currentRow = addExcelPropertyRow(sheet, currentRow, "Fecha de Creación",
                                                        createdAt, fieldStyle,
                                                        valueStyle);
                                }

                                // Espacio entre propiedades
                                currentRow += 2;
                        }

                        // Ajustar anchos de columnas
                        sheet.setColumnWidth(0, 6000); // Campo
                        sheet.setColumnWidth(1, 15000); // Valor - más ancho para textos largos

                        workbook.write(baos);
                        return baos.toByteArray();

                } catch (Exception e) {
                        throw new RuntimeException("Error al generar reporte Excel: " + e.getMessage(), e);
                }
        }

        private int addExcelPropertyRow(org.apache.poi.ss.usermodel.Sheet sheet, int rowNum,
                        String field, String value,
                        org.apache.poi.ss.usermodel.CellStyle fieldStyle,
                        org.apache.poi.ss.usermodel.CellStyle valueStyle) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum);

                // Celda de campo
                org.apache.poi.ss.usermodel.Cell fieldCell = row.createCell(0);
                fieldCell.setCellValue(field);
                fieldCell.setCellStyle(fieldStyle);

                // Celda de valor
                org.apache.poi.ss.usermodel.Cell valueCell = row.createCell(1);
                valueCell.setCellValue(value);
                valueCell.setCellStyle(valueStyle);

                // Aproximadamente 50 caracteres por línea con el ancho de columna actual
                int charsPerLine = 50;
                int numLines = (int) Math.ceil((double) value.length() / charsPerLine);
                if (numLines > 1) {
                        // Altura por defecto es ~300, multiplicar por número de líneas
                        row.setHeightInPoints(Math.max(15f, numLines * 15f));
                }

                return rowNum + 1;
        }

        // Método público para renderizar propiedades en formato clave-valor en un
        // documento PDF existente
        public void renderPropertiesInPdf(com.itextpdf.layout.Document document, List<Property> properties) {
                if (properties == null || properties.isEmpty()) {
                        com.itextpdf.layout.element.Paragraph noProp = new com.itextpdf.layout.element.Paragraph(
                                        "No hay propiedades registradas.")
                                        .setFontSize(11)
                                        .setItalic();
                        document.add(noProp);
                        return;
                }

                // Crear una tabla separada para cada propiedad
                for (int i = 0; i < properties.size(); i++) {
                        Property property = properties.get(i);

                        // Título de la propiedad
                        com.itextpdf.layout.element.Paragraph propertyTitle = new com.itextpdf.layout.element.Paragraph(
                                        "Propiedad #" + (i + 1))
                                        .setFontSize(12)
                                        .setBold()
                                        .setFontColor(new com.itextpdf.kernel.colors.DeviceRgb(46, 134, 193))
                                        .setMarginTop(15)
                                        .setMarginBottom(10);
                        document.add(propertyTitle);

                        // Tabla de 2 columnas: Campo | Valor
                        float[] columnWidths = { 2f, 3f };
                        com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(
                                        com.itextpdf.layout.properties.UnitValue.createPercentArray(columnWidths))
                                        .useAllAvailableWidth();

                        // Agregar filas de información
                        addPdfPropertyRow(table, "Título", property.getTitle());

                        String description = property.getDescription() != null && !property.getDescription().isEmpty()
                                        ? property.getDescription()
                                        : "N/A";
                        addPdfPropertyRow(table, "Descripción", description);

                        String address = property.getAddress() != null && !property.getAddress().isEmpty()
                                        ? property.getAddress()
                                        : "N/A";
                        addPdfPropertyRow(table, "Dirección", address);

                        addPdfPropertyRow(table, "Ciudad", property.getCity());

                        String state = property.getState() != null && !property.getState().isEmpty()
                                        ? property.getState()
                                        : "N/A";
                        addPdfPropertyRow(table, "Departamento", state);

                        addPdfPropertyRow(table, "Precio", "$" + String.format("%,.0f", property.getPrice()));

                        String area = property.getArea() != null ? property.getArea() + " m²" : "N/A";
                        addPdfPropertyRow(table, "Área", area);

                        addPdfPropertyRow(table, "Habitaciones", property.getBedrooms().toString());
                        addPdfPropertyRow(table, "Baños", property.getBathrooms().toString());
                        addPdfPropertyRow(table, "Garajes", property.getGarages().toString());

                        // Tipo de Propiedad
                        String propertyType = property.getPropertyType();
                        String propertyTypeLabel = switch (propertyType) {
                                case "HOUSE" -> "Casa";
                                case "APARTMENT" -> "Apartamento";
                                case "LAND" -> "Terreno";
                                case "COMMERCIAL" -> "Comercial";
                                default -> propertyType;
                        };
                        addPdfPropertyRow(table, "Tipo de Propiedad", propertyTypeLabel);

                        // Tipo de Transacción
                        String transactionType = property.getTransactionType();
                        String transactionLabel = transactionType.equals("SALE") ? "Venta" : "Alquiler";
                        addPdfPropertyRow(table, "Tipo de Transacción", transactionLabel);

                        addPdfPropertyRow(table, "Disponible", property.getAvailable() ? "Sí" : "No");

                        // Fecha de Creación
                        if (property.getCreatedAt() != null) {
                                String createdAt = property.getCreatedAt().format(
                                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                                addPdfPropertyRow(table, "Fecha de Creación", createdAt);
                        }

                        document.add(table);
                }
        }
}
