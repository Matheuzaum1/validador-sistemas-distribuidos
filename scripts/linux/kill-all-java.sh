#!/bin/bash

echo "🛑 Parando todos os processos Java..."

# Encontrar todos os processos Java
JAVA_PIDS=$(pgrep -f java)

if [ -z "$JAVA_PIDS" ]; then
    echo "✅ Nenhum processo Java encontrado."
else
    echo "🔍 Processos Java encontrados:"
    ps -f -p $JAVA_PIDS
    echo
    
    echo "🛑 Terminando processos Java..."
    
    # Tentar terminar graciosamente primeiro
    for pid in $JAVA_PIDS; do
        echo "📤 Enviando SIGTERM para PID $pid..."
        kill -TERM $pid 2>/dev/null
    done
    
    # Aguardar um pouco
    sleep 3
    
    # Verificar se ainda existem processos Java
    REMAINING_PIDS=$(pgrep -f java)
    
    if [ ! -z "$REMAINING_PIDS" ]; then
        echo "⚠️  Alguns processos ainda estão rodando. Forçando terminação..."
        
        for pid in $REMAINING_PIDS; do
            echo "💥 Enviando SIGKILL para PID $pid..."
            kill -KILL $pid 2>/dev/null
        done
        
        sleep 1
    fi
    
    # Verificação final
    FINAL_CHECK=$(pgrep -f java)
    
    if [ -z "$FINAL_CHECK" ]; then
        echo "✅ Todos os processos Java foram terminados com sucesso!"
    else
        echo "❌ Alguns processos Java ainda estão rodando:"
        ps -f -p $FINAL_CHECK
        echo "⚠️  Talvez seja necessário reiniciar o sistema."
    fi
fi

echo "🧹 Limpeza concluída!"
