#!/bin/bash
# Menu interativo para gerenciar o sistema - Linux/macOS
set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

function show_menu() {
    clear
    echo ""
    echo "========================================"
    echo "   SISTEMA DISTRIBUÍDO - MENU v2.0     "
    echo "========================================"
    echo ""
    echo " [SISTEMA]"
    echo "  1 - Executar Sistema Completo"
    echo "  2 - Iniciar Servidor"
    echo "  3 - Iniciar Cliente"
    echo ""
    echo " [FERRAMENTAS]"
    echo "  4 - Iniciar Error Injector (se disponível)"
    echo ""
    echo " [BUILD]"
    echo "  5 - Compilar Projeto"
    echo "  6 - Limpar Build (mvn clean)"
    echo "  7 - Parar Processos Java"
    echo ""
    echo "  0 - Sair"
    echo ""
}

while true; do
    show_menu
    read -p "Escolha uma opção: " choice
    
    case $choice in
        1)
            echo "🚀 Executando sistema completo..."
            "$PROJECT_ROOT/scripts/sistema.sh"
            ;;
        2)
            echo "🖥️  Iniciando servidor..."
            "$PROJECT_ROOT/scripts/servidor.sh"
            ;;
        3)
            echo "🖥️  Iniciando cliente..."
            "$PROJECT_ROOT/scripts/cliente.sh"
            ;;
        4)
            if [ -f "$PROJECT_ROOT/error-injector/target/simple-error-injector-1.0.0.jar" ]; then
                echo "🔧 Iniciando Error Injector..."
                cd "$PROJECT_ROOT/error-injector"
                java -jar "target/simple-error-injector-1.0.0.jar"
            else
                echo "⚠️  Error Injector não encontrado. Execute compilar primeiro."
            fi
            ;;
        5)
            echo "🔨 Compilando projeto..."
            "$PROJECT_ROOT/scripts/compilar.sh"
            ;;
        6)
            echo "🧹 Limpando build..."
            cd "$PROJECT_ROOT"
            mvn clean
            echo "✅ Limpeza concluída!"
            ;;
        7)
            echo "🛑 Parando processos Java..."
            pkill -f "java.*validador-sistemas-distribuidos" || echo "Nenhum processo encontrado"
            echo "✅ Processos finalizados"
            ;;
        0)
            echo "👋 Até logo!"
            exit 0
            ;;
        *)
            echo "❌ Opção inválida!"
            ;;
    esac
    
    if [ "$choice" != "0" ]; then
        echo ""
        read -p "Pressione Enter para voltar ao menu..."
    fi
done