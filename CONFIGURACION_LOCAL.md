# 🔧 Configuración para Desarrollo Local

Este documento explica cómo configurar el proyecto para trabajar con una base de datos PostgreSQL local en lugar de Railway/Supabase.

## 📋 Requisitos Previos

1. **PostgreSQL instalado localmente**

   - Descarga desde: https://www.postgresql.org/download/
   - Versión recomendada: 14 o superior

2. **Java 17**
3. **Maven**

## 🗄️ Configuración de PostgreSQL Local

### Paso 1: Crear la base de datos

Abre **pgAdmin** o la terminal de PostgreSQL y ejecuta:

```sql
CREATE DATABASE inmobix_db;
```

### Paso 2: Verificar usuario y contraseña

Por defecto, el perfil de desarrollo usa:

- **Usuario**: `postgres`
- **Contraseña**: `postgres`
- **Puerto**: `5432`
- **Base de datos**: `inmobix_db`

Si tu configuración es diferente, edita el archivo:

```
src/main/resources/application-dev.properties
```

## 🚀 Ejecutar el Proyecto en Modo Local

### Opción 1: Usando Maven (Recomendado)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Opción 2: Usando variable de entorno

**Windows (PowerShell):**

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
```

**Windows (CMD):**

```cmd
set SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

**Linux/Mac:**

```bash
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

### Opción 3: Desde tu IDE

En **IntelliJ IDEA** o **Eclipse**:

1. Ve a Run → Edit Configurations
2. Agrega variable de entorno: `SPRING_PROFILES_ACTIVE=dev`
3. Ejecuta la aplicación

## 📁 Archivos de Configuración

### Para Desarrollo Local

- **Archivo**: `src/main/resources/application-dev.properties`
- **Uso**: Configuración automática para PostgreSQL local
- **Activar con**: `-Dspring-boot.run.profiles=dev`

### Para Producción (Railway/Supabase)

- **Archivo**: `src/main/resources/application.properties`
- **Uso**: Variables de entorno para producción
- **Plantilla**: `.env.example`

## 🔍 Verificar que Funciona

1. Ejecuta el proyecto con el perfil `dev`
2. Deberías ver en los logs:
   ```
   Tomcat started on port(s): 8080 (http)
   Started BackendApplication in X.XXX seconds
   ```
3. Abre tu navegador: http://localhost:8080
4. Prueba un endpoint: http://localhost:8080/api/users

## 📧 Configuración de Email (Opcional)

El perfil de desarrollo **NO requiere** configurar Postmark. Si necesitas probar el envío de emails:

1. Crea una cuenta en [Postmark](https://postmarkapp.com/)
2. Edita `application-dev.properties`:
   ```properties
   postmark.api.token=tu-token-aqui
   postmark.from.email=tu-email@ejemplo.com
   ```

## 🌐 CORS para Frontend Local

El perfil `dev` ya permite conexiones desde:

- `http://localhost:4200` (Angular)
- `http://localhost:3000` (React)
- `http://localhost:5173` (Vite)

Si usas otro puerto, agrégalo en `application-dev.properties`:

```properties
app.cors.allowed-origins=http://localhost:4200,http://localhost:TU_PUERTO
```

## 🔄 Cambiar entre Local y Producción

### Desarrollo Local

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Producción (con variables de entorno)

```bash
# Configura las variables de entorno primero
mvn spring-boot:run
```

## 🛠️ Solución de Problemas

### Error: "Connection refused to localhost:5432"

- ✅ Verifica que PostgreSQL esté corriendo
- ✅ Confirma el puerto en `application-dev.properties`

### Error: "database inmobix_db does not exist"

- ✅ Crea la base de datos: `CREATE DATABASE inmobix_db;`

### Error: "password authentication failed"

- ✅ Verifica usuario/contraseña en `application-dev.properties`

### Las tablas no se crean automáticamente

- ✅ Verifica que `spring.jpa.hibernate.ddl-auto=update` esté configurado
- ✅ Revisa los logs para errores de JPA/Hibernate

## 📝 Notas Importantes

- ⚠️ **NO subas** archivos `.env` con credenciales reales a Git
- ✅ El archivo `.gitignore` ya excluye `.env`
- ✅ Usa `.env.example` como plantilla para producción
- ✅ Para desarrollo, usa siempre el perfil `dev`
