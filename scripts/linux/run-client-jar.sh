#!/bin/bash

echo "=========================================="
echo "   💻 Executando Cliente via JAR"
echo "=========================================="
echo

# Ir para o diretório raiz do projeto
cd "$(dirname "$0")/../.."

# Verificar se o JAR existe
if [ ! -f "dist/newpix-client.jar" ]; then
    echo "❌ JAR do cliente não encontrado!"
    echo "📝 Execute primeiro: ./scripts/linux/build-jars.sh"
    exit 1
fi

# Verificar se as dependências existem
if [ ! -d "dist/lib/" ]; then
    echo "❌ Dependências não encontradas!"
    echo "📝 Execute primeiro: ./scripts/linux/build-jars.sh"
    exit 1
fi

echo "🚀 Iniciando cliente NewPix via JAR..."
echo "📱 Interface gráfica aparecerá automaticamente!"
echo "🛑 Para parar: Feche a janela do cliente"
echo

# Configurar variáveis de ambiente para GUI
export DISPLAY=${DISPLAY:-:0}

# Executar o JAR do cliente
java -Djava.awt.headless=false -jar "dist/newpix-client.jar"

echo
echo "🛑 Cliente encerrado."
