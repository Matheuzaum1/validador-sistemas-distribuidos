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
    echo "2. 🖥️  Iniciar servidor (Maven)"
    echo "3. 💻 Iniciar cliente (Maven)"
    echo "4. 🔨 Compilar JARs"
    echo "5. 🖥️  Executar servidor (JAR)"
    echo "6. 💻 Executar cliente (JAR)"
    echo "7. 🧪 Executar testes"
    echo "8. 🧹 Limpar processos Java"
    echo "9. 🔍 Verificar codificação UTF-8"
    echo "0. 🚪 Sair"
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
    read -p "Digite sua escolha (0-9): " choice
    
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
            "$SCRIPT_DIR/build-jars.sh"
            wait_key
            ;;
        5)
            echo
            "$SCRIPT_DIR/run-server-jar.sh"
            wait_key
            ;;
        6)
            echo
            "$SCRIPT_DIR/run-client-jar.sh"
            wait_key
            ;;
        7)
            echo
            "$SCRIPT_DIR/run-tests.sh"
            wait_key
            ;;
        8)
            echo
            "$SCRIPT_DIR/kill-all-java.sh"
            wait_key
            ;;
        9)
            echo
            "$SCRIPT_DIR/check-utf8-bom.sh"
            wait_key
            ;;
        0)
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
