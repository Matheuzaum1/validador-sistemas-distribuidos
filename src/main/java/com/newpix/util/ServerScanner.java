package com.newpix.util;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utilitário para escanear servidores NewPix na rede local.
 */
public class ServerScanner {
    
    private static final int DEFAULT_PORT = 8080;
    private static final int TIMEOUT_MS = 1000;
    private static final int MAX_THREADS = 50;
    
    public static class ServerInfo {
        public final String host;
        public final int port;
        public final String name;
        public final boolean isOnline;
        public final long responseTime;
        
        public ServerInfo(String host, int port, String name, boolean isOnline, long responseTime) {
            this.host = host;
            this.port = port;
            this.name = name;
            this.isOnline = isOnline;
            this.responseTime = responseTime;
        }
        
        @Override
        public String toString() {
            return String.format("%s:%d (%s) - %s [%dms]", 
                host, port, name, isOnline ? "Online" : "Offline", responseTime);
        }
    }
    
    public interface ScanCallback {
        void onServerFound(ServerInfo serverInfo);
        void onScanProgress(int current, int total);
        void onScanComplete(List<ServerInfo> servers);
    }
    
    /**
     * Escaneia a rede local procurando por servidores NewPix
     */
    public static void scanLocalNetwork(ScanCallback callback) {
        scanLocalNetwork(DEFAULT_PORT, callback);
    }
    
    /**
     * Escaneia a rede local procurando por servidores em uma porta específica
     */
    public static void scanLocalNetwork(int port, ScanCallback callback) {
        SwingWorker<List<ServerInfo>, ServerInfo> worker = new SwingWorker<List<ServerInfo>, ServerInfo>() {
            @Override
            protected List<ServerInfo> doInBackground() throws Exception {
                CLILogger.info("Iniciando escaneamento da rede local na porta " + port);
                
                List<ServerInfo> servers = new ArrayList<>();
                String localIP = getLocalIP();
                String networkBase = getNetworkBase(localIP);
                
                ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                
                // Escanear IPs de 1 a 254
                for (int i = 1; i <= 254; i++) {
                    final String targetIP = networkBase + "." + i;
                    final int currentIndex = i;
                    
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            callback.onScanProgress(currentIndex, 254);
                            
                            ServerInfo serverInfo = checkServer(targetIP, port);
                            if (serverInfo != null && serverInfo.isOnline) {
                                servers.add(serverInfo);
                                publish(serverInfo);
                            }
                        } catch (Exception e) {
                            CLILogger.debug("Erro ao escanear " + targetIP + ": " + e.getMessage());
                        }
                    }, executor);
                    
                    futures.add(future);
                }
                
                // Aguardar todas as tarefas
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                executor.shutdown();
                
                CLILogger.success("Escaneamento concluído. " + servers.size() + " servidores encontrados");
                return servers;
            }
            
            @Override
            protected void process(List<ServerInfo> chunks) {
                for (ServerInfo server : chunks) {
                    callback.onServerFound(server);
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<ServerInfo> servers = get();
                    callback.onScanComplete(servers);
                } catch (Exception e) {
                    CLILogger.error("Erro no escaneamento: " + e.getMessage());
                    callback.onScanComplete(new ArrayList<>());
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Verifica se um servidor específico está online
     */
    public static ServerInfo checkServer(String host, int port) {
        long startTime = System.currentTimeMillis();
        
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), TIMEOUT_MS);
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Tentar obter nome do host
            String hostname = "Unknown";
            try {
                hostname = InetAddress.getByName(host).getHostName();
                if (hostname.equals(host)) {
                    hostname = "Server-" + host.substring(host.lastIndexOf('.') + 1);
                }
            } catch (Exception e) {
                hostname = "Server-" + host.substring(host.lastIndexOf('.') + 1);
            }
            
            return new ServerInfo(host, port, hostname, true, responseTime);
            
        } catch (IOException e) {
            long responseTime = System.currentTimeMillis() - startTime;
            return new ServerInfo(host, port, "Offline", false, responseTime);
        }
    }
    
    /**
     * Obtém o IP local da máquina
     */
    private static String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
    
    /**
     * Obtém a base da rede (ex: 192.168.1.0 -> 192.168.1)
     */
    private static String getNetworkBase(String ip) {
        int lastDot = ip.lastIndexOf('.');
        return ip.substring(0, lastDot);
    }
    
    /**
     * Cria um diálogo para escaneamento de servidores
     */
    public static JDialog createScanDialog(JFrame parent, ScanCallback callback) {
        JDialog dialog = new JDialog(parent, "Escanear Servidores", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // Área de texto para mostrar resultados
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        // Barra de progresso
        JProgressBar progressBar = new JProgressBar(0, 254);
        progressBar.setStringPainted(true);
        progressBar.setString("Pronto para escanear");
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton scanButton = new JButton("Escanear");
        JButton cancelButton = new JButton("Fechar");
        
        buttonPanel.add(scanButton);
        buttonPanel.add(cancelButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Event handlers
        scanButton.addActionListener(e -> {
            textArea.setText("Iniciando escaneamento da rede local...\n");
            scanButton.setEnabled(false);
            progressBar.setValue(0);
            progressBar.setString("Escaneando...");
            
            ScanCallback dialogCallback = new ScanCallback() {
                @Override
                public void onServerFound(ServerInfo serverInfo) {
                    SwingUtilities.invokeLater(() -> {
                        textArea.append("✓ Servidor encontrado: " + serverInfo + "\n");
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                        if (callback != null) callback.onServerFound(serverInfo);
                    });
                }
                
                @Override
                public void onScanProgress(int current, int total) {
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(current);
                        progressBar.setString(String.format("Escaneando %d/%d", current, total));
                        if (callback != null) callback.onScanProgress(current, total);
                    });
                }
                
                @Override
                public void onScanComplete(List<ServerInfo> servers) {
                    SwingUtilities.invokeLater(() -> {
                        textArea.append("\n=== Escaneamento Concluído ===\n");
                        textArea.append(String.format("Total de servidores encontrados: %d\n", servers.size()));
                        scanButton.setEnabled(true);
                        progressBar.setString("Concluído");
                        if (callback != null) callback.onScanComplete(servers);
                    });
                }
            };
            
            scanLocalNetwork(dialogCallback);
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        return dialog;
    }
}
