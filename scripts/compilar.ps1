param(
    [switch]$test = $false,
    [switch]$errorInjector = $false
)
$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot

if ($errorInjector) {
    # Compilar Error Injector
    Push-Location "$projectRoot\error-injector"
    $JAR_PATH = "target\simple-error-injector-1.0.0.jar"
    Write-Host "Compilando Error Injector..." -ForegroundColor Magenta
    mvn clean package -DskipTests
    if (Test-Path $JAR_PATH) {
        $size = (Get-Item $JAR_PATH).Length / 1MB
        Write-Host "Sucesso! Error Injector JAR: $($size.ToString('N2')) MB" -ForegroundColor Green
    } else {
        Write-Host "ERRO: JAR do Error Injector nao gerado" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
} else {
    # Compilar projeto principal
    Push-Location $projectRoot
    $JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"
    Write-Host "Compilando projeto principal..." -ForegroundColor Cyan
    if ($test) {
        mvn clean package
    } else {
        mvn clean package -DskipTests
    }
    if (Test-Path $JAR_PATH) {
        $size = (Get-Item $JAR_PATH).Length / 1MB
        Write-Host "Sucesso! JAR: $($size.ToString('N2')) MB" -ForegroundColor Green
    } else {
        Write-Host "ERRO: JAR nao gerado" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
}
