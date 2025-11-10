# Script para iniciar o sistema completo (servidor + cliente)
Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "  Sistema Distribuido - INICIALIZACAO" -ForegroundColor Cyan
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

Write-Host "Iniciando servidor em segundo plano..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Write-Host 'SERVIDOR - Porta 8080' -ForegroundColor Green; java -cp 'target\validador-sistemas-distribuidos-1.0.0.jar' com.distribuidos.server.ServerMain"

Write-Host "Aguardando servidor inicializar..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

Write-Host "Iniciando cliente..." -ForegroundColor Yellow
Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

Start-Process powershell -ArgumentList "-NoExit", "-Command", "Write-Host 'CLIENTE - Interface Grafica' -ForegroundColor Green; java -cp 'target\validador-sistemas-distribuidos-1.0.0.jar' com.distribuidos.client.ClientMain"

Write-Host ""
Write-Host "===============================================" -ForegroundColor Green
Write-Host "[OK] Sistema iniciado!" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Servidor: Janela 'SERVIDOR - Porta 8080'" -ForegroundColor Cyan
Write-Host "Cliente:  Janela 'CLIENTE - Interface Grafica'" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para encerrar, feche as janelas correspondentes" -ForegroundColor Yellow
Write-Host ""

Read-Host "Pressione Enter para fechar esta janela"
