# Controlador AndresGomezController

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