package com.distribuidos.errorinjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple Error Injector - Proxy TCP simples com interface gráfica
 */
public class SimpleErrorInjectorMain extends JFrame implements ActionListener {
    
    // Componentes da GUI
    private JTextField proxyPortField;
    private JTextField serverHostField;
    private JTextField serverPortField;
    private JButton startButton;
    private JButton stopButton;
    private JCheckBox clientErrorCheckBox;
    private JCheckBox serverErrorCheckBox;
    private JTextArea logArea;
    
    // Estado do proxy
    private ServerSocket proxySocket;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile boolean firstMessagePassed = false; // Para permitir 'conectar' passar
    private volatile boolean waitingConnectResponse = false; // Aguardando resposta do conectar
    
    // Cores
    private static final Color GREEN = new Color(76, 175, 80);
    private static final Color RED = new Color(244, 67, 54);
    
    public SimpleErrorInjectorMain() {
        initGUI();
    }
    
    private void initGUI() {
        setTitle("Simple Error Injector v1.0 - Proxy TCP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Painel superior - Configuração
        JPanel configPanel = new JPanel(new FlowLayout());
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuração"));
        
        configPanel.add(new JLabel("Porta Proxy:"));
        proxyPortField = new JTextField("9999", 5);
        configPanel.add(proxyPortField);
        
        configPanel.add(new JLabel("Host Servidor:"));
        serverHostField = new JTextField("localhost", 10);
        configPanel.add(serverHostField);
        
        configPanel.add(new JLabel("Porta Servidor:"));
        serverPortField = new JTextField("8080", 5);
        configPanel.add(serverPortField);
        
        startButton = new JButton("🚀 INICIAR PROXY");
        startButton.setBackground(GREEN);
        startButton.setForeground(Color.WHITE);
        startButton.setOpaque(true);
        startButton.setBorderPainted(false);
        startButton.addActionListener(this);
        configPanel.add(startButton);
        
        stopButton = new JButton("🛑 PARAR PROXY");
        stopButton.setBackground(RED);
        stopButton.setForeground(Color.WHITE);
        stopButton.setOpaque(true);
        stopButton.setBorderPainted(false);
        stopButton.setEnabled(false);
        stopButton.addActionListener(this);
        configPanel.add(stopButton);
        
        add(configPanel, BorderLayout.NORTH);
        
        // Painel central - Controles de erro
        JPanel errorPanel = new JPanel(new FlowLayout());
        errorPanel.setBorder(BorderFactory.createTitledBorder("Injeção de Erros"));
        
        clientErrorCheckBox = new JCheckBox("Injetar erros em mensagens do CLIENTE");
        clientErrorCheckBox.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        errorPanel.add(clientErrorCheckBox);
        
        serverErrorCheckBox = new JCheckBox("Injetar erros em respostas do SERVIDOR");
        serverErrorCheckBox.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        errorPanel.add(serverErrorCheckBox);
        
        add(errorPanel, BorderLayout.CENTER);
        
        // Painel inferior - Logs
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Logs"));
        
        logArea = new JTextArea(15, 0);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(logPanel, BorderLayout.SOUTH);
        
        log("✅ Simple Error Injector v1.0 iniciado");
        log("📋 Configure as portas e clique em INICIAR PROXY");
        
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startProxy();
        } else if (e.getSource() == stopButton) {
            stopProxy();
        }
    }
    
    private void startProxy() {
        try {
            int proxyPort = Integer.parseInt(proxyPortField.getText().trim());
            String serverHost = serverHostField.getText().trim();
            int serverPort = Integer.parseInt(serverPortField.getText().trim());
            
            proxySocket = new ServerSocket(proxyPort);
            running.set(true);
            
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            
            log("🚀 Proxy iniciado na porta " + proxyPort);
            log("🎯 Redirecionando para " + serverHost + ":" + serverPort);
            log("📡 Aguardando conexões...");
            
            // Thread para aceitar conexões
            Thread acceptThread = new Thread(() -> {
                while (running.get()) {
                    try {
                        Socket clientSocket = proxySocket.accept();
                        log("🔗 Cliente conectado: " + clientSocket.getInetAddress());
                        
                        // Thread para lidar com cada cliente
                        Thread clientThread = new Thread(() -> handleClient(clientSocket, serverHost, serverPort));
                        clientThread.setDaemon(true);
                        clientThread.start();
                        
                    } catch (IOException ex) {
                        if (running.get()) {
                            log("❌ Erro ao aceitar conexão: " + ex.getMessage());
                        }
                    }
                }
            });
            acceptThread.setDaemon(true);
            acceptThread.start();
            
        } catch (Exception ex) {
            log("❌ Erro ao iniciar proxy: " + ex.getMessage());
        }
    }
    
    private void stopProxy() {
        running.set(false);
        
        if (proxySocket != null) {
            try {
                proxySocket.close();
            } catch (IOException e) {
                log("⚠️ Erro ao fechar proxy: " + e.getMessage());
            }
        }
        
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        
        log("🛑 Proxy parado");
    }
    
    private void handleClient(Socket clientSocket, String serverHost, int serverPort) {
        Socket serverSocket = null;
        // Reset para cada nova conexão de cliente
        firstMessagePassed = false;
        waitingConnectResponse = false;
        
        try {
            // Conectar ao servidor real
            serverSocket = new Socket(serverHost, serverPort);
            log("🔗 Conectado ao servidor: " + serverHost + ":" + serverPort);
            
            // Variáveis final para lambda
            final Socket finalServerSocket = serverSocket;
            
            // Threads para relay bidirecional
            Thread clientToServer = new Thread(() -> relay(clientSocket, finalServerSocket, "CLIENTE→SERVIDOR", true));
            Thread serverToClient = new Thread(() -> relay(finalServerSocket, clientSocket, "SERVIDOR→CLIENTE", false));
            
            clientToServer.setDaemon(true);
            serverToClient.setDaemon(true);
            
            clientToServer.start();
            serverToClient.start();
            
            // Aguardar uma das threads terminar
            try {
                clientToServer.join();
                serverToClient.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            
        } catch (IOException ex) {
            log("❌ Erro ao conectar com servidor: " + ex.getMessage());
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
                clientSocket.close();
                log("🔌 Conexão encerrada");
            } catch (IOException ex) {
                log("⚠️ Erro ao fechar conexões: " + ex.getMessage());
            }
        }
    }
    
    private void relay(Socket from, Socket to, String direction, boolean isClientMessage) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(from.getInputStream()));
             PrintWriter writer = new PrintWriter(to.getOutputStream(), true)) {
            
            String line;
            while ((line = reader.readLine()) != null && running.get()) {
                
                String originalMessage = line;
                String processedMessage = line;
                
                // IMPORTANTE: Permitir a primeira mensagem (conectar) e sua resposta passar sem injeção
                // Conforme seção 5.3 do protocolo, a primeira operação DEVE ser 'conectar'
                boolean skipInjection = false;
                
                if (isClientMessage && !firstMessagePassed) {
                    // Primeira mensagem do cliente (deve ser conectar)
                    if (line.contains("\"operacao\"") && line.contains("\"conectar\"")) {
                        firstMessagePassed = true;
                        waitingConnectResponse = true;
                        skipInjection = true;
                        log("✅ Mensagem 'conectar' permitida passar sem injeção");
                    }
                } else if (!isClientMessage && waitingConnectResponse) {
                    // Resposta do servidor para conectar
                    if (line.contains("\"operacao\"") && line.contains("\"conectar\"")) {
                        waitingConnectResponse = false;
                        skipInjection = true;
                        log("✅ Resposta 'conectar' permitida passar sem injeção");
                    }
                }
                
                // Aplicar injeção de erro se ativada (e não for mensagem de conexão)
                if (!skipInjection) {
                    if (isClientMessage && clientErrorCheckBox.isSelected()) {
                        processedMessage = injectClientError(line);
                        if (!processedMessage.equals(originalMessage)) {
                            log("🔴 ERRO INJETADO (Cliente): " + processedMessage);
                        }
                    } else if (!isClientMessage && serverErrorCheckBox.isSelected()) {
                        processedMessage = injectServerError(line);
                        if (!processedMessage.equals(originalMessage)) {
                            log("🔴 ERRO INJETADO (Servidor): " + processedMessage);
                        }
                    }
                }
                
                log("📨 " + direction + ": " + processedMessage);
                writer.println(processedMessage);
            }
            
        } catch (IOException ex) {
            if (running.get()) {
                log("⚠️ Erro no relay " + direction + ": " + ex.getMessage());
            }
        }
    }
    
    private String injectClientError(String message) {
        // Simular erro removendo campo 'operacao' se for JSON
        if (message.contains("operacao")) {
            message = message.replaceAll(",?\"operacao\"[^,}]*", "");
        }
        return message;
    }
    
    private String injectServerError(String message) {
        // Simular erro removendo campo 'status' se for JSON
        if (message.contains("status")) {
            message = message.replaceAll(",?\"status\"[^,}]*", "");
        }
        return message;
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                // Usar look and feel padrão se falhar
            }
            new SimpleErrorInjectorMain();
        });
    }
}