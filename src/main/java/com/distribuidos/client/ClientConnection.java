package com.distribuidos.client;

import com.distribuidos.common.MessageBuilder;
import com.distribuidos.common.MDCManager;
import com.distribuidos.exception.ConnectionException;
import com.distribuidos.handler.GlobalExceptionHandler;
import validador.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

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
    
    /**
     * Testa a conectividade básica com o servidor antes de tentar conectar
     */
    public boolean testConnectivity(String serverHost, int serverPort) {
        try {
            clientGUI.addLogMessage("=== TESTE DE CONECTIVIDADE ===");
            clientGUI.addLogMessage("Testando alcançabilidade de " + serverHost + "...");
            
            // Teste de alcançabilidade do host
            InetAddress address = InetAddress.getByName(serverHost);
            clientGUI.addLogMessage("✓ Host resolvido: " + address.getHostAddress());
            
            // Teste de ping (se possível)
            boolean reachable = address.isReachable(3000);
            if (reachable) {
                clientGUI.addLogMessage("✓ Host alcançável (ping respondeu)");
            } else {
                clientGUI.addLogMessage("⚠ Host pode não responder ping (normal em alguns casos)");
            }
            
            // Teste de conectividade da porta
            clientGUI.addLogMessage("Testando porta " + serverPort + "...");
            try (Socket testSocket = new Socket()) {
                testSocket.connect(new InetSocketAddress(serverHost, serverPort), 2000);
                clientGUI.addLogMessage("✓ Porta " + serverPort + " está aberta e acessível");
                clientGUI.addLogMessage("=== TESTE CONCLUÍDO: SUCESSO ===");
                return true;
            } catch (IOException e) {
                clientGUI.addLogMessage("✗ Porta " + serverPort + " não está acessível");
                clientGUI.addLogMessage("Erro: " + e.getMessage());
                clientGUI.addLogMessage("=== TESTE CONCLUÍDO: FALHA ===");
                return false;
            }
            
        } catch (UnknownHostException e) {
            clientGUI.addLogMessage("✗ Erro ao resolver hostname: " + e.getMessage());
            clientGUI.addLogMessage("=== TESTE CONCLUÍDO: FALHA ===");
            return false;
        } catch (IOException e) {
            clientGUI.addLogMessage("✗ Erro de rede: " + e.getMessage());
            clientGUI.addLogMessage("=== TESTE CONCLUÍDO: FALHA ===");
            return false;
        }
    }
    
    /**
     * Conecta ao servidor com retry automático e logging estruturado.
     */
    public boolean connect(String serverHost, int serverPort) throws IOException {
        MDCManager.setUserId("cliente-" + serverHost);
        
        try {
            logger.info("Iniciando conexão com servidor {}:{}", serverHost, serverPort);
            clientGUI.addLogMessage("=== INICIANDO CONEXÃO ===");
            clientGUI.addLogMessage("Servidor: " + serverHost);
            clientGUI.addLogMessage("Porta: " + serverPort);
            clientGUI.addLogMessage("Timeout de conexão: 5 segundos");
            clientGUI.addLogMessage("Timeout de leitura: 10 segundos");
            
            // Conectar ao servidor
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverHost, serverPort), 5000);
            socket.setSoTimeout(10000);
            
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            connected = true;
            
            // Log informações da conexão estabelecida
            String localInfo = socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort();
            String remoteInfo = socket.getRemoteSocketAddress().toString();
            
            clientGUI.addLogMessage("✓ Socket TCP conectado com sucesso!");
            clientGUI.addLogMessage("Local: " + localInfo);
            clientGUI.addLogMessage("Remoto: " + remoteInfo);
            logger.info("Conectado ao servidor: {}:{} (local: {})", serverHost, serverPort, localInfo);
            
            // Enviar mensagem de conexão conforme protocolo
            try {
                clientGUI.addLogMessage("Enviando mensagem de protocolo 'conectar'...");
                String response = connectToServer();
                if (MessageBuilder.extractStatus(response)) {
                    clientGUI.addLogMessage("✓ Protocolo de conexão concluído com sucesso");
                    clientGUI.addLogMessage("=== CONEXÃO ESTABELECIDA ===");
                    logger.info("Protocolo de conexão concluído com sucesso");
                    return true;
                } else {
                    String errorInfo = MessageBuilder.extractInfo(response);
                    clientGUI.addLogMessage("✗ Falha no protocolo de conexão: " + errorInfo);
                    clientGUI.addLogMessage("=== CONEXÃO REJEITADA ===");
                    logger.error("Falha no protocolo de conexão: {}", errorInfo);
                    disconnect();
                    return false;
                }
            } catch (Exception e) {
                logger.error("Erro no protocolo de conexão", e);
                clientGUI.addLogMessage("✗ Erro no protocolo de conexão: " + e.getMessage());
                clientGUI.addLogMessage("=== CONEXÃO FALHOU ===");
                GlobalExceptionHandler.logDetailedError("protocolo de conexão", e);
                disconnect();
                return false;
            }
            
        } catch (ConnectionException e) {
            logger.error("Falha na conexão (ConnectionException): {}:{} - {}", e.getHost(), e.getPort(), e.getMessage());
            clientGUI.addLogMessage("=== DIAGNÓSTICO DE CONEXÃO ===");
            clientGUI.addLogMessage("✗ Falha após " + e.getRetryCount() + " tentativas");
            clientGUI.addLogMessage(e.getMessage());
            GlobalExceptionHandler.handleException(e, "conexão ao servidor");
            disconnect();
            return false;
            
        } catch (IOException e) {
            logger.error("Erro ao conectar ao servidor {}:{} - {}", serverHost, serverPort, e.getMessage());
            GlobalExceptionHandler.logDetailedError("conexão IoException", e);
            clientGUI.addLogMessage("=== DIAGNÓSTICO DE CONEXÃO ===");
            
            String errorMsg = "✗ Falha na conexão: ";
            
            if (e instanceof ConnectException) {
                errorMsg += "Conexão recusada";
                clientGUI.addLogMessage(errorMsg);
                clientGUI.addLogMessage("• O servidor não está rodando na porta " + serverPort);
                clientGUI.addLogMessage("• Firewall bloqueando a conexão");
                clientGUI.addLogMessage("Verifique se o servidor está rodando");
                
            } else if (e instanceof SocketTimeoutException) {
                errorMsg += "Timeout de conexão";
                clientGUI.addLogMessage(errorMsg);
                clientGUI.addLogMessage("• Servidor demorou mais de 5 segundos");
                clientGUI.addLogMessage("• Problemas de rede ou latência alta");
                clientGUI.addLogMessage("Tente novamente em alguns segundos");
                
            } else if (e instanceof UnknownHostException) {
                errorMsg += "Host não encontrado";
                clientGUI.addLogMessage(errorMsg);
                clientGUI.addLogMessage("• Endereço incorreto: " + serverHost);
                clientGUI.addLogMessage("• Verifique o endereço do servidor");
                
            } else if (e instanceof NoRouteToHostException) {
                errorMsg += "Rota não encontrada";
                clientGUI.addLogMessage(errorMsg);
                clientGUI.addLogMessage("• Não há rota de rede para " + serverHost);
                clientGUI.addLogMessage("• Verifique conectividade de rede");
                
            } else if (e instanceof PortUnreachableException) {
                errorMsg += "Porta inacessível";
                clientGUI.addLogMessage(errorMsg);
                clientGUI.addLogMessage("• Porta " + serverPort + " não está acessível");
                clientGUI.addLogMessage("• Verifique configurações de firewall");
                
            } else {
                errorMsg += e.getClass().getSimpleName() + ": " + e.getMessage();
                clientGUI.addLogMessage(errorMsg);
                clientGUI.addLogMessage("• Erro inesperado de rede");
                clientGUI.addLogMessage("• Tente novamente");
            }
            
            clientGUI.addLogMessage("=== CONEXÃO FALHOU ===");
            disconnect();
            return false;
        }
    }
    
    public void disconnect() {
        try {
            if (connected) {
                clientGUI.addLogMessage("=== DESCONECTANDO ===");
                logger.info("Iniciando desconexão do servidor");
            }
            
            connected = false;
            
            if (in != null) {
                in.close();
                clientGUI.addLogMessage("✓ Stream de entrada fechado");
            }
            if (out != null) {
                out.close();
                clientGUI.addLogMessage("✓ Stream de saída fechado");
            }
            if (socket != null && !socket.isClosed()) {
                String remoteInfo = socket.getRemoteSocketAddress().toString();
                socket.close();
                clientGUI.addLogMessage("✓ Socket TCP fechado");
                clientGUI.addLogMessage("Conexão com " + remoteInfo + " encerrada");
            }
            
            clientGUI.addLogMessage("=== DESCONECTADO ===");
            logger.info("Desconectado do servidor com sucesso");
            
        } catch (IOException e) {
            logger.error("Erro durante desconexão", e);
            clientGUI.addLogMessage("⚠ Erro durante desconexão: " + e.getMessage());
            clientGUI.addLogMessage("=== DESCONEXÃO FORÇADA ===");
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
    
    /**
     * Retorna informações detalhadas sobre o estado da conexão
     */
    public String getConnectionInfo() {
        if (!isConnected()) {
            return "Não conectado";
        }
        
        try {
            String localAddr = socket.getLocalAddress().getHostAddress();
            int localPort = socket.getLocalPort();
            String remoteAddr = socket.getInetAddress().getHostAddress();
            int remotePort = socket.getPort();
            String remoteHost = socket.getInetAddress().getHostName();
            
            return String.format("Conectado:\n" +
                "• Local: %s:%d\n" +
                "• Remoto: %s:%d (%s)\n" +
                "• Socket: %s\n" +
                "• Timeout leitura: %dms",
                localAddr, localPort, 
                remoteAddr, remotePort, remoteHost,
                socket.isClosed() ? "Fechado" : "Aberto",
                socket.getSoTimeout());
        } catch (Exception e) {
            return "Conectado (erro ao obter detalhes: " + e.getMessage() + ")";
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
    
    public String connectToServer() {
        String message = MessageBuilder.buildConnectMessage();
        return sendMessage(message);
    }
}