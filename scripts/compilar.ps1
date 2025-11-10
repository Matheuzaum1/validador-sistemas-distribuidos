param([switch]$test = $false)
$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
$JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"
Write-Host "Compilando projeto..." -ForegroundColor Cyan
mvn clean package -DskipTests
if (Test-Path $JAR_PATH) {
    $size = (Get-Item $JAR_PATH).Length / 1MB
    Write-Host "Sucesso! JAR: $($size.ToString('N2')) MB" -ForegroundColor Green
} else {
    Write-Host "ERRO: JAR nao gerado" -ForegroundColor Red
    Pop-Location
    exit 1
}
Pop-Location
