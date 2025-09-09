#!/bin/bash

echo "🧪 Executando Testes do Sistema NewPix"
echo "======================================"

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

# Parar processos Java anteriores
echo "🧹 Limpando processos anteriores..."
./scripts/linux/kill-all-java.sh
sleep 1

# Compilar e executar testes
echo "🔨 Compilando e executando testes..."
mvn clean test

RESULT=$?

if [ $RESULT -eq 0 ]; then
    echo
    echo "✅ ================================"
    echo "✅   Todos os testes passaram!"
    echo "✅ ================================"
else
    echo
    echo "❌ ================================"
    echo "❌   Alguns testes falharam!"
    echo "❌ ================================"
fi

echo
echo "📊 Relatório de testes salvo em: target/surefire-reports/"
echo "🔍 Para mais detalhes: mvn surefire-report:report"

exit $RESULT
