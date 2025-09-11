#!/bin/bash

echo "=========================================="
echo "   📦 Compilando JARs - Sistema NewPix"
echo "=========================================="
echo

# Ir para o diretório raiz do projeto
cd "$(dirname "$0")/../.."

# Limpar compilação anterior
echo "🧹 Limpando compilação anterior..."
if [ -d "dist" ]; then
    rm -rf dist
fi
mvn clean -q

echo
echo "🔨 Compilando e empacotando JARs..."
mvn package -q

if [ $? -ne 0 ]; then
    echo "❌ Falha na compilação!"
    exit 1
fi

echo
echo "✅ =========================================="
echo "✅   Compilação concluída com sucesso!"
echo "✅ =========================================="
echo
echo "📁 Arquivos criados em: dist/"
echo "   📄 newpix-server.jar (Servidor completo)"
echo "   📄 newpix-client.jar (Cliente)"
echo "   📂 lib/ (Dependências)"
echo
echo "🚀 Para executar:"
echo "   📖 Servidor: java -jar dist/newpix-server.jar"
echo "   📖 Cliente:  java -jar dist/newpix-client.jar"
echo

if [ -d "dist" ]; then
    echo "📋 Conteúdo da pasta dist/:"
    ls -la dist/
    echo
    if [ -d "dist/lib" ]; then
        echo "📋 Dependências (lib/):"
        ls -1 dist/lib/*.jar | head -10
        echo "... (e mais dependências)"
    fi
fi
