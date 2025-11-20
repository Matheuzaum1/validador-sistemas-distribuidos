package com.distribuidos.test;

import com.distribuidos.common.MessageBuilder;

/**
 * Teste simples para demonstrar o funcionamento do sistema de relatório de erros.
 */
public class ErrorReportTest {
    
    public static void main(String[] args) {
        // Simulação de uma mensagem de erro que o cliente enviaria
        String errorMessage = MessageBuilder.buildServerErrorMessage(
            "usuario_login", 
            "Resposta usuario_login chegou sem campo 'token' ou token é nulo"
        );
        
        System.out.println("=== TESTE DE RELATÓRIO DE ERRO ===");
        System.out.println("Mensagem que seria enviada pelo cliente:");
        System.out.println(errorMessage);
        System.out.println();
        
        // Simulação de outras violações de protocolo
        String[] testCases = {
            "Resposta usuario_ler chegou sem campo 'usuario' ou usuario é nulo",
            "Resposta transacao_ler chegou sem campo 'transacoes' ou transacoes é nulo",
            "Servidor retornou status false mas sem código de erro",
            "Formato JSON inválido na resposta do servidor"
        };
        
        String[] operations = {
            "usuario_ler",
            "transacao_ler", 
            "usuario_criar",
            "transacao_criar"
        };
        
        System.out.println("Exemplos de outros erros que podem ser reportados:");
        for (int i = 0; i < testCases.length; i++) {
            String msg = MessageBuilder.buildServerErrorMessage(operations[i], testCases[i]);
            System.out.println("Operação: " + operations[i]);
            System.out.println("Erro: " + msg);
            System.out.println();
        }
    }
}