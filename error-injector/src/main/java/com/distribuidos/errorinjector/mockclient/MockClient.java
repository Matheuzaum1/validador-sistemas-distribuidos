package com.distribuidos.errorinjector.mockclient;

import com.distribuidos.errorinjector.core.ErrorInjector;
import com.distribuidos.errorinjector.core.ErrorType;
import com.distribuidos.errorinjector.ui.ErrorInjectorGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cliente mock que envia mensagens com erros injetados
 * Útil para testar como o servidor real lida com mensagens malformadas
 */
public class MockClient {
    private static final Logger logger = LoggerFactory.getLogger(MockClient.class);
    
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicInteger messagesSent = new AtomicInteger(0);
    private ErrorInjectorGUI gui;
    private String host;
    private int port;
    
    public MockClient(ErrorInjectorGUI gui) {
        this.gui = gui;
    }
    
    /**
     * Conecta ao servidor especificado
     */
    public boolean connect(String host, int port) {
        if (connected.get()) {
            gui.addLog("❌ Cliente mock já está conectado");
            return false;
        }
        
        this.host = host;
        this.port = port;
        
        try {
            gui.addLog("🔄 Conectando ao servidor " + host + ":" + port + "...");
            
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8), true);
            
            connected.set(true);
            gui.addLog("✅ Cliente mock conectado com sucesso");
            gui.addLog("🔗 " + socket.getLocalSocketAddress() + " → " + socket.getRemoteSocketAddress());
            
            return true;
            
        } catch (IOException e) {
            logger.error("Erro ao conectar ao servidor {}:{}", host, port, e);
            gui.addLog("❌ Erro ao conectar: " + e.getMessage());
            connected.set(false);
            cleanup();
            return false;
        }
    }
    
    /**
     * Desconecta do servidor
     */
    public void disconnect() {
        if (!connected.get()) {
            gui.addLog("⚠️ Cliente mock já estava desconectado");
            return;
        }
        
        connected.set(false);
        cleanup();
        
        gui.addLog("👋 Cliente mock desconectado");
        gui.addLog("📊 Total de mensagens enviadas: " + messagesSent.get());
    }
    
    /**
     * Envia mensagem com erro injetado
     */
    public boolean sendMessage(String originalMessage, ErrorType errorType) {
        if (!connected.get()) {
            gui.addLog("❌ Cliente não está conectado");
            return false;
        }
        
        try {
            // Injetar erro na mensagem
            String messageWithError = ErrorInjector.injectError(originalMessage, errorType);
            
            gui.addLog("📝 Mensagem original: " + originalMessage);
            gui.addLog("💥 Mensagem com erro: " + messageWithError);
            gui.addLog("🔧 Tipo de erro: " + errorType.getDisplayName());
            
            // Enviar mensagem
            writer.println(messageWithError);
            int messageId = messagesSent.incrementAndGet();
            gui.addLog("📤 Mensagem #" + messageId + " enviada");
            
            // Tentar ler resposta do servidor
            try {
                socket.setSoTimeout(5000); // 5 segundos de timeout
                String response = reader.readLine();
                
                if (response != null) {
                    gui.addLog("📥 Resposta recebida: " + response);
                    analyzeServerResponse(response, errorType);
                } else {
                    gui.addLog("⚠️ Servidor fechou a conexão");
                    connected.set(false);
                }
                
            } catch (IOException e) {
                gui.addLog("⏰ Timeout ao aguardar resposta do servidor");
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem com erro", e);
            gui.addLog("❌ Erro ao enviar mensagem: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envia sequência de mensagens com diferentes tipos de erro
     */
    public void sendTestSequence(String[] messageTemplates, ErrorType[] errorTypes) {
        if (!connected.get()) {
            gui.addLog("❌ Cliente não está conectado para sequência de testes");
            return;
        }
        
        gui.addLog("🚀 Iniciando sequência de testes automatizada");
        gui.addLog("📋 " + messageTemplates.length + " mensagens, " + errorTypes.length + " tipos de erro");
        
        int testCount = 0;
        
        for (String template : messageTemplates) {
            for (ErrorType errorType : errorTypes) {
                if (errorType.canAffectClient()) {
                    testCount++;
                    gui.addLog("🧪 Teste #" + testCount + ": " + errorType.getDisplayName());
                    
                    boolean success = sendMessage(template, errorType);
                    if (!success) {
                        gui.addLog("❌ Teste #" + testCount + " falhou");
                        break;
                    }
                    
                    // Pausa entre testes
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    
                    if (!connected.get()) {
                        gui.addLog("⚠️ Conexão perdida durante sequência de testes");
                        break;
                    }
                }
            }
        }
        
        gui.addLog("🏁 Sequência de testes concluída: " + testCount + " testes executados");
    }
    
    /**
     * Analisa a resposta do servidor para detectar como lidou com o erro
     */
    private void analyzeServerResponse(String response, ErrorType sentErrorType) {
        try {
            gui.addLog("🔍 Analisando resposta do servidor...");
            
            // Verificar se resposta contém erro_servidor (protocolo 4.11)
            if (response.contains("\"operacao\"") && response.contains("\"erro_servidor\"")) {
                gui.addLog("✅ Servidor detectou erro e enviou erro_servidor (protocolo 4.11)");
                gui.addLog("🎯 Tipo de erro enviado: " + sentErrorType.getDisplayName());
                return;
            }
            
            // Verificar se servidor retornou erro genérico
            if (response.contains("\"status\"") && response.contains("false")) {
                gui.addLog("⚠️ Servidor retornou status:false (erro detectado)");
                return;
            }
            
            // Verificar se servidor respondeu normalmente (pode não ter detectado erro)
            if (response.contains("\"status\"") && response.contains("true")) {
                gui.addLog("❓ Servidor respondeu com sucesso (erro pode não ter sido detectado)");
                gui.addLog("💡 Verifique se o tipo de erro " + sentErrorType.getDisplayName() + " foi tratado");
                return;
            }
            
            // Resposta inesperada
            gui.addLog("❓ Resposta inesperada do servidor");
            
        } catch (Exception e) {
            logger.error("Erro ao analisar resposta do servidor", e);
            gui.addLog("❌ Erro ao analisar resposta: " + e.getMessage());
        }
    }
    
    /**
     * Testa conectividade básica sem injeção de erro
     */
    public boolean testBasicConnectivity() {
        if (!connected.get()) {
            gui.addLog("❌ Cliente não está conectado para teste de conectividade");
            return false;
        }
        
        try {
            gui.addLog("🔄 Testando conectividade básica...");
            
            // Enviar mensagem de conectar padrão
            String connectMessage = "{\"operacao\": \"conectar\"}";
            writer.println(connectMessage);
            gui.addLog("📤 Enviado: " + connectMessage);
            
            // Aguardar resposta
            socket.setSoTimeout(5000);
            String response = reader.readLine();
            
            if (response != null) {
                gui.addLog("📥 Resposta: " + response);
                gui.addLog("✅ Conectividade básica OK");
                return true;
            } else {
                gui.addLog("❌ Servidor não respondeu");
                return false;
            }
            
        } catch (IOException e) {
            logger.error("Erro no teste de conectividade", e);
            gui.addLog("❌ Erro no teste: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Limpa recursos de conexão
     */
    private void cleanup() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            logger.error("Erro ao limpar recursos", e);
        }
        
        reader = null;
        writer = null;
        socket = null;
    }
    
    /**
     * Retorna estatísticas do cliente
     */
    public String getStats() {
        return String.format("Cliente Mock - %s:%d | Mensagens: %d | Status: %s",
            host != null ? host : "N/A",
            port,
            messagesSent.get(),
            connected.get() ? "Conectado" : "Desconectado"
        );
    }
    
    public boolean isConnected() {
        return connected.get();
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public int getMessagesSent() {
        return messagesSent.get();
    }
}