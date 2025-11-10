# ============================================================================
# Script: menu.ps1
# Descrição: Menu interativo para gerenciamento do sistema
# Uso: .\scripts\menu.ps1
# ============================================================================

$ErrorActionPreference = "Stop"

function Show-Banner {
    Clear-Host
    Write-Host ""
    Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
    Write-Host "║                                                                ║" -ForegroundColor Cyan
    Write-Host "║         🚀 SISTEMA DISTRIBUÍDO - MENU PRINCIPAL 🚀           ║" -ForegroundColor Cyan
    Write-Host "║                                                                ║" -ForegroundColor Cyan
    Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
    Write-Host ""
}

function Show-Menu {
    Write-Host "┌────────────────────────────────────────────────────────────────┐" -ForegroundColor White
    Write-Host "│  OPÇÕES:                                                       │" -ForegroundColor White
    Write-Host "├────────────────────────────────────────────────────────────────┤" -ForegroundColor White
    Write-Host "│  1  │ 🚀 Executar Sistema Completo (Recomendado)              │" -ForegroundColor Green
    Write-Host "│  2  │ 🖥️  Iniciar Apenas Servidor                             │" -ForegroundColor Cyan
    Write-Host "│  3  │ 💻 Iniciar Apenas Cliente                               │" -ForegroundColor Cyan
    Write-Host "│  4  │ 🔨 Compilar Projeto                                      │" -ForegroundColor Yellow
    Write-Host "│  5  │ 🗑️  Limpar Build                                         │" -ForegroundColor Red
    Write-Host "│  6  │ 🧹 Limpeza Completa                                      │" -ForegroundColor Red
    Write-Host "│  7  │ 🔄 Limpar e Recompilar                                   │" -ForegroundColor Yellow
    Write-Host "│  8  │ 📖 Ver Instruções                                        │" -ForegroundColor Magenta
    Write-Host "│  0  │ ❌ Sair                                                  │" -ForegroundColor Red
    Write-Host "└────────────────────────────────────────────────────────────────┘" -ForegroundColor White
    Write-Host ""
}

function Show-Instructions {
    Clear-Host
    Write-Host ""
    Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
    Write-Host "║                   📖 INSTRUÇÕES DE USO 📖                     ║" -ForegroundColor Cyan
    Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "👤 PRIMEIRO ACESSO:" -ForegroundColor Green
    Write-Host "  1. Execute: .\scripts\sistema.ps1" -ForegroundColor White
    Write-Host "  2. Na interface, selecione 'Criar Conta'" -ForegroundColor White
    Write-Host "  3. Preencha os dados (CPF é formatado automaticamente)" -ForegroundColor White
    Write-Host "  4. Você será autenticado automaticamente" -ForegroundColor White
    Write-Host ""
    
    Write-Host "🔑 LOGIN:" -ForegroundColor Green
    Write-Host "  1. Execute: .\scripts\sistema.ps1" -ForegroundColor White
    Write-Host "  2. Na interface, selecione 'Login'" -ForegroundColor White
    Write-Host "  3. Digite seu CPF e senha" -ForegroundColor White
    Write-Host ""
    
    Write-Host "💰 OPERAÇÕES:" -ForegroundColor Green
    Write-Host "  Aba Conta:" -ForegroundColor Cyan
    Write-Host "    • Consultar: Ver seu saldo e dados" -ForegroundColor White
    Write-Host "    • Atualizar: Alterar nome ou senha" -ForegroundColor White
    Write-Host "    • Deletar: Remover sua conta" -ForegroundColor White
    Write-Host ""
    Write-Host "  Aba Transações:" -ForegroundColor Cyan
    Write-Host "    • Transferir: Enviar dinheiro para outro usuário" -ForegroundColor White
    Write-Host "    • Depositar: Adicionar saldo à sua conta" -ForegroundColor White
    Write-Host ""
    
    Write-Host "⚙️  SCRIPTS DISPONÍVEIS:" -ForegroundColor Green
    Write-Host "  .\scripts\sistema.ps1       - Inicia tudo automaticamente" -ForegroundColor White
    Write-Host "  .\scripts\compilar.ps1      - Apenas compila o projeto" -ForegroundColor White
    Write-Host "  .\scripts\servidor.ps1      - Inicia só o servidor" -ForegroundColor White
    Write-Host "  .\scripts\cliente.ps1       - Inicia só o cliente" -ForegroundColor White
    Write-Host "  .\scripts\limpeza.ps1       - Limpa o projeto" -ForegroundColor White
    Write-Host ""
    
    Write-Host "🔧 ARGUMENTOS DOS SCRIPTS:" -ForegroundColor Green
    Write-Host "  compilar.ps1 -test              - Executa testes também" -ForegroundColor White
    Write-Host "  servidor.ps1 -port 9000         - Usa porta diferente" -ForegroundColor White
    Write-Host "  cliente.ps1 -host 192.168.1.1   - Conecta em outro host" -ForegroundColor White
    Write-Host "  limpeza.ps1 -completa -rebuild  - Limpeza total + recompila" -ForegroundColor White
    Write-Host ""
    
    Read-Host "Pressione Enter para voltar ao menu"
}

function Execute-Option {
    param([int]$option)
    
    switch ($option) {
        1 {
            & .\scripts\sistema.ps1
        }
        2 {
            & .\scripts\servidor.ps1
        }
        3 {
            & .\scripts\cliente.ps1
        }
        4 {
            & .\scripts\compilar.ps1
        }
        5 {
            & .\scripts\limpeza.ps1
        }
        6 {
            & .\scripts\limpeza.ps1 -completa
        }
        7 {
            & .\scripts\limpeza.ps1 -completa -rebuild
        }
        8 {
            Show-Instructions
        }
        0 {
            Write-Host ""
            Write-Host "👋 Até logo!" -ForegroundColor Cyan
            Write-Host ""
            exit 0
        }
        default {
            Write-Host ""
            Write-Host "❌ Opção inválida! Digite um número de 0 a 8." -ForegroundColor Red
            Write-Host ""
            Read-Host "Pressione Enter para continuar"
        }
    }
}

# ============================================================================
# LOOP PRINCIPAL
# ============================================================================

while ($true) {
    Show-Banner
    Show-Menu
    
    $choice = Read-Host "Escolha uma opção"
    
    if ([int]::TryParse($choice, [ref]0)) {
        Execute-Option ([int]$choice)
    }
    else {
        Write-Host ""
        Write-Host "❌ Entrada inválida! Digite um número." -ForegroundColor Red
        Write-Host ""
        Read-Host "Pressione Enter para continuar"
    }
    
    if ([int]$choice -ne 0 -and [int]$choice -ne 8) {
        Write-Host ""
        Read-Host "Pressione Enter para voltar ao menu"
    }
}
