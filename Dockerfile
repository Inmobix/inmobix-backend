# Usa una imagen base con Java 17
FROM eclipse-temurin:17-jdk

# Crea un directorio de trabajo
WORKDIR /app

# Copia todo el proyecto al contenedor
COPY . .

# Construye el proyecto con Maven
RUN ./mvnw clean package -DskipTests

# Expone el puerto 8080 (el que usa Spring Boot)
EXPOSE 8080

# Comando de inicio
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
