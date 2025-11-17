#!/bin/bash
# Script para compilar o projeto - Linux/macOS
set -e

echo "🔨 Compilando projeto..."
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Erro: Maven não encontrado no PATH"
    echo "   Instale o Maven ou adicione ao PATH"
    exit 1
fi

echo "📦 Executando: mvn clean compile package"
mvn clean compile package

if [ $? -eq 0 ]; then
    echo "✅ Compilação concluída com sucesso!"
    echo "📄 JAR gerado: target/validador-sistemas-distribuidos-1.0.0.jar"
else
    echo "❌ Erro na compilação"
    exit 1
fi