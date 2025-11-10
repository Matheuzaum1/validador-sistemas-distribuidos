# ============================================================================
# Script: sistema.ps1
# Descrição: Inicia o sistema completo (servidor + cliente em janelas separadas)
# Uso: .\scripts\sistema.ps1 [-port 8080] [-rebuild]
# ============================================================================

param(
    [int]$port = 8080,
    [switch]$rebuild = $false
)

$ErrorActionPreference = "Stop"
$JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"

# ============================================================================
# FUNÇÕES
# ============================================================================

function Show-Banner {
    Write-Host ""
    Write-Host "================================================================================================" -ForegroundColor Cyan
    Write-Host "  ███████╗██╗███████╗████████╗███████╗███╗   ███╗ █████╗      ██████╗ ██╗███████╗████████╗██████╗ ██╗██████╗ " -ForegroundColor Cyan
    Write-Host "  ██╔════╝██║██╔════╝╚══██╔══╝██╔════╝████╗ ████║██╔══██╗    ██╔════╝ ██║██╔════╝╚══██╔══╝██╔══██╗██║██╔══██╗" -ForegroundColor Cyan
    Write-Host "  ███████╗██║███████╗   ██║   █████╗  ██╔████╔██║███████║    ██║  ███╗██║███████╗   ██║   ██████╔╝██║██║  ██║" -ForegroundColor Cyan
    Write-Host "  ╚════██║██║╚════██║   ██║   ██╔══╝  ██║╚██╔╝██║██╔══██║    ██║   ██║██║╚════██║   ██║   ██╔══██╗██║██║  ██║" -ForegroundColor Cyan
    Write-Host "  ███████║██║███████║   ██║   ███████╗██║ ╚═╝ ██║██║  ██║    ╚██████╔╝██║███████║   ██║   ██║  ██║██║██████╔╝" -ForegroundColor Cyan
    Write-Host "  ╚══════╝╚═╝╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚═╝╚═╝  ╚═╝     ╚═════╝ ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝╚═════╝ " -ForegroundColor Cyan
    Write-Host ""
    Write-Host "================================================================================================" -ForegroundColor Cyan
    Write-Host "  Sistema Distribuido - INICIALIZAÇÃO COMPLETA" -ForegroundColor Cyan
    Write-Host "================================================================================================" -ForegroundColor Cyan
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
    } elseif ($rebuild) {
        Show-Info "Recompilando projeto..."
        Write-Host ""
        & .\scripts\compilar.ps1 -clean -test:$false
        if ($LASTEXITCODE -ne 0) {
            Show-Error "Falha na recompilação"
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

function Start-ServerWindow {
    param([int]$port)
    
    Show-Info "Iniciando servidor em nova janela..."
    $serverScript = @"
`$host.UI.RawUI.WindowTitle = "SERVIDOR - Sistema Distribuído [Porta $port]"
Write-Host "Executando servidor na porta $port...`n" -ForegroundColor Green
& '.\scripts\servidor.ps1' -port $port
"@
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $serverScript
    Show-Success "Janela do servidor iniciada"
}

function Start-ClientWindow {
    param(
        [string]$host,
        [int]$port
    )
    
    Show-Info "Iniciando cliente em nova janela..."
    $clientScript = @"
`$host.UI.RawUI.WindowTitle = "CLIENTE - Sistema Distribuído [Conectando a $host`:$port]"
Write-Host "Executando cliente...`n" -ForegroundColor Green
& '.\scripts\cliente.ps1' -host $host -port $port
"@
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $clientScript
    Show-Success "Janela do cliente iniciada"
}

# ============================================================================
# EXECUÇÃO
# ============================================================================

Show-Banner

# Validar pré-requisitos
Show-Info "Verificando pré-requisitos..."
Ensure-JAR

# Verificar porta
Write-Host ""
Show-Info "Verificando disponibilidade da porta $port..."
if (-not (Check-PortAvailable $port)) {
    $response = Read-Host "Deseja tentar em outra porta? (S/N)"
    if ($response -eq "S" -or $response -eq "s") {
        $port = Read-Host "Digite a nova porta"
    } else {
        Show-Error "Não é possível continuar"
        exit 1
    }
}

# Iniciar servidor
Write-Host ""
Write-Host "================================================================================================" -ForegroundColor Cyan
Show-Info "Iniciando servidor..."
Start-ServerWindow $port

# Aguardar servidor inicializar
Write-Host ""
Show-Info "Aguardando servidor inicializar... (3 segundos)"
Start-Sleep -Seconds 3

# Iniciar cliente
Show-Info "Iniciando cliente..."
Start-ClientWindow "localhost" $port

# Resumo
Write-Host ""
Write-Host "================================================================================================" -ForegroundColor Green
Write-Host "  [✓] SISTEMA INICIADO COM SUCESSO!" -ForegroundColor Green
Write-Host "================================================================================================" -ForegroundColor Green
Write-Host ""
Write-Host "  📡 Servidor: Janela 'SERVIDOR - Sistema Distribuído'" -ForegroundColor Cyan
Write-Host "  💻 Cliente:  Janela 'CLIENTE - Sistema Distribuído'" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Para encerrar, feche as janelas correspondentes" -ForegroundColor Yellow
Write-Host ""
Write-Host "================================================================================================" -ForegroundColor Cyan
Write-Host ""

Read-Host "Pressione Enter para fechar esta janela"
