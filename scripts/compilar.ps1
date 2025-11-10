# ============================================================================
# Script: compilar.ps1
# Descrição: Compila o projeto Maven e gera o JAR executável
# Uso: .\scripts\compilar.ps1 [-clean] [-test]
# ============================================================================

param(
    [switch]$clean = $true,
    [switch]$test = $false
)

$ErrorActionPreference = "Stop"
$JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"

# ============================================================================
# FUNÇÕES
# ============================================================================

function Show-Banner {
    Write-Host ""
    Write-Host "===============================================" -ForegroundColor Cyan
    Write-Host "  Sistema Distribuido - COMPILACAO" -ForegroundColor Cyan
    Write-Host "===============================================" -ForegroundColor Cyan
    Write-Host ""
}

function Show-Error {
    param([string]$message)
    Write-Host ""
    Write-Host "[✗] ERRO: $message" -ForegroundColor Red
    Write-Host ""
}

function Show-Success {
    param([string]$message)
    Write-Host "[✓] $message" -ForegroundColor Green
}

function Show-Info {
    param([string]$message)
    Write-Host "[*] $message" -ForegroundColor Cyan
}

function Verify-MavenInstalled {
    try {
        $null = mvn --version 2>&1
        Show-Success "Maven encontrado"
    }
    catch {
        Show-Error "Maven não está instalado ou não está no PATH"
        exit 1
    }
}

# ============================================================================
# EXECUÇÃO
# ============================================================================

Show-Banner

# Verificar Maven
Show-Info "Verificando pré-requisitos..."
Verify-MavenInstalled

# Limpar build anterior
if ($clean) {
    Write-Host ""
    Show-Info "Limpando builds anteriores..."
    & mvn clean -q
    if ($LASTEXITCODE -ne 0) {
        Show-Error "Falha ao limpar projeto"
        exit 1
    }
    Show-Success "Limpeza concluída"
}

# Compilar
Write-Host ""
Show-Info "Compilando projeto..."
$buildArgs = @("package", "-DskipTests")
if (-not $test) {
    $buildArgs += "-DskipTests"
}

& mvn $buildArgs
if ($LASTEXITCODE -ne 0) {
    Show-Error "Falha na compilação"
    Write-Host ""
    Read-Host "Pressione Enter para sair"
    exit 1
}

# Verificar JAR
if (Test-Path $JAR_PATH) {
    $jarSize = (Get-Item $JAR_PATH).Length / 1MB
    Write-Host ""
    Write-Host "===============================================" -ForegroundColor Green
    Show-Success "Compilação concluída com sucesso!"
    Write-Host "===============================================" -ForegroundColor Green
    Write-Host ""
    Show-Info "Arquivo gerado:"
    Write-Host "  📦 $JAR_PATH" -ForegroundColor White
    Write-Host "  📊 Tamanho: $([Math]::Round($jarSize, 2)) MB" -ForegroundColor White
    Write-Host ""
    Show-Info "Próximos passos:"
    Write-Host "  • Servidor: .\scripts\servidor.ps1" -ForegroundColor White
    Write-Host "  • Cliente:  .\scripts\cliente.ps1" -ForegroundColor White
    Write-Host "  • Sistema:  .\scripts\sistema.ps1" -ForegroundColor White
    Write-Host ""
} else {
    Show-Error "JAR não foi gerado"
    exit 1
}

if (-not $test) {
    Read-Host "Pressione Enter para sair"
}
