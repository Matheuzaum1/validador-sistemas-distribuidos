package com.distribuidos.errorinjector.ui;

import com.distribuidos.errorinjector.core.ErrorInjector;
import com.distribuidos.errorinjector.core.ErrorType;
import com.distribuidos.errorinjector.mockserver.MockServer;
import com.distribuidos.errorinjector.mockclient.MockClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Interface gráfica principal do Error Injector
 */
public class ErrorInjectorGUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(ErrorInjectorGUI.class);
    
    // Componentes principais
    private JTabbedPane mainTabbedPane;
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    
    // Aba Servidor Mock
    private JComboBox<ErrorType> serverErrorTypeCombo;
    private JComboBox<String> serverMessageTypeCombo;
    private JTextArea serverOriginalMessageArea;
    private JTextArea serverModifiedMessageArea;
    private JButton serverStartButton;
    private JButton serverStopButton;
    private JTextField serverPortField;
    private MockServer mockServer;
    
    // Aba Cliente Mock
    private JComboBox<ErrorType> clientErrorTypeCombo;
    private JComboBox<String> clientMessageTypeCombo;
    private JTextArea clientOriginalMessageArea;
    private JTextArea clientModifiedMessageArea;
    private JButton clientConnectButton;
    private JButton clientSendButton;
    private JButton clientDisconnectButton;
    private JTextField clientHostField;
    private JTextField clientPortField;
    private MockClient mockClient;
    
    // Aba Gerador de Casos de Teste
    private JList<ErrorType> testCasesList;
    private JButton generateAllTestsButton;
    private JButton exportTestsButton;
    private JProgressBar testProgressBar;
    
    public ErrorInjectorGUI() {
        initializeGUI();
        setupEventHandlers();
        populateData();
    }
    
    private void initializeGUI() {
        setTitle("Error Injector v1.0.0 - Sistema de Injeção de Erros para Protocolo Distribuído");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Layout principal
        setLayout(new BorderLayout());
        
        // Criar componentes
        createMainTabbedPane();
        createMenuBar();
        
        // Adicionar ao frame
        add(mainTabbedPane, BorderLayout.CENTER);
        add(createLogPanel(), BorderLayout.SOUTH);
        
        // Garantir que a janela seja visível
        setVisible(true);
        toFront();
        requestFocus();
    }
    
    private void createMainTabbedPane() {
        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        // Aba 1: Servidor Mock
        JPanel serverPanel = createServerMockPanel();
        mainTabbedPane.addTab("📡 Servidor Mock", null, serverPanel, 
            "Simula servidor com respostas com erros injetados");
        
        // Aba 2: Cliente Mock  
        JPanel clientPanel = createClientMockPanel();
        mainTabbedPane.addTab("💻 Cliente Mock", null, clientPanel, 
            "Simula cliente enviando mensagens com erros injetados");
        
        // Aba 3: Gerador de Casos de Teste
        JPanel testPanel = createTestGeneratorPanel();
        mainTabbedPane.addTab("🧪 Gerador de Testes", null, testPanel, 
            "Gera casos de teste automatizados para todos os tipos de erro");
        
        // Aba 4: Proxy de Interceptação
        JPanel proxyPanel = createProxyPanel();
        mainTabbedPane.addTab("🔍 Proxy Interceptador", null, proxyPanel, 
            "Intercepta comunicação entre cliente e servidor real");
    }
    
    private JPanel createServerMockPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de controle
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(new TitledBorder("Configurações do Servidor Mock"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Porta do servidor
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Porta:"), gbc);
        gbc.gridx = 1;
        serverPortField = new JTextField("12345", 10);
        controlPanel.add(serverPortField, gbc);
        
        // Tipo de erro
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Tipo de Erro:"), gbc);
        gbc.gridx = 1;
        serverErrorTypeCombo = new JComboBox<>(ErrorType.getServerErrors());
        serverErrorTypeCombo.setRenderer(new ErrorTypeRenderer());
        controlPanel.add(serverErrorTypeCombo, gbc);
        
        // Tipo de mensagem
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("Tipo de Resposta:"), gbc);
        gbc.gridx = 1;
        serverMessageTypeCombo = new JComboBox<>();
        controlPanel.add(serverMessageTypeCombo, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        serverStartButton = new JButton("▶️ Iniciar Servidor");
        serverStopButton = new JButton("⏹️ Parar Servidor");
        serverStopButton.setEnabled(false);
        buttonPanel.add(serverStartButton);
        buttonPanel.add(serverStopButton);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        controlPanel.add(buttonPanel, gbc);
        
        // Painel de mensagens
        JPanel messagePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Mensagem original
        JPanel originalPanel = new JPanel(new BorderLayout());
        originalPanel.setBorder(new TitledBorder("Mensagem Original"));
        serverOriginalMessageArea = new JTextArea(8, 0);
        serverOriginalMessageArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        originalPanel.add(new JScrollPane(serverOriginalMessageArea));
        
        // Mensagem com erro
        JPanel modifiedPanel = new JPanel(new BorderLayout());
        modifiedPanel.setBorder(new TitledBorder("Mensagem com Erro Injetado"));
        serverModifiedMessageArea = new JTextArea(8, 0);
        serverModifiedMessageArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        serverModifiedMessageArea.setEditable(false);
        modifiedPanel.add(new JScrollPane(serverModifiedMessageArea));
        
        messagePanel.add(originalPanel);
        messagePanel.add(modifiedPanel);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(messagePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createClientMockPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de controle
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(new TitledBorder("Configurações do Cliente Mock"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Host e porta
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        clientHostField = new JTextField("localhost", 10);
        controlPanel.add(clientHostField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        controlPanel.add(new JLabel("Porta:"), gbc);
        gbc.gridx = 3;
        clientPortField = new JTextField("20000", 8);
        controlPanel.add(clientPortField, gbc);
        
        // Tipo de erro
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Tipo de Erro:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        clientErrorTypeCombo = new JComboBox<>(ErrorType.getClientErrors());
        clientErrorTypeCombo.setRenderer(new ErrorTypeRenderer());
        controlPanel.add(clientErrorTypeCombo, gbc);
        
        // Tipo de mensagem
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        controlPanel.add(new JLabel("Tipo de Mensagem:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        clientMessageTypeCombo = new JComboBox<>();
        controlPanel.add(clientMessageTypeCombo, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        clientConnectButton = new JButton("🔗 Conectar ao Servidor");
        clientSendButton = new JButton("📤 Enviar Mensagem com Erro");
        clientDisconnectButton = new JButton("❌ Desconectar");
        clientSendButton.setEnabled(false);
        clientDisconnectButton.setEnabled(false);
        buttonPanel.add(clientConnectButton);
        buttonPanel.add(clientSendButton);
        buttonPanel.add(clientDisconnectButton);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        controlPanel.add(buttonPanel, gbc);
        
        // Painel de mensagens
        JPanel messagePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Mensagem original
        JPanel originalPanel = new JPanel(new BorderLayout());
        originalPanel.setBorder(new TitledBorder("Mensagem Original"));
        clientOriginalMessageArea = new JTextArea(8, 0);
        clientOriginalMessageArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        originalPanel.add(new JScrollPane(clientOriginalMessageArea));
        
        // Mensagem com erro
        JPanel modifiedPanel = new JPanel(new BorderLayout());
        modifiedPanel.setBorder(new TitledBorder("Mensagem com Erro Injetado"));
        clientModifiedMessageArea = new JTextArea(8, 0);
        clientModifiedMessageArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        clientModifiedMessageArea.setEditable(false);
        modifiedPanel.add(new JScrollPane(clientModifiedMessageArea));
        
        messagePanel.add(originalPanel);
        messagePanel.add(modifiedPanel);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(messagePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTestGeneratorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Lista de tipos de erro
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(new TitledBorder("Casos de Teste Disponíveis"));
        
        testCasesList = new JList<>(ErrorType.values());
        testCasesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        testCasesList.setCellRenderer(new ErrorTypeRenderer());
        listPanel.add(new JScrollPane(testCasesList));
        
        // Painel de controle
        JPanel controlPanel = new JPanel(new FlowLayout());
        generateAllTestsButton = new JButton("🚀 Gerar Todos os Testes");
        exportTestsButton = new JButton("💾 Exportar Casos de Teste");
        
        controlPanel.add(generateAllTestsButton);
        controlPanel.add(exportTestsButton);
        
        // Barra de progresso
        testProgressBar = new JProgressBar(0, ErrorType.values().length);
        testProgressBar.setStringPainted(true);
        
        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(testProgressBar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createProxyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel label = new JLabel("<html><h2>🔍 Proxy Interceptador</h2>" +
            "<p>Esta funcionalidade permite interceptar comunicação entre cliente e servidor real,</p>" +
            "<p>modificando mensagens em tempo real para injetar erros específicos.</p>" +
            "<p><b>Status:</b> Em desenvolvimento para v1.1.0</p></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Log de Atividades"));
        panel.setPreferredSize(new Dimension(0, 200));
        
        logArea = new JTextArea();
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        
        // Auto-scroll
        DefaultCaret caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        logScrollPane = new JScrollPane(logArea);
        panel.add(logScrollPane);
        
        // Botão para limpar log
        JButton clearLogButton = new JButton("🗑️ Limpar Log");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(clearLogButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Arquivo
        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem exportItem = new JMenuItem("Exportar Relatório de Testes");
        JMenuItem exitItem = new JMenuItem("Sair");
        
        exportItem.addActionListener(this::exportTestReport);
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Menu Ferramentas
        JMenu toolsMenu = new JMenu("Ferramentas");
        JMenuItem validateJsonItem = new JMenuItem("Validar JSON");
        JMenuItem formatJsonItem = new JMenuItem("Formatar JSON");
        
        validateJsonItem.addActionListener(this::validateJson);
        formatJsonItem.addActionListener(this::formatJson);
        
        toolsMenu.add(validateJsonItem);
        toolsMenu.add(formatJsonItem);
        
        // Menu Ajuda
        JMenu helpMenu = new JMenu("Ajuda");
        JMenuItem aboutItem = new JMenuItem("Sobre");
        JMenuItem docItem = new JMenuItem("Documentação");
        
        aboutItem.addActionListener(this::showAbout);
        docItem.addActionListener(this::showDocumentation);
        
        helpMenu.add(docItem);
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupEventHandlers() {
        // Servidor Mock
        serverErrorTypeCombo.addActionListener(this::onServerErrorTypeChanged);
        serverMessageTypeCombo.addActionListener(this::onServerMessageTypeChanged);
        serverStartButton.addActionListener(this::startMockServer);
        serverStopButton.addActionListener(this::stopMockServer);
        
        // Cliente Mock
        clientErrorTypeCombo.addActionListener(this::onClientErrorTypeChanged);
        clientMessageTypeCombo.addActionListener(this::onClientMessageTypeChanged);
        clientConnectButton.addActionListener(this::connectMockClient);
        clientSendButton.addActionListener(this::sendClientMessage);
        clientDisconnectButton.addActionListener(this::disconnectMockClient);
        
        // Gerador de testes
        generateAllTestsButton.addActionListener(this::generateAllTests);
        exportTestsButton.addActionListener(this::exportTestCases);
    }
    
    private void populateData() {
        // Popular combos de tipos de mensagem
        Map<String, String> examples = ErrorInjector.getValidMessageExamples();
        
        DefaultComboBoxModel<String> serverModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> clientModel = new DefaultComboBoxModel<>();
        
        for (String key : examples.keySet()) {
            if (key.startsWith("resposta_")) {
                serverModel.addElement(key);
            } else {
                clientModel.addElement(key);
            }
        }
        
        serverMessageTypeCombo.setModel(serverModel);
        clientMessageTypeCombo.setModel(clientModel);
        
        // Configurar mensagens iniciais
        updateServerMessage();
        updateClientMessage();
        
        // Log inicial
        addLog("Error Injector v1.0.0 iniciado");
        addLog("Selecione um tipo de erro e mensagem para começar");
    }
    
    private void updateServerMessage() {
        String messageType = (String) serverMessageTypeCombo.getSelectedItem();
        if (messageType != null) {
            Map<String, String> examples = ErrorInjector.getValidMessageExamples();
            String originalMessage = examples.get(messageType);
            serverOriginalMessageArea.setText(originalMessage);
            updateServerErrorInjection();
        }
    }
    
    private void updateClientMessage() {
        String messageType = (String) clientMessageTypeCombo.getSelectedItem();
        if (messageType != null) {
            Map<String, String> examples = ErrorInjector.getValidMessageExamples();
            String originalMessage = examples.get(messageType);
            clientOriginalMessageArea.setText(originalMessage);
            updateClientErrorInjection();
        }
    }
    
    private void updateServerErrorInjection() {
        ErrorType errorType = (ErrorType) serverErrorTypeCombo.getSelectedItem();
        String originalMessage = serverOriginalMessageArea.getText();
        
        if (errorType != null && !originalMessage.trim().isEmpty()) {
            String modifiedMessage = ErrorInjector.injectError(originalMessage, errorType);
            serverModifiedMessageArea.setText(modifiedMessage);
        }
    }
    
    private void updateClientErrorInjection() {
        ErrorType errorType = (ErrorType) clientErrorTypeCombo.getSelectedItem();
        String originalMessage = clientOriginalMessageArea.getText();
        
        if (errorType != null && !originalMessage.trim().isEmpty()) {
            String modifiedMessage = ErrorInjector.injectError(originalMessage, errorType);
            clientModifiedMessageArea.setText(modifiedMessage);
        }
    }
    
    // Event handlers
    private void onServerErrorTypeChanged(ActionEvent e) {
        updateServerErrorInjection();
    }
    
    private void onServerMessageTypeChanged(ActionEvent e) {
        updateServerMessage();
    }
    
    private void onClientErrorTypeChanged(ActionEvent e) {
        updateClientErrorInjection();
    }
    
    private void onClientMessageTypeChanged(ActionEvent e) {
        updateClientMessage();
    }
    
    private void startMockServer(ActionEvent e) {
        String portText = serverPortField.getText().trim();
        
        try {
            int port = Integer.parseInt(portText);
            ErrorType selectedError = (ErrorType) serverErrorTypeCombo.getSelectedItem();
            String responseMessage = serverOriginalMessageArea.getText().trim();
            
            if (selectedError == null) {
                addLog("❌ Selecione um tipo de erro");
                return;
            }
            
            if (responseMessage.isEmpty()) {
                addLog("❌ Selecione uma mensagem de resposta");
                return;
            }
            
            if (mockServer == null) {
                mockServer = new MockServer(this);
            }
            
            boolean success = mockServer.start(port, selectedError, responseMessage);
            
            if (success) {
                serverStartButton.setEnabled(false);
                serverStopButton.setEnabled(true);
                serverPortField.setEnabled(false);
                addLog("🎯 Servidor mock iniciado na porta " + port);
                addLog("💡 Configure o tipo de erro e aguarde conexões de clientes");
            }
            
        } catch (NumberFormatException ex) {
            addLog("❌ Porta deve ser um número válido");
        }
    }
    
    private void stopMockServer(ActionEvent e) {
        if (mockServer != null) {
            mockServer.stop();
            addLog("📊 " + mockServer.getStats());
        }
        
        serverStartButton.setEnabled(true);
        serverStopButton.setEnabled(false);
        serverPortField.setEnabled(true);
        addLog("🛑 Servidor mock parado");
    }
    
    private void connectMockClient(ActionEvent e) {
        String host = clientHostField.getText().trim();
        String portText = clientPortField.getText().trim();
        
        if (host.isEmpty()) {
            addLog("❌ Host não pode estar vazio");
            return;
        }
        
        try {
            int port = Integer.parseInt(portText);
            
            if (mockClient == null) {
                mockClient = new MockClient(this);
            }
            
            boolean success = mockClient.connect(host, port);
            
            if (success) {
                clientConnectButton.setEnabled(false);
                clientSendButton.setEnabled(true);
                clientDisconnectButton.setEnabled(true);
                clientHostField.setEnabled(false);
                clientPortField.setEnabled(false);
            }
            
        } catch (NumberFormatException ex) {
            addLog("❌ Porta deve ser um número válido");
        }
    }
    
    private void sendClientMessage(ActionEvent e) {
        if (mockClient == null || !mockClient.isConnected()) {
            addLog("❌ Cliente não está conectado");
            return;
        }
        
        String message = clientOriginalMessageArea.getText().trim();
        if (message.isEmpty()) {
            addLog("❌ Mensagem não pode estar vazia");
            return;
        }
        
        ErrorType selectedError = (ErrorType) clientErrorTypeCombo.getSelectedItem();
        if (selectedError == null) {
            addLog("❌ Selecione um tipo de erro");
            return;
        }
        
        mockClient.sendMessage(message, selectedError);
        addLog("📊 " + mockClient.getStats());
    }
    
    private void disconnectMockClient(ActionEvent e) {
        if (mockClient != null) {
            mockClient.disconnect();
        }
        
        clientConnectButton.setEnabled(true);
        clientSendButton.setEnabled(false);
        clientDisconnectButton.setEnabled(false);
        clientHostField.setEnabled(true);
        clientPortField.setEnabled(true);
    }
    
    private void generateAllTests(ActionEvent e) {
        addLog("🧪 Gerando casos de teste automatizados...");
        addLog("🔍 Analisando tipos de erro disponíveis...");
        
        ErrorType[] allErrors = ErrorType.values();
        Map<String, String> examples = ErrorInjector.getValidMessageExamples();
        
        int totalTests = 0;
        StringBuilder testSummary = new StringBuilder();
        testSummary.append("📋 CASOS DE TESTE GERADOS\n");
        testSummary.append("=".repeat(50)).append("\n\n");
        
        for (ErrorType errorType : allErrors) {
            testSummary.append("🔧 ").append(errorType.getDisplayName()).append("\n");
            testSummary.append("   Descrição: ").append(errorType.getDescription()).append("\n");
            
            if (errorType.canAffectServer()) {
                testSummary.append("   📤 Aplicável a respostas do servidor\n");
                totalTests++;
            }
            
            if (errorType.canAffectClient()) {
                testSummary.append("   📥 Aplicável a mensagens do cliente\n");
                totalTests++;
            }
            
            testSummary.append("\n");
        }
        
        testSummary.append("📊 Total de casos de teste: ").append(totalTests).append("\n");
        testSummary.append("📋 Tipos de mensagem suportados: ").append(examples.size()).append("\n");
        
        // Mostrar em diálogo
        JTextArea textArea = new JTextArea(testSummary.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Casos de Teste Gerados", JOptionPane.INFORMATION_MESSAGE);
        
        addLog("✅ " + totalTests + " casos de teste foram gerados");
    }
    
    private void exportTestCases(ActionEvent e) {
        addLog("📁 Exportando casos de teste para arquivo...");
        
        try {
            // Usar JFileChooser para selecionar onde salvar
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar Casos de Teste");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Arquivos de Texto (*.txt)", "txt"));
            fileChooser.setSelectedFile(new File("casos_de_teste_error_injector.txt"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                
                try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave, StandardCharsets.UTF_8))) {
                    writer.println("CASOS DE TESTE - ERROR INJECTOR");
                    writer.println("Gerado em: " + java.time.LocalDateTime.now());
                    writer.println("=".repeat(50));
                    writer.println();
                    
                    ErrorType[] allErrors = ErrorType.values();
                    Map<String, String> examples = ErrorInjector.getValidMessageExamples();
                    
                    for (ErrorType errorType : allErrors) {
                        writer.println("ERRO: " + errorType.getDisplayName());
                        writer.println("DESCRIÇÃO: " + errorType.getDescription());
                        writer.println("CATEGORIA: " + errorType.getCategory());
                        
                        if (errorType.canAffectServer()) {
                            writer.println("SERVIDOR: SIM - Pode afetar respostas do servidor");
                        }
                        
                        if (errorType.canAffectClient()) {
                            writer.println("CLIENTE: SIM - Pode afetar mensagens do cliente");
                        }
                        
                        writer.println();
                        writer.println("EXEMPLOS DE TESTE:");
                        
                        for (Map.Entry<String, String> example : examples.entrySet()) {
                            String originalMessage = example.getValue();
                            String corruptedMessage = ErrorInjector.injectError(originalMessage, errorType);
                            
                            writer.println("  Mensagem Original:");
                            writer.println("    " + originalMessage);
                            writer.println("  Mensagem com Erro:");
                            writer.println("    " + corruptedMessage);
                            writer.println();
                        }
                        
                        writer.println("-".repeat(50));
                        writer.println();
                    }
                }
                
                addLog("✅ Casos de teste exportados para: " + fileToSave.getAbsolutePath());
            }
            
        } catch (IOException ex) {
            logger.error("Erro ao exportar casos de teste", ex);
            addLog("❌ Erro ao exportar casos de teste: " + ex.getMessage());
        }
    }
    
    private void exportTestReport(ActionEvent e) {
        addLog("Exportando relatório de testes...");
    }
    
    private void validateJson(ActionEvent e) {
        addLog("Validando JSON...");
    }
    
    private void formatJson(ActionEvent e) {
        addLog("Formatando JSON...");
    }
    
    private void showAbout(ActionEvent e) {
        JOptionPane.showMessageDialog(this,
            "<html><h2>Error Injector v1.0.0</h2>" +
            "<p>Sistema de Injeção de Erros para Protocolo Distribuído</p>" +
            "<p>Desenvolvido para testes de robustez e conformidade de protocolo</p>" +
            "<p><b>Recursos:</b></p>" +
            "<ul>" +
            "<li>Servidor mock com respostas com erro</li>" +
            "<li>Cliente mock com mensagens malformadas</li>" +
            "<li>Geração automática de casos de teste</li>" +
            "<li>Cobertura completa do protocolo 4.11</li>" +
            "</ul></html>",
            "Sobre Error Injector",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showDocumentation(ActionEvent e) {
        addLog("Documentação disponível em: README.md");
    }
    
    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().toString();
            logArea.append(String.format("[%s] %s%n", timestamp, message));
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
        logger.info(message);
    }
    
    /**
     * Renderer customizado para exibir tipos de erro de forma mais amigável
     */
    private static class ErrorTypeRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ErrorType) {
                ErrorType errorType = (ErrorType) value;
                setText(String.format("%s - %s", errorType.getDisplayName(), errorType.getDescription()));
                setToolTipText(errorType.getDescription());
            }
            
            return this;
        }
    }
}