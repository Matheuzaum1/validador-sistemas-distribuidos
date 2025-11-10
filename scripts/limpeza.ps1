param([switch]$completa = $false, [switch]$rebuild = $false)
$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
Write-Host "Limpando projeto..." -ForegroundColor Cyan
if ($completa) {
    Write-Host "Limpeza COMPLETA..." -ForegroundColor Yellow
    if (Test-Path "target") { Remove-Item -Recurse -Force "target" }
    if (Test-Path "logs") { Remove-Item -Recurse -Force "logs" }
    mvn clean | Out-Null
} else {
    Write-Host "Limpeza simples..." -ForegroundColor Yellow
    mvn clean | Out-Null
}
Write-Host "Limpeza concluida!" -ForegroundColor Green
if ($rebuild) {
    Write-Host ""
    Write-Host "Recompilando projeto..." -ForegroundColor Cyan
    & "$PSScriptRoot\compilar.ps1"
}
Write-Host ""
Write-Host "Operacao concluida!" -ForegroundColor Green
Pop-Location
