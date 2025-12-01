param(
    [switch]$completa = $false,
    [switch]$rebuild = $false
)
$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot

Write-Host "Limpando projeto..." -ForegroundColor Cyan

if ($completa) {
    Write-Host "Limpeza COMPLETA..." -ForegroundColor Yellow
    
    # Limpar projeto principal
    Push-Location $projectRoot
    if (Test-Path "target") { Remove-Item -Recurse -Force "target" }
    if (Test-Path "logs") { Remove-Item -Recurse -Force "logs" }
    mvn clean | Out-Null
    Pop-Location
    
    # Limpar error-injector
    Push-Location "$projectRoot\error-injector"
    if (Test-Path "target") { Remove-Item -Recurse -Force "target" }
    mvn clean | Out-Null
    Pop-Location
    
    Write-Host "Projeto principal e Error Injector limpos!" -ForegroundColor Green
} else {
    Write-Host "Limpeza simples..." -ForegroundColor Yellow
    Push-Location $projectRoot
    mvn clean | Out-Null
    Pop-Location
}

Write-Host "Limpeza concluida!" -ForegroundColor Green

if ($rebuild) {
    Write-Host ""
    Write-Host "Recompilando projeto principal..." -ForegroundColor Cyan
    & "$PSScriptRoot\compilar.ps1"
    Write-Host ""
    Write-Host "Recompilando Error Injector..." -ForegroundColor Magenta
    & "$PSScriptRoot\compilar.ps1" -errorInjector
}

Write-Host ""
Write-Host "Operacao concluida!" -ForegroundColor Green
