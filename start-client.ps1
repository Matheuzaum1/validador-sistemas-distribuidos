# Script PowerShell para iniciar cliente NewPix
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "      NewPix Cliente - Iniciando" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se estamos no diretório correto
if (-not (Test-Path "pom.xml")) {
    Write-Host "ERRO: Execute este script no diretório raiz do projeto!" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

# Verificar se o projeto já foi compilado
if (-not (Test-Path "target/classes")) {
    Write-Host "Compilando projeto..." -ForegroundColor Yellow
    mvn clean compile -q
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERRO na compilação!" -ForegroundColor Red
        Read-Host "Pressione Enter para sair"
        exit 1
    }
}

Write-Host "Iniciando cliente..." -ForegroundColor Yellow
Write-Host "Conectará automaticamente em localhost:8080" -ForegroundColor Gray
Write-Host ""

# Tentar diferentes métodos de execução
Write-Host "Método 1: Maven exec plugin..." -ForegroundColor Gray
mvn exec:java -Pclient

if ($LASTEXITCODE -ne 0) {
    Write-Host "Método 1 falhou. Tentando método 2..." -ForegroundColor Yellow
    
    # Método alternativo com java direto
    $classpath = "target/classes"
    $dependencies = @(
        "$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.19.2/jackson-databind-2.19.2.jar",
        "$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.19.2/jackson-core-2.19.2.jar",
        "$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.19.2/jackson-annotations-2.19.2.jar",
        "$env:USERPROFILE/.m2/repository/org/xerial/sqlite-jdbc/3.43.0.0/sqlite-jdbc-3.43.0.0.jar",
        "$env:USERPROFILE/.m2/repository/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar"
    )
    
    $fullClasspath = $classpath + ";" + ($dependencies -join ";")
    
    Write-Host "Método 2: Java direto..." -ForegroundColor Gray
    java -cp "$fullClasspath" com.newpix.client.gui.LoginGUI
}

Read-Host "Pressione Enter para sair"
