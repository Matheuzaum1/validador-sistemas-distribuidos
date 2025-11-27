package com.distribuidos.errorinjector.mockserver;

import com.distribuidos.errorinjector.core.ErrorInjector;
import com.distribuidos.errorinjector.core.ErrorType;
import com.distribuidos.errorinjector.ui.ErrorInjectorGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servidor mock que responde com mensagens contendo erros injetados
 * Útil para testar como o cliente real lida com respostas malformadas
 */
public class MockServer {
    private static final Logger logger = LoggerFactory.getLogger(MockServer.class);
    
    private ServerSocket serverSocket;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger connectionCount = new AtomicInteger(0);
    private ErrorInjectorGUI gui;
    private int port;
    private ErrorType currentErrorType;
    private String responseTemplate;
    
    public MockServer(ErrorInjectorGUI gui) {
        this.gui = gui;
    }
    
    /**
     * Inicia o servidor mock na porta especificada
     */
    public boolean start(int port, ErrorType errorType, String responseTemplate) {
        if (running.get()) {
            gui.addLog("❌ Servidor mock já está em execução");
            return false;
        }
        
        this.port = port;
        this.currentErrorType = errorType;
        this.responseTemplate = responseTemplate;
        
        try {
            serverSocket = new ServerSocket(port);
            running.set(true);
            
            gui.addLog("✅ Servidor mock iniciado na porta " + port);
            gui.addLog("🔧 Tipo de erro: " + errorType.getDisplayName());
            gui.addLog("📝 Template de resposta configurado");
            
            // Iniciar thread para aceitar conexões
            Thread acceptThread = new Thread(this::acceptConnections);
            acceptThread.setDaemon(true);
            acceptThread.setName("MockServer-Accept");
            acceptThread.start();
            
            return true;
            
        } catch (IOException e) {
            logger.error("Erro ao iniciar servidor mock na porta {}", port, e);
            gui.addLog("❌ Erro ao iniciar servidor mock: " + e.getMessage());
            running.set(false);
            return false;
        }
    }
    
    /**
     * Para o servidor mock
     */
    public void stop() {
        if (!running.get()) {
            gui.addLog("⚠️ Servidor mock já estava parado");
            return;
        }
        
        running.set(false);
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            gui.addLog("🛑 Servidor mock parado");
            gui.addLog("📊 Total de conexões atendidas: " + connectionCount.get());
            
        } catch (IOException e) {
            logger.error("Erro ao parar servidor mock", e);
            gui.addLog("⚠️ Erro ao parar servidor mock: " + e.getMessage());
        }
    }
    
    /**
     * Thread principal para aceitar conexões
     */
    private void acceptConnections() {
        gui.addLog("🔄 Aguardando conexões...");
        
        while (running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                int connectionId = connectionCount.incrementAndGet();
                
                String clientInfo = clientSocket.getRemoteSocketAddress().toString();
                gui.addLog("📞 Nova conexão #" + connectionId + " de " + clientInfo);
                
                // Processar cada cliente em thread separada
                Thread clientThread = new Thread(() -> handleClient(clientSocket, connectionId));
                clientThread.setDaemon(true);
                clientThread.setName("MockServer-Client-" + connectionId);
                clientThread.start();
                
            } catch (IOException e) {
                if (running.get()) {
                    logger.error("Erro ao aceitar conexão", e);
                    gui.addLog("❌ Erro ao aceitar conexão: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Processa um cliente específico
     */
    private void handleClient(Socket clientSocket, int connectionId) {
        String clientInfo = clientSocket.getRemoteSocketAddress().toString();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            
            gui.addLog("🔗 Cliente #" + connectionId + " conectado");
            
            String inputLine;
            int messageCount = 0;
            
            while ((inputLine = reader.readLine()) != null && running.get()) {
                messageCount++;
                gui.addLog("📥 #" + connectionId + " recebeu: " + inputLine);
                
                // Gerar resposta com erro injetado
                String response = generateErrorResponse(inputLine);
                
                // Enviar resposta
                writer.println(response);
                gui.addLog("📤 #" + connectionId + " enviou: " + response);
                
                // Log da injeção de erro
                gui.addLog("🔴 Erro injetado: " + currentErrorType.getDisplayName());
                
                // Simular processamento
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            gui.addLog("👋 Cliente #" + connectionId + " desconectado (" + messageCount + " mensagens)");
            
        } catch (IOException e) {
            logger.error("Erro ao processar cliente #{}", connectionId, e);
            gui.addLog("❌ Erro com cliente #" + connectionId + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.error("Erro ao fechar socket do cliente #{}", connectionId, e);
            }
        }
    }
    
    /**
     * Gera resposta com erro injetado baseado na mensagem recebida
     */
    private String generateErrorResponse(String receivedMessage) {
        try {
            // Determinar tipo de resposta baseado na operação recebida
            String responseToUse = determineResponseType(receivedMessage);
            
            if (responseToUse == null) {
                responseToUse = responseTemplate;
            }
            
            // Injetar erro na resposta
            String responseWithError = ErrorInjector.injectError(responseToUse, currentErrorType);
            
            gui.addLog("🔧 Resposta original: " + responseToUse);
            gui.addLog("💥 Resposta com erro: " + responseWithError);
            
            return responseWithError;
            
        } catch (Exception e) {
            logger.error("Erro ao gerar resposta com erro", e);
            gui.addLog("❌ Erro ao gerar resposta: " + e.getMessage());
            
            // Fallback para resposta de erro genérica
            return "{\"operacao\": \"erro\", \"status\": false, \"info\": \"Erro interno do servidor mock\"}";
        }
    }
    
    /**
     * Determina o tipo de resposta apropriado baseado na operação recebida
     */
    private String determineResponseType(String receivedMessage) {
        try {
            // Parse básico para extrair operação
            if (receivedMessage.contains("\"operacao\"")) {
                if (receivedMessage.contains("\"conectar\"")) {
                    return "{\"operacao\": \"conectar\", \"status\": true, \"info\": \"Servidor conectado com sucesso\"}";
                } else if (receivedMessage.contains("\"usuario_login\"")) {
                    return "{\"operacao\": \"usuario_login\", \"status\": true, \"info\": \"Login realizado com sucesso\", \"token\": \"token123\"}";
                } else if (receivedMessage.contains("\"usuario_ler\"")) {
                    return "{\"operacao\": \"usuario_ler\", \"status\": true, \"info\": \"Dados obtidos com sucesso\", \"usuario\": {\"cpf\": \"123.456.789-01\", \"nome\": \"João Silva\", \"saldo\": 1000.00}}";
                } else if (receivedMessage.contains("\"transacao_ler\"")) {
                    return "{\"operacao\": \"transacao_ler\", \"status\": true, \"info\": \"Transações obtidas com sucesso\", \"transacoes\": []}";
                } else if (receivedMessage.contains("\"usuario_criar\"")) {
                    return "{\"operacao\": \"usuario_criar\", \"status\": true, \"info\": \"Usuário criado com sucesso\"}";
                } else if (receivedMessage.contains("\"transacao_criar\"")) {
                    return "{\"operacao\": \"transacao_criar\", \"status\": true, \"info\": \"Transação realizada com sucesso\"}";
                } else if (receivedMessage.contains("\"depositar\"")) {
                    return "{\"operacao\": \"depositar\", \"status\": true, \"info\": \"Depósito realizado com sucesso\"}";
                } else if (receivedMessage.contains("\"usuario_logout\"")) {
                    return "{\"operacao\": \"usuario_logout\", \"status\": true, \"info\": \"Logout realizado com sucesso\"}";
                }
            }
            
            // Resposta padrão
            return "{\"operacao\": \"resposta_generica\", \"status\": true, \"info\": \"Resposta padrão do servidor mock\"}";
            
        } catch (Exception e) {
            logger.error("Erro ao determinar tipo de resposta", e);
            return null;
        }
    }
    
    /**
     * Atualiza configurações do servidor em tempo real
     */
    public void updateConfiguration(ErrorType errorType, String responseTemplate) {
        this.currentErrorType = errorType;
        this.responseTemplate = responseTemplate;
        
        if (running.get()) {
            gui.addLog("🔄 Configuração atualizada em tempo real");
            gui.addLog("🔧 Novo tipo de erro: " + errorType.getDisplayName());
        }
    }
    
    /**
     * Retorna estatísticas do servidor
     */
    public String getStats() {
        return String.format("Servidor Mock - Porta: %d | Conexões: %d | Status: %s | Erro: %s",
            port,
            connectionCount.get(),
            running.get() ? "Ativo" : "Parado",
            currentErrorType != null ? currentErrorType.getDisplayName() : "N/A"
        );
    }
    
    public boolean isRunning() {
        return running.get();
    }
    
    public int getPort() {
        return port;
    }
    
    public int getConnectionCount() {
        return connectionCount.get();
    }
}