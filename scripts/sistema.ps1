param([int]$port = 8080, [switch]$rebuild = $false)
$ErrorActionPreference = "Stop"
$JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"
Write-Host "Iniciando SISTEMA DISTRIBUIDO..." -ForegroundColor Cyan
if ($rebuild -or -not (Test-Path $JAR_PATH)) {
    Write-Host "Compilando projeto..." -ForegroundColor Yellow
    & "$PSScriptRoot\compilar.ps1"
}
Write-Host ""
Write-Host "Abrindo servidor em nova janela..." -ForegroundColor Cyan
$serverScript = @"
Write-Host "SERVIDOR - Porta $port" -ForegroundColor Green
& '$PSScriptRoot\servidor.ps1' -port $port
"@
Start-Process powershell -ArgumentList "-NoExit", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", $serverScript
Write-Host "Aguardando servidor iniciar..." -ForegroundColor Yellow
Start-Sleep -Seconds 3
Write-Host "Abrindo cliente em nova janela..." -ForegroundColor Cyan
$clientScript = @"
Write-Host "CLIENTE - Conectando a localhost`:$port" -ForegroundColor Green
& '$PSScriptRoot\cliente.ps1' -host localhost -port $port
"@
Start-Process powershell -ArgumentList "-NoExit", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", $clientScript
Write-Host ""
Write-Host "Sistema iniciado! Verifique as janelas abertas." -ForegroundColor Green
