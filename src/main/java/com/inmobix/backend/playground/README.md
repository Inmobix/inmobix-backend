# Controlador AndresGomezController

*Ruta:* com.inmobix.backend.playground.andres.controller

Controlador REST que maneja una lista de tareas usando los métodos HTTP básicos.

Ruta base: /andresgomez/tareas

---

## Métodos

### 1. GET - Listar tareas

GET /andresgomez/tareas

Devuelve la lista de tareas actuales.

---

### 2. POST - Agregar tarea

POST /andresgomez/tareas?nombre=AprenderGit

Agrega una nueva tarea con el nombre especificado.

---

### 3. PUT - Actualizar tarea

PUT /andresgomez/tareas/0?nombre=NuevaTarea

Actualiza la tarea en la posición indicada (por índice).

---

### 4. DELETE - Eliminar tarea

DELETE /andresgomez/tareas/0

Elimina la tarea en la posición indicada (por índice).

---

# Controlador LenguajesController por Jordy Prada

Controlador REST que maneja una lista de lenguajes de programación usando los métodos HTTP básicos.

**Ruta base:** `/ejemplo/lenguajes`

**Lenguajes iniciales:** Java, Python, JavaScript, TypeScript

---

## Métodos

### 1. GET - Listar todos los lenguajes
```
GET /ejemplo/lenguajes
```
Devuelve la lista completa de lenguajes de programación.

---

### 2. GET - Obtener lenguaje por posición
```
GET /ejemplo/lenguajes/{index}
```
Obtiene el lenguaje de programación en la posición especificada.

**Ejemplo:** `GET /ejemplo/lenguajes/2`

---

### 3. POST - Agregar nuevo lenguaje
```
POST /ejemplo/lenguajes?lenguaje=Go
```
Agrega un nuevo lenguaje de programación a la lista.

---

### 4. PUT - Actualizar lenguaje existente
```
PUT /ejemplo/lenguajes/{index}?nuevo=C++
```
Actualiza el lenguaje en la posición indicada (por índice).

**Ejemplo:** `PUT /ejemplo/lenguajes/3?nuevo=C++`

---

### 5. DELETE - Eliminar lenguaje
```
DELETE /ejemplo/lenguajes/{index}
```
Elimina el lenguaje en la posición indicada (por índice).

**Ejemplo:** `DELETE /ejemplo/lenguajes/1`

---

## Notas
- Los índices comienzan en 0
- Al eliminar un elemento, los índices de los elementos posteriores se ajustan automáticamente
- Si se especifica un índice fuera de rango en GET individual, se retorna un mensaje de error informativo