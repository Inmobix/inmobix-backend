# Inmobix-backend

Este es el backend para la plataforma Inmobix. Permite a los usuarios gestionar propiedades en venta o arriendo con un sistema completo de autenticación y seguridad.

## Entidades

### User

La entidad `User` representa un usuario de la plataforma con sistema de verificación por email y gestión segura de acciones.

| Atributo               | Tipo          | Descripción                                  | Restricciones                  |
|------------------------|---------------|----------------------------------------------|--------------------------------|
| id                     | UUID          | Identificador único del usuario              | Primary Key, Auto-generado     |
| name                   | String        | Nombre completo del usuario                  | Not Blank                      |
| email                  | String        | Correo electrónico del usuario               | Not Blank, Valid Email, Unique |
| username               | String        | Nombre de usuario para iniciar sesión        | Not Blank, Unique              |
| password               | String        | Contraseña del usuario (hasheada con BCrypt) | Not Blank                      |
| documento              | String        | Documento de identidad del usuario           | Unique                         |
| phone                  | String        | Número de teléfono del usuario               | Opcional                       |
| birthDate              | LocalDate     | Fecha de nacimiento del usuario              | Opcional                       |
| role                   | Enum (Role)   | Rol del usuario (USER o ADMIN)               | Not Null, Default 'USER'       |
| verified               | Boolean       | Estado de verificación de email              | Default false                  |
| verificationToken      | String        | Token único para verificación                | Temporal                       |
| verificationCodeExpiry | LocalDateTime | Marca de tiempo de expiración                | Temporal                       |
| verificationCode       | String        | Código de verificación de email              | Temporal                       |
| resetToken             | String        | Token para recuperación de contraseña        | Temporal                       |
| resetPasswordToken     | String        | Token único para reset                       | Temporal                       |
| resetTokenExpiry       | LocalDateTime | Fecha de expiración del token de reset       | Temporal                       |
| editToken              | String        | Token para confirmar edición de cuenta       | Temporal                       |
| editTokenExpiry        | LocalDateTime | Fecha de expiración del token de edición     | Temporal                       |
| deleteToken            | String        | Token para confirmar eliminación de cuenta   | Temporal                       |
| deleteTokenExpiry      | LocalDateTime | Fecha de expiración del token de eliminación | Temporal                       |

### Property

La entidad `Property` representa una propiedad inmobiliaria en el sistema.

| Atributo        | Tipo          | Descripción                                         | Restricciones                             |
|-----------------|---------------|-----------------------------------------------------|-------------------------------------------|
| id              | Long          | Identificador único de la propiedad                 | Primary Key, Auto-generado                |
| title           | String        | Título del anuncio de la propiedad                  | Not Blank                                 |
| description     | String        | Descripción detallada de la propiedad               | Not Blank                                 |
| address         | String        | Dirección física de la propiedad                    | Not Blank                                 |
| city            | String        | Ciudad donde se ubica la propiedad                  | Not Blank                                 |
| state           | String        | Departamento donde se ubica la propiedad            | Not Blank                                 |
| price           | BigDecimal    | Precio de la propiedad                              | Not Null, Positive                        |
| area            | BigDecimal    | Área total de la propiedad                          | Positive                                  |
| bedrooms        | Integer       | Número de habitaciones                              | Not Null, Default 0                       |
| bathrooms       | Integer       | Número de baños                                     | Not Null, Default 0                       |
| garages         | Integer       | Número de garajes                                   | Not Null, Default 0                       |
| propertyType    | String        | Tipo de propiedad (casa, apartamento, local, etc.)  | Not Blank                                 |
| transactionType | String        | Tipo de transacción (venta, arriendo)               | Not Blank                                 |
| available       | Boolean       | Indica si la propiedad está disponible              | Not Null, Default true                    |
| imageUrl        | String        | URL de la imagen de la propiedad                    | Opcional                                  |
| createdAt       | LocalDateTime | Fecha y hora de creación de la propiedad            | Se establece automáticamente              |
| updatedAt       | LocalDateTime | Fecha y hora de última actualización                | Se actualiza automáticamente              |
| user            | User          | Usuario propietario o agente de la propiedad        | Relación Many-to-One con User             |

---

## 🔌 Endpoints de la API

Esta sección proporciona detalles sobre los endpoints disponibles. Puedes usar herramientas como Postman para interactuar con ellos.

### Endpoints de Usuario

**Ruta base:** `/api`

#### Autenticación y Registro

| Método   | Ruta                        | Descripción                                      | Requiere Auth |
|----------|-----------------------------|--------------------------------------------------|---------------|
| **POST** | `/register`                 | Registra un nuevo usuario                        | No            |
| **POST** | `/login`                    | Autentica un usuario y devuelve sus datos        | No            |
| **POST** | `/forgot-password`          | Inicia el proceso de recuperación de contraseña  | No            |
| **POST** | `/user/verify`              | Verifica el correo mediante token y código       | No            |
| **POST** | `/user/reset-password`      | Restablece la contraseña mediante token y código | No            |
| **POST** | `/user/resend-verification` | Reenvía el correo de verificación                | No            |

#### Gestión de Usuarios

| Método     | Ruta                          | Descripción                              | Requiere Auth |
|------------|-------------------------------|------------------------------------------|---------------|
| **GET**    | `/user/documento/{documento}` | Obtiene un usuario por documento         | Sí (Headers)  |
| **GET**    | `/users`                      | Obtiene lista de todos los usuarios      | Sí (ADMIN)    |
| **POST**   | `/user/request-edit/{id}`     | Solicita token para editar cuenta        | No            |
| **PUT**    | `/user/confirm-edit`          | Confirma y ejecuta edición con token     | No            |
| **POST**   | `/user/request-delete/{id}`   | Solicita token para eliminar cuenta      | No            |
| **DELETE** | `/user/confirm-delete`        | Confirma y ejecuta eliminación con token | No            |


**Headers requeridos para endpoints protegidos:**
- `X-User-Id`: UUID del usuario que hace la petición
- `X-User-Role`: Rol del usuario (USER o ADMIN)

### Endpoints de Propiedad

**Ruta base:** `/api/properties`

| Método | Ruta                              | Descripción                                          |
|--------|-----------------------------------|------------------------------------------------------|
| POST   | `/`                               | Crea una nueva propiedad                             |
| GET    | `/`                               | Obtiene una lista de todas las propiedades           |
| GET    | `/{id}`                           | Obtiene una propiedad específica por su ID           |
| PUT    | `/{id}`                           | Actualiza una propiedad existente                    |
| DELETE | `/{id}`                           | Elimina una propiedad                                |
| GET    | `/available`                      | Obtiene todas las propiedades disponibles            |
| GET    | `/city/{city}`                    | Busca propiedades en una ciudad específica           |
| GET    | `/type/{propertyType}`            | Busca propiedades por tipo                           |
| GET    | `/transaction/{transactionType}`  | Busca propiedades por tipo de transacción            |
| GET    | `/price-range`                    | Busca propiedades dentro de un rango de precio       |
| GET    | `/user/{userId}`                  | Busca todas las propiedades de un usuario específico |

---

## 🛡️ Sistema de Seguridad

### Características de Seguridad Implementadas

1. **Verificación de Email**
    - Código de 6 dígitos enviado al registrarse
    - Los usuarios deben verificar su email antes de poder iniciar sesión
    - Opción para reenviar el código de verificación

2. **Recuperación de Contraseña**
    - Sistema de tokens con expiración de 30 minutos
    - Enlaces seguros enviados por email
    - Tokens de un solo uso

3. **Confirmación de Acciones Críticas**
    - **Edición de cuenta**: Requiere confirmación por email (token válido 15 min)
    - **Eliminación de cuenta**: Requiere confirmación por email (token válido 15 min)
    - Protección contra cambios no autorizados

4. **Encriptación de Contraseñas**
    - BCrypt con salt automático
    - Las contraseñas nunca se almacenan en texto plano

5. **Control de Acceso**
    - Sistema de roles (USER, ADMIN)
    - Endpoints protegidos requieren headers de autenticación
    - Validación de permisos en operaciones sensibles

6. **CORS Configurado Dinámicamente**
    - El origen permitido se configura mediante la variable `FRONTEND_URL`
    - Headers personalizados permitidos: `X-User-Id`, `X-User-Role`
    - Métodos HTTP permitidos: GET, POST, PUT, DELETE, OPTIONS
    - Credenciales habilitadas para autenticación

7. **Respuestas Estandarizadas**
    - Todas las respuestas usan el formato `ApiResponse<T>`
    - Manejo centralizado de errores con `GlobalExceptionHandler`
    - Mensajes de error informativos y seguros

### Excepciones Personalizadas

- `ResourceNotFoundException`: Recurso no encontrado (404)
- `DuplicateResourceException`: Recurso duplicado (409)
- `AuthenticationException`: Error de autenticación (401)
- `BadRequestException`: Petición inválida (400)

---

## 📧 Sistema de Correos

El sistema implementa envío de correos HTML con plantillas personalizadas para:

- ✅ Verificación de email al registrarse
- 🔑 Recuperación de contraseña
- ✏️ Confirmación de edición de cuenta
- ⚠️ Confirmación de eliminación de cuenta

---

## 📁 Estructura del Proyecto

```
inmobix-backend/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── inmobix/
│   │   │           └── backend/
│   │   │               ├── config/
│   │   │               │   ├── AppConfig.java
│   │   │               │   └── CorsConfig.java
│   │   │               │
│   │   │               ├── controller/
│   │   │               │   ├── PropertyController.java
│   │   │               │   └── UserController.java
│   │   │               │
│   │   │               ├── dto/
│   │   │               │   ├── ApiResponse.java
│   │   │               │   ├── ConfirmActionRequest.java
│   │   │               │   ├── ForgotPasswordRequest.java
│   │   │               │   ├── ForgotPasswordResponse.java
│   │   │               │   ├── LoginRequest.java
│   │   │               │   ├── PropertyRequest.java
│   │   │               │   ├── PropertyResponse.java
│   │   │               │   ├── ResetPasswordRequest.java
│   │   │               │   ├── ResetPasswordWithTokenRequest.java
│   │   │               │   ├── UserRequest.java
│   │   │               │   ├── UserResponse.java
│   │   │               │   ├── UserUpdateRequest.java
│   │   │               │   ├── VerifyEmailRequest.java
│   │   │               │   └── VerifyWithTokenRequest.java
│   │   │               │
│   │   │               ├── exception/
│   │   │               │   ├── AuthenticationException.java
│   │   │               │   ├── BadRequestException.java
│   │   │               │   ├── DuplicateResourceException.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   └── ResourceNotFoundException.java
│   │   │               │
│   │   │               ├── model/
│   │   │               │   ├── Property.java
│   │   │               │   ├── Role.java
│   │   │               │   └── User.java
│   │   │               │
│   │   │               ├── repository/
│   │   │               │   ├── PropertyRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               │
│   │   │               ├── service/
│   │   │               │   ├── EmailService.java
│   │   │               │   ├── PropertyService.java
│   │   │               │   └── UserService.java
│   │   │               │
│   │   │               ├── playground/
│   │   │               │   ├── andres/
│   │   │               │   ├── jordy/
│   │   │               │   ├── playgraundJordy/
│   │   │               │   └── README.md
│   │   │               │
│   │   │               └── InmobixBackendApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── inmobix/
│                   └── backend/
│                       └── InmobixBackendApplicationTests.java
│
├── .mvn/
├── .gitattributes
├── .gitignore
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

### 📦 Descripción de Carpetas

| Carpeta/Paquete | Descripción |
|-----------------|-------------|
| **config/** | Configuraciones de la aplicación (PasswordEncoder, CORS, etc.) |
| **controller/** | Controladores REST que manejan las peticiones HTTP |
| **dto/** | Data Transfer Objects para Request y Response |
| **exception/** | Excepciones personalizadas y manejador global |
| **model/** | Entidades JPA que representan las tablas de la base de datos |
| **repository/** | Interfaces JPA Repository para acceso a datos |
| **service/** | Lógica de negocio de la aplicación |
| **playground/** | Ejercicios y prácticas de los desarrolladores |
| **resources/** | Archivos de configuración (application.properties) |

---

## 📜 Historial de Cambios

| Fecha      | Ticket | Cambio                                                                                                     | Autor             |
|------------|--------|------------------------------------------------------------------------------------------------------------|-------------------|
| 12/11/2025 | INB-46 | Se configuró CORS                                                                                          | Andrés Gómez      |
| 12/11/2025 | INB-45 | Mejorar sistema de correos: manejo con tokens únicos, expiración, nuevos DTOs y plantillas HTML unificadas | Andrés Gómez      |
| 06/11/2025 | INB-42 | Se actualizó la documentación con las nuevas implementaciones                                              | Andrés Gómez      |
| 05/11/2025 | INB-43 | Se configuró CORS dinámico mediante variables de entorno                                                   | Andrés Gómez      |
| 05/11/2025 | INB-41 | Se externalizó URLs de dominio en configuración                                                            | Andrés Gómez      |
| 05/11/2025 | INB-40 | Se actualizó UserController con nuevos endpoints de seguridad                                              | Andrés Gómez      |
| 05/11/2025 | INB-39 | Se implementó verificación de email y sistema de tokens en UserService                                     | Andrés Gómez      |
| 05/11/2025 | INB-38 | Se actualizó DTOs de usuario con campo documento                                                           | Andrés Gómez      |
| 05/11/2025 | INB-37 | Se extendió UserRepository con nuevos métodos de búsqueda                                                  | Andrés Gómez      |
| 05/11/2025 | INB-36 | Se agregaron documento y tokens de seguridad al modelo User                                                | Andrés Gómez      |
| 05/11/2025 | INB-35 | Se crearon DTOs para respuestas estandarizadas de la API                                                   | Andrés Gómez      |
| 05/11/2025 | INB-34 | Se implementó sistema de excepciones personalizadas                                                        | Andrés Gómez      |
| 30/10/2025 | INB-33 | Se configuró para desplegar en Render (Dockerfile)                                                         | Andrés Gómez      |
| 30/10/2025 | INB-32 | Se avanzó en verificación de correo y recuperar contraseña                                                 | Andrés Gómez      |
| 30/10/2025 | INB-31 | Se implementó el servicio de correos                                                                       | Andrés Gómez      |
| 29/10/2025 | INB-29 | Reemplazo de ID por UUID y conexión con Supabase completada                                                | Andrés Gómez      |
| 03/10/2025 | INB-28 | Agregar métodos PUT y DELETE en UserController y UserService                                               | Andrés Gómez      |
| 02/10/2025 | INB-20 | Completar/Actualizar README backend                                                                        | Jordy Prada Yanes |
| 02/10/2025 | INB-19 | Redactar README inicial backend                                                                            | Jordy Prada Yanes |
| 02/10/2025 | INB-17 | Configurar H2 y cargar datos de prueba                                                                     | Jordy Prada Yanes |
| 02/10/2025 | INB-26 | Crear Property Model                                                                                       | Jordy Prada Yanes |
| 02/10/2025 | INB-25 | Implementar DTO de property (Response y Request)                                                           | Jordy Prada Yanes |
| 02/10/2025 | INB-16 | Crear PropertyController                                                                                   | Jordy Prada Yanes |
| 02/10/2025 | INB-15 | Implementar PropertyClassService                                                                           | Jordy Prada Yanes |
| 02/10/2025 | INB-14 | Crear entidad y repositorio PropertyClass                                                                  | Jordy Prada Yanes |
| 01/10/2025 | INB-24 | Actividad clase PlaygroundJordy                                                                            | Jordy Prada Yanes |
| 29/09/2025 | INB-23 | Añadir atributo crossorigin para permitir consumo del servidor                                             | Andrés Gómez      |
| 28/09/2025 | INB-22 | Implementar endpoint para listar todos los usuarios                                                        | Andrés Gómez      |
| 28/09/2025 | INB-22 | Cambiar configuración de H2 de memoria a archivo                                                           | Andrés Gómez      |
| 27/09/2025 | INB-21 | Agregar encriptación de contraseñas con BCrypt                                                             | Andrés Gómez      |
| 27/09/2025 | INB-21 | Configurar PasswordEncoder                                                                                 | Andrés Gómez      |
| 26/09/2025 | INB-13 | Crear UserController con endpoints principales                                                             | Andrés Gómez      |
| 26/09/2025 | INB-13 | Ajustar UserService para devolver respuestas correctas                                                     | Andrés Gómez      |
| 25/09/2025 | INB-12 | Crear UserService con métodos principales                                                                  | Andrés Gómez      |
| 25/09/2025 | INB-12 | Añadir DTOs: UserRequest y UserResponse                                                                    | Andrés Gómez      |
| 25/09/2025 | INB-12 | Implementar validaciones en la entidad User                                                                | Andrés Gómez      |
| 25/09/2025 | INB-11 | Crear clase UserClass con sus campos                                                                       | Andrés Gómez      |
| 25/09/2025 | INB-11 | Crear UserClassRepository extendiendo JpaRepository                                                        | Andrés Gómez      |
| 25/09/2025 | INB-18 | Crear estructura inicial de carpetas                                                                       | Andrés Gómez      |
| 18/09/2025 | INB-10 | Ejercicio realizado en clase                                                                               | Andrés Gómez      |
| 13/09/2025 | INB-7  | Actualización de README                                                                                    | Jordy Prada Yanes |
| 13/09/2025 | INB-7  | Subir LenguajesController                                                                                  | Jordy Prada Yanes |
| 11/09/2025 | INB-6  | Especificar en el README el ejercicio realizado                                                            | Andrés Gómez      |
| 11/09/2025 | INB-6  | Creación de AndresGomezController con CRUD de tareas                                                       | Andrés Gómez      |
| 11/09/2025 | -      | Subida inicial del proyecto Spring Boot                                                                    | Andrés Gómez      |

---

## 👥 Contribuidores

- **Andrés Gómez** (@afgomezvufpso)
- **Jordy Prada Yanes** (@JordyPradaYanes)

---

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **PostgreSQL** (Supabase)
- **Spring Mail** (Envío de correos)
- **Lombok**
- **BCrypt** (Spring Security Crypto)
- **Maven**
- **Docker** (Dockerfile para despliegue)

---

## 🚀 Cómo Ejecutar el Proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/Inmobix/inmobix-backend.git
cd inmobix-backend
```

### 2. Configurar variables de entorno

Antes de ejecutar el proyecto, configura las siguientes variables de entorno:

#### Base de datos (PostgreSQL/Supabase)
- `DB_URL`: URL de conexión a PostgreSQL
- `DB_USER`: Usuario de la base de datos
- `DB_PASSWORD`: Contraseña de la base de datos

#### Servidor de correo
- `MAIL_HOST`: Servidor SMTP (ej: smtp.gmail.com)
- `MAIL_PORT`: Puerto SMTP (ej: 587)
- `MAIL_USERNAME`: Email desde el que se enviarán los correos
- `MAIL_PASSWORD`: Contraseña o App Password del correo

#### URLs de la aplicación
- `BACKEND_URL`: URL del backend (ej: http://localhost:8080)
- `FRONTEND_URL`: URL del frontend (ej: http://localhost:4200)
    - **IMPORTANTE**: Esta variable también configura el origen CORS permitido

#### Puerto del servidor
- `PORT`: Puerto en el que correrá la aplicación (opcional, default: 8080)

### 3. Ejecutar con Maven

```bash
./mvnw spring-boot:run
```

### 4. Ejecutar con Docker

```bash
docker build -t inmobix-backend .
docker run -p 8080:8080 \
  -e DB_URL=your_db_url \
  -e DB_USER=your_db_user \
  -e DB_PASSWORD=your_db_password \
  -e MAIL_HOST=smtp.gmail.com \
  -e MAIL_PORT=587 \
  -e MAIL_USERNAME=your_email \
  -e MAIL_PASSWORD=your_password \
  -e BACKEND_URL=https://your-backend.com \
  -e FRONTEND_URL=https://your-frontend.com \
  inmobix-backend
```

---

## 📝 Notas Importantes

### Base de datos
- El proyecto utiliza **PostgreSQL (Supabase)** para persistencia de datos
- Los IDs de usuario son **UUID** y se generan automáticamente
- Los IDs de propiedades son **Long** con auto-incremento
- Hibernate está configurado en modo `update` (crea/actualiza tablas automáticamente)

### Seguridad
- Las contraseñas se almacenan **hasheadas con BCrypt**
- Los tokens de verificación, reset, edición y eliminación expiran automáticamente
- Los usuarios deben verificar su email antes de poder iniciar sesión
- Las acciones críticas (editar/eliminar cuenta) requieren confirmación por email

### Correos electrónicos
- Los correos se envían en formato HTML con plantillas personalizadas

### Despliegue
- El proyecto incluye un `Dockerfile` para facilitar el despliegue
- Configurado para funcionar en plataformas como Render
- Las URLs de dominio son configurables mediante variables de entorno
- **CORS se configura automáticamente** según el `FRONTEND_URL` que definas

### Respuesta Estándar de la API

Todas las respuestas de la API siguen el formato `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Mensaje descriptivo",
  "data": "objeto JSON",
  "timestamp": "2025-11-05T10:30:00"
}
```

### Códigos de Estado HTTP

- `200 OK`: Operación exitosa
- `201 Created`: Recurso creado exitosamente
- `204 No Content`: Operación exitosa sin contenido de respuesta
- `400 Bad Request`: Error en la petición o validación
- `401 Unauthorized`: Error de autenticación
- `404 Not Found`: Recurso no encontrado
- `409 Conflict`: Conflicto (recurso duplicado)
- `500 Internal Server Error`: Error interno del servidor
