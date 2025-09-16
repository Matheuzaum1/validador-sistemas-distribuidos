package com.newpix.server;

import com.newpix.util.ErrorHandler;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Thread que processa a conexão de um cliente de forma extremamente robusta.
 */
public class ClientHandler extends Thread {
    
    private final Socket clientSocket;
    private final MessageProcessor messageProcessor;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = true;
    private String clientAddress = "unknown";
    
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.messageProcessor = new MessageProcessor();
        
        // Configurar thread como daemon para shutdown gracioso
        setDaemon(true);
        setName("ClientHandler-" + socket.getRemoteSocketAddress());
    }
    
    @Override
    public void run() {
        ErrorHandler.safeExecuteVoid(() -> {
            setupStreams();
            handleClient();
        }, null, "Cliente Handler Main Loop");
        
        // Sempre fazer cleanup, mesmo em caso de erro
        cleanup();
    }
    
    private void setupStreams() throws IOException {
        try {
            // Configurar timeouts para evitar clientes travados
            clientSocket.setSoTimeout(60000); // 60 segundos timeout
            clientSocket.setKeepAlive(true);
            
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            clientAddress = clientSocket.getRemoteSocketAddress().toString();
            
        } catch (Exception e) {
            throw new IOException("Falha ao configurar streams de comunicação", e);
        }
    }
    
    private void handleClient() throws IOException {
        System.out.println("Cliente conectado: " + clientAddress);
        
        try {
            String inputLine;
            while (running && (inputLine = readLineWithTimeout()) != null) {
                
                // Verificar comando de desconexão
                if ("DISCONNECT".equals(inputLine.trim())) {
                    System.out.println("Cliente solicitou desconexão: " + clientAddress);
                    break;
                }
                
                // Verificar se não é uma linha vazia
                if (inputLine.trim().isEmpty()) {
                    continue;
                }
                
                // Processar mensagem de forma robusta
                processClientMessage(inputLine);
            }
            
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout na conexão com cliente: " + clientAddress);
        } catch (SocketException e) {
            System.out.println("Conexão perdida com cliente: " + clientAddress);
        } catch (Exception e) {
            ErrorHandler.handleUnexpectedError(e, null, "Comunicação com cliente " + clientAddress);
        }
        
        System.out.println("Cliente desconectado: " + clientAddress);
    }
    
    private String readLineWithTimeout() throws IOException {
        try {
            return in.readLine();
        } catch (SocketTimeoutException e) {
            // Log timeout mas não propaga exceção - permite graceful shutdown
            System.out.println("Timeout de leitura para cliente: " + clientAddress);
            return null;
        }
    }
    
    private void processClientMessage(String inputLine) {
        ErrorHandler.safeExecuteVoid(() -> {
            
            // Processar mensagem JSON
            String response = messageProcessor.processMessage(inputLine);
            
            // Verificar se a resposta é válida
            if (response == null || response.trim().isEmpty()) {
                response = createErrorResponse("Resposta vazia do processador");
            }
            
            // Enviar resposta de forma segura
            sendResponse(response);
            
            System.out.println("Mensagem processada para: " + clientAddress);
            
        }, null, "Processamento de mensagem do cliente " + clientAddress);
    }
    
    private void sendResponse(String response) {
        try {
            if (out != null && !out.checkError()) {
                out.println(response);
                out.flush();
                
                // Verificar se houve erro na escrita
                if (out.checkError()) {
                    throw new IOException("Erro ao escrever resposta para cliente");
                }
            } else {
                throw new IOException("Stream de saída inválido ou com erro");
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar resposta para cliente " + clientAddress + ": " + e.getMessage());
            
            // Tentar enviar resposta de erro como fallback
            try {
                if (out != null) {
                    String errorResponse = createErrorResponse("Erro interno na comunicação");
                    out.println(errorResponse);
                    out.flush();
                }
            } catch (Exception fallbackError) {
                System.err.println("Falha completa na comunicação com cliente " + clientAddress);
                running = false; // Forçar encerramento da conexão
            }
        }
    }
    
    private String createErrorResponse(String errorMessage) {
        return String.format(
            "{\"operacao\":\"unknown\",\"status\":false,\"info\":\"%s\"}", 
            errorMessage.replace("\"", "\\\"")
        );
    }
    
    private void cleanup() {
        running = false;
        
        ErrorHandler.safeExecuteVoid(() -> {
            
            if (out != null) {
                out.close();
            }
            
        }, null, "Cleanup PrintWriter");
        
        ErrorHandler.safeExecuteVoid(() -> {
            
            if (in != null) {
                in.close();
            }
            
        }, null, "Cleanup BufferedReader");
        
        ErrorHandler.safeExecuteVoid(() -> {
            
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            
        }, null, "Cleanup Socket");
        
        System.out.println("Cleanup completo para cliente: " + clientAddress);
    }
    
    /**
     * Método para encerrar graciosamente a conexão.
     */
    public void shutdown() {
        running = false;
        
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (Exception e) {
            System.err.println("Erro durante shutdown do cliente " + clientAddress + ": " + e.getMessage());
        }
    }
    
    /**
     * Obtém o endereço IP do cliente.
     */
    public String getClientIP() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                String address = clientSocket.getRemoteSocketAddress().toString();
                
                // Remover a porta e o '/' do início se presente
                if (address.startsWith("/")) {
                    address = address.substring(1);
                }
                if (address.contains(":")) {
                    address = address.substring(0, address.lastIndexOf(":"));
                }
                
                // Se for um IP interno do Docker/Kubernetes, tentar obter o IP real
                if (address.equals("127.0.0.1") || 
                    address.startsWith("kubernetes.docker.internal") ||
                    address.startsWith("docker.") ||
                    address.startsWith("host.docker.internal")) {
                    
                    // Tentar obter IP real via diferentes métodos
                    String realIP = getRealClientIP();
                    if (realIP != null && !realIP.equals("unknown")) {
                        System.out.println("IP Docker detectado (" + address + "), usando IP real: " + realIP);
                        return realIP;
                    }
                    
                    System.out.println("Conexão via Docker/Kubernetes detectada: " + address);
                    return address + " (Docker)";
                }
                
                return address;
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter IP do cliente: " + e.getMessage());
        }
        return "unknown";
    }
    
    /**
     * Tenta obter o IP real do cliente quando conectado via Docker/proxy.
     */
    private String getRealClientIP() {
        try {
            // Método 1: Verificar se há headers HTTP (se fosse uma conexão HTTP)
            // Como não é HTTP, vamos tentar outros métodos
            
            // Método 2: Tentar obter o IP da máquina local (servidor)
            // Assumir que cliente e servidor estão na mesma máquina/rede
            for (java.net.NetworkInterface netInterface : java.util.Collections.list(java.net.NetworkInterface.getNetworkInterfaces())) {
                if (netInterface.isUp() && !netInterface.isLoopback() && !netInterface.isVirtual()) {
                    for (java.net.InetAddress address : java.util.Collections.list(netInterface.getInetAddresses())) {
                        if (!address.isLoopbackAddress() && 
                            !address.isLinkLocalAddress() && 
                            !address.isMulticastAddress() &&
                            address.getHostAddress().indexOf(':') == -1) { // IPv4 apenas
                            
                            String ip = address.getHostAddress();
                            
                            // Priorizar IPs das redes comuns
                            if (ip.startsWith("172.") || ip.startsWith("192.168.") || ip.startsWith("10.")) {
                                return ip;
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao tentar obter IP real: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtém a porta do cliente.
     */
    public int getClientPort() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                return clientSocket.getPort();
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter porta do cliente: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Obtém o hostname do cliente.
     */
    public String getClientHostname() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                String hostname = clientSocket.getInetAddress().getHostName();
                
                // Se for hostname interno do Docker/Kubernetes, usar algo mais amigável
                if (hostname.contains("kubernetes.docker.internal") ||
                    hostname.contains("docker.internal") ||
                    hostname.equals("127.0.0.1") ||
                    hostname.startsWith("docker-")) {
                    
                    // Tentar obter hostname real da máquina
                    try {
                        String realHostname = java.net.InetAddress.getLocalHost().getHostName();
                        if (realHostname != null && !realHostname.isEmpty()) {
                            System.out.println("Hostname Docker detectado (" + hostname + "), usando hostname real: " + realHostname);
                            return realHostname + " (via Docker)";
                        }
                    } catch (Exception e) {
                        // Fallback para hostname mais amigável
                        return "Cliente-Docker";
                    }
                }
                
                return hostname;
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter hostname do cliente: " + e.getMessage());
        }
        return "unknown";
    }
    
    /**
     * Verifica se a conexão ainda está ativa.
     */
    public boolean isConnected() {
        return running && clientSocket != null && !clientSocket.isClosed() && clientSocket.isConnected();
    }
    
    /**
     * Obtém informações completas do cliente.
     */
    public String getClientInfo() {
        return String.format("%s:%d (%s)", getClientIP(), getClientPort(), getClientHostname());
    }
}
