#!/bin/bash

echo "==================================="
echo "  Sistema Validador Distribuido"
echo "  Versao 1.0.0 - Java 21"
echo "==================================="
echo

# Verifica se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "ERRO: Java não encontrado!"
    echo
    echo "Por favor, instale o Java 21 ou superior:"
    echo "https://adoptium.net/temurin/releases/"
    echo
    exit 1
fi

echo "Java encontrado. Verificando versão..."
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
echo "Versão do Java: $JAVA_VERSION"
echo

while true; do
    echo "Escolha uma opção:"
    echo
    echo "[1] Iniciar Servidor (com interface gráfica)"
    echo "[2] Iniciar Servidor (modo headless)"
    echo "[3] Iniciar Cliente"
    echo "[4] Sair"
    echo
    read -p "Digite sua opção (1-4): " choice

    case $choice in
        1)
            echo
            echo "Iniciando servidor com interface gráfica..."
            echo "Pressione Ctrl+C para parar o servidor."
            echo
            java -jar validador-sistemas-distribuidos-1.0.0-server.jar
            ;;
        2)
            echo
            echo "Iniciando servidor em modo headless (sem interface)..."
            echo "Pressione Ctrl+C para parar o servidor."
            echo
            java -jar validador-sistemas-distribuidos-1.0.0-server.jar -headless
            ;;
        3)
            echo
            echo "Iniciando cliente..."
            echo
            java -jar validador-sistemas-distribuidos-1.0.0-client.jar
            ;;
        4)
            echo
            echo "Saindo..."
            exit 0
            ;;
        *)
            echo "Opção inválida! Tente novamente."
            echo
            ;;
    esac
done