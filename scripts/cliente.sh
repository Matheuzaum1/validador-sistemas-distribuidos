#!/bin/bash
# Script para executar o cliente - Linux/macOS
set -e

SERVER_HOST=${1:-localhost}
PORT=${2:-20000}
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

JAR_PATH="target/validador-sistemas-distribuidos-1.0.0.jar"

if [ ! -f "$JAR_PATH" ]; then
    echo "❌ JAR não encontrado. Compilando..."
    ./scripts/compilar.sh
fi

echo "🖥️  Iniciando cliente para conectar em $SERVER_HOST:$PORT..."
java -Dserver.host=$SERVER_HOST -Dserver.port=$PORT -cp "$JAR_PATH" "com.distribuidos.client.ClientMain"