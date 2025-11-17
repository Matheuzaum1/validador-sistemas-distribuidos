#!/bin/bash
# Menu interativo para gerenciar o sistema - Linux/macOS
set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

echo "🏦 Sistema Bancário Distribuído - EP-2"
echo "=================================="
echo ""
echo "Escolha uma opção:"
echo "1) 🔨 Compilar projeto"
echo "2) 🚀 Iniciar servidor"
echo "3) 🖥️  Iniciar cliente"
echo "4) 🔄 Compilar e iniciar servidor"
echo "5) 🛑 Parar processos Java"
echo "6) ❌ Sair"
echo ""
read -p "Digite sua opção (1-6): " option

case $option in
    1)
        echo "🔨 Compilando projeto..."
        ./scripts/compilar.sh
        ;;
    2)
        echo "🚀 Iniciando servidor..."
        ./scripts/servidor.sh
        ;;
    3)
        echo "🖥️  Iniciando cliente..."
        ./scripts/cliente.sh
        ;;
    4)
        echo "🔄 Compilando e iniciando servidor..."
        ./scripts/compilar.sh
        ./scripts/servidor.sh
        ;;
    5)
        echo "🛑 Parando processos Java..."
        pkill -f "java.*validador-sistemas-distribuidos" || echo "Nenhum processo encontrado"
        echo "✅ Processos finalizados"
        ;;
    6)
        echo "❌ Saindo..."
        exit 0
        ;;
    *)
        echo "❌ Opção inválida! Use 1-6"
        exit 1
        ;;
esac