# Script de Diagnóstico para Problemas com Dependências
Write-Host "======================================" -ForegroundColor Red
Write-Host "   Diagnóstico de Dependências" -ForegroundColor Red  
Write-Host "======================================" -ForegroundColor Red
Write-Host ""

function Test-Command($command) {
    try {
        & $command --version 2>&1 | Out-Null
        return $true
    } catch {
        return $false
    }
}

function Test-NetworkConnection {
    Write-Host "Testando conexão com repositórios Maven..." -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri "https://repo1.maven.org/maven2" -UseBasicParsing -TimeoutSec 10
        return $response.StatusCode -eq 200
    } catch {
        return $false
    }
}

Write-Host "1. Verificando Java..." -ForegroundColor Cyan
if (Test-Command "java") {
    $javaVersion = java -version 2>&1 | Select-String "version" | Select-Object -First 1
    Write-Host "   ✓ Java instalado: $javaVersion" -ForegroundColor Green
} else {
    Write-Host "   ✗ Java NÃO encontrado!" -ForegroundColor Red
    Write-Host "   → Instale Java 17 ou superior" -ForegroundColor Yellow
    Write-Host "   → https://www.oracle.com/java/technologies/downloads/" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "2. Verificando Maven..." -ForegroundColor Cyan
if (Test-Command "mvn") {
    $mvnVersion = mvn --version 2>&1 | Select-Object -First 1
    Write-Host "   ✓ Maven instalado: $mvnVersion" -ForegroundColor Green
} else {
    Write-Host "   ✗ Maven NÃO encontrado!" -ForegroundColor Red
    Write-Host "   → Instale Apache Maven" -ForegroundColor Yellow
    Write-Host "   → https://maven.apache.org/download.cgi" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "3. Verificando conectividade..." -ForegroundColor Cyan
if (Test-NetworkConnection) {
    Write-Host "   ✓ Conexão com repositório Maven OK" -ForegroundColor Green
} else {
    Write-Host "   ✗ Problema de conexão com repositório" -ForegroundColor Red
    Write-Host "   → Verifique sua conexão com internet" -ForegroundColor Yellow
    Write-Host "   → Verifique proxy/firewall corporativo" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "4. Verificando arquivos do projeto..." -ForegroundColor Cyan
$arquivos = @("pom.xml", "src/main/java", ".m2/settings.xml")
foreach ($arquivo in $arquivos) {
    if (Test-Path $arquivo) {
        Write-Host "   ✓ $arquivo existe" -ForegroundColor Green
    } else {
        Write-Host "   ✗ $arquivo FALTANDO" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "5. Verificando cache Maven..." -ForegroundColor Cyan
$mavenRepo = "$env:USERPROFILE\.m2\repository"
if (Test-Path $mavenRepo) {
    $tamanho = (Get-ChildItem $mavenRepo -Recurse | Measure-Object -Property Length -Sum).Sum
    $tamanhoMB = [math]::Round($tamanho / 1MB, 2)
    Write-Host "   ✓ Cache Maven: $tamanhoMB MB" -ForegroundColor Green
} else {
    Write-Host "   ⚠ Cache Maven vazio ou não existe" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "           SOLUÇÕES RECOMENDADAS" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Se há problemas:" -ForegroundColor Yellow
Write-Host "1. Execute: .\scripts\setup-inicial.ps1" -ForegroundColor White
Write-Host "2. Se falhar, execute: mvn clean install -U" -ForegroundColor White
Write-Host "3. Limpe cache: mvn dependency:purge-local-repository" -ForegroundColor White
Write-Host "4. Force download: mvn dependency:resolve -U" -ForegroundColor White
Write-Host ""
Write-Host "Para proxy corporativo, configure ~/.m2/settings.xml" -ForegroundColor Yellow
Write-Host ""

Read-Host "Pressione Enter para continuar"
