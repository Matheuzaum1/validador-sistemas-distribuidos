# Script de Setup Inicial - NewPix Banking System
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "    NewPix - Setup de Dependências" -ForegroundColor Cyan  
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se estamos no diretório correto
if (-not (Test-Path "pom.xml")) {
    Write-Host "ERRO: Execute este script no diretório raiz do projeto!" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host "Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✓ Java encontrado: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Java não encontrado! Instale Java 17 ou superior." -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host ""
Write-Host "Verificando Maven..." -ForegroundColor Yellow
try {
    $mvnVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "✓ Maven encontrado: $mvnVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Maven não encontrado! Instale Apache Maven." -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host ""
Write-Host "Limpando cache local do Maven..." -ForegroundColor Yellow
mvn clean

Write-Host ""
Write-Host "Baixando todas as dependências do projeto..." -ForegroundColor Yellow
mvn dependency:resolve -s .m2/settings.xml

Write-Host ""
Write-Host "Baixando dependências de plugins..." -ForegroundColor Yellow
mvn dependency:resolve-plugins -s .m2/settings.xml

Write-Host ""
Write-Host "Compilando projeto pela primeira vez..." -ForegroundColor Yellow
mvn compile -s .m2/settings.xml

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✓ Setup concluído com sucesso!" -ForegroundColor Green
    Write-Host "✓ Todas as dependências foram baixadas" -ForegroundColor Green
    Write-Host "✓ Projeto compilado e pronto para uso" -ForegroundColor Green
    Write-Host ""
    Write-Host "Agora você pode usar:" -ForegroundColor Cyan
    Write-Host "  .\scripts\start-server.ps1  (para servidor)" -ForegroundColor White
    Write-Host "  .\scripts\start-client.ps1  (para cliente)" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "✗ Erro durante o setup!" -ForegroundColor Red
    Write-Host "Verifique sua conexão com a internet e tente novamente." -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Pressione Enter para continuar"
