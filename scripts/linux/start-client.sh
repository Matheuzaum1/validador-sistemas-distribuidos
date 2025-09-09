#!/bin/bash

echo "💻 Iniciando Cliente NewPix..."
echo "==============================="

# Verificar se Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado!"
    echo "📝 Execute primeiro: ./scripts/linux/install-dependencies.sh"
    exit 1
fi

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado!"
    echo "📝 Execute primeiro: ./scripts/linux/install-dependencies.sh"
    exit 1
fi

# Ir para o diretório raiz do projeto
cd "$(dirname "$0")/../.."

# Verificar se o pom.xml existe
if [ ! -f "pom.xml" ]; then
    echo "❌ Arquivo pom.xml não encontrado!"
    echo "📝 Certifique-se de estar no diretório correto do projeto."
    exit 1
fi

# Verificar se o projeto foi compilado
if [ ! -d "target/classes" ]; then
    echo "🔨 Projeto não compilado. Compilando..."
    mvn compile -q
    
    if [ $? -ne 0 ]; then
        echo "❌ Falha na compilação!"
        exit 1
    fi
fi

# Configurar variáveis de ambiente para GUI
export DISPLAY=${DISPLAY:-:0}
export JAVA_OPTS="-Djava.awt.headless=false"

# Aguardar um pouco para garantir que o servidor esteja rodando
echo "⏳ Aguardando servidor estar disponível..."
sleep 2

# Verificar se o servidor está rodando
if ! pgrep -f "NewPixServer" > /dev/null; then
    echo "⚠️  Servidor não encontrado rodando!"
    echo "📝 Certifique-se de iniciar o servidor primeiro:"
    echo "   ./scripts/linux/start-server.sh"
    echo
    echo "🔄 Tentando conectar mesmo assim..."
fi

# Iniciar o cliente com GUI obrigatório
echo "🚀 Iniciando cliente NewPix..."
echo "📱 Interface gráfica aparecerá automaticamente!"
echo "🛑 Para parar: Feche a janela do cliente"
echo

# Executar cliente
mvn exec:java -Pclient -Dexec.args="-Djava.awt.headless=false" -q

echo
echo "🛑 Cliente encerrado."
