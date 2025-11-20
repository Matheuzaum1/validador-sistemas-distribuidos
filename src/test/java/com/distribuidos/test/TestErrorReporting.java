package com.distribuidos.test;

import com.distribuidos.common.MessageBuilder;

public class TestErrorReporting {
    public static void main(String[] args) {
        // Simular construção de mensagem erro_servidor
        String errorMessage = MessageBuilder.buildServerErrorMessage(
            "usuario_login", 
            "Teste: resposta do servidor sem campo obrigatório"
        );
        
        System.out.println("Mensagem erro_servidor construída:");
        System.out.println(errorMessage);
        System.out.println();
        
        // Verificar se contém os campos corretos
        if (errorMessage.contains("\"operacao\":\"erro_servidor\"") && 
            errorMessage.contains("\"operacao_enviada\":\"usuario_login\"") &&
            errorMessage.contains("\"info\":")) {
            System.out.println("✅ Estrutura da mensagem está correta!");
        } else {
            System.out.println("❌ Estrutura da mensagem está incorreta!");
        }
        
        System.out.println("\n=== TESTE CONCLUÍDO ===");
        System.out.println("Para testar completamente:");
        System.out.println("1. Inicie o servidor");
        System.out.println("2. Inicie o cliente");
        System.out.println("3. Execute operação que cause erro de protocolo");
        System.out.println("4. Verifique logs do servidor para 🚨 ERRO REPORTADO PELO CLIENTE");
    }
}