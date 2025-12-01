param(
    [int]$proxyPort = 9999,
    [string]$serverHost = "localhost",
    [int]$serverPort = 20000
)
$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location "$projectRoot\error-injector"

$JAR_PATH = "target\simple-error-injector-1.0.0.jar"

if (-not (Test-Path $JAR_PATH)) {
    Write-Host "JAR do Error Injector nao encontrado. Compilando..." -ForegroundColor Yellow
    & "$PSScriptRoot\compilar.ps1" -errorInjector
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "       ERROR INJECTOR (Proxy TCP)      " -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta
Write-Host ""
Write-Host "Configuracao padrao:" -ForegroundColor Cyan
Write-Host "  Porta Proxy:    $proxyPort" -ForegroundColor White
Write-Host "  Servidor:       $serverHost`:$serverPort" -ForegroundColor White
Write-Host ""
Write-Host "IMPORTANTE:" -ForegroundColor Yellow
Write-Host "  1. Inicie o SERVIDOR primeiro (porta $serverPort)" -ForegroundColor White
Write-Host "  2. Configure o proxy na GUI" -ForegroundColor White
Write-Host "  3. Conecte o CLIENTE na porta do proxy ($proxyPort)" -ForegroundColor White
Write-Host ""

Write-Host "Iniciando Error Injector..." -ForegroundColor Magenta
& java -jar $JAR_PATH

Pop-Location
