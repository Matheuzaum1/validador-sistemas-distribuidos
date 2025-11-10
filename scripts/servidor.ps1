# ============================================================================
# Script: servidor.ps1
# Descrição: Inicia o servidor do sistema distribuído
# Uso: .\scripts\servidor.ps1 [-port 8080] [-background]
# ============================================================================

param(
    [int]$port = 8080,
    [switch]$background = $false,
    [switch]$rebuild = $false
)

$ErrorActionPreference = "Stop"
$JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"
$JAVA_MAIN = "com.distribuidos.server.ServerMain"

# ============================================================================
# FUNÇÕES
# ============================================================================

function Show-Banner {
    Write-Host ""
    Write-Host "===============================================" -ForegroundColor Cyan
    Write-Host "  Sistema Distribuido - SERVIDOR" -ForegroundColor Cyan
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

function Ensure-JAR {
    if (-not (Test-Path $JAR_PATH)) {
        Show-Warning "JAR não encontrado"
        Show-Info "Compilando projeto..."
        Write-Host ""
        & .\scripts\compilar.ps1 -clean -test:$false
        
        if (-not (Test-Path $JAR_PATH)) {
            Show-Error "Falha ao gerar JAR"
            exit 1
        }
    }
    Show-Success "JAR disponível"
}

function Check-PortAvailable {
    param([int]$port)
    
    try {
        $connection = New-Object System.Net.Sockets.TcpClient
        $connection.Connect("127.0.0.1", $port)
        $connection.Close()
        Show-Warning "Porta $port já está em uso"
        return $false
    }
    catch {
        Show-Success "Porta $port disponível"
        return $true
    }
}

function Stop-ProcessOnPort {
    param([int]$port)
    
    try {
        $process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
        if ($process) {
            Show-Warning "Encerrando processo na porta $port..."
            Stop-Process -Id $process.OwningProcess -Force
            Start-Sleep -Seconds 1
            Show-Success "Processo encerrado"
        }
    }
    catch {
        # Porta já estava livre
    }
}

# ============================================================================
# EXECUÇÃO
# ============================================================================

Show-Banner

# Validar pré-requisitos
Show-Info "Verificando pré-requisitos..."
Ensure-JAR

# Verificar porta
if (-not (Check-PortAvailable $port)) {
    $response = Read-Host "Deseja encerrar o processo na porta $port? (S/N)"
    if ($response -eq "S" -or $response -eq "s") {
        Stop-ProcessOnPort $port
    } else {
        Show-Error "Não é possível continuar"
        exit 1
    }
}

# Iniciar servidor
Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Show-Info "Iniciando servidor na porta $port..."
Show-Info "Pressione Ctrl+C para parar o servidor"
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

try {
    & java -cp $JAR_PATH $JAVA_MAIN
}
catch {
    Show-Error "Falha ao iniciar servidor: $_"
    exit 1
}
finally {
    Show-Info "Servidor encerrado"
}
