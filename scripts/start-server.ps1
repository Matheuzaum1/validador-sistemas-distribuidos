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
$compileResult = mvn clean compile -q -s .m2/settings.xml
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO na compilação!" -ForegroundColor Red
    Write-Host "Execute primeiro: .\scripts\setup-inicial.ps1" -ForegroundColor Yellow
    Write-Host "Ou tente: mvn clean compile -s .m2/settings.xml" -ForegroundColor Yellow
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host "Projeto compilado com sucesso!" -ForegroundColor Green
Write-Host ""

Write-Host "Iniciando servidor na porta 8080..." -ForegroundColor Yellow
Write-Host "Para parar o servidor, pressione Ctrl+C" -ForegroundColor Gray
Write-Host ""

# FORÇAR ABERTURA DO GUI DO SERVIDOR
Write-Host "Iniciando interface gráfica do servidor..." -ForegroundColor Green

# Método direto com GUI - OBRIGATÓRIO aparecer a interface
$classpath = "target/classes"
    $dependencies = @(
        "$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.19.2/jackson-databind-2.19.2.jar",
        "$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.19.2/jackson-core-2.19.2.jar", 
        "$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.19.2/jackson-annotations-2.19.2.jar",
        "$env:USERPROFILE/.m2/repository/org/xerial/sqlite-jdbc/3.43.0.0/sqlite-jdbc-3.43.0.0.jar",
        "$env:USERPROFILE/.m2/repository/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar"
    )

$fullClasspath = $classpath + ";" + ($dependencies -join ";")

Write-Host "Abrindo GUI do Servidor..." -ForegroundColor Green
# SEMPRE usar ServerGUI para garantir interface visual
java "-Djava.awt.headless=false" -cp "$fullClasspath" com.newpix.server.gui.ServerGUI

Read-Host "Pressione Enter para sair"
