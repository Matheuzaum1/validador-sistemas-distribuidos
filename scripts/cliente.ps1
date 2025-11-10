param([string]$host = "localhost", [int]$port = 8080)
$ErrorActionPreference = "Stop"
$JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"
if (-not (Test-Path $JAR_PATH)) {
    Write-Host "JAR nao encontrado. Compilando..." -ForegroundColor Yellow
    & "$PSScriptRoot\compilar.ps1"
}
Write-Host "Iniciando cliente para conectar em $host`:$port..." -ForegroundColor Cyan
java -Dserver.host=$host -Dserver.port=$port -jar $JAR_PATH
