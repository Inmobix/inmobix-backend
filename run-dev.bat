@echo off
REM Script para ejecutar el proyecto en modo DESARROLLO LOCAL
REM Usa PostgreSQL local en lugar de Railway/Supabase

echo.
echo ========================================
echo  Inmobix Backend - MODO DESARROLLO
echo ========================================
echo.
echo Configuracion:
echo   - Base de datos: PostgreSQL local
echo   - Puerto: 8080
echo   - Perfil: dev
echo.
echo Asegurate de que PostgreSQL este corriendo
echo y la base de datos 'inmobix_db' exista
echo.
echo ========================================
echo.

mvn spring-boot:run -Dspring-boot.run.profiles=dev
