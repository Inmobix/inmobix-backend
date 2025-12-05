# ReadmeProperties

## Modelo de Propiedades

El modelo `Property` es la entidad central que representa los bienes inmuebles gestionados en la plataforma Inmobix. Este modelo estructura toda la información relevante para la publicación, búsqueda y gestión de propiedades.

### Estructura de Datos

La clase `Property` incluye los siguientes atributos clave para describir un inmueble:

- **Identificación y Auditoría**:

  - `id`: Identificador único universal (UUID).
  - `createdAt` / `updatedAt`: Marcas de tiempo para el control de creación y edición.

- **Información Básica**:

  - `title`: Título descriptivo del anuncio.
  - `description`: Detalle extenso de las características y bondades del inmueble.
  - `price`: Valor monetario de la propiedad.

- **Ubicación**:

  - `address`: Dirección física.
  - `city`: Ciudad de ubicación.
  - `state`: Departamento o estado.

- **Características Físicas**:

  - `area`: Superficie en metros cuadrados.
  - `bedrooms`: Número de habitaciones.
  - `bathrooms`: Número de baños.
  - `garages`: Cantidad de espacios de estacionamiento.

- **Clasificación**:

  - `propertyType`: Categoría del inmueble (ej. casa, apartamento, local, lote, finca).
  - `transactionType`: Modalidad de negocio (ej. venta, arriendo).
  - `available`: Indicador booleano de disponibilidad.

- **Relaciones**:
  - `user`: Vinculación con el usuario (propietario o agente) responsable de la propiedad.

---

## Gestión de Reportes (PDF y Excel)

El backend de Inmobix expone endpoints específicos para la generación dinámica de reportes. Estos servicios recopilan la información actual de la base de datos y la transforman en documentos descargables.

### Endpoints Disponibles

1.  **Reporte PDF (`/api/properties/report/pdf`)**:
    - Diseñado para presentar un listado formal y legible.
    - Ideal para compartir información estática o para impresión.
2.  **Reporte Excel (`/api/properties/report/excel`)**:
    - Genera un archivo en formato `.xlsx`.
    - Permite a los administradores y usuarios manipular los datos, realizar cálculos o importar la información a otras herramientas.

---

## Proceso de Swagger para Reportes

Swagger UI es una herramienta esencial en nuestro flujo de desarrollo, permitiendo probar y validar la generación de documentos directamente desde el navegador, sin depender del frontend.

### Pasos para Generar Reportes desde Swagger

1.  **Ingreso a la Plataforma**:

    - Acceda a la interfaz de Swagger UI (por defecto en `http://localhost:8080/swagger-ui/index.html` cuando el servidor está en ejecución).

2.  **Ubicar el Controlador**:

    - Navegue hasta la sección **`property-controller`**. Aquí encontrará todos los endpoints relacionados con la gestión de propiedades.

3.  **Generación del Reporte PDF**:

    - Busque y expanda la operación **`GET /api/properties/report/pdf`**.
    - Haga clic en el botón **"Try it out"** (Probar).
    - Dado que no requiere parámetros de entrada, haga clic directamente en **"Execute"** (Ejecutar).
    - **Resultado**:
      - El servidor procesará la solicitud y devolverá un código `200 OK`.
      - En el cuerpo de la respuesta ("Response body"), verá un enlace de descarga o un botón que dice **"Download file"**.
      - Al hacer clic, se descargará un archivo con el formato: `reporte_propiedades_YYYYMMDD_HHMMSS.pdf`.

4.  **Generación del Reporte Excel**:
    - Busque y expanda la operación **`GET /api/properties/report/excel`**.
    - Repita el proceso: **"Try it out"** -> **"Execute"**.
    - **Resultado**:
      - Tras una ejecución exitosa (Código `200`), aparecerá la opción **"Download file"**.
      - El archivo descargado tendrá el formato: `reporte_propiedades_YYYYMMDD_HHMMSS.xlsx`.

### Importancia de este Proceso

El uso de Swagger para esta tarea garantiza que:

- La lógica de generación de archivos (binarios) funciona correctamente.
- Los encabezados HTTP (`Content-Disposition`, `Content-Type`) están configurados adecuadamente para forzar la descarga.
- Los datos reflejados en los reportes corresponden fielmente al estado actual de la base de datos.
