#!/bin/bash
# Script para executar o servidor - Linux/macOS
set -e

PORT=${1:-20000}
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

JAR_PATH="target/validador-sistemas-distribuidos-1.0.0.jar"

if [ ! -f "$JAR_PATH" ]; then
    echo "❌ JAR não encontrado. Compilando..."
    ./scripts/compilar.sh
fi

echo "🚀 Iniciando servidor na porta $PORT..."
java -Dserver.port=$PORT -jar "$JAR_PATH"