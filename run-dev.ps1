# Script para ejecutar el proyecto en modo DESARROLLO LOCAL
# Usa PostgreSQL local en lugar de Railway/Supabase

Write-Host "Iniciando Inmobix Backend en modo DESARROLLO LOCAL..." -ForegroundColor Green
Write-Host ""
Write-Host "Configuracion:" -ForegroundColor Cyan
Write-Host "   - Base de datos: PostgreSQL local (localhost:5432/inmobix_db)" -ForegroundColor White
Write-Host "   - Puerto: 8080" -ForegroundColor White
Write-Host "   - Perfil: dev" -ForegroundColor White
Write-Host ""
Write-Host "Asegurate de que PostgreSQL este corriendo y la base de datos inmobix_db exista" -ForegroundColor Yellow
Write-Host ""

# Ejecutar con perfil dev
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
