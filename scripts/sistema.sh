#!/bin/bash
# Script para executar sistema completo - Linux/macOS
set -e

PORT=${1:-20000}
REBUILD=${2:-false}
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAR_PATH="target/validador-sistemas-distribuidos-1.0.0.jar"

echo "🚀 Iniciando SISTEMA DISTRIBUÍDO..."

# Compilar se necessário
if [ "$REBUILD" = "true" ] || [ ! -f "$PROJECT_ROOT/$JAR_PATH" ]; then
    echo "🔨 Compilando projeto..."
    "$PROJECT_ROOT/scripts/compilar.sh"
fi

echo ""
echo "🖥️  Abrindo servidor em nova janela..."

# Detectar terminal disponível e abrir servidor
if command -v gnome-terminal &> /dev/null; then
    gnome-terminal -- bash -c "$PROJECT_ROOT/scripts/servidor.sh $PORT; exec bash"
elif command -v xterm &> /dev/null; then
    xterm -e "bash -c '$PROJECT_ROOT/scripts/servidor.sh $PORT; exec bash'" &
elif command -v konsole &> /dev/null; then
    konsole -e bash -c "$PROJECT_ROOT/scripts/servidor.sh $PORT; exec bash" &
else
    echo "⚠️  Terminal gráfico não encontrado. Execute manualmente:"
    echo "   ./scripts/servidor.sh $PORT"
    echo ""
    read -p "Pressione Enter quando o servidor estiver rodando..."
fi

echo "⏳ Aguardando servidor iniciar..."
sleep 3

echo "🖥️  Abrindo cliente em nova janela..."

# Detectar terminal disponível e abrir cliente
if command -v gnome-terminal &> /dev/null; then
    gnome-terminal -- bash -c "$PROJECT_ROOT/scripts/cliente.sh localhost $PORT; exec bash"
elif command -v xterm &> /dev/null; then
    xterm -e "bash -c '$PROJECT_ROOT/scripts/cliente.sh localhost $PORT; exec bash'" &
elif command -v konsole &> /dev/null; then
    konsole -e bash -c "$PROJECT_ROOT/scripts/cliente.sh localhost $PORT; exec bash" &
else
    echo "⚠️  Terminal gráfico não encontrado. Execute manualmente:"
    echo "   ./scripts/cliente.sh localhost $PORT"
fi

echo ""
echo "✅ Sistema iniciado! Verifique as janelas abertas."
echo "📋 Configuração:"
echo "   Servidor: localhost:$PORT"
echo "   Cliente: conectando em localhost:$PORT"