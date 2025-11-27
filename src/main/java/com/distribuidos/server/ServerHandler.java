package com.distribuidos.server;

import com.distribuidos.common.ClientInfo;
import com.distribuidos.common.MessageBuilder;
import com.distribuidos.common.TokenManager;
import com.distribuidos.common.Usuario;
import com.distribuidos.database.DatabaseManager;
import validador.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerHandler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    
    // Contador estático para estatísticas de erros reportados pelos clientes
    private static final AtomicInteger errorReportsCount = new AtomicInteger(0);
    
    // Executor to perform reverse DNS lookups asynchronously to avoid blocking the handler thread
    private static final ExecutorService DNS_RESOLVER = Executors.newCachedThreadPool();
    private final Socket clientSocket;
    private final ServerGUI serverGUI;
    private final DatabaseManager dbManager;
    private final Map<String, ClientInfo> connectedClients;
    private BufferedReader in;
    private PrintWriter out;
    private ClientInfo clientInfo;
    private boolean isFirstOperation = true;  // Para validar que a primeira operação seja 'conectar'
    
    public ServerHandler(Socket clientSocket, ServerGUI serverGUI, Map<String, ClientInfo> connectedClients) {
        this.clientSocket = clientSocket;
        this.serverGUI = serverGUI;
        this.dbManager = DatabaseManager.getInstance();
        this.connectedClients = connectedClients;
        
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            
            // Cria informações do cliente
            String clientIP = clientSocket.getInetAddress().getHostAddress();
            int clientPort = clientSocket.getPort();
            String hostname = clientSocket.getInetAddress().getHostName();
            
            this.clientInfo = new ClientInfo(clientIP, clientPort, hostname);
            String clientKey = clientIP + ":" + clientPort;
            connectedClients.put(clientKey, clientInfo);

            // Resolve reverse DNS asynchronously to avoid blocking accept/handler startup
            DNS_RESOLVER.submit(() -> {
                try {
                    InetAddress addr = InetAddress.getByName(clientIP);
                    String resolved = addr.getCanonicalHostName();
                    if (resolved != null && !resolved.isEmpty() && !resolved.equals(clientIP)) {
                        clientInfo.setHostname(resolved);
                        if (this.serverGUI != null) {
                            serverGUI.updateConnectedClients();
                        }
                    }
                } catch (Exception ignored) {
                    // Ignore DNS resolution failures; keep IP/initial hostname as fallback
                }
            });

            if (this.serverGUI != null) {
                serverGUI.addLogMessage("Cliente conectado: " + clientIP + ":" + clientPort);
                serverGUI.updateConnectedClients();
            }
            
        } catch (IOException e) {
            logger.error("Erro ao inicializar handler do cliente", e);
        }
    }
    
    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                serverGUI.addLogMessage("Recebido de " + clientInfo.getIp() + ": " + inputLine);
                
                try {
                    // Valida a mensagem usando o Validator
                    Validator.validateClient(inputLine);
                    
                    // Processa a mensagem
                    String response = processMessage(inputLine);
                    
                    // Envia resposta
                    out.println(response);
                    if (this.serverGUI != null) {
                        serverGUI.addLogMessage("Enviado para " + clientInfo.getIp() + ": " + response);
                    }
                    
                } catch (Exception e) {
                    String operation = extractOperationSafely(inputLine);
                    String errorResponse = MessageBuilder.buildErrorResponse(operation, 
                        "Erro de validação: " + e.getMessage());
                    out.println(errorResponse);
                    if (this.serverGUI != null) {
                        serverGUI.addLogMessage("Erro enviado para " + clientInfo.getIp() + ": " + errorResponse);
                    }
                    logger.error("Erro ao processar mensagem do cliente", e);
                }
            }
        } catch (IOException e) {
            logger.error("Erro na comunicação com cliente", e);
        } finally {
            cleanup();
        }
    }
    
    private String processMessage(String message) {
        try {
            // ========== PROTOCOLO 5.2: Verificar se operacao existe ==========
            String operation;
            try {
                operation = MessageBuilder.extractOperation(message);
            } catch (Exception e) {
                // Se não conseguir extrair a operação, significa que o campo não existe ou é inválido
                logger.error("🔴 PROTOCOLO 5.2: Cliente enviou mensagem sem operacao válida. Encerrando conexão.");
                if (serverGUI != null) {
                    serverGUI.addLogMessage("❌ Cliente " + clientInfo.getIp() + " enviou mensagem sem operacao - encerrando conexão");
                }
                // Protocolo 5.2: servidor deve retornar null para encerrar conexão
                out.println("null");
                return null;
            }
            
            // Verificar se operacao é válida (não nula ou vazia)
            if (operation == null || operation.trim().isEmpty()) {
                logger.error("🔴 PROTOCOLO 5.2: operacao nula ou vazia. Encerrando conexão.");
                if (serverGUI != null) {
                    serverGUI.addLogMessage("❌ Cliente " + clientInfo.getIp() + " enviou operacao nula - encerrando conexão");
                }
                // Protocolo 5.2: servidor deve retornar null para encerrar conexão
                out.println("null");
                return null;
            }
            
            // Verificar se a operação existe no protocolo
            boolean operationExists = false;
            String[] validOperations = {"conectar", "usuario_login", "usuario_logout", "usuario_criar", 
                                       "usuario_ler", "usuario_atualizar", "usuario_deletar", 
                                       "transacao_criar", "transacao_ler", "depositar", "erro_servidor"};
            for (String validOp : validOperations) {
                if (validOp.equals(operation)) {
                    operationExists = true;
                    break;
                }
            }
            
            if (!operationExists) {
                logger.error("🔴 PROTOCOLO 5.2: operacao '{}' não existe no protocolo. Encerrando conexão.", operation);
                if (serverGUI != null) {
                    serverGUI.addLogMessage("❌ Cliente " + clientInfo.getIp() + " enviou operacao inválida '" + operation + "' - encerrando conexão");
                }
                // Protocolo 5.2: servidor deve retornar null para encerrar conexão
                out.println("null");
                return null;
            }
            // ================================================================
            
            // ========== PROTOCOLO 5.3: Verificar primeira operação ==========
            if (isFirstOperation && !"conectar".equals(operation)) {
                isFirstOperation = false; // Marca que já recebeu a primeira operação
                return MessageBuilder.buildErrorResponse(operation, 
                    "Erro, para receber uma operacao, a primeira operacao deve ser 'conectar'");
            }
            
            if (isFirstOperation && "conectar".equals(operation)) {
                isFirstOperation = false; // Marca que recebeu conectar corretamente
            }
            // ================================================================
            
            switch (operation) {
                case "usuario_login":
                    return handleLogin(message);
                case "usuario_logout":
                    return handleLogout(message);
                case "usuario_criar":
                    return handleCreateUser(message);
                case "usuario_ler":
                    return handleReadUser(message);
                case "usuario_atualizar":
                    return handleUpdateUser(message);
                case "usuario_deletar":
                    return handleDeleteUser(message);
                case "transacao_criar":
                    return handleTransfer(message);
                case "depositar":
                    return handleDeposit(message);
                case "transacao_ler":
                    return handleTransacaoLer(message);
                case "conectar":
                    return handleConnect(message);
                case "erro_servidor":
                    return handleErrorReport(message);
                default:
                    return MessageBuilder.buildErrorResponse(operation, "Operação não suportada");
            }
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem", e);
            return MessageBuilder.buildErrorResponse("unknown", "Erro interno do servidor");
        }
    }
    
    private String handleLogin(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);
            
            String cpf = node.get("cpf").asText();
            String senha = node.get("senha").asText();
            
                if (dbManager.authenticateUser(cpf, senha)) {
                Usuario user = dbManager.getUser(cpf);
                String token = TokenManager.generateToken(cpf);
                
                // Atualiza informações do cliente
                clientInfo.setCpfUsuario(cpf);
                clientInfo.setNomeUsuario(user.getNome());
                if (this.serverGUI != null) {
                    serverGUI.updateConnectedClients();
                }
                
                return MessageBuilder.buildSuccessResponse("usuario_login", "Login realizado com sucesso", token);
            } else {
                return MessageBuilder.buildErrorResponse("usuario_login", "CPF ou senha inválidos");
            }
        } catch (Exception e) {
            logger.error("Erro no login", e);
            return MessageBuilder.buildErrorResponse("usuario_login", "Erro interno no login");
        }
    }
    
    private String handleLogout(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);
            
            String token = node.get("token").asText();
            
            if (TokenManager.isValidToken(token)) {
                TokenManager.removeToken(token);
                
                // Remove informações do usuário do cliente
                clientInfo.setCpfUsuario(null);
                clientInfo.setNomeUsuario(null);
                serverGUI.updateConnectedClients();
                
                return MessageBuilder.buildSuccessResponse("usuario_logout", "Logout realizado com sucesso");
            } else {
                return MessageBuilder.buildErrorResponse("usuario_logout", "Token inválido ou expirado");
            }
        } catch (Exception e) {
            logger.error("Erro no logout", e);
            return MessageBuilder.buildErrorResponse("usuario_logout", "Erro interno no logout");
        }
    }
    
    private String handleCreateUser(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);
            
            String nome = node.get("nome").asText();
            String cpf = node.get("cpf").asText();
            String senha = node.get("senha").asText();
            
            if (dbManager.userExists(cpf)) {
                return MessageBuilder.buildErrorResponse("usuario_criar", "Usuário já existe com este CPF");
            }
            
            if (dbManager.createUser(cpf, nome, senha)) {
                if (this.serverGUI != null) {
                    serverGUI.updateDatabaseView();
                }
                return MessageBuilder.buildSuccessResponse("usuario_criar", "Usuário criado com sucesso");
            } else {
                return MessageBuilder.buildErrorResponse("usuario_criar", "Erro ao criar usuário");
            }
        } catch (Exception e) {
            logger.error("Erro ao criar usuário", e);
            return MessageBuilder.buildErrorResponse("usuario_criar", "Erro interno ao criar usuário");
        }
    }
    
    private String handleReadUser(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);
            
            String token = node.get("token").asText();
            String cpf = TokenManager.getCpfFromToken(token);
            
            if (cpf == null) {
                return MessageBuilder.buildErrorResponse("usuario_ler", "Token inválido ou expirado");
            }
            
            Usuario user = dbManager.getUser(cpf);
            if (user != null) {
                return MessageBuilder.buildSuccessResponse("usuario_ler", "Dados do usuário obtidos com sucesso", user);
            } else {
                return MessageBuilder.buildErrorResponse("usuario_ler", "Usuário não encontrado");
            }
        } catch (Exception e) {
            logger.error("Erro ao ler usuário", e);
            return MessageBuilder.buildErrorResponse("usuario_ler", "Erro interno ao ler usuário");
        }
    }
    
    private String handleUpdateUser(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);
            
            String token = node.get("token").asText();
            String cpf = TokenManager.getCpfFromToken(token);
            
            if (cpf == null) {
                return MessageBuilder.buildErrorResponse("usuario_atualizar", "Token inválido ou expirado");
            }
            
            com.fasterxml.jackson.databind.JsonNode usuarioNode = node.get("usuario");
            String nome = usuarioNode.has("nome") ? usuarioNode.get("nome").asText() : null;
            String senha = usuarioNode.has("senha") ? usuarioNode.get("senha").asText() : null;
            
            if (dbManager.updateUser(cpf, nome, senha)) {
                serverGUI.updateDatabaseView();
                return MessageBuilder.buildSuccessResponse("usuario_atualizar", "Usuário atualizado com sucesso");
            } else {
                return MessageBuilder.buildErrorResponse("usuario_atualizar", "Erro ao atualizar usuário");
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário", e);
            return MessageBuilder.buildErrorResponse("usuario_atualizar", "Erro interno ao atualizar usuário");
        }
    }
    
    private String handleDeleteUser(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);
            
            String token = node.get("token").asText();
            String cpf = TokenManager.getCpfFromToken(token);
            
            if (cpf == null) {
                return MessageBuilder.buildErrorResponse("usuario_deletar", "Token inválido ou expirado");
            }
            
            if (dbManager.deleteUser(cpf)) {
                TokenManager.removeToken(token);
                
                // Remove informações do usuário do cliente
                clientInfo.setCpfUsuario(null);
                clientInfo.setNomeUsuario(null);
                if (this.serverGUI != null) {
                    serverGUI.updateConnectedClients();
                }
                if (this.serverGUI != null) {
                    serverGUI.updateDatabaseView();
                }
                
                return MessageBuilder.buildSuccessResponse("usuario_deletar", "Usuário deletado com sucesso");
            } else {
                return MessageBuilder.buildErrorResponse("usuario_deletar", "Erro ao deletar usuário");
            }
        } catch (Exception e) {
            logger.error("Erro ao deletar usuário", e);
            return MessageBuilder.buildErrorResponse("usuario_deletar", "Erro interno ao deletar usuário");
        }
    }

    private String handleTransfer(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);

            String token = node.get("token").asText();
            String cpfOrigem = TokenManager.getCpfFromToken(token);
            if (cpfOrigem == null) {
                return MessageBuilder.buildErrorResponse("transacao_criar", "Token inválido ou expirado");
            }

            String cpfDestino = node.get("cpf_destino").asText();
            double valor = node.get("valor").asDouble();

            if (!dbManager.userExists(cpfDestino)) {
                return MessageBuilder.buildErrorResponse("transacao_criar", "Usuário destino não encontrado");
            }

            boolean ok = dbManager.createTransfer(cpfOrigem, cpfDestino, valor);
            if (ok) {
                if (this.serverGUI != null) {
                    serverGUI.updateDatabaseView();
                    serverGUI.addLogMessage(String.format("Transferência: %s -> %s : R$ %.2f", cpfOrigem, cpfDestino, valor));
                }
                return MessageBuilder.buildSuccessResponse("transacao_criar", "Transferência realizada com sucesso");
            } else {
                return MessageBuilder.buildErrorResponse("transacao_criar", "Erro ao realizar transferência (saldo insuficiente ou usuário inválido)");
            }
        } catch (Exception e) {
            logger.error("Erro ao processar transferência", e);
            return MessageBuilder.buildErrorResponse("transacao_criar", "Erro interno ao processar transferência");
        }
    }

    private String handleDeposit(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);

            String token = node.get("token").asText();
            String cpf = TokenManager.getCpfFromToken(token);
            if (cpf == null) {
                return MessageBuilder.buildErrorResponse("transacao_depositar", "Token inválido ou expirado");
            }

            // canonical protocol sends deposit amount in 'valor_enviado'
            double valor = node.has("valor_enviado") ? node.get("valor_enviado").asDouble() : node.get("valor").asDouble();

            boolean ok = dbManager.createDeposit(cpf, valor);
            if (ok) {
                if (this.serverGUI != null) {
                    serverGUI.updateDatabaseView();
                    serverGUI.addLogMessage(String.format("Depósito: %s : R$ %.2f", cpf, valor));
                }
                return MessageBuilder.buildSuccessResponse("depositar", "Depósito realizado com sucesso");
            } else {
                return MessageBuilder.buildErrorResponse("depositar", "Erro ao realizar depósito");
            }
        } catch (Exception e) {
            logger.error("Erro ao processar depósito", e);
            return MessageBuilder.buildErrorResponse("transacao_depositar", "Erro interno ao processar depósito");
        }
    }

    private String handleTransacaoLer(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);

            String token = node.get("token").asText();
            String cpf = TokenManager.getCpfFromToken(token);
            if (cpf == null) {
                return MessageBuilder.buildErrorResponse("transacao_ler", "Token inválido ou expirado");
            }

            String dataInicial = node.has("data_inicial") ? node.get("data_inicial").asText() : null;
            String dataFinal = node.has("data_final") ? node.get("data_final").asText() : null;

            if (dataInicial == null || dataFinal == null) {
                return MessageBuilder.buildErrorResponse("transacao_ler", "data_inicial e data_final são obrigatórias");
            }

            java.time.Instant inicio;
            java.time.Instant fim;
            try {
                inicio = java.time.Instant.parse(dataInicial);
                fim = java.time.Instant.parse(dataFinal);
            } catch (Exception e) {
                return MessageBuilder.buildErrorResponse("transacao_ler", "Formato de data inválido. Use ISO 8601 UTC");
            }

            long days = java.time.Duration.between(inicio, fim).toDays();
            if (days < 0 || days > 31) {
                return MessageBuilder.buildErrorResponse("transacao_ler", "Intervalo de data inválido (máximo 31 dias)");
            }

            // Busca apenas as transações do usuário logado e filtra pelo período
            java.util.List<com.distribuidos.common.Transacao> all = dbManager.getAllTransacoes();
            java.util.List<com.distribuidos.common.Transacao> filtered = new java.util.ArrayList<>();
            for (com.distribuidos.common.Transacao t : all) {
                if (t.getTimestamp() == null) continue;
                
                // Filtrar apenas transações onde o usuário é origem OU destino
                boolean isUserTransaction = cpf.equals(t.getCpfOrigem()) || cpf.equals(t.getCpfDestino());
                if (!isUserTransaction) continue;
                
                java.time.Instant ts = t.getTimestamp().atZone(java.time.ZoneId.systemDefault()).toInstant();
                if (!ts.isBefore(inicio) && !ts.isAfter(fim)) {
                    filtered.add(t);
                }
            }

            return MessageBuilder.buildTransacoesResponse("transacao_ler", "Transações recuperadas com sucesso.", filtered);
        } catch (Exception e) {
            logger.error("Erro ao processar transacao_ler", e);
            return MessageBuilder.buildErrorResponse("transacao_ler", "Erro interno ao ler transações");
        }
    }
    
    private String handleConnect(String message) {
        try {
            // Marca que a primeira operação foi recebida
            isFirstOperation = false;
            // A operação conectar simplesmente confirma que a conexão foi estabelecida
            return MessageBuilder.buildSuccessResponse("conectar", "Servidor conectado com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao processar conectar", e);
            return MessageBuilder.buildErrorResponse("conectar", "Erro ao se conectar");
        }
    }
    
    private String extractOperationSafely(String message) {
        try {
            return MessageBuilder.extractOperation(message);
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    private void cleanup() {
        try {
            String clientKey = clientInfo.getIp() + ":" + clientInfo.getPorta();
            connectedClients.remove(clientKey);
            
            // Remove token se existir
            if (clientInfo.getCpfUsuario() != null) {
                TokenManager.removeTokenForCpf(clientInfo.getCpfUsuario());
            }
            
            if (this.serverGUI != null) {
                serverGUI.addLogMessage("Cliente desconectado: " + clientInfo.getIp() + ":" + clientInfo.getPorta());
                serverGUI.updateConnectedClients();
            }
            
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            
        } catch (IOException e) {
            logger.error("Erro ao limpar recursos do cliente", e);
        }
    }
    
    /**
     * Trata relatórios de erro enviados pelo cliente (operacao erro_servidor).
     * Conforme seção 4.11 do protocolo: cliente reporta erros do servidor.
     */
    private String handleErrorReport(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);
            
            // Conforme protocolo 4.11, operacao_enviada pode ser null se operacao estava ausente/nula
            String operacaoEnviada = "null";
            if (node.has("operacao_enviada") && !node.get("operacao_enviada").isNull()) {
                operacaoEnviada = node.get("operacao_enviada").asText();
            }
            
            String info = node.get("info").asText();
            
            // Incrementa contador de erros reportados
            int totalErrors = errorReportsCount.incrementAndGet();
            
            // Log do erro reportado pelo cliente - MOSTRAR JSON COMPLETO
            String clientIdentification = getClientIdentification();
            logger.warn("🚨 ERRO REPORTADO PELO CLIENTE [{}]: JSON completo: {}", clientIdentification, message);
            logger.warn("🚨 Operação: '{}' | Info: {} | Total de erros: {}", operacaoEnviada, info, totalErrors);
            
            // Adiciona o erro no log da GUI do servidor - MOSTRAR JSON COMPLETO
            if (serverGUI != null) {
                serverGUI.addLogMessage("=== ERRO_SERVIDOR RECEBIDO ===");
                serverGUI.addLogMessage("🚨 Cliente: " + clientIdentification);
                serverGUI.addLogMessage("📄 JSON completo: " + message);
                serverGUI.addLogMessage("⚠ Operação com erro: " + operacaoEnviada);
                serverGUI.addLogMessage("💬 Descrição: " + info);
                serverGUI.addLogMessage("📊 Total de erros reportados: " + totalErrors);
                serverGUI.addLogMessage("================================");
            }
            
            // Resposta de confirmação ao cliente
            return MessageBuilder.buildSuccessResponse("erro_servidor", 
                "Erro reportado e registrado com sucesso. Total de erros: " + totalErrors);
                
        } catch (Exception e) {
            logger.error("Erro ao processar relatório de erro do cliente", e);
            if (serverGUI != null) {
                serverGUI.addLogMessage("❌ Erro ao processar erro_servidor: " + e.getMessage());
            }
            return MessageBuilder.buildErrorResponse("erro_servidor", 
                "Erro ao processar relatório de erro");
        }
    }
    
    /**
     * Obtém identificação do cliente para logs.
     */
    private String getClientIdentification() {
        if (clientInfo != null) {
            return String.format("%s:%d", clientInfo.getIp(), clientInfo.getPorta());
        }
        return clientSocket != null ? 
            clientSocket.getRemoteSocketAddress().toString() : "Unknown";
    }
    
    /**
     * Retorna o número total de erros reportados pelos clientes.
     */
    public static int getTotalErrorReports() {
        return errorReportsCount.get();
    }
}