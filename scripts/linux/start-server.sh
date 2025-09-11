#!/bin/bash

echo "🖥️  Iniciando Servidor NewPix..."
echo "================================="

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

# Parar processos Java anteriores
echo "🧹 Limpando processos Java anteriores..."
./scripts/linux/kill-all-java.sh
sleep 2

# Compilar o projeto
echo "🔨 Compilando projeto..."
mvn compile -q

if [ $? -ne 0 ]; then
    echo "❌ Falha na compilação!"
    echo "📝 Verifique os erros acima e tente novamente."
    exit 1
fi

echo "✅ Compilação bem-sucedida!"
echo

# Configurar variáveis de ambiente para GUI
export DISPLAY=${DISPLAY:-:0}
export JAVA_OPTS="-Djava.awt.headless=false"

# Iniciar o servidor com GUI obrigatório
echo "🚀 Iniciando servidor NewPix..."
echo "📱 Interface gráfica aparecerá automaticamente!"
echo "🛑 Para parar: Ctrl+C ou feche a janela do servidor"
echo

# Executar servidor
mvn exec:java -Pserver -Dexec.args="-Djava.awt.headless=false" -q

echo
echo "🛑 Servidor encerrado."
