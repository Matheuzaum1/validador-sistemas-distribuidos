# Script para iniciar o cliente
Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "  Sistema Distribuido - CLIENTE" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

# Verifica se o JAR existe
if (-not (Test-Path "target\validador-sistemas-distribuidos-1.0.0.jar")) {
    Write-Host "[!] JAR nao encontrado. Compilando projeto..." -ForegroundColor Yellow
    Write-Host ""
    
    & mvn clean package -DskipTests
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "[ERRO] Falha na compilacao!" -ForegroundColor Red
        Write-Host ""
        Read-Host "Pressione Enter para sair"
        exit 1
    }
    
    Write-Host ""
    Write-Host "[OK] Compilacao concluida!" -ForegroundColor Green
    Write-Host ""
}

Write-Host "Iniciando interface grafica do cliente..." -ForegroundColor Green
Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

java -cp "target\validador-sistemas-distribuidos-1.0.0.jar" com.distribuidos.client.ClientMain
