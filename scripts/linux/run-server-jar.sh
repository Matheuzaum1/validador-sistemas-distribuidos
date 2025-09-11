#!/bin/bash

echo "=========================================="
echo "   🖥️ Executando Servidor via JAR"
echo "=========================================="
echo

# Ir para o diretório raiz do projeto
cd "$(dirname "$0")/../.."

# Verificar se o JAR existe
if [ ! -f "dist/newpix-server.jar" ]; then
    echo "❌ JAR do servidor não encontrado!"
    echo "📝 Execute primeiro: ./scripts/linux/build-jars.sh"
    exit 1
fi

echo "🚀 Iniciando servidor NewPix via JAR..."
echo "📱 Interface gráfica aparecerá automaticamente!"
echo "🛑 Para parar: Ctrl+C ou feche a janela do servidor"
echo

# Configurar variáveis de ambiente para GUI
export DISPLAY=${DISPLAY:-:0}

# Executar o JAR do servidor
java -Djava.awt.headless=false -jar "dist/newpix-server.jar"

echo
echo "🛑 Servidor encerrado."
