# ============================================================================
# Script: cliente.ps1
# Descrição: Inicia o cliente GUI do sistema distribuído
# Uso: .\scripts\cliente.ps1 [-host localhost] [-port 8080]
# ============================================================================

param(
    [string]$host = "localhost",
    [int]$port = 8080
)

$ErrorActionPreference = "Stop"
$JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"
$JAVA_MAIN = "com.distribuidos.client.ClientMain"

# ============================================================================
# FUNÇÕES
# ============================================================================

function Show-Banner {
    Write-Host ""
    Write-Host "===============================================" -ForegroundColor Cyan
    Write-Host "  Sistema Distribuido - CLIENTE" -ForegroundColor Cyan
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

function Test-ServerConnection {
    param(
        [string]$serverHost,
        [int]$serverPort
    )
    
    try {
        $connection = New-Object System.Net.Sockets.TcpClient
        $connection.Connect($serverHost, $serverPort)
        $connection.Close()
        Show-Success "Servidor disponível em $serverHost`:$serverPort"
        return $true
    }
    catch {
        Show-Warning "Servidor não está disponível em $serverHost`:$serverPort"
        Show-Info "Você pode tentar conectar mesmo assim, ou inicie o servidor com:"
        Write-Host "  .\scripts\servidor.ps1" -ForegroundColor White
        return $false
    }
}

# ============================================================================
# EXECUÇÃO
# ============================================================================

Show-Banner

# Validar pré-requisitos
Show-Info "Verificando pré-requisitos..."
Ensure-JAR

# Testar conexão com servidor
Write-Host ""
Show-Info "Testando conexão com servidor..."
$serverOk = Test-ServerConnection $host $port

# Iniciar cliente
Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Show-Info "Iniciando interface gráfica do cliente..."
Show-Info "Servidor: $host`:$port"
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

try {
    & java -cp $JAR_PATH $JAVA_MAIN
}
catch {
    Show-Error "Falha ao iniciar cliente: $_"
    exit 1
}
finally {
    Show-Info "Cliente encerrado"
}
