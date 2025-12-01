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
            
            // ========== VALIDAÇÃO DE PROTOCOLO (Seção 4.11 e 5.2) ==========
            // PRIMEIRO verificar violações de protocolo antes de usar o Validator
            // Isso é necessário porque o Validator lança exceção antes de podermos enviar erro_servidor
            com.fasterxml.jackson.databind.JsonNode responseNode;
            try {
                responseNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response);
            } catch (Exception parseEx) {
                // JSON malformado - enviar erro_servidor
                sendServerErrorReport(null, "Resposta do servidor não é um JSON válido: " + parseEx.getMessage());
                throw new RuntimeException("JSON inválido recebido do servidor");
            }
            
            // Verificar se "operacao" está ausente ou nulo - conforme seção 4.11/5.2
            if (!responseNode.has("operacao") || responseNode.get("operacao").isNull()) {
                String errorDesc = "O campo 'operacao' estava ausente ou nulo na resposta do servidor";
                logger.error("PROTOCOLO VIOLATION: {}", errorDesc);
                clientGUI.addLogMessage("❌ PROTOCOLO VIOLATION: " + errorDesc);
                clientGUI.addLogMessage("📄 JSON recebido: " + response);
                sendServerErrorReport(null, errorDesc);
                throw new RuntimeException("Violação de protocolo: " + errorDesc);
            }
            
            String operacao = responseNode.get("operacao").asText();
            
            // Verificar se "status" está ausente ou nulo - conforme seção 5.2
            if (!responseNode.has("status") || responseNode.get("status").isNull()) {
                String errorDesc = "O campo 'status' estava ausente ou nulo na resposta do servidor";
                logger.error("PROTOCOLO VIOLATION: {}", errorDesc);
                clientGUI.addLogMessage("❌ PROTOCOLO VIOLATION: " + errorDesc);
                clientGUI.addLogMessage("📄 JSON recebido: " + response);
                sendServerErrorReport(operacao, errorDesc);
                throw new RuntimeException("Violação de protocolo: " + errorDesc);
            }
            
            boolean status = responseNode.get("status").asBoolean();
            
            // Verificar se "info" está ausente ou nulo - conforme seção 5.2
            if (!responseNode.has("info") || responseNode.get("info").isNull()) {
                String errorDesc = "O campo 'info' estava ausente ou nulo na resposta do servidor";
                logger.error("PROTOCOLO VIOLATION: {}", errorDesc);
                clientGUI.addLogMessage("❌ PROTOCOLO VIOLATION: " + errorDesc);
                clientGUI.addLogMessage("📄 JSON recebido: " + response);
                sendServerErrorReport(operacao, errorDesc);
                throw new RuntimeException("Violação de protocolo: " + errorDesc);
            }
            
            // ========== TRATAMENTO PARA status: false (Seção 5.1 - Erros padrões) ==========
            // Conforme seção 5.1: Quando o cliente receber status:false, ele deve tratar o erro
            // localmente e mostrar a info ao usuário. NÃO deve enviar erro_servidor!
            // O erro_servidor (seção 4.11) só é enviado para VIOLAÇÕES DE PROTOCOLO.
            if (!status) {
                String errorInfo = responseNode.get("info").asText();
                logger.warn("Servidor retornou erro para {}: {}", operacao, errorInfo);
                clientGUI.addLogMessage("⚠ Servidor retornou erro: " + errorInfo);
                
                // Retornar a resposta original para que o cliente possa tratá-la normalmente
                // (conforme seção 5.1, o cliente trata o erro pela operação e mostra info ao usuário)
                return response;
            }
            
            // ========== VALIDAÇÃO DE CAMPOS OBRIGATÓRIOS PARA status: true (Seção 4.11) ==========
            // Verificar campos obrigatórios que devem existir quando status é true
            if (status) {
                switch (operacao) {
                    case "usuario_login":
                        if (!responseNode.has("token") || responseNode.get("token").isNull()) {
                            String errorDesc = "Resposta usuario_login chegou sem campo 'token' ou token é nulo";
                            logger.error("PROTOCOLO VIOLATION: {}", errorDesc);
                            clientGUI.addLogMessage("❌ PROTOCOLO VIOLATION: " + errorDesc);
                            clientGUI.addLogMessage("📄 JSON recebido: " + response);
                            sendServerErrorReport("usuario_login", errorDesc);
                            throw new RuntimeException("Violação de protocolo: " + errorDesc);
                        }
                        break;
                        
                    case "usuario_ler":
                        if (!responseNode.has("usuario") || responseNode.get("usuario").isNull()) {
                            String errorDesc = "Resposta usuario_ler chegou sem campo 'usuario' ou usuario é nulo";
                            logger.error("PROTOCOLO VIOLATION: {}", errorDesc);
                            clientGUI.addLogMessage("❌ PROTOCOLO VIOLATION: " + errorDesc);
                            clientGUI.addLogMessage("📄 JSON recebido: " + response);
                            sendServerErrorReport("usuario_ler", errorDesc);
                            throw new RuntimeException("Violação de protocolo: " + errorDesc);
                        }
                        break;
                        
                    case "transacao_ler":
                        if (!responseNode.has("transacoes") || responseNode.get("transacoes").isNull()) {
                            String errorDesc = "Resposta transacao_ler chegou sem campo 'transacoes' ou transacoes é nulo";
                            logger.error("PROTOCOLO VIOLATION: {}", errorDesc);
                            clientGUI.addLogMessage("❌ PROTOCOLO VIOLATION: " + errorDesc);
                            clientGUI.addLogMessage("📄 JSON recebido: " + response);
                            sendServerErrorReport("transacao_ler", errorDesc);
                            throw new RuntimeException("Violação de protocolo: " + errorDesc);
                        }
                        break;
                }
            }
            
            // Agora que validamos as violações de protocolo, usar o Validator para validação completa
            try {
                Validator.validateServer(response);
            } catch (Exception valEx) {
                // Se o Validator detectar algum erro que não tratamos, enviar erro_servidor
                String errorDesc = "Validação de protocolo falhou: " + valEx.getMessage();
                logger.error("PROTOCOLO VIOLATION: {}", errorDesc);
                clientGUI.addLogMessage("❌ PROTOCOLO VIOLATION: " + errorDesc);
                sendServerErrorReport(operacao, errorDesc);
                throw new RuntimeException("Violação de protocolo: " + errorDesc);
            }
            // ===================================================================
            
            return response;
            
        } catch (IOException e) {
            logger.error("Erro na comunicação com servidor", e);
            clientGUI.addLogMessage("Erro de comunicação: " + e.getMessage());
            disconnect();
            throw new RuntimeException("Erro de comunicação: " + e.getMessage());
        } catch (RuntimeException e) {
            // Re-throw RuntimeExceptions (incluindo violações de protocolo)
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado", e);
            clientGUI.addLogMessage("Erro inesperado: " + e.getMessage());
            throw new RuntimeException("Erro inesperado: " + e.getMessage());
        }
    }
    
    /**
     * Envia mensagem erro_servidor ao servidor conforme seção 4.11 do protocolo.
     * Usado quando o cliente detecta uma violação de protocolo na resposta do servidor.
     * 
     * @param operacaoEnviada A operação que causou o erro (pode ser null se operacao estava ausente)
     * @param descricaoErro Descrição do erro encontrado
     */
    private void sendServerErrorReport(String operacaoEnviada, String descricaoErro) {
        try {
            String erroMsg = MessageBuilder.buildServerErrorMessage(operacaoEnviada, descricaoErro);
            
            // Validação antes de enviar
            Validator.validateClient(erroMsg);
            
            out.println(erroMsg);
            out.flush();
            
            clientGUI.addLogMessage("📤 Erro_servidor enviado: " + erroMsg);
            logger.info("📤 Enviando erro_servidor: {}", erroMsg);
            
            // Aguardar resposta do servidor para confirmar recebimento
            String confirmacao = in.readLine();
            if (confirmacao != null) {
                clientGUI.addLogMessage("📥 Erro_servidor confirmado: " + confirmacao);
                logger.info("📥 Erro_servidor confirmado pelo servidor: {}", confirmacao);
            }
            
        } catch (Exception ex) {
            logger.error("Erro ao enviar erro_servidor", ex);
            clientGUI.addLogMessage("❌ Falha ao enviar erro_servidor: " + ex.getMessage());
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
    
    public String readTransactions(String token, String dataInicial, String dataFinal) {
        String message = MessageBuilder.buildReadTransactionsMessage(token, dataInicial, dataFinal);
        return sendMessage(message);
    }
    
    public String connectToServer() {
        String message = MessageBuilder.buildConnectMessage();
        return sendMessage(message);
    }
}