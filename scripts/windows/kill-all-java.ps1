# Script para parar todas as instâncias Java do projeto
Write-Host "======================================" -ForegroundColor Red
Write-Host "    Parando todos os processos Java" -ForegroundColor Red  
Write-Host "======================================" -ForegroundColor Red
Write-Host ""

# Parar todos os processos Java
Write-Host "Parando processos Java..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue

# Aguardar um pouco para garantir que tudo parou
Start-Sleep -Seconds 2

# Verificar se ainda há conexões na porta 8080
$conexoes = netstat -an | Select-String "8080"
if ($conexoes) {
    Write-Host "Ainda há conexões na porta 8080:" -ForegroundColor Yellow
    $conexoes
} else {
    Write-Host "Porta 8080 está livre!" -ForegroundColor Green
}

Write-Host ""
Write-Host "Processos Java parados com sucesso!" -ForegroundColor Green
Write-Host "Agora você pode executar os scripts do projeto." -ForegroundColor Cyan
Write-Host ""

Read-Host "Pressione Enter para continuar"
