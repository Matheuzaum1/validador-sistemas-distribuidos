param([string]$Action = "menu", [int]$Port = 8080)

function Show-Header {
    Clear-Host
    Write-Host "===============================================" -ForegroundColor Cyan
    Write-Host "          NEWPIX BANKING SYSTEM               " -ForegroundColor Cyan  
    Write-Host "===============================================" -ForegroundColor Cyan
    Write-Host ""
}

function Test-Java {
    try {
        $null = java -version 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Java: OK" -ForegroundColor Green
            return $true
        }
    } catch {
        Write-Host "Java: NAO ENCONTRADO" -ForegroundColor Red
        return $false
    }
    return $false
}

function Test-Maven {
    try {
        $null = mvn -version 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Maven: OK" -ForegroundColor Green
            return $true
        }
    } catch {
        Write-Host "Maven: NAO ENCONTRADO" -ForegroundColor Red
        return $false
    }
    return $false
}

function Get-Status {
    Show-Header
    Write-Host "STATUS DO SISTEMA:" -ForegroundColor Yellow
    Write-Host "==================" -ForegroundColor Yellow
    Write-Host ""
    
    Test-Java
    Test-Maven
    
    if (Test-Path "target/classes") {
        Write-Host "Build: OK" -ForegroundColor Green
    } else {
        Write-Host "Build: NECESSARIO COMPILAR" -ForegroundColor Yellow
    }
    
    $javaProcs = Get-Process java -ErrorAction SilentlyContinue
    if ($javaProcs) {
        Write-Host "Processos Java: $($javaProcs.Count) em execucao" -ForegroundColor Green
    } else {
        Write-Host "Processos Java: Nenhum em execucao" -ForegroundColor Gray
    }
    Write-Host ""
}

function Get-ClassPath {
    $currentDir = Get-Location
    $classPath = "$currentDir\target\classes"
    
    # Adicionar JARs de dependencias se existirem
    if (Test-Path "target\dependency") {
        $jars = Get-ChildItem "target\dependency" -Filter "*.jar"
        foreach ($jar in $jars) {
            $classPath += ";$($jar.FullName)"
        }
    }
    
    return $classPath
}

function Start-Build {
    Write-Host "Compilando projeto..." -ForegroundColor Yellow
    try {
        mvn clean compile dependency:copy-dependencies
        Write-Host "Compilacao concluida!" -ForegroundColor Green
    } catch {
        Write-Host "Erro na compilacao!" -ForegroundColor Red
    }
}

function Start-Clean {
    Write-Host "Limpando projeto..." -ForegroundColor Yellow
    try {
        mvn clean
        Write-Host "Projeto limpo com sucesso!" -ForegroundColor Green
        Write-Host "Execute 'build' para recompilar." -ForegroundColor Cyan
    } catch {
        Write-Host "Erro na limpeza!" -ForegroundColor Red
    }
}

function Start-ServerGUI {
    Write-Host "Iniciando GUI do Servidor..." -ForegroundColor Yellow
    $classpath = Get-ClassPath
    Start-Process java -ArgumentList "-cp", $classpath, "com.newpix.server.gui.ServerGUI" -WindowStyle Normal
    Write-Host "Servidor GUI iniciado!" -ForegroundColor Green
}

function Start-ClientGUI {
    Write-Host "Iniciando GUI do Cliente..." -ForegroundColor Yellow
    $classpath = Get-ClassPath
    Start-Process java -ArgumentList "-cp", $classpath, "com.newpix.client.gui.LoginWindow" -WindowStyle Normal
    Write-Host "Cliente GUI iniciado!" -ForegroundColor Green
}

function Start-BothGUI {
    Show-Header
    Write-Host "Iniciando sistema completo..." -ForegroundColor Cyan
    Write-Host ""
    
    if (!(Test-Path "target/classes")) {
        Write-Host "Projeto nao compilado. Compilando..." -ForegroundColor Yellow
        Start-Build
    }
    
    Write-Host "1/2 - Iniciando GUI do Servidor..." -ForegroundColor Yellow
    Start-ServerGUI
    Start-Sleep 2
    
    Write-Host "2/2 - Iniciando GUI do Cliente..." -ForegroundColor Yellow
    Start-ClientGUI
    
    Write-Host ""
    Write-Host "Sistema iniciado com sucesso!" -ForegroundColor Green
    Write-Host "Para parar: .\newpix.ps1 stop" -ForegroundColor Cyan
}

function Stop-Services {
    Write-Host "Parando servicos Java..." -ForegroundColor Yellow
    $javaProcs = Get-Process java -ErrorAction SilentlyContinue
    if ($javaProcs) {
        $javaProcs | Stop-Process -Force
        Write-Host "Servicos parados!" -ForegroundColor Green
    } else {
        Write-Host "Nenhum servico em execucao." -ForegroundColor Gray
    }
}

function Show-Menu {
    do {
        Show-Header
        Get-Status
        
        Write-Host "MENU PRINCIPAL:" -ForegroundColor Yellow
        Write-Host "===============" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "1. Sistema Completo (Recomendado)"
        Write-Host "2. Servidor GUI"
        Write-Host "3. Cliente GUI"
        Write-Host "4. Status"
        Write-Host "5. Compilar"
        Write-Host "6. Limpar Projeto"
        Write-Host "7. Parar Servicos"
        Write-Host "0. Sair"
        Write-Host ""
        
        $choice = Read-Host "Digite sua escolha (0-7)"
        
        switch ($choice) {
            "1" { Start-BothGUI; break }
            "2" { Start-ServerGUI; break }
            "3" { Start-ClientGUI; break }
            "4" { Get-Status; Read-Host "Pressione ENTER para continuar" }
            "5" { Start-Build; Read-Host "Pressione ENTER para continuar" }
            "6" { Start-Clean; Read-Host "Pressione ENTER para continuar" }
            "7" { Stop-Services; Read-Host "Pressione ENTER para continuar" }
            "0" { Write-Host "Ate logo!" -ForegroundColor Green; exit }
            default { Write-Host "Opcao invalida! Digite um numero de 0-7." -ForegroundColor Red; Start-Sleep 1 }
        }
    } while ($choice -ne "0")
}

function Show-Help {
    Show-Header
    Write-Host "AJUDA - NewPix Banking System" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "COMANDOS DISPONIVEIS:" -ForegroundColor Yellow
    Write-Host "  .\newpix.ps1 menu       - Menu interativo"
    Write-Host "  .\newpix.ps1 both-gui   - Sistema completo"
    Write-Host "  .\newpix.ps1 server-gui - Servidor GUI"
    Write-Host "  .\newpix.ps1 client-gui - Cliente GUI"
    Write-Host "  .\newpix.ps1 status     - Status do sistema"
    Write-Host "  .\newpix.ps1 build      - Compilar projeto"
    Write-Host "  .\newpix.ps1 clean      - Limpar projeto"
    Write-Host "  .\newpix.ps1 stop       - Parar servicos"
    Write-Host "  .\newpix.ps1 help       - Esta ajuda"
    Write-Host ""
}

Write-Host "Parametro recebido: $Action" -ForegroundColor Yellow

switch ($Action.ToLower()) {
    "menu"       { Show-Menu }
    "both-gui"   { Start-BothGUI }
    "server-gui" { Start-ServerGUI }
    "client-gui" { Start-ClientGUI }
    "status"     { Get-Status }
    "build"      { Start-Build }
    "clean"      { Start-Clean }
    "stop"       { Stop-Services }
    "help"       { Show-Help }
    default      { Show-Menu }
}