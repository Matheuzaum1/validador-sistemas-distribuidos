# Script para executar o Simple Error Injector
# Proxy TCP simples e funcional

Write-Host "=== Simple Error Injector v1.0 ===" -ForegroundColor Green
Write-Host "Proxy TCP simples com injeção de erros" -ForegroundColor Yellow
Write-Host ""

# Verificar se o JAR existe
$jarPath = "target/simple-error-injector-1.0.0.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "❌ JAR não encontrado. Compilando..." -ForegroundColor Yellow
    mvn clean package -q
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Erro na compilação!" -ForegroundColor Red
        exit 1
    }
}

Write-Host "🚀 Executando Simple Error Injector..." -ForegroundColor Green
Write-Host "📋 Configure as portas na interface gráfica" -ForegroundColor Cyan
Write-Host ""

java -jar $jarPath