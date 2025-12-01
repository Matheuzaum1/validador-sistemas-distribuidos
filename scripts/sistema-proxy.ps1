param(
    [int]$serverPort = 20000,
    [int]$proxyPort = 9999,
    [switch]$rebuild = $false
)
$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"
$INJECTOR_JAR_PATH = "error-injector\target\simple-error-injector-1.0.0.jar"

Write-Host ""
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "   SISTEMA COM PROXY (Error Injector)  " -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta
Write-Host ""
Write-Host "Arquitetura:" -ForegroundColor Cyan
Write-Host "  Cliente --> Proxy ($proxyPort) --> Servidor ($serverPort)" -ForegroundColor White
Write-Host ""

# Compilar projeto principal se necessário
if ($rebuild -or -not (Test-Path "$projectRoot\$JAR_PATH")) {
    Write-Host "Compilando projeto principal..." -ForegroundColor Yellow
    & "$PSScriptRoot\compilar.ps1"
}

# Compilar error injector se necessário
if ($rebuild -or -not (Test-Path "$projectRoot\$INJECTOR_JAR_PATH")) {
    Write-Host "Compilando Error Injector..." -ForegroundColor Yellow
    & "$PSScriptRoot\compilar.ps1" -errorInjector
}

Write-Host ""

# 1. Iniciar Servidor
Write-Host "[1/3] Abrindo SERVIDOR (porta $serverPort)..." -ForegroundColor Cyan
$serverScript = @"
`$host.UI.RawUI.WindowTitle = 'SERVIDOR - Porta $serverPort'
Write-Host '========================================' -ForegroundColor Green
Write-Host '            SERVIDOR                   ' -ForegroundColor Green
Write-Host '========================================' -ForegroundColor Green
Write-Host ''
& '$PSScriptRoot\servidor.ps1' -port $serverPort
"@
Start-Process powershell -ArgumentList "-NoExit", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", $serverScript

Write-Host "Aguardando servidor iniciar (3s)..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

# 2. Iniciar Error Injector (Proxy)
Write-Host "[2/3] Abrindo ERROR INJECTOR (proxy $proxyPort --> $serverPort)..." -ForegroundColor Magenta
$proxyScript = @"
`$host.UI.RawUI.WindowTitle = 'ERROR INJECTOR - Proxy $proxyPort'
Write-Host '========================================' -ForegroundColor Magenta
Write-Host '         ERROR INJECTOR (Proxy)        ' -ForegroundColor Magenta
Write-Host '========================================' -ForegroundColor Magenta
Write-Host ''
Write-Host 'Configure na GUI:' -ForegroundColor Yellow
Write-Host '  Porta Proxy: $proxyPort' -ForegroundColor White
Write-Host '  Host Servidor: localhost' -ForegroundColor White
Write-Host '  Porta Servidor: $serverPort' -ForegroundColor White
Write-Host ''
cd '$projectRoot\error-injector'
java -jar target\simple-error-injector-1.0.0.jar
"@
Start-Process powershell -ArgumentList "-NoExit", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", $proxyScript

Write-Host "Aguardando proxy iniciar (2s)..." -ForegroundColor Yellow
Start-Sleep -Seconds 2

# 3. Iniciar Cliente
Write-Host "[3/3] Abrindo CLIENTE (conectando no proxy $proxyPort)..." -ForegroundColor Cyan
$clientScript = @"
`$host.UI.RawUI.WindowTitle = 'CLIENTE - via Proxy $proxyPort'
Write-Host '========================================' -ForegroundColor Cyan
Write-Host '              CLIENTE                  ' -ForegroundColor Cyan
Write-Host '========================================' -ForegroundColor Cyan
Write-Host ''
Write-Host 'Conectando via PROXY na porta $proxyPort' -ForegroundColor Yellow
Write-Host '(O proxy redireciona para o servidor na porta $serverPort)' -ForegroundColor White
Write-Host ''
& '$PSScriptRoot\cliente.ps1' -serverHost localhost -port $proxyPort
"@
Start-Process powershell -ArgumentList "-NoExit", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", $clientScript

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "         SISTEMA INICIADO!             " -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Janelas abertas:" -ForegroundColor White
Write-Host "  [1] Servidor (porta $serverPort)" -ForegroundColor Green
Write-Host "  [2] Error Injector (proxy $proxyPort --> $serverPort)" -ForegroundColor Magenta
Write-Host "  [3] Cliente (conectado no proxy $proxyPort)" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para testar:" -ForegroundColor Yellow
Write-Host "  1. No Error Injector, clique 'INICIAR PROXY'" -ForegroundColor White
Write-Host "  2. Marque os checkboxes para injetar erros" -ForegroundColor White
Write-Host "  3. Use o cliente normalmente" -ForegroundColor White
Write-Host ""
