# NewPix Banking System - Script de Gerenciamento Único
# Versão: 2.0
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

# Configurações
$PROJECT_ROOT = Split-Path $MyInvocation.MyCommand.Path
$TARGET_DIR = Join-Path $PROJECT_ROOT "target\classes"
$M2_REPO = Join-Path $env:USERPROFILE ".m2\repository"

# Dependências (versões atuais)
$DEPENDENCIES = @(
    "com\fasterxml\jackson\core\jackson-databind\2.19.2\jackson-databind-2.19.2.jar",
    "com\fasterxml\jackson\core\jackson-core\2.19.2\jackson-core-2.19.2.jar", 
    "com\fasterxml\jackson\core\jackson-annotations\2.19.2\jackson-annotations-2.19.2.jar",
    "org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar",
    "org\xerial\sqlite-jdbc\3.43.0.0\sqlite-jdbc-3.43.0.0.jar"
)

# Função para exibir cabeçalho
function Show-Header {
    Clear-Host
    Write-Host "╔══════════════════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
    Write-Host "║                      🏦 NEWPIX BANKING SYSTEM 🏦                           ║" -ForegroundColor Cyan
    Write-Host "║                        Sistema Bancário Distribuído                         ║" -ForegroundColor Cyan
    Write-Host "╠══════════════════════════════════════════════════════════════════════════════╣" -ForegroundColor Cyan
    Write-Host "║  Versão: 2.0 | CPF Fix Implementado | Janelas Separadas | Script Único     ║" -ForegroundColor Green
    Write-Host "╚══════════════════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
    Write-Host ""
}

# Função para construir o classpath
function Get-ClassPath {
    $classpath = $TARGET_DIR
    foreach ($dep in $DEPENDENCIES) {
        $depPath = Join-Path $M2_REPO $dep
        if (Test-Path $depPath) {
            $classpath += ";$depPath"
        } else {
            Write-Warning "Dependência não encontrada: $depPath"
        }
    }
    return $classpath
}

# Função para verificar se o Maven está disponível
function Test-Maven {
    try {
        $null = Get-Command mvn -ErrorAction Stop
        return $true
    } catch {
        Write-Error "Maven não encontrado no PATH. Instale o Maven primeiro."
        return $false
    }
}

# Função para verificar se o projeto foi compilado
function Test-Build {
    if (!(Test-Path $TARGET_DIR)) {
        Write-Warning "Projeto não compilado. Executando build..."
        Invoke-Build
    }
}

# Função para compilar o projeto
function Invoke-Build {
    Write-Host "🔨 Compilando projeto..." -ForegroundColor Yellow
    
    if (!(Test-Maven)) { return }
    
    try {
        & mvn clean compile -q
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Projeto compilado com sucesso!" -ForegroundColor Green
        } else {
            Write-Error "❌ Falha na compilação!"
            exit 1
        }
    } catch {
        Write-Error "❌ Erro durante a compilação: $_"
        exit 1
    }
}

# Função para instalar dependências
function Install-Dependencies {
    Write-Host "📦 Instalando dependências..." -ForegroundColor Yellow
    
    if (!(Test-Maven)) { return }
    
    try {
        & mvn dependency:resolve -q
        Write-Host "✅ Dependências instaladas com sucesso!" -ForegroundColor Green
    } catch {
        Write-Error "❌ Erro ao instalar dependências: $_"
    }
}

# Função para verificar status dos serviços
function Get-Status {
    Write-Host "📊 Status do Sistema NewPix" -ForegroundColor Cyan
    Write-Host "═══════════════════════════════════" -ForegroundColor Cyan
    
    # Verificar processos Java
    $javaProcesses = Get-Process java -ErrorAction SilentlyContinue
    if ($javaProcesses) {
        Write-Host "☕ Processos Java:" -ForegroundColor Yellow
        $javaProcesses | ForEach-Object {
            Write-Host "   - PID: $($_.Id) | Memória: $([math]::Round($_.WorkingSet64/1MB, 2)) MB" -ForegroundColor White
        }
    } else {
        Write-Host "☕ Nenhum processo Java rodando" -ForegroundColor Gray
    }
    
    # Verificar porta 8080
    $port8080 = netstat -an | Select-String ":8080.*LISTENING"
    if ($port8080) {
        Write-Host "🌐 Servidor: ✅ Rodando na porta 8080" -ForegroundColor Green
    } else {
        Write-Host "🌐 Servidor: ❌ Não está rodando na porta 8080" -ForegroundColor Red
    }
    
    # Verificar arquivos de build
    if (Test-Path $TARGET_DIR) {
        Write-Host "🔨 Build: ✅ Projeto compilado" -ForegroundColor Green
    } else {
        Write-Host "🔨 Build: ❌ Projeto não compilado" -ForegroundColor Red
    }
    
    # Verificar banco de dados
    $dbFile = Join-Path $PROJECT_ROOT "newpix.db"
    if (Test-Path $dbFile) {
        $dbSize = [math]::Round((Get-Item $dbFile).Length / 1KB, 2)
        Write-Host "🗄️  Database: ✅ newpix.db ($dbSize KB)" -ForegroundColor Green
    } else {
        Write-Host "🗄️  Database: ❌ Arquivo não encontrado" -ForegroundColor Red
    }
    
    Write-Host ""
}

# Função para parar todos os serviços
function Stop-Services {
    Write-Host "🛑 Parando serviços NewPix..." -ForegroundColor Yellow
    
    $javaProcesses = Get-Process java -ErrorAction SilentlyContinue
    if ($javaProcesses) {
        $javaProcesses | ForEach-Object {
            Write-Host "   Parando processo Java (PID: $($_.Id))" -ForegroundColor Gray
            Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
        }
        Start-Sleep 2
        Write-Host "✅ Serviços parados!" -ForegroundColor Green
    } else {
        Write-Host "ℹ️  Nenhum serviço rodando" -ForegroundColor Gray
    }
}

# Função para iniciar o servidor
function Start-Server {
    param([int]$ServerPort = 8080)
    
    Write-Host "🚀 Iniciando Servidor NewPix na porta $ServerPort..." -ForegroundColor Yellow
    
    Test-Build
    $classpath = Get-ClassPath
    
    if ($Background) {
        Write-Host "   Modo: Background" -ForegroundColor Gray
        Start-Process java -ArgumentList "-cp", $classpath, "com.newpix.server.NewPixServer", $ServerPort -WindowStyle Hidden
        Start-Sleep 3
        $port = netstat -an | Select-String ":$ServerPort.*LISTENING"
        if ($port) {
            Write-Host "✅ Servidor iniciado com sucesso!" -ForegroundColor Green
        } else {
            Write-Host "❌ Falha ao iniciar servidor" -ForegroundColor Red
        }
    } else {
        Write-Host "   Modo: Foreground (Ctrl+C para parar)" -ForegroundColor Gray
        Write-Host ""
        & java -cp $classpath com.newpix.server.NewPixServer $ServerPort
    }
}

# Função para iniciar janela de login
function Start-LoginWindow {
    Write-Host "🔐 Iniciando Janela de Login..." -ForegroundColor Yellow
    
    Test-Build
    $classpath = Get-ClassPath
    
    Start-Process java -ArgumentList "-cp", $classpath, "com.newpix.client.gui.LoginWindow" -WindowStyle Normal
    Write-Host "✅ Janela de login iniciada!" -ForegroundColor Green
}

# Função para iniciar janela de cadastro
function Start-SignupWindow {
    Write-Host "📝 Iniciando Janela de Cadastro..." -ForegroundColor Yellow
    
    Test-Build
    $classpath = Get-ClassPath
    
    Start-Process java -ArgumentList "-cp", $classpath, "com.newpix.client.gui.CadastroWindow" -WindowStyle Normal
    Write-Host "✅ Janela de cadastro iniciada!" -ForegroundColor Green
}

# Função para executar testes
function Invoke-Tests {
    Write-Host "🧪 Executando testes..." -ForegroundColor Yellow
    
    if (!(Test-Maven)) { return }
    
    try {
        & mvn test
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Todos os testes passaram!" -ForegroundColor Green
        } else {
            Write-Host "❌ Alguns testes falharam!" -ForegroundColor Red
        }
    } catch {
        Write-Error "❌ Erro durante os testes: $_"
    }
}

# Função para limpeza
function Invoke-Clean {
    Write-Host "🧹 Limpando projeto..." -ForegroundColor Yellow
    
    if (!(Test-Maven)) { return }
    
    try {
        & mvn clean -q
        Write-Host "✅ Projeto limpo!" -ForegroundColor Green
    } catch {
        Write-Error "❌ Erro durante limpeza: $_"
    }
}

# Menu principal
function Show-Menu {
    Show-Header
    Get-Status
    
    Write-Host "🎛️  MENU PRINCIPAL" -ForegroundColor Cyan
    Write-Host "═══════════════════" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "📋 GERENCIAMENTO:" -ForegroundColor Yellow
    Write-Host "   1. 🚀 Iniciar Servidor            (server)"
    Write-Host "   2. 🛑 Parar Todos Serviços        (stop)"
    Write-Host "   3. 📊 Status do Sistema           (status)"
    Write-Host ""
    Write-Host "🖥️  INTERFACE:" -ForegroundColor Yellow  
    Write-Host "   4. 🔐 Janela de Login             (login)"
    Write-Host "   5. 📝 Janela de Cadastro          (signup)"
    Write-Host ""
    Write-Host "🔧 DESENVOLVIMENTO:" -ForegroundColor Yellow
    Write-Host "   6. 🔨 Compilar Projeto            (build)"
    Write-Host "   7. 📦 Instalar Dependências       (install)"
    Write-Host "   8. 🧪 Executar Testes             (test)"
    Write-Host "   9. 🧹 Limpar Projeto              (clean)"
    Write-Host ""
    Write-Host "   0. ❌ Sair                        (Ctrl+C)"
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
            "0" { Write-Host "👋 Até logo!" -ForegroundColor Green; exit 0 }
            default { 
                Write-Host "❌ Opção inválida! Digite um número de 1-9 ou 0." -ForegroundColor Red 
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

# Função de ajuda
function Show-Help {
    Show-Header
    Write-Host "📖 AJUDA - NewPix Banking System" -ForegroundColor Cyan
    Write-Host "═══════════════════════════════════" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "SINTAXE:" -ForegroundColor Yellow
    Write-Host "   .\newpix.ps1 [AÇÃO] [OPÇÕES]" -ForegroundColor White
    Write-Host ""
    Write-Host "AÇÕES DISPONÍVEIS:" -ForegroundColor Yellow
    Write-Host "   menu     - Exibe menu interativo (padrão)"
    Write-Host "   server   - Inicia o servidor"
    Write-Host "   login    - Abre janela de login"
    Write-Host "   signup   - Abre janela de cadastro"  
    Write-Host "   stop     - Para todos os serviços"
    Write-Host "   status   - Mostra status do sistema"
    Write-Host "   build    - Compila o projeto"
    Write-Host "   install  - Instala dependências"
    Write-Host "   test     - Executa testes"
    Write-Host "   clean    - Limpa o projeto"
    Write-Host "   help     - Exibe esta ajuda"
    Write-Host ""
    Write-Host "PARÂMETROS:" -ForegroundColor Yellow
    Write-Host "   -Port N      - Porta do servidor (padrão: 8080)"
    Write-Host "   -Background  - Executa servidor em background"
    Write-Host "   -Force       - Força operação"
    Write-Host ""
    Write-Host "EXEMPLOS:" -ForegroundColor Yellow
    Write-Host "   .\newpix.ps1                    # Menu interativo"
    Write-Host "   .\newpix.ps1 server             # Inicia servidor (porta 8080)"
    Write-Host "   .\newpix.ps1 server -Port 9090  # Servidor na porta 9090"
    Write-Host "   .\newpix.ps1 server -Background # Servidor em background"
    Write-Host "   .\newpix.ps1 login              # Abre janela de login"
    Write-Host "   .\newpix.ps1 stop               # Para todos os serviços"
    Write-Host ""
    Write-Host "💡 DICAS:" -ForegroundColor Cyan
    Write-Host "   • Execute 'build' antes de usar pela primeira vez"
    Write-Host "   • Use 'status' para verificar o estado do sistema"
    Write-Host "   • O CPF aceita formatos: 000.000.000-00 ou 00000000000"
    Write-Host "   • Dados de teste: CPF=100.181.699-45, Senha=123456"
    Write-Host ""
}

# Execução principal
try {
    Set-Location $PROJECT_ROOT
    
    switch ($Action.ToLower()) {
        "menu"    { Show-Menu }
        "server"  { Start-Server -ServerPort $Port }
        "login"   { Start-LoginWindow }
        "signup"  { Start-SignupWindow }
        "client"  { Start-LoginWindow }  # Alias para login
        "stop"    { Stop-Services }
        "status"  { Get-Status }
        "build"   { Invoke-Build }
        "install" { Install-Dependencies }
        "test"    { Invoke-Tests }
        "clean"   { Invoke-Clean }
        "help"    { Show-Help }
        default   { 
            Write-Host "❌ Ação desconhecida: $Action" -ForegroundColor Red
            Write-Host "Use '.\newpix.ps1 help' para ver as opções disponíveis." -ForegroundColor Yellow
        }
    }
} catch {
    Write-Error "❌ Erro inesperado: $_"
    exit 1
}
