# NewPix Banking System - Script de Gerenciamento Unico
# Versao: 2.0
# Autor: Sistema NewPix
# Data: Setembro 2025

param(
    [Parameter(Position=0)]
    [ValidateSet("menu", "server", "login", "signup", "client", "stop", "status", "build", "clean", "install", "test", "help")]
    [string]$Action = "menu",
    
    [Parameter(Position=1)]
    [int]$Port = 8080,
    
    [switch]$Background,
    [switch]$Force
)

# ============================================================================
# CONFIGURACAO AUTOMATICA DE EXECUTION POLICY
# ============================================================================
try {
    $currentPolicy = Get-ExecutionPolicy -Scope CurrentUser
    if ($currentPolicy -eq "Restricted" -or $currentPolicy -eq "Undefined") {
        Write-Host "Configurando ExecutionPolicy para permitir scripts..." -ForegroundColor Yellow
        Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force
        Write-Host "ExecutionPolicy configurado com sucesso!" -ForegroundColor Green
    }
} catch {
    Write-Warning "Nao foi possivel configurar ExecutionPolicy automaticamente."
    Write-Host "Execute manualmente: Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser" -ForegroundColor Cyan
}

# Configuracoes
$PROJECT_ROOT = Split-Path $MyInvocation.MyCommand.Path
$TARGET_DIR = Join-Path $PROJECT_ROOT "target\classes"
$M2_REPO = Join-Path $env:USERPROFILE ".m2\repository"

# Dependencias (versoes atuais)
$DEPENDENCIES = @(
    "com\fasterxml\jackson\core\jackson-databind\2.19.2\jackson-databind-2.19.2.jar",
    "com\fasterxml\jackson\core\jackson-core\2.19.2\jackson-core-2.19.2.jar", 
    "com\fasterxml\jackson\core\jackson-annotations\2.19.2\jackson-annotations-2.19.2.jar",
    "org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar",
    "org\xerial\sqlite-jdbc\3.43.0.0\sqlite-jdbc-3.43.0.0.jar"
)

# Funcao para verificar privilegios
function Test-AdminPrivileges {
    $currentUser = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($currentUser)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

# Funcao para exibir cabecalho
function Show-Header {
    Clear-Host
    Write-Host "=============================================================================" -ForegroundColor Cyan
    Write-Host "                    NEWPIX BANKING SYSTEM                                   " -ForegroundColor Cyan
    Write-Host "                   Sistema Bancario Distribuido                            " -ForegroundColor Cyan
    Write-Host "=============================================================================" -ForegroundColor Cyan
    Write-Host "  Versao: 2.0 | Java 17+ | ExecutionPolicy Auto-Config | Script Unico    " -ForegroundColor Green
    
    # Mostrar status de privilegios
    if (Test-AdminPrivileges) {
        Write-Host "  Modo: Administrador | Privilegios Elevados                             " -ForegroundColor Yellow
    } else {
        Write-Host "  Modo: Usuario Normal | Sem Necessidade de Admin                        " -ForegroundColor Green
    }
    
    Write-Host "=============================================================================" -ForegroundColor Cyan
    Write-Host ""
}

# Funcao para construir o classpath
function Get-ClassPath {
    $classpath = $TARGET_DIR
    foreach ($dep in $DEPENDENCIES) {
        $depPath = Join-Path $M2_REPO $dep
        if (Test-Path $depPath) {
            $classpath += ";$depPath"
        }
    }
    return $classpath
}

# Funcao para testar se o projeto esta compilado
function Test-Build {
    if (-not (Test-Path $TARGET_DIR)) {
        Write-Host "Projeto nao compilado. Compilando..." -ForegroundColor Yellow
        Invoke-Build
    }
}

# Funcao para compilar o projeto
function Invoke-Build {
    Write-Host "Compilando projeto..." -ForegroundColor Yellow
    $result = & mvn clean compile
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Projeto compilado com sucesso!" -ForegroundColor Green
        return $true
    } else {
        Write-Host "Falha na compilacao!" -ForegroundColor Red
        return $false
    }
}

# Funcao para instalar dependencias
function Install-Dependencies {
    Write-Host "Instalando dependencias..." -ForegroundColor Yellow
    $result = & mvn dependency:copy-dependencies
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Dependencias instaladas com sucesso!" -ForegroundColor Green
    } else {
        Write-Host "Falha na instalacao das dependencias!" -ForegroundColor Red
    }
}

# Funcao para mostrar status
function Get-Status {
    Show-Header
    Write-Host "Status do Sistema NewPix" -ForegroundColor Cyan
    Write-Host "===========================" -ForegroundColor Cyan
    
    # Verificar processos Java
    $javaProcesses = Get-Process | Where-Object { $_.Name -like "*java*" }
    if ($javaProcesses) {
        Write-Host "Processos Java:" -ForegroundColor Yellow
        foreach ($proc in $javaProcesses) {
            Write-Host "   - PID: $($proc.Id) | Memoria: $([math]::Round($proc.WorkingSet64/1MB, 2)) MB" -ForegroundColor White
        }
    } else {
        Write-Host "Nenhum processo Java rodando" -ForegroundColor Gray
    }
    
    # Verificar servidor na porta 8080
    $port8080 = netstat -an | Select-String ":8080.*LISTENING"
    if ($port8080) {
        Write-Host "Servidor: Rodando na porta 8080" -ForegroundColor Green
    } else {
        Write-Host "Servidor: Nao esta rodando na porta 8080" -ForegroundColor Red
    }
    
    # Verificar build
    if (Test-Path $TARGET_DIR) {
        Write-Host "Build: Projeto compilado" -ForegroundColor Green
    } else {
        Write-Host "Build: Projeto nao compilado" -ForegroundColor Red
    }
    
    # Verificar banco de dados
    $dbFile = Join-Path $PROJECT_ROOT "newpix.db"
    if (Test-Path $dbFile) {
        $dbSize = [math]::Round((Get-Item $dbFile).Length / 1KB, 2)
        Write-Host "Database: newpix.db ($dbSize KB)" -ForegroundColor Green
    } else {
        Write-Host "Database: Arquivo nao encontrado" -ForegroundColor Red
    }
    
    Write-Host ""
}

# Funcao para parar todos os servicos
function Stop-Services {
    Write-Host "Parando todos os servicos Java..." -ForegroundColor Yellow
    
    $javaProcesses = Get-Process | Where-Object { $_.Name -like "*java*" }
    if ($javaProcesses) {
        foreach ($proc in $javaProcesses) {
            Write-Host "Parando processo Java PID: $($proc.Id)" -ForegroundColor Gray
            Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
        }
        Write-Host "Servicos parados com sucesso!" -ForegroundColor Green
    } else {
        Write-Host "Nenhum processo Java encontrado." -ForegroundColor Gray
    }
}

# Funcao para iniciar o servidor
function Start-Server {
    param([int]$ServerPort = 8080)
    
    Write-Host "Iniciando Servidor NewPix na porta $ServerPort..." -ForegroundColor Yellow
    
    Test-Build
    $classpath = Get-ClassPath
    
    # Dica sobre firewall
    Write-Host "Dica: Se o Windows Firewall aparecer, clique em 'Permitir acesso'" -ForegroundColor Cyan
    Write-Host ""
    
    if ($Background) {
        Write-Host "   Modo: Background" -ForegroundColor Gray
        try {
            Start-Process java -ArgumentList "-cp", $classpath, "com.newpix.server.NewPixServer", $ServerPort -WindowStyle Hidden
            Start-Sleep 3
            $port = netstat -an | Select-String ":$ServerPort.*LISTENING"
            if ($port) {
                Write-Host "Servidor iniciado com sucesso!" -ForegroundColor Green
            } else {
                Write-Host "Falha ao iniciar servidor" -ForegroundColor Red
                Write-Host "Verifique se a porta $ServerPort nao esta sendo usada" -ForegroundColor Yellow
            }
        } catch {
            Write-Host "Erro ao iniciar servidor: $($_.Exception.Message)" -ForegroundColor Red
        }
    } else {
        Write-Host "   Modo: Foreground (Ctrl+C para parar)" -ForegroundColor Gray
        Write-Host ""
        try {
            & java -cp $classpath com.newpix.server.NewPixServer $ServerPort
        } catch {
            Write-Host "Erro ao executar servidor: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
}

# Funcao para iniciar janela de login
function Start-LoginWindow {
    Write-Host "Iniciando Janela de Login..." -ForegroundColor Yellow
    
    Test-Build
    $classpath = Get-ClassPath
    
    Start-Process java -ArgumentList "-cp", $classpath, "com.newpix.client.gui.LoginWindow" -WindowStyle Normal
    Write-Host "Janela de login iniciada!" -ForegroundColor Green
}

# Funcao para iniciar janela de cadastro
function Start-SignupWindow {
    Write-Host "Iniciando Janela de Cadastro..." -ForegroundColor Yellow
    
    Test-Build
    $classpath = Get-ClassPath
    
    Start-Process java -ArgumentList "-cp", $classpath, "com.newpix.client.gui.SignupWindow" -WindowStyle Normal
    Write-Host "Janela de cadastro iniciada!" -ForegroundColor Green
}

# Funcao para executar testes
function Invoke-Tests {
    Write-Host "Executando testes..." -ForegroundColor Yellow
    $result = & mvn test
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Testes executados com sucesso!" -ForegroundColor Green
    } else {
        Write-Host "Falha nos testes!" -ForegroundColor Red
    }
}

# Funcao para limpar projeto
function Invoke-Clean {
    Write-Host "Limpando projeto..." -ForegroundColor Yellow
    $result = & mvn clean
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Projeto limpo com sucesso!" -ForegroundColor Green
    } else {
        Write-Host "Falha na limpeza!" -ForegroundColor Red
    }
}

# Funcao para exibir menu
function Show-Menu {
    Show-Header
    
    Get-Status
    
    Write-Host "MENU PRINCIPAL" -ForegroundColor Cyan
    Write-Host "==============" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "GERENCIAMENTO:" -ForegroundColor Yellow
    Write-Host "   1. Iniciar Servidor            (server)"
    Write-Host "   2. Parar Todos Servicos        (stop)"
    Write-Host "   3. Status do Sistema           (status)"
    Write-Host ""
    Write-Host "INTERFACE:" -ForegroundColor Yellow
    Write-Host "   4. Janela de Login             (login)"
    Write-Host "   5. Janela de Cadastro          (signup)"
    Write-Host ""
    Write-Host "DESENVOLVIMENTO:" -ForegroundColor Yellow
    Write-Host "   6. Compilar Projeto            (build)"
    Write-Host "   7. Instalar Dependencias       (install)"
    Write-Host "   8. Executar Testes             (test)"
    Write-Host "   9. Limpar Projeto              (clean)"
    Write-Host ""
    Write-Host "   0. Sair                        (Ctrl+C)"
    Write-Host ""
    
    do {
        $choice = Read-Host "Digite sua escolha (1-9, 0 para sair)"
        
        switch ($choice) {
            "1" { Start-Server -ServerPort $Port }
            "2" { Stop-Services }
            "3" { Get-Status }
            "4" { Start-LoginWindow }
            "5" { Start-SignupWindow }
            "6" { Invoke-Build }
            "7" { Install-Dependencies }
            "8" { Invoke-Tests }
            "9" { Invoke-Clean }
            "0" { Write-Host "Ate logo!" -ForegroundColor Green; exit 0 }
            default { 
                Write-Host "Opcao invalida! Digite um numero de 1-9 ou 0." -ForegroundColor Red 
            }
        }
        
        if ($choice -ne "0") {
            Write-Host ""
            Write-Host "Pressione ENTER para continuar..." -ForegroundColor Gray
            Read-Host
            Show-Menu
        }
    } while ($choice -ne "0")
}

# Funcao de ajuda
function Show-Help {
    Show-Header
    Write-Host "AJUDA - NewPix Banking System" -ForegroundColor Cyan
    Write-Host "=============================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "SINTAXE:" -ForegroundColor Yellow
    Write-Host "   .\newpix.ps1 [ACAO] [OPCOES]" -ForegroundColor White
    Write-Host ""
    Write-Host "ACOES DISPONIVEIS:" -ForegroundColor Yellow
    Write-Host "   menu     - Exibe menu interativo (padrao)"
    Write-Host "   server   - Inicia o servidor"
    Write-Host "   login    - Abre janela de login"
    Write-Host "   signup   - Abre janela de cadastro"  
    Write-Host "   stop     - Para todos os servicos"
    Write-Host "   status   - Mostra status do sistema"
    Write-Host "   build    - Compila o projeto"
    Write-Host "   install  - Instala dependencias"
    Write-Host "   test     - Executa testes"
    Write-Host "   clean    - Limpa o projeto"
    Write-Host "   help     - Exibe esta ajuda"
    Write-Host ""
    Write-Host "PARAMETROS:" -ForegroundColor Yellow
    Write-Host "   -Port    - Porta do servidor (padrao: 8080)"
    Write-Host "   -Background - Executar servidor em background"
    Write-Host "   -Force   - Forcar operacao"
    Write-Host ""
    Write-Host "EXEMPLOS:" -ForegroundColor Yellow
    Write-Host "   .\newpix.ps1                    # Menu interativo"
    Write-Host "   .\newpix.ps1 server             # Iniciar servidor"
    Write-Host "   .\newpix.ps1 server -Port 9090  # Servidor na porta 9090"
    Write-Host "   .\newpix.ps1 server -Background # Servidor em background"
    Write-Host "   .\newpix.ps1 login              # Abrir tela de login"
    Write-Host "   .\newpix.ps1 status             # Ver status"
    Write-Host ""
}

# ============================================================================
# EXECUCAO PRINCIPAL
# ============================================================================

# Executar acao baseada no parametro
switch ($Action.ToLower()) {
    "menu"    { Show-Menu }
    "server"  { Start-Server -ServerPort $Port }
    "login"   { Start-LoginWindow }
    "signup"  { Start-SignupWindow }
    "stop"    { Stop-Services }
    "status"  { Get-Status }
    "build"   { Invoke-Build }
    "install" { Install-Dependencies }
    "test"    { Invoke-Tests }
    "clean"   { Invoke-Clean }
    "help"    { Show-Help }
    default   { Show-Menu }
}
