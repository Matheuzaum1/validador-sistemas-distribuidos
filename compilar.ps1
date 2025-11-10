# Script para compilar o projeto
Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "  Sistema Distribuido - COMPILACAO" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Limpando builds anteriores..." -ForegroundColor Yellow
& mvn clean

Write-Host ""
Write-Host "Compilando projeto..." -ForegroundColor Yellow
& mvn package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "[ERRO] Falha na compilacao!" -ForegroundColor Red
    Write-Host ""
    Read-Host "Pressione Enter para sair"
    exit 1
} else {
    Write-Host ""
    Write-Host "===============================================" -ForegroundColor Green
    Write-Host "[OK] Compilacao concluida com sucesso!" -ForegroundColor Green
    Write-Host "===============================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Arquivo gerado:" -ForegroundColor Cyan
    Write-Host "  target\validador-sistemas-distribuidos-1.0.0.jar" -ForegroundColor White
    Write-Host ""
    Write-Host "Para executar:" -ForegroundColor Cyan
    Write-Host "  - Servidor: .\iniciar-servidor.ps1" -ForegroundColor White
    Write-Host "  - Cliente:  .\iniciar-cliente.ps1" -ForegroundColor White
    Write-Host ""
}

Read-Host "Pressione Enter para sair"
