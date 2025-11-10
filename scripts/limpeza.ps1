# ============================================================================
# Script: limpeza.ps1
# Descrição: Limpa builds e cache do projeto
# Uso: .\scripts\limpeza.ps1 [-completa] [-rebuild]
# ============================================================================

param(
    [switch]$completa = $false,
    [switch]$rebuild = $false
)

$ErrorActionPreference = "Stop"

# ============================================================================
# FUNÇÕES
# ============================================================================

function Show-Banner {
    Write-Host ""
    Write-Host "===============================================" -ForegroundColor Cyan
    Write-Host "  Sistema Distribuido - LIMPEZA" -ForegroundColor Cyan
    Write-Host "===============================================" -ForegroundColor Cyan
    Write-Host ""
}

function Show-Info {
    param([string]$message)
    Write-Host "[*] $message" -ForegroundColor Cyan
}

function Show-Success {
    param([string]$message)
    Write-Host "[✓] $message" -ForegroundColor Green
}

function Show-Error {
    param([string]$message)
    Write-Host "[✗] ERRO: $message" -ForegroundColor Red
}

function Show-Warning {
    param([string]$message)
    Write-Host "[!] $message" -ForegroundColor Yellow
}

# ============================================================================
# EXECUÇÃO
# ============================================================================

Show-Banner

# Limpeza padrão (target)
Show-Info "Removendo pasta 'target'..."
try {
    Remove-Item -Path "target" -Recurse -Force -ErrorAction SilentlyContinue
    Show-Success "Pasta 'target' removida"
}
catch {
    Show-Error "Erro ao remover 'target': $_"
}

# Limpeza completa
if ($completa) {
    Write-Host ""
    Show-Info "Realizando limpeza completa..."
    
    Show-Info "Removendo database (usuarios.db)..."
    try {
        Remove-Item -Path "usuarios.db" -Force -ErrorAction SilentlyContinue
        Show-Success "Database removido"
    }
    catch {
        Show-Error "Erro ao remover database: $_"
    }
    
    Show-Info "Removendo logs..."
    try {
        Remove-Item -Path "logs" -Recurse -Force -ErrorAction SilentlyContinue
        Show-Success "Logs removidos"
    }
    catch {
        Show-Error "Erro ao remover logs: $_"
    }
    
    Show-Info "Executando 'mvn clean'..."
    try {
        & mvn clean -q
        Show-Success "Maven clean concluído"
    }
    catch {
        Show-Error "Erro ao executar Maven clean: $_"
    }
}

# Recompilar
if ($rebuild) {
    Write-Host ""
    Show-Info "Recompilando projeto..."
    & .\scripts\compilar.ps1 -clean
}

# Resumo
Write-Host ""
Write-Host "===============================================" -ForegroundColor Green
Show-Success "Limpeza concluída!"
Write-Host "===============================================" -ForegroundColor Green
Write-Host ""

if ($rebuild) {
    Show-Info "Projeto recompilado e pronto para uso"
}
else {
    Show-Info "Você pode agora recompilar com: .\scripts\compilar.ps1"
}

Write-Host ""
