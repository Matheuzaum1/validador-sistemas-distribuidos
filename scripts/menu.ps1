$ErrorActionPreference = "Stop"
function Show-Menu {
    Clear-Host
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "   SISTEMA DISTRIBUIDO - MENU v2.0     " -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host " [SISTEMA]" -ForegroundColor White
    Write-Host "  1 - Executar Sistema Completo" -ForegroundColor Green
    Write-Host "  2 - Iniciar Servidor" -ForegroundColor Cyan
    Write-Host "  3 - Iniciar Cliente" -ForegroundColor Cyan
    Write-Host ""
    Write-Host " [FERRAMENTAS]" -ForegroundColor White
    Write-Host "  4 - Iniciar Error Injector (Proxy)" -ForegroundColor Magenta
    Write-Host "  5 - Sistema com Proxy (Servidor + Proxy + Cliente)" -ForegroundColor Magenta
    Write-Host ""
    Write-Host " [BUILD]" -ForegroundColor White
    Write-Host "  6 - Compilar Projeto" -ForegroundColor Yellow
    Write-Host "  7 - Compilar Error Injector" -ForegroundColor Yellow
    Write-Host "  8 - Limpar Build" -ForegroundColor Red
    Write-Host "  9 - Limpeza Completa" -ForegroundColor Red
    Write-Host ""
    Write-Host "  0 - Sair" -ForegroundColor Red
    Write-Host ""
}
$projectRoot = Split-Path -Parent $PSScriptRoot
while ($true) {
    Show-Menu
    $choice = Read-Host "Escolha uma opcao"
    switch ($choice) {
        "1" { & "$projectRoot\scripts\sistema.ps1" }
        "2" { & "$projectRoot\scripts\servidor.ps1" }
        "3" { & "$projectRoot\scripts\cliente.ps1" }
        "4" { & "$projectRoot\scripts\error-injector.ps1" }
        "5" { & "$projectRoot\scripts\sistema-proxy.ps1" }
        "6" { & "$projectRoot\scripts\compilar.ps1" }
        "7" { & "$projectRoot\scripts\compilar.ps1" -errorInjector }
        "8" { & "$projectRoot\scripts\limpeza.ps1" }
        "9" { & "$projectRoot\scripts\limpeza.ps1" -completa }
        "0" { Write-Host "Ate logo!" -ForegroundColor Cyan; exit 0 }
        default { Write-Host "Opcao invalida!" -ForegroundColor Red; Read-Host "Pressione Enter" }
    }
    if ($choice -ne "0") {
        Write-Host ""
        Read-Host "Pressione Enter para voltar ao menu"
    }
}
