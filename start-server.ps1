# Script PowerShell para iniciar servidor NewPix
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "      NewPix Server - Iniciando" -ForegroundColor Cyan  
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se estamos no diretório correto
if (-not (Test-Path "pom.xml")) {
    Write-Host "ERRO: Execute este script no diretório raiz do projeto!" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host "Compilando projeto..." -ForegroundColor Yellow
$compileResult = mvn clean compile -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO na compilação!" -ForegroundColor Red
    Write-Host "Tente executar: mvn clean compile" -ForegroundColor Yellow
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host "Projeto compilado com sucesso!" -ForegroundColor Green
Write-Host ""

Write-Host "Iniciando servidor na porta 8080..." -ForegroundColor Yellow
Write-Host "Para parar o servidor, pressione Ctrl+C" -ForegroundColor Gray
Write-Host ""

# Tentar diferentes métodos de execução
Write-Host "Método 1: Maven exec plugin..." -ForegroundColor Gray
mvn exec:java -Pserver

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
    java -cp "$fullClasspath" com.newpix.server.NewPixServer 8080
}

Read-Host "Pressione Enter para sair"
