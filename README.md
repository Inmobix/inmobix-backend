# Inmobix

Inmobix es una aplicación backend para una plataforma inmobiliaria. Permite a los usuarios gestionar propiedades en venta o arriendo.

## Entidades

### User

La entidad `User` representa un usuario de la plataforma.

| Atributo  | Tipo      | Descripción                                       | Restricciones              |
|-----------|-----------|---------------------------------------------------|----------------------------|
| id        | Long      | Identificador único del usuario                   | Primary Key, Auto-generado |
| name      | String    | Nombre completo del usuario                       | Not Blank                  |
| email     | String    | Correo electrónico del usuario                    | Not Blank, Valid Email, Unique |
| username  | String    | Nombre de usuario para iniciar sesión             | Not Blank, Unique          |
| password  | String    | Contraseña del usuario                            | Not Blank                  |
| phone     | String    | Número de teléfono del usuario                    | Opcional                   |
| birthDate | LocalDate | Fecha de nacimiento del usuario                   | Opcional                   |

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

| Método | Ruta                | Descripción                                       |
|--------|---------------------|---------------------------------------------------|
| POST   | `/register`         | Registra un nuevo usuario                         |
| POST   | `/login`            | Autentica un usuario y devuelve un token          |
| POST   | `/forgot-password`  | Inicia el proceso de recuperación de contraseña   |
| GET    | `/user/{id}`        | Obtiene un usuario específico por su ID           |
| GET    | `/users`            | Obtiene una lista de todos los usuarios           |
| PUT    | `/user/{id}`        | Actualiza un usuario existente                    |
| DELETE | `/user/{id}`        | Elimina un usuario                                |

### Endpoints de Propiedad

**Ruta base:** `/api/properties`

| Método | Ruta                        | Descripción                                          |
|--------|-----------------------------|------------------------------------------------------|
| POST   | `/`                         | Crea una nueva propiedad                             |
| GET    | `/`                         | Obtiene una lista de todas las propiedades           |
| GET    | `/{id}`                     | Obtiene una propiedad específica por su ID           |
| PUT    | `/{id}`                     | Actualiza una propiedad existente                    |
| DELETE | `/{id}`                     | Elimina una propiedad                                |
| GET    | `/available`                | Obtiene todas las propiedades disponibles            |
| GET    | `/city/{city}`              | Busca propiedades en una ciudad específica           |
| GET    | `/type/{propertyType}`      | Busca propiedades por tipo                           |
| GET    | `/transaction/{transactionType}` | Busca propiedades por tipo de transacción      |
| GET    | `/price-range`              | Busca propiedades dentro de un rango de precio       |
| GET    | `/user/{userId}`            | Busca todas las propiedades de un usuario específico |

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
│   │   │               │   └── AppConfig.java
│   │   │               │
│   │   │               ├── controller/
│   │   │               │   ├── PropertyController.java
│   │   │               │   └── UserController.java
│   │   │               │
│   │   │               ├── dto/
│   │   │               │   ├── PropertyRequest.java
│   │   │               │   ├── PropertyResponse.java
│   │   │               │   ├── UserRequest.java
│   │   │               │   └── UserResponse.java
│   │   │               │
│   │   │               ├── model/
│   │   │               │   ├── Property.java
│   │   │               │   └── User.java
│   │   │               │
│   │   │               ├── repository/
│   │   │               │   ├── PropertyRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               │
│   │   │               ├── service/
│   │   │               │   ├── PropertyService.java
│   │   │               │   └── UserService.java
│   │   │               │
│   │   │               ├── playground/
│   │   │               │   ├── andres/
│   │   │               │   │   ├── controller/
│   │   │               │   │   │   ├── AndresGomezController.java
│   │   │               │   │   │   └── UserClassController.java
│   │   │               │   │   ├── dto/
│   │   │               │   │   │   ├── UserClassRequest.java
│   │   │               │   │   │   └── UserClassResponse.java
│   │   │               │   │   ├── model/
│   │   │               │   │   │   └── UserClass.java
│   │   │               │   │   ├── repository/
│   │   │               │   │   │   └── UserClassRepository.java
│   │   │               │   │   └── service/
│   │   │               │   │       └── UserClassService.java
│   │   │               │   │
│   │   │               │   ├── jordy/
│   │   │               │   │   └── controller/
│   │   │               │   │       └── LenguajesController.java
│   │   │               │   │
│   │   │               │   ├── playgraundJordy/
│   │   │               │   │   ├── controller/
│   │   │               │   │   │   └── UserClassControllerJDPY.java
│   │   │               │   │   ├── dto/
│   │   │               │   │   │   ├── UserClassRequestJDPY.java
│   │   │               │   │   │   └── UserClassResponseJDPY.java
│   │   │               │   │   ├── model/
│   │   │               │   │   │   └── UserClassJDPY.java
│   │   │               │   │   ├── repository/
│   │   │               │   │   │   └── UserClassRepositoryJDPY.java
│   │   │               │   │   └── service/
│   │   │               │   │       └── UserClassServiceJDPY.java
│   │   │               │   │
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
│   └── wrapper/
│       └── maven-wrapper.properties
│
├── data/
│   └── inmobix-backend.mv.db (H2 Database)
│
├── .gitattributes
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
├── package-lock.json
└── README.md
```

### 📦 Descripción de Carpetas

| Carpeta/Paquete | Descripción |
|-----------------|-------------|
| **config/** | Configuraciones de la aplicación (PasswordEncoder, etc.) |
| **controller/** | Controladores REST que manejan las peticiones HTTP |
| **dto/** | Data Transfer Objects para Request y Response |
| **model/** | Entidades JPA que representan las tablas de la base de datos |
| **repository/** | Interfaces JPA Repository para acceso a datos |
| **service/** | Lógica de negocio de la aplicación |
| **playground/** | Ejercicios y prácticas de los desarrolladores |
| **resources/** | Archivos de configuración (application.properties) |
| **data/** | Base de datos H2 en modo archivo |

---

## 📜 Historial de Cambios

| Fecha | Ticket | Cambio | Autor |
|-------|--------|--------|-------|
| 03/10/2025 | INB-28 | Agregar métodos PUT y DELETE en UserController y UserService | Andrés Gómez |
| 02/10/2025 | INB-20 | Completar/Actualizar README backend | Jordy Prada Yanes |
| 02/10/2025 | INB-19 | Redactar README inicial backend | Jordy Prada Yanes |
| 02/10/2025 | INB-17 | Configurar H2 y cargar datos de prueba | Jordy Prada Yanes |
| 02/10/2025 | INB-26 | Crear Property Model | Jordy Prada Yanes |
| 02/10/2025 | INB-25 | Implementar DTO de property (Response y Request) | Jordy Prada Yanes |
| 02/10/2025 | INB-16 | Crear PropertyController | Jordy Prada Yanes |
| 02/10/2025 | INB-15 | Implementar PropertyClassService | Jordy Prada Yanes |
| 02/10/2025 | INB-14 | Crear entidad y repositorio PropertyClass | Jordy Prada Yanes |
| 01/10/2025 | INB-24 | Actividad clase PlaygroundJordy | Jordy Prada Yanes |
| 29/09/2025 | INB-23 | Añadir atributo crossorigin para permitir consumo del servidor | Andrés Gómez |
| 28/09/2025 | INB-22 | Implementar endpoint para listar todos los usuarios | Andrés Gómez |
| 28/09/2025 | INB-22 | Cambiar configuración de H2 de memoria a archivo | Andrés Gómez |
| 27/09/2025 | INB-21 | Agregar encriptación de contraseñas con BCrypt | Andrés Gómez |
| 27/09/2025 | INB-21 | Configurar PasswordEncoder | Andrés Gómez |
| 26/09/2025 | INB-13 | Crear UserController con endpoints principales | Andrés Gómez |
| 26/09/2025 | INB-13 | Ajustar UserService para devolver respuestas correctas | Andrés Gómez |
| 25/09/2025 | INB-12 | Crear UserService con métodos principales | Andrés Gómez |
| 25/09/2025 | INB-12 | Añadir DTOs: UserRequest y UserResponse | Andrés Gómez |
| 25/09/2025 | INB-12 | Implementar validaciones en la entidad User | Andrés Gómez |
| 25/09/2025 | INB-11 | Crear clase UserClass con sus campos | Andrés Gómez |
| 25/09/2025 | INB-11 | Crear UserClassRepository extendiendo JpaRepository | Andrés Gómez |
| 25/09/2025 | INB-18 | Crear estructura inicial de carpetas | Andrés Gómez |
| 18/09/2025 | INB-10 | Ejercicio realizado en clase | Andrés Gómez |
| 13/09/2025 | INB-7  | Actualización de README | Jordy Prada Yanes |
| 13/09/2025 | INB-7  | Subir LenguajesController | Jordy Prada Yanes |
| 11/09/2025 | INB-6  | Especificar en el README el ejercicio realizado | Andrés Gómez |
| 11/09/2025 | INB-6  | Creación de AndresGomezController con CRUD de tareas | Andrés Gómez |
| 11/09/2025 | -      | Subida inicial del proyecto Spring Boot | Andrés Gómez |

---

## 👥 Contribuidores

- **Andrés Gómez** (@afgomezvufpso)
- **Jordy Prada Yanes** (@JordyPradaYanes)

---

## 🛠️ Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **H2 Database**
- **Lombok**
- **BCrypt** (Spring Security Crypto)
- **Maven**

---

## 🚀 Cómo Ejecutar el Proyecto

1. **Clonar el repositorio:**
```bash
git clone https://github.com/Inmobix/inmobix-backend.git
```

2. **Navegar al directorio del proyecto:**
```bash
cd inmobix-backend
```

3. **Ejecutar con Maven:**
```bash
./mvnw spring-boot:run
```

4. **La aplicación estará disponible en:**
```
http://localhost:8080
```

5. **Acceder a la consola H2:**
```
http://localhost:8080/h2-console
```
- **JDBC URL:** `jdbc:h2:file:./data/inmobix-backend`
- **Usuario:** `sa`
- **Contraseña:** (vacío)

---

## 📝 Notas Importantes

- El proyecto utiliza **H2 en modo archivo** para persistencia de datos
- Las contraseñas se almacenan **hasheadas con BCrypt**
- **CORS** configurado para `http://localhost:4200`
- Los IDs se generan automáticamente con `@GeneratedValue`
- Al eliminar un usuario, primero debes eliminar sus propiedades asociadas
