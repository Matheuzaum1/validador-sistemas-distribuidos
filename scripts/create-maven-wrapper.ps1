# Script para baixar Maven Wrapper (funciona offline após primeira execução)
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  Configurando Maven Wrapper" -ForegroundColor Cyan  
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path "pom.xml")) {
    Write-Host "ERRO: Execute no diretório raiz do projeto!" -ForegroundColor Red
    exit 1
}

Write-Host "Gerando Maven Wrapper..." -ForegroundColor Yellow
mvn wrapper:wrapper -Dmaven=3.9.4

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Maven Wrapper criado com sucesso!" -ForegroundColor Green
    Write-Host "Agora você pode usar .\mvnw em vez de mvn" -ForegroundColor Cyan
} else {
    Write-Host "✗ Erro ao criar Maven Wrapper" -ForegroundColor Red
}

Read-Host "Pressione Enter para continuar"
