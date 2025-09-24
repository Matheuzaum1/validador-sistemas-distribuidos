package com.distribuidos.client;

import com.distribuidos.common.MessageBuilder;
import validador.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

public class ClientConnection {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnection.class);
    
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected = false;
    private ClientGUI clientGUI;
    
    public ClientConnection(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }
    
    public boolean connect(String serverHost, int serverPort) {
        try {
            socket = new Socket(serverHost, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            
            clientGUI.addLogMessage("Conectado ao servidor: " + serverHost + ":" + serverPort);
            logger.info("Conectado ao servidor: {}:{}", serverHost, serverPort);
            
            return true;
        } catch (IOException e) {
            logger.error("Erro ao conectar ao servidor", e);
            clientGUI.addLogMessage("Erro ao conectar: " + e.getMessage());
            return false;
        }
    }
    
    public void disconnect() {
        try {
            connected = false;
            
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            
            clientGUI.addLogMessage("Desconectado do servidor");
            logger.info("Desconectado do servidor");
            
        } catch (IOException e) {
            logger.error("Erro ao desconectar", e);
        }
    }
    
    public String sendMessage(String message) {
        if (!connected || out == null || in == null) {
            throw new RuntimeException("Não conectado ao servidor");
        }
        
        try {
            // Valida mensagem antes de enviar
            Validator.validateClient(message);
            
            // Envia mensagem
            out.println(message);
            clientGUI.addLogMessage("Enviado: " + message);
            
            // Recebe resposta
            String response = in.readLine();
            if (response == null) {
                throw new IOException("Conexão perdida com o servidor");
            }
            
            clientGUI.addLogMessage("Recebido: " + response);
            
            // Valida resposta
            Validator.validateServer(response);
            
            return response;
            
        } catch (IOException e) {
            logger.error("Erro na comunicação com servidor", e);
            clientGUI.addLogMessage("Erro de comunicação: " + e.getMessage());
            disconnect();
            throw new RuntimeException("Erro de comunicação: " + e.getMessage());
        } catch (Exception e) {
            // Validation errors (from Validator) are surfaced here. For token-related issues,
            // present a friendlier message and return a well-formed error JSON so callers
            // handle it uniformly.
            logger.error("Erro de validação", e);
            clientGUI.addLogMessage("Erro de validação: " + e.getMessage());

            String lower = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (lower.contains("token") || lower.contains("campo 'token'") || lower.contains("campo \"token\"") ) {
                // Show a user-friendly dialog and return an error JSON indicating invalid token
                javax.swing.SwingUtilities.invokeLater(() -> {
                    javax.swing.JOptionPane.showMessageDialog(null,
                        "Token inválido — por favor reconecte ou efetue login novamente.",
                        "Token inválido",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                });

                return com.distribuidos.common.MessageBuilder.buildErrorResponse("validation_error",
                    "Token inválido - reconecte ou faça login novamente");
            }

            // For other validation errors, rethrow to let callers decide how to present them.
            throw new RuntimeException("Erro de validação: " + e.getMessage());
        }
    }
    
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
    
    // Métodos de conveniência para operações específicas
    public String login(String cpf, String senha) {
        String message = MessageBuilder.buildLoginMessage(cpf, senha);
        return sendMessage(message);
    }
    
    public String logout(String token) {
        String message = MessageBuilder.buildLogoutMessage(token);
        return sendMessage(message);
    }
    
    public String createUser(String nome, String cpf, String senha) {
        String message = MessageBuilder.buildCreateUserMessage(nome, cpf, senha);
        return sendMessage(message);
    }
    
    public String readUser(String token) {
        String message = MessageBuilder.buildReadUserMessage(token);
        return sendMessage(message);
    }
    
    public String updateUser(String token, String nome, String senha) {
        String message = MessageBuilder.buildUpdateUserMessage(token, nome, senha);
        return sendMessage(message);
    }
    
    public String deleteUser(String token) {
        String message = MessageBuilder.buildDeleteUserMessage(token);
        return sendMessage(message);
    }

    // Métodos para transações
    public String transfer(String token, String cpfDestino, double valor) {
        String message = MessageBuilder.buildTransferMessage(token, cpfDestino, valor);
        return sendMessage(message);
    }

    public String deposit(String token, double valor) {
        String message = MessageBuilder.buildDepositMessage(token, valor);
        return sendMessage(message);
    }
}