# Registro de Cambios - Inmobix

## Cambio 1: Configuración de Entorno Local

**Problema:** El frontend apuntaba a producción en lugar de localhost.
**Solución:** Configurar `environment.ts` con `apiUrl: "http://localhost:8081/api"` para desarrollo local.

## Cambio 2: Modo de Prueba sin Email

**Problema:** El servicio de email (Postmark) fallaba en desarrollo local.
**Solución:** Se modificó `EmailService.java` para que los errores de email no bloqueen el registro.

## Cambio 3: Reportes de Propiedades

### Reportes Resumidos

Generan información clave de las propiedades:

- **PDF Resumido**: `GET /api/properties/report/pdf`
- **Excel Resumido**: `GET /api/properties/report/excel`

**Contenido:** Título, Ciudad, Precio, Área, Habitaciones, Baños, Garajes, Propietario, Email, Teléfono, Disponible

### Reportes Detallados (COMPLETOS)

Incluyen **TODOS** los campos del modelo Property:

- **PDF Detallado**: `GET /api/properties/report/pdf/detailed`
- **Excel Detallado**: `GET /api/properties/report/excel/detailed`

**Contenido Completo:**

- **Información Básica:** Título, Descripción
- **Ubicación:** Dirección, Ciudad, Departamento
- **Características:** Precio, Área, Habitaciones, Baños, Garajes
- **Clasificación:** Tipo de Propiedad, Tipo de Transacción
- **Propietario:** Nombre, Email, Teléfono
- **Estado:** Disponible

El reporte PDF detallado usa formato horizontal (landscape) para mejor visualización.

### Cómo Usar

1. Ejecutar backend: `mvn spring-boot:run "-Dspring-boot.run.profiles=dev"`
2. Abrir Swagger: `http://localhost:8081/swagger-ui.html`
3. Buscar endpoints en "property-controller"
4. Probar cualquiera de los 4 endpoints disponibles
