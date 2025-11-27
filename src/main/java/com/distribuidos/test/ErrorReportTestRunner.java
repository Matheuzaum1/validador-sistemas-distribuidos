package com.distribuidos.test;

import com.distribuidos.client.ClientConnection;
import com.distribuidos.client.ClientGUI;
import com.distribuidos.common.MessageBuilder;
import com.distribuidos.server.ServerHandler;
import com.distribuidos.server.ServerGUI;
import validador.Validator;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe para testar o sistema de relatório de erros conforme protocolo 4.11
 * Testa cenários onde o servidor envia respostas malformadas e o cliente deve reportar erro_servidor
 */
public class ErrorReportTestRunner {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Teste de Relatório de Erros - Protocolo 4.11");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            JTextArea logArea = new JTextArea(25, 80);
            logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            logArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(logArea);
            
            JPanel buttonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Botões para testes específicos
            JButton testNullOperacaoBtn = new JButton("Teste: Operação Null");
            JButton testMissingOperacaoBtn = new JButton("Teste: Operação Ausente");
            JButton testLoginWithoutTokenBtn = new JButton("Teste: Login sem Token");
            JButton testUserReadNullBtn = new JButton("Teste: Usuario_ler com Usuario Null");
            JButton testStatusFalseBtn = new JButton("Teste: Status False");
            JButton clearLogBtn = new JButton("Limpar Log");
            
            gbc.gridx = 0; gbc.gridy = 0;
            buttonPanel.add(testNullOperacaoBtn, gbc);
            gbc.gridx = 1;
            buttonPanel.add(testMissingOperacaoBtn, gbc);
            gbc.gridx = 2;
            buttonPanel.add(testLoginWithoutTokenBtn, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            buttonPanel.add(testUserReadNullBtn, gbc);
            gbc.gridx = 1;
            buttonPanel.add(testStatusFalseBtn, gbc);
            gbc.gridx = 2;
            buttonPanel.add(clearLogBtn, gbc);
            
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.add(buttonPanel, BorderLayout.SOUTH);
            
            // Inicializar servidor de teste em thread separada
            MockServer mockServer = new MockServer(logArea);
            new Thread(() -> mockServer.start()).start();
            
            // Aguardar servidor iniciar
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            
            // Actions dos botões
            testNullOperacaoBtn.addActionListener(e -> testNullOperacao(logArea, mockServer));
            testMissingOperacaoBtn.addActionListener(e -> testMissingOperacao(logArea, mockServer));
            testLoginWithoutTokenBtn.addActionListener(e -> testLoginWithoutToken(logArea, mockServer));
            testUserReadNullBtn.addActionListener(e -> testUserReadNull(logArea, mockServer));
            testStatusFalseBtn.addActionListener(e -> testStatusFalse(logArea, mockServer));
            clearLogBtn.addActionListener(e -> logArea.setText(""));
            
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            logArea.append("=== TESTE DE RELATÓRIO DE ERROS - PROTOCOLO 4.11 ===\n");
            logArea.append("Servidor mock iniciado na porta 12345\n");
            logArea.append("Clique nos botões para executar os testes\n\n");
        });
    }
    
    private static void testNullOperacao(JTextArea logArea, MockServer mockServer) {
        logArea.append("\n=== TESTE: Operação Null ===\n");
        mockServer.setNextResponse("{\"operacao\": null, \"status\": true, \"info\": \"Teste com operacao null\"}");
        testClientConnection(logArea);
    }
    
    private static void testMissingOperacao(JTextArea logArea, MockServer mockServer) {
        logArea.append("\n=== TESTE: Operação Ausente ===\n");
        mockServer.setNextResponse("{\"status\": true, \"info\": \"Teste sem campo operacao\"}");
        testClientConnection(logArea);
    }
    
    private static void testLoginWithoutToken(JTextArea logArea, MockServer mockServer) {
        logArea.append("\n=== TESTE: Login sem Token ===\n");
        mockServer.setNextResponse("{\"operacao\": \"usuario_login\", \"status\": true, \"info\": \"Login realizado\", \"token\": null}");
        testClientConnection(logArea);
    }
    
    private static void testUserReadNull(JTextArea logArea, MockServer mockServer) {
        logArea.append("\n=== TESTE: Usuario_ler com Usuario Null ===\n");
        mockServer.setNextResponse("{\"operacao\": \"usuario_ler\", \"status\": true, \"info\": \"Dados obtidos\", \"usuario\": null}");
        testClientConnection(logArea);
    }
    
    private static void testStatusFalse(JTextArea logArea, MockServer mockServer) {
        logArea.append("\n=== TESTE: Status False ===\n");
        mockServer.setNextResponse("{\"operacao\": \"usuario_login\", \"status\": false, \"info\": \"Erro de autenticação\"}");
        testClientConnection(logArea);
    }
    
    private static void testClientConnection(JTextArea logArea) {
        try {
            // Criar um cliente mock sem GUI para teste
            MockClientGUI mockGUI = new MockClientGUI(logArea);
            ClientConnection client = new ClientConnection(mockGUI);
            
            if (client.connect("localhost", 12345)) {
                // Simular uma operação qualquer que será respondida pelo mock server
                String response = client.sendMessage("{\"operacao\": \"conectar\"}");
                logArea.append("Resposta recebida: " + response + "\n");
            }
            
            client.disconnect();
            
        } catch (Exception e) {
            logArea.append("Erro durante teste: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
    
    // Classe para simular ClientGUI em testes
    static class MockClientGUI extends ClientGUI {
        private final JTextArea logArea;
        
        public MockClientGUI(JTextArea logArea) {
            this.logArea = logArea;
        }
        
        @Override
        public void addLogMessage(String message) {
            SwingUtilities.invokeLater(() -> {
                logArea.append(message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }
    
    // Servidor mock para simular respostas malformadas
    static class MockServer {
        private ServerSocket serverSocket;
        private String nextResponse = null;
        private final JTextArea logArea;
        
        public MockServer(JTextArea logArea) {
            this.logArea = logArea;
        }
        
        public void setNextResponse(String response) {
            this.nextResponse = response;
        }
        
        public void start() {
            try {
                serverSocket = new ServerSocket(12345);
                SwingUtilities.invokeLater(() -> logArea.append("Servidor mock iniciado na porta 12345\n"));
                
                while (!serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(() -> handleClient(clientSocket)).start();
                    } catch (IOException e) {
                        if (!serverSocket.isClosed()) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        private void handleClient(Socket clientSocket) {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)
            ) {
                String request;
                while ((request = in.readLine()) != null) {
                    final String finalRequest = request;
                    SwingUtilities.invokeLater(() -> logArea.append("Servidor recebeu: " + finalRequest + "\n"));
                    
                    String response;
                    if (nextResponse != null) {
                        response = nextResponse;
                        nextResponse = null; // Usar apenas uma vez
                    } else {
                        // Resposta padrão para conectar
                        response = "{\"operacao\": \"conectar\", \"status\": true, \"info\": \"Conectado\"}";
                    }
                    
                    final String finalResponse = response;
                    out.println(response);
                    SwingUtilities.invokeLater(() -> logArea.append("Servidor enviou: " + finalResponse + "\n"));
                }
                
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> logArea.append("Erro no servidor mock: " + e.getMessage() + "\n"));
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
        
        public void stop() {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}