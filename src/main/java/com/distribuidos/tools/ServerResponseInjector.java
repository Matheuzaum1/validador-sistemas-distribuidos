package com.distribuidos.tools;

import com.distribuidos.common.MessageBuilder;
import validador.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitário para injetar respostas malformadas no servidor para teste do protocolo 4.11
 * Permite simular cenários onde o servidor envia respostas incorretas para testar o cliente
 */
public class ServerResponseInjector {
    private static final Logger logger = LoggerFactory.getLogger(ServerResponseInjector.class);
    
    /**
     * Gera resposta de login malformada (sem token)
     */
    public static String generateBadLoginResponse() {
        // Resposta de login sem token (viola protocolo)
        String badResponse = "{\"operacao\": \"usuario_login\", \"status\": true, \"info\": \"Login realizado com sucesso\"}";
        logger.info("🔴 Gerando resposta MALFORMADA de login (sem token): {}", badResponse);
        return badResponse;
    }
    
    /**
     * Gera resposta de login com token null
     */
    public static String generateLoginWithNullToken() {
        String badResponse = "{\"operacao\": \"usuario_login\", \"status\": true, \"info\": \"Login realizado\", \"token\": null}";
        logger.info("🔴 Gerando resposta de login com token NULL: {}", badResponse);
        return badResponse;
    }
    
    /**
     * Gera resposta de usuario_ler malformada (com usuario null)
     */
    public static String generateBadUserReadResponse() {
        String badResponse = "{\"operacao\": \"usuario_ler\", \"status\": true, \"info\": \"Dados do usuário recuperados com sucesso\", \"usuario\": null}";
        logger.info("🔴 Gerando resposta MALFORMADA de usuario_ler (usuario null): {}", badResponse);
        return badResponse;
    }
    
    /**
     * Gera resposta sem campo operacao
     */
    public static String generateResponseWithoutOperacao() {
        String badResponse = "{\"status\": true, \"info\": \"Resposta sem campo operacao\"}";
        logger.info("🔴 Gerando resposta SEM campo operacao: {}", badResponse);
        return badResponse;
    }
    
    /**
     * Gera resposta com operacao null
     */
    public static String generateResponseWithNullOperacao() {
        String badResponse = "{\"operacao\": null, \"status\": true, \"info\": \"Resposta com operacao null\"}";
        logger.info("🔴 Gerando resposta com operacao NULL: {}", badResponse);
        return badResponse;
    }
    
    /**
     * Gera resposta de transacao_ler com transacoes null
     */
    public static String generateBadTransactionReadResponse() {
        String badResponse = "{\"operacao\": \"transacao_ler\", \"status\": true, \"info\": \"Transações recuperadas\", \"transacoes\": null}";
        logger.info("🔴 Gerando resposta MALFORMADA de transacao_ler (transacoes null): {}", badResponse);
        return badResponse;
    }
    
    /**
     * Testa se uma resposta é válida usando o Validator
     */
    public static boolean isValidServerResponse(String jsonResponse) {
        try {
            Validator.validateServer(jsonResponse);
            return true;
        } catch (Exception e) {
            logger.warn("Resposta inválida detectada: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Simula que o cliente detectou um erro e deve enviar erro_servidor
     */
    public static String generateErrorServerMessage(String operacaoComErro, String descricaoErro) {
        String errorMessage = MessageBuilder.buildServerErrorMessage(operacaoComErro, descricaoErro);
        logger.info("📤 Gerando mensagem erro_servidor: {}", errorMessage);
        return errorMessage;
    }
    
    /**
     * Método main para testes rápidos
     */
    public static void main(String[] args) {
        System.out.println("=== TESTE DE INJEÇÃO DE ERROS SERVIDOR ===\n");
        
        // Teste 1: Login sem token
        System.out.println("1. Teste Login sem token:");
        String badLogin = generateBadLoginResponse();
        System.out.println("   Resposta: " + badLogin);
        System.out.println("   Válida? " + isValidServerResponse(badLogin));
        System.out.println("   Cliente enviaria: " + generateErrorServerMessage("usuario_login", "Token ausente na resposta"));
        System.out.println();
        
        // Teste 2: Usuario_ler com usuario null
        System.out.println("2. Teste Usuario_ler com usuario null:");
        String badUserRead = generateBadUserReadResponse();
        System.out.println("   Resposta: " + badUserRead);
        System.out.println("   Válida? " + isValidServerResponse(badUserRead));
        System.out.println("   Cliente enviaria: " + generateErrorServerMessage("usuario_ler", "Campo usuario é null"));
        System.out.println();
        
        // Teste 3: Resposta sem operacao
        System.out.println("3. Teste resposta sem operacao:");
        String noOperacao = generateResponseWithoutOperacao();
        System.out.println("   Resposta: " + noOperacao);
        System.out.println("   Válida? " + isValidServerResponse(noOperacao));
        System.out.println("   Cliente enviaria: " + generateErrorServerMessage(null, "Campo operacao ausente"));
        System.out.println();
        
        // Teste 4: Resposta com operacao null
        System.out.println("4. Teste resposta com operacao null:");
        String nullOperacao = generateResponseWithNullOperacao();
        System.out.println("   Resposta: " + nullOperacao);
        System.out.println("   Válida? " + isValidServerResponse(nullOperacao));
        System.out.println("   Cliente enviaria: " + generateErrorServerMessage(null, "Campo operacao é null"));
        System.out.println();
        
        // Teste 5: Resposta correta para comparação
        System.out.println("5. Teste resposta CORRETA (para comparação):");
        String goodResponse = MessageBuilder.buildSuccessResponse("usuario_login", "Login realizado com sucesso", "token123");
        System.out.println("   Resposta: " + goodResponse);
        System.out.println("   Válida? " + isValidServerResponse(goodResponse));
        System.out.println();
        
        System.out.println("=== FIM DOS TESTES ===");
    }
}