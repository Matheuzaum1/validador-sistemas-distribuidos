# PowerShell script para executar o cliente
Write-Host "Iniciando Cliente do Sistema Distribuido..." -ForegroundColor Green

# Configurar Java 17
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Ir para o diretório do projeto
Set-Location $PSScriptRoot

# Verificar se o projeto foi compilado
if (-not (Test-Path "target\classes")) {
    Write-Host "Compilando projeto..." -ForegroundColor Yellow
    mvn clean compile
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Erro na compilação!" -ForegroundColor Red
        Read-Host "Pressione Enter para sair"
        exit 1
    }
}

# Executar o cliente
Write-Host "Iniciando cliente..." -ForegroundColor Yellow
try {
    & java -cp "target\classes;$env:USERPROFILE\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.17.2\jackson-databind-2.17.2.jar;$env:USERPROFILE\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.17.2\jackson-annotations-2.17.2.jar;$env:USERPROFILE\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.17.2\jackson-core-2.17.2.jar;$env:USERPROFILE\.m2\repository\org\xerial\sqlite-jdbc\3.46.1.0\sqlite-jdbc-3.46.1.0.jar;$env:USERPROFILE\.m2\repository\org\slf4j\slf4j-api\2.0.16\slf4j-api-2.0.16.jar;$env:USERPROFILE\.m2\repository\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar;$env:USERPROFILE\.m2\repository\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar;$env:USERPROFILE\.m2\repository\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar;$env:USERPROFILE\.m2\repository\com\formdev\flatlaf\3.2.1\flatlaf-3.2.1.jar" com.distribuidos.client.ClientMain
} catch {
    Write-Host "Erro ao executar o cliente: $_" -ForegroundColor Red
}

Write-Host "Cliente encerrado." -ForegroundColor Yellow
Read-Host "Pressione Enter para sair"