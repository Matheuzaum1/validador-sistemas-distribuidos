param([int]$port = 8080)
$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
$JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"
if (-not (Test-Path $JAR_PATH)) {
    Write-Host "JAR nao encontrado. Compilando..." -ForegroundColor Yellow
    & "$PSScriptRoot\compilar.ps1"
}
Write-Host "Iniciando servidor na porta $port..." -ForegroundColor Cyan
java -Dserver.port=$port -jar $JAR_PATH
Pop-Location
