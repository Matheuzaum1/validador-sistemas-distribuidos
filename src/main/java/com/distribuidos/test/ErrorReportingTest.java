package com.distribuidos.test;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Teste para validar se o cliente envia erro_servidor quando recebe status:false do servidor
 */
public class ErrorReportingTest {
    
    private static final int TEST_PORT = 22222;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static void main(String[] args) {
        System.out.println("=== TESTE: Validação de envio erro_servidor para status:false ===");
        
        // Executar servidor de teste em thread separada
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> serverTask = executor.submit(() -> runTestServer());
        
        try {
            // Aguardar um pouco para o servidor iniciar
            Thread.sleep(1000);
            
            // Testar conexão do cliente
            testClientErrorReporting();
            
        } catch (Exception e) {
            System.err.println("Erro no teste: " + e.getMessage());
            e.printStackTrace();
        } finally {
            serverTask.cancel(true);
            executor.shutdown();
        }
    }
    
    private static void runTestServer() {
        try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
            System.out.println("Servidor de teste iniciado na porta " + TEST_PORT);
            System.out.println("Aguardando conexão do cliente...");
            
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                
                System.out.println("Cliente conectado de " + clientSocket.getRemoteSocketAddress());
                
                // Receber mensagem de conexão
                String connectMsg = in.readLine();
                System.out.println("Recebido: " + connectMsg);
                
                // Responder com sucesso para conectar
                String connectResponse = "{\"operacao\":\"conectar\",\"status\":true,\"info\":\"Conectado com sucesso\"}";
                out.println(connectResponse);
                System.out.println("Enviado: " + connectResponse);
                
                // Aguardar próxima mensagem (provavelmente login)
                String loginMsg = in.readLine();
                System.out.println("Recebido: " + loginMsg);
                
                // Responder com ERROR (status:false) - isso deve fazer o cliente enviar erro_servidor
                String errorResponse = "{\"operacao\":\"usuario_login\",\"status\":false,\"info\":\"CPF ou senha inválidos\"}";
                out.println(errorResponse);
                System.out.println("Enviado ERRO: " + errorResponse);
                
                // Aguardar mensagem erro_servidor do cliente
                String errorServerMsg = in.readLine();
                System.out.println("Recebido erro_servidor: " + errorServerMsg);
                
                // Verificar se é realmente erro_servidor
                try {
                    JsonNode errorNode = mapper.readTree(errorServerMsg);
                    String operacao = errorNode.get("operacao").asText();
                    
                    if ("erro_servidor".equals(operacao)) {
                        System.out.println("✅ SUCESSO: Cliente enviou erro_servidor corretamente!");
                        System.out.println("   operacao_enviada: " + errorNode.get("operacao_enviada").asText());
                        System.out.println("   info: " + errorNode.get("info").asText());
                        
                        // Responder para confirmar recebimento
                        String confirmResponse = "{\"operacao\":\"erro_servidor\",\"status\":true,\"info\":\"Erro reportado recebido\"}";
                        out.println(confirmResponse);
                        System.out.println("Enviado confirmação: " + confirmResponse);
                    } else {
                        System.out.println("❌ FALHA: Cliente não enviou erro_servidor");
                        System.out.println("   Operação recebida: " + operacao);
                    }
                } catch (Exception e) {
                    System.err.println("❌ ERRO ao analisar mensagem: " + e.getMessage());
                    System.err.println("   Dados: " + errorServerMsg);
                }
                
            }
            
        } catch (IOException e) {
            System.err.println("Erro no servidor de teste: " + e.getMessage());
        }
    }
    
    private static void testClientErrorReporting() {
        System.out.println("\n--- Iniciando teste do cliente ---");
        System.out.println("Para testar, execute o cliente e conecte na porta " + TEST_PORT);
        System.out.println("Use CPF: 123.456.789-01 e senha: 123456");
        System.out.println("O cliente deve receber erro e automaticamente enviar erro_servidor de volta");
        
        // Aguardar tempo suficiente para o teste
        try {
            Thread.sleep(30000); // 30 segundos para teste manual
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}