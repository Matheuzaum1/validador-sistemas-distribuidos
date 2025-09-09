#!/bin/bash

clear
echo "=========================================="
echo "   🚀 Sistema NewPix - Linux"
echo "=========================================="

# Função para mostrar o menu
show_menu() {
    echo
    echo "📋 Escolha uma opção:"
    echo
    echo "1. 📦 Instalar dependências"
    echo "2. 🖥️  Iniciar servidor"
    echo "3. 💻 Iniciar cliente"
    echo "4. 🧪 Executar testes"
    echo "5. 🧹 Limpar processos Java"
    echo "6. 🔍 Verificar codificação UTF-8"
    echo "7. 🚪 Sair"
    echo
}

# Função para aguardar tecla
wait_key() {
    echo
    echo "Pressione Enter para voltar ao menu..."
    read
}

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Loop principal
while true; do
    show_menu
    read -p "Digite sua escolha (1-7): " choice
    
    case $choice in
        1)
            echo
            "$SCRIPT_DIR/install-dependencies.sh"
            wait_key
            ;;
        2)
            echo
            "$SCRIPT_DIR/start-server.sh"
            wait_key
            ;;
        3)
            echo
            "$SCRIPT_DIR/start-client.sh"
            wait_key
            ;;
        4)
            echo
            "$SCRIPT_DIR/run-tests.sh"
            wait_key
            ;;
        5)
            echo
            "$SCRIPT_DIR/kill-all-java.sh"
            wait_key
            ;;
        6)
            echo
            "$SCRIPT_DIR/check-utf8-bom.sh"
            wait_key
            ;;
        7)
            echo "👋 Encerrando..."
            exit 0
            ;;
        *)
            echo "❌ Opção inválida! Tente novamente."
            sleep 2
            ;;
    esac
    
    clear
    echo "=========================================="
    echo "   🚀 Sistema NewPix - Linux"
    echo "=========================================="
done
