package com.newpix.client.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpix.client.NewPixClient;
import com.newpix.client.gui.theme.NewPixTheme;
import com.newpix.client.gui.animations.AnimationUtils;
import com.newpix.util.CpfUtil;
import com.newpix.util.CLILogger;
import com.newpix.util.UserPreferences;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Interface gráfica principal do cliente NewPix.
 */
public class MainGUI extends JFrame {
    
    private NewPixClient client;
    private String token;
    private String nomeUsuario;
    private String cpfUsuario;
    private double saldoUsuario;
    
    // Componentes da interface
    private JLabel nomeLabel;
    private JLabel cpfLabel;
    private JLabel saldoLabel;
    private JTextField valorPixField;
    private JTextField cpfDestinoField;
    private JButton pixButton;
    private JButton depositoButton;
    private JTextField valorDepositoField;
    private JButton atualizarButton;
    private JButton extratoButton;
    private JButton logoutButton;
    private JTable extratoTable;
    private DefaultTableModel tableModel;
    private JTextField novoNomeField;
    private JPasswordField novaSenhaField;
    private JLabel alertLabel; // Para alertas tipo PHP
    
    // Componentes para configurações de conexão
    private JTextField configHostField;
    private JTextField configPortField;
    private JButton testarConexaoButton;
    private JButton salvarConfigButton;
    
    private DecimalFormat currencyFormat = new DecimalFormat("R$ #,##0.00");
    
    // Controle de tentativas de carregamento
    private int tentativasCarregamento = 0;
    private static final int MAX_TENTATIVAS_CARREGAMENTO = 3;
    
    public MainGUI(NewPixClient client, String token) {
        this.client = client;
        this.token = token;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        carregarDadosUsuario();
    }
    
    private void initializeComponents() {
        setTitle("NewPix - Sistema Bancário");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 249, 250));
        
        // Labels de informação do usuário modernos
        nomeLabel = new JLabel("Nome: Carregando...");
        nomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nomeLabel.setForeground(Color.BLACK);
        
        cpfLabel = new JLabel("CPF: Carregando...");
        cpfLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cpfLabel.setForeground(new Color(108, 117, 125));
        
        saldoLabel = new JLabel("Saldo: Carregando...");
        saldoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saldoLabel.setForeground(new Color(40, 167, 69));
        
        // Campos para PIX modernos
        valorPixField = createModernTextField(15);
        cpfDestinoField = createModernTextField(20);
        pixButton = createModernButton("Enviar PIX", new Color(0, 123, 255), new Color(0, 86, 179));
        
        // Campos para depósito modernos
        valorDepositoField = createModernTextField(15);
        depositoButton = createModernButton("Fazer Depósito", new Color(40, 167, 69), new Color(32, 134, 55));
        
        // Label para alertas (tipo PHP)
        alertLabel = new JLabel(" ");
        alertLabel.setHorizontalAlignment(SwingConstants.CENTER);
        alertLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        alertLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        alertLabel.setOpaque(true);
        alertLabel.setVisible(false);
        
        // Campos para atualização de dados modernos
        novoNomeField = createModernTextField(20);
        novaSenhaField = new JPasswordField(20);
        novaSenhaField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        novaSenhaField.setBackground(Color.WHITE);
        novaSenhaField.setForeground(Color.BLACK);
        novaSenhaField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(108, 117, 125), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        atualizarButton = createModernButton("Atualizar Dados", new Color(255, 193, 7), new Color(227, 172, 6));
        
        // Botões de ação modernos
        extratoButton = createModernButton("Atualizar Extrato", new Color(0, 123, 255), new Color(0, 86, 179));
        logoutButton = createModernButton("Logout", new Color(220, 53, 69), new Color(200, 35, 51));
        
        // Tabela de extrato
        String[] columns = {"Data", "Tipo", "Valor", "Origem/Destino"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        extratoTable = new JTable(tableModel);
        
        // Configurar tabela de extrato
        extratoTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        extratoTable.setBackground(Color.WHITE);
        extratoTable.setForeground(Color.BLACK);
        extratoTable.setSelectionBackground(new Color(0, 123, 255, 50));
        extratoTable.setSelectionForeground(Color.BLACK);
        extratoTable.setRowHeight(25);
        extratoTable.setShowGrid(true);
        extratoTable.setGridColor(new Color(233, 236, 239));
        
        // Header da tabela de extrato
        extratoTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        extratoTable.getTableHeader().setBackground(new Color(0, 123, 255));
        extratoTable.getTableHeader().setForeground(Color.WHITE);
        
        // Componentes de configuração de conexão modernos
        configHostField = createModernTextField(20);
        configPortField = createModernTextField(8);
        testarConexaoButton = createModernButton("Testar Conexão", new Color(0, 123, 255), new Color(0, 86, 179));
        salvarConfigButton = createModernButton("Salvar Configuração", new Color(40, 167, 69), new Color(32, 134, 55));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(248, 249, 250));
        
        // Panel superior - informações do usuário moderno
        JPanel userPanel = createModernCard();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "CONTA - Informações do Usuário", 
                0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.BLACK),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Linha com nome e CPF
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        infoPanel.add(nomeLabel);
        infoPanel.add(Box.createHorizontalStrut(30));
        infoPanel.add(cpfLabel);
        
        // Linha com saldo
        JPanel saldoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saldoPanel.setOpaque(false);
        saldoPanel.add(saldoLabel);
        
        // Alert label
        alertLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        userPanel.add(infoPanel);
        userPanel.add(Box.createVerticalStrut(5));
        userPanel.add(saldoPanel);
        userPanel.add(Box.createVerticalStrut(5));
        userPanel.add(alertLabel);
        
        // Panel central - operações com abas modernas
        JTabbedPane tabbedPane = createModernTabbedPane();
        
        // Aba PIX
        JPanel pixPanel = createPixPanel();
        tabbedPane.addTab("PIX", pixPanel);
        
        // Aba Depósito
        JPanel depositoPanel = createDepositoPanel();
        tabbedPane.addTab("Depósito", depositoPanel);
        
        // Aba Extrato
        JPanel extratoPanel = createExtratoPanel();
        tabbedPane.addTab("Extrato", extratoPanel);
        
        // Aba Configurações
        JPanel configPanel = createConfigPanel();
        tabbedPane.addTab("Configurações", configPanel);
        
        // Panel inferior - botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.add(extratoButton);
        actionPanel.add(logoutButton);
        
        add(userPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPixPanel() {
        JPanel panel = NewPixTheme.createCard();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            panel.getBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridx = 1;
        panel.add(valorPixField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("CPF Destino:"), gbc);
        gbc.gridx = 1;
        panel.add(cpfDestinoField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(pixButton, gbc);
        
        // Área de instruções
        gbc.gridy = 3;
        JTextArea instructions = new JTextArea(
            "Instruções:\n" +
            "1. Digite o valor a ser enviado\n" +
            "2. Informe o CPF do destinatário\n" +
            "3. Clique em 'Enviar PIX'\n\n" +
            "Formatos de CPF aceitos:\n" +
            "• 000.000.000-00 (com pontos e hífen)\n" +
            "• 00000000000 (apenas números)"
        );
        instructions.setEditable(false);
        instructions.setBackground(panel.getBackground());
        instructions.setBorder(BorderFactory.createTitledBorder("Instruções"));
        panel.add(instructions, gbc);
        
        return panel;
    }
    
    private JPanel createDepositoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Valor do Depósito (R$):"), gbc);
        gbc.gridx = 1;
        panel.add(valorDepositoField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(depositoButton, gbc);
        
        // Área de instruções
        gbc.gridy = 2;
        JTextArea instructions = new JTextArea(
            "Instruções para Depósito:\n" +
            "1. Digite o valor que deseja depositar\n" +
            "2. Clique em 'Fazer Depósito'\n" +
            "3. O valor será adicionado ao seu saldo\n\n" +
            "Exemplo: 100.50 ou 100,50\n" +
            "Valores mínimos: R$ 0,01"
        );
        instructions.setEditable(false);
        instructions.setBackground(panel.getBackground());
        instructions.setBorder(BorderFactory.createTitledBorder("Instruções"));
        panel.add(instructions, gbc);
        
        return panel;
    }
    
    private JPanel createExtratoPanel() {
        JPanel panel = NewPixTheme.createCard();
        panel.setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(extratoTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Aplicar tema na tabela
        NewPixTheme.styleTable(extratoTable);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(NewPixTheme.BACKGROUND_CARD);
        JButton refreshButton = NewPixTheme.createStyledButton(NewPixTheme.Icons.REFRESH + " Atualizar", NewPixTheme.ButtonStyle.PRIMARY);
        refreshButton.addActionListener(e -> carregarExtrato());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createConfigPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(NewPixTheme.BACKGROUND_LIGHT);
        
        // Painel de configurações do usuário
        JPanel userPanel = NewPixTheme.createCard();
        userPanel.setLayout(new GridBagLayout());
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, NewPixTheme.Icons.SETTINGS + " Configurações da Conta", 
                0, 0, NewPixTheme.FONT_SUBTITLE, NewPixTheme.TEXT_PRIMARY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        userPanel.add(new JLabel("Novo Nome:"), gbc);
        gbc.gridx = 1;
        userPanel.add(novoNomeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        userPanel.add(new JLabel("Nova Senha:"), gbc);
        gbc.gridx = 1;
        userPanel.add(novaSenhaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        userPanel.add(atualizarButton, gbc);
        
        // Painel de configurações de conexão
        JPanel connectionPanel = new JPanel(new GridBagLayout());
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Configurações de Conexão"));
        
        // Carregar valores atuais
        configHostField.setText(UserPreferences.getServerHost());
        configPortField.setText(String.valueOf(UserPreferences.getServerPort()));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        connectionPanel.add(new JLabel("Host do Servidor:"), gbc);
        gbc.gridx = 1;
        connectionPanel.add(configHostField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        connectionPanel.add(new JLabel("Porta:"), gbc);
        gbc.gridx = 1;
        connectionPanel.add(configPortField, gbc);
        
        // Painel de botões de conexão
        JPanel connectionButtonPanel = new JPanel(new FlowLayout());
        connectionButtonPanel.add(testarConexaoButton);
        connectionButtonPanel.add(salvarConfigButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        connectionPanel.add(connectionButtonPanel, gbc);
        
        // IP Local
        gbc.gridy = 3;
        JLabel ipLocalLabel = new JLabel("IP Local: " + getLocalIP());
        ipLocalLabel.setForeground(Color.BLUE);
        ipLocalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        connectionPanel.add(ipLocalLabel, gbc);
        
        // Aviso geral
        JTextArea warning = new JTextArea(
            "Configurações da Conta:\n" +
            "• Deixe em branco os campos que não deseja alterar\n" +
            "• A senha deve ter entre 6 e 120 caracteres\n" +
            "• O nome deve ter entre 6 e 120 caracteres\n\n" +
            "Configurações de Conexão:\n" +
            "• Mudanças são salvas automaticamente\n" +
            "• Teste a conexão antes de salvar\n" +
            "• Use 'localhost' para servidor local"
        );
        warning.setEditable(false);
        warning.setBackground(mainPanel.getBackground());
        warning.setBorder(BorderFactory.createTitledBorder("Instruções"));
        
        // Layout principal
        mainPanel.add(userPanel, BorderLayout.NORTH);
        mainPanel.add(connectionPanel, BorderLayout.CENTER);
        mainPanel.add(warning, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private String getLocalIP() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
    
    private void testarNovaConexao() {
        String host = configHostField.getText().trim();
        String portStr = configPortField.getText().trim();
        
        if (host.isEmpty() || portStr.isEmpty()) {
            showAlert("Por favor, preencha host e porta", "warning");
            return;
        }
        
        try {
            int port = Integer.parseInt(portStr);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return com.newpix.util.ConnectionConfig.testConnection(host, port);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            showAlert("Conexão bem-sucedida com " + host + ":" + port, "success");
                        } else {
                            showAlert("Falha na conexão com " + host + ":" + port, "error");
                        }
                    } catch (Exception e) {
                        showAlert("Erro no teste: " + e.getMessage(), "error");
                    }
                }
            };
            
            testarConexaoButton.setEnabled(false);
            testarConexaoButton.setText("Testando...");
            
            worker.addPropertyChangeListener(evt -> {
                if (worker.isDone()) {
                    testarConexaoButton.setEnabled(true);
                    testarConexaoButton.setText("Testar Conexão");
                }
            });
            
            worker.execute();
            
        } catch (NumberFormatException e) {
            showAlert("Porta deve ser um número válido", "error");
        }
    }
    
    private void salvarConfiguracaoConexao() {
        String host = configHostField.getText().trim();
        String portStr = configPortField.getText().trim();
        
        if (host.isEmpty() || portStr.isEmpty()) {
            showAlert("Por favor, preencha host e porta", "warning");
            return;
        }
        
        try {
            int port = Integer.parseInt(portStr);
            
            // Salvar nas preferências
            UserPreferences.saveServerConfig(host, port);
            
            showAlert("Configuração salva com sucesso!", "success");
            CLILogger.info("Configuração de servidor salva: " + host + ":" + port);
            
        } catch (NumberFormatException e) {
            showAlert("Porta deve ser um número válido", "error");
        }
    }
    
    private void setupEventHandlers() {
        pixButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarPix();
            }
        });
        
        atualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarDados();
            }
        });
        
        extratoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarDadosUsuario();
                carregarExtrato();
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogout();
            }
        });
        
        // Setup deposit action
        setupDepositoAction();
        
        // Setup configuration actions
        testarConexaoButton.addActionListener(e -> testarNovaConexao());
        salvarConfigButton.addActionListener(e -> salvarConfiguracaoConexao());
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                realizarLogout();
            }
        });
    }
    
    private void carregarDadosUsuario() {
        carregarDadosUsuarioComTentativas(false);
    }
    
    private void carregarDadosUsuarioComTentativas(boolean isRetry) {
        if (isRetry) {
            tentativasCarregamento++;
        } else {
            tentativasCarregamento = 0;
        }
        
        if (tentativasCarregamento >= MAX_TENTATIVAS_CARREGAMENTO) {
            CLILogger.error("Máximo de tentativas de carregamento excedido");
            
            // Mostrar dados de erro permanente
            nomeLabel.setText("Nome: Erro - máx tentativas");
            cpfLabel.setText("CPF: Indisponível");
            saldoLabel.setText("Saldo: Indisponível");
            
            AnimationUtils.showToast(this, 
                                   "❌ Falha ao carregar dados após " + MAX_TENTATIVAS_CARREGAMENTO + " tentativas", 
                                   5000, 
                                   NewPixTheme.ERROR_COLOR);
            
            // Oferecer opção de voltar ao login
            int option = JOptionPane.showConfirmDialog(this,
                "Não foi possível carregar os dados do usuário após várias tentativas.\n\n" +
                "Isso pode indicar um problema de conexão ou sessão.\n\n" +
                "Deseja voltar à tela de login?",
                "Erro Persistente",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);
                
            if (option == JOptionPane.YES_OPTION) {
                client.disconnect();
                LoginWindow loginWindow = new LoginWindow();
                loginWindow.setVisible(true);
                this.dispose();
            }
            return;
        }
        
        try {
            String dadosJson = client.getDadosUsuario(token);
            CLILogger.info("Resposta do servidor para dados do usuário: " + dadosJson);
            
            if (dadosJson != null && !dadosJson.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode dadosNode = mapper.readTree(dadosJson);
                
                // Verificar diferentes formatos de resposta
                if (dadosNode.has("success") && dadosNode.get("success").asBoolean()) {
                    // Formato com success=true
                    nomeUsuario = dadosNode.get("nome").asText();
                    cpfUsuario = dadosNode.get("cpf").asText();
                    saldoUsuario = dadosNode.get("saldo").asDouble();
                    
                    nomeLabel.setText("Nome: " + nomeUsuario);
                    cpfLabel.setText("CPF: " + cpfUsuario);
                    saldoLabel.setText("Saldo: " + currencyFormat.format(saldoUsuario));
                    
                    // Reset tentativas em caso de sucesso
                    tentativasCarregamento = 0;
                    
                } else if (dadosNode.has("status") && dadosNode.get("status").asBoolean()) {
                    // Formato com status=true
                    JsonNode dataNode = dadosNode.get("data");
                    if (dataNode != null) {
                        nomeUsuario = dataNode.get("nome").asText();
                        cpfUsuario = dataNode.get("cpf").asText();
                        saldoUsuario = dataNode.get("saldo").asDouble();
                        
                        nomeLabel.setText("Nome: " + nomeUsuario);
                        cpfLabel.setText("CPF: " + cpfUsuario);
                        saldoLabel.setText("Saldo: " + currencyFormat.format(saldoUsuario));
                        
                        // Reset tentativas em caso de sucesso
                        tentativasCarregamento = 0;
                    }
                    
                } else if (dadosNode.has("nome") && dadosNode.has("cpf") && dadosNode.has("saldo")) {
                    // Formato direto com dados
                    nomeUsuario = dadosNode.get("nome").asText();
                    cpfUsuario = dadosNode.get("cpf").asText();
                    saldoUsuario = dadosNode.get("saldo").asDouble();
                    
                    nomeLabel.setText("Nome: " + nomeUsuario);
                    cpfLabel.setText("CPF: " + cpfUsuario);
                    saldoLabel.setText("Saldo: " + currencyFormat.format(saldoUsuario));
                    
                    // Reset tentativas em caso de sucesso
                    tentativasCarregamento = 0;
                    
                } else {
                    // Erro na resposta
                    String errorMsg = "Dados do usuário não encontrados";
                    if (dadosNode.has("message")) {
                        errorMsg = dadosNode.get("message").asText();
                    } else if (dadosNode.has("info")) {
                        errorMsg = dadosNode.get("info").asText();
                    }
                    
                    CLILogger.error("Erro ao carregar dados: " + errorMsg);
                    
                    // Definir valores padrão em caso de erro
                    nomeLabel.setText("Nome: Carregando...");
                    cpfLabel.setText("CPF: Carregando...");
                    saldoLabel.setText("Saldo: Indisponível");
                    
                    // Usar toast em vez de JOptionPane com ações de recuperação
                    if (errorMsg.toLowerCase().contains("token") || errorMsg.toLowerCase().contains("sessão")) {
                        // Problema de sessão/token - sugerir login novamente
                        int option = JOptionPane.showConfirmDialog(this,
                            "Sua sessão expirou ou é inválida.\n\nDeseja fazer login novamente?",
                            "Sessão Expirada",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                            
                        if (option == JOptionPane.YES_OPTION) {
                            // Voltar para tela de login
                            client.disconnect();
                            LoginWindow loginWindow = new LoginWindow();
                            loginWindow.setVisible(true);
                            this.dispose();
                            return;
                        }
                    } else {
                        // Outros tipos de erro - mostrar toast com opção de tentar novamente
                        AnimationUtils.showToast(this, 
                                               "❌ Erro ao carregar dados: " + errorMsg + " (Tentativa " + (tentativasCarregamento + 1) + "/" + MAX_TENTATIVAS_CARREGAMENTO + ")", 
                                               5000, 
                                               NewPixTheme.ERROR_COLOR);
                        
                        // Tentar novamente automaticamente se ainda não excedeu o limite
                        if (tentativasCarregamento < MAX_TENTATIVAS_CARREGAMENTO - 1) {
                            Timer retryTimer = new Timer(3000, e -> {
                                carregarDadosUsuarioComTentativas(true);
                            });
                            retryTimer.setRepeats(false);
                            retryTimer.start();
                        } else {
                            // Última tentativa - oferecer opção manual
                            Timer retryTimer = new Timer(3000, e -> {
                                int retryOption = JOptionPane.showConfirmDialog(this,
                                    "Erro ao carregar dados do usuário (Última tentativa).\n\nDeseja tentar novamente?",
                                    "Erro de Carregamento",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);
                                    
                                if (retryOption == JOptionPane.YES_OPTION) {
                                    carregarDadosUsuarioComTentativas(true);
                                }
                            });
                            retryTimer.setRepeats(false);
                            retryTimer.start();
                        }
                    }
                }
                
            } else {
                CLILogger.error("Resposta vazia do servidor para dados do usuário");
                
                // Definir valores padrão
                nomeLabel.setText("Nome: Erro de conexão (Tentativa " + (tentativasCarregamento + 1) + "/" + MAX_TENTATIVAS_CARREGAMENTO + ")");
                cpfLabel.setText("CPF: Indisponível");
                saldoLabel.setText("Saldo: Indisponível");
                
                // Verificar se ainda está conectado
                if (client == null || !client.isConnected()) {
                    int option = JOptionPane.showConfirmDialog(this,
                        "Conexão com o servidor foi perdida.\n\nDeseja tentar reconectar?",
                        "Conexão Perdida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                        
                    if (option == JOptionPane.YES_OPTION) {
                        // Voltar para tela de login para reconectar
                        LoginWindow loginWindow = new LoginWindow();
                        loginWindow.setVisible(true);
                        this.dispose();
                        return;
                    }
                } else {
                    AnimationUtils.showToast(this, 
                                           "❌ Erro: Resposta vazia do servidor (Tentativa " + (tentativasCarregamento + 1) + "/" + MAX_TENTATIVAS_CARREGAMENTO + ")", 
                                           4000, 
                                           NewPixTheme.ERROR_COLOR);
                    
                    // Tentar novamente automaticamente se ainda não excedeu o limite
                    if (tentativasCarregamento < MAX_TENTATIVAS_CARREGAMENTO - 1) {
                        Timer retryTimer = new Timer(3000, e -> {
                            carregarDadosUsuarioComTentativas(true);
                        });
                        retryTimer.setRepeats(false);
                        retryTimer.start();
                    }
                }
            }
            
        } catch (Exception e) {
            CLILogger.error("Erro ao processar dados do usuário: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            
            // Definir valores padrão em caso de erro crítico
            nomeLabel.setText("Nome: Erro crítico (Tentativa " + (tentativasCarregamento + 1) + "/" + MAX_TENTATIVAS_CARREGAMENTO + ")");
            cpfLabel.setText("CPF: Indisponível");
            saldoLabel.setText("Saldo: Indisponível");
            
            String errorMessage;
            if (e.getMessage() != null) {
                String msg = e.getMessage().toLowerCase();
                if (msg.contains("connection") || msg.contains("conexão")) {
                    errorMessage = "Erro de conexão com o servidor";
                } else if (msg.contains("timeout")) {
                    errorMessage = "Timeout na comunicação";
                } else if (msg.contains("json") || msg.contains("parse")) {
                    errorMessage = "Erro ao processar resposta do servidor";
                } else {
                    errorMessage = "Erro ao processar dados: " + e.getMessage();
                }
            } else {
                errorMessage = "Erro crítico: " + e.getClass().getSimpleName();
            }
            
            AnimationUtils.showToast(this, 
                                   "❌ " + errorMessage + " (Tentativa " + (tentativasCarregamento + 1) + "/" + MAX_TENTATIVAS_CARREGAMENTO + ")", 
                                   4000, 
                                   NewPixTheme.ERROR_COLOR);
                                   
            // Tentar novamente automaticamente se ainda não excedeu o limite
            if (tentativasCarregamento < MAX_TENTATIVAS_CARREGAMENTO - 1) {
                Timer errorTimer = new Timer(2000, ex -> {
                    carregarDadosUsuarioComTentativas(true);
                });
                errorTimer.setRepeats(false);
                errorTimer.start();
            } else {
                // Última tentativa - oferecer opção manual
                Timer errorTimer = new Timer(2000, ex -> {
                    int retryOption = JOptionPane.showConfirmDialog(this,
                        "Erro crítico ao carregar dados do usuário (Última tentativa).\n\n" + errorMessage + 
                        "\n\nDeseja tentar novamente ou voltar ao login?",
                        "Erro Crítico",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                        
                    if (retryOption == JOptionPane.YES_OPTION) {
                        carregarDadosUsuarioComTentativas(true);
                    } else if (retryOption == JOptionPane.NO_OPTION) {
                        // Voltar para login
                        client.disconnect();
                        LoginWindow loginWindow = new LoginWindow();
                        loginWindow.setVisible(true);
                        this.dispose();
                    }
                });
                errorTimer.setRepeats(false);
                errorTimer.start();
            }
        }
    }
    
    private void enviarPix() {
        try {
            String valorStr = valorPixField.getText().trim().replace(",", ".");
            String cpfDestino = cpfDestinoField.getText().trim();
            
            if (valorStr.isEmpty() || cpfDestino.isEmpty()) {
                AnimationUtils.showToast(this, 
                                       "⚠️ Todos os campos são obrigatórios!", 
                                       2500, 
                                       NewPixTheme.WARNING_COLOR);
                return;
            }
            
            // Validar CPF de destino
            if (!CpfUtil.validarFormatoCpf(cpfDestino)) {
                AnimationUtils.showToast(this, 
                                       "⚠️ CPF de destino inválido! Use: 000.000.000-00", 
                                       3000, 
                                       NewPixTheme.WARNING_COLOR);
                cpfDestinoField.requestFocus();
                return;
            }
            
            // Normalizar CPF para envio
            String cpfDestinoNormalizado = CpfUtil.normalizarCpf(cpfDestino);
            
            double valor = Double.parseDouble(valorStr);
            
            if (valor <= 0) {
                AnimationUtils.showToast(this, 
                                       "⚠️ Valor deve ser positivo!", 
                                       2500, 
                                       NewPixTheme.WARNING_COLOR);
                return;
            }
            
            boolean success = client.criarPix(token, valor, cpfDestinoNormalizado);
            
            if (success) {
                AnimationUtils.showToast(this, 
                                       "✅ PIX enviado com sucesso!", 
                                       3000, 
                                       NewPixTheme.SUCCESS_COLOR);
                valorPixField.setText("");
                cpfDestinoField.setText("");
                carregarDadosUsuario(); // Atualizar saldo
                carregarExtrato(); // Atualizar extrato
            } else {
                AnimationUtils.showToast(this, 
                                       "❌ Erro ao enviar PIX", 
                                       3000, 
                                       NewPixTheme.ERROR_COLOR);
            }
            
        } catch (NumberFormatException e) {
            AnimationUtils.showToast(this, 
                                   "⚠️ Valor inválido!", 
                                   2500, 
                                   NewPixTheme.WARNING_COLOR);
        }
    }
    
    private void atualizarDados() {
        String novoNome = novoNomeField.getText().trim();
        String novaSenha = new String(novaSenhaField.getPassword());
        
        if (novoNome.isEmpty() && novaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe pelo menos um campo para atualizar!",
                                        "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Funcionalidade de atualização não disponível na versão simplificada
        JOptionPane.showMessageDialog(this, "Funcionalidade em desenvolvimento",
                                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void carregarExtrato() {
        String historicoJson = client.getHistoricoTransacoes(token);
        
        if (historicoJson != null && !historicoJson.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode responseNode = mapper.readTree(historicoJson);
                
                // Limpar tabela
                tableModel.setRowCount(0);
                
                if (responseNode.has("success") && responseNode.get("success").asBoolean() 
                    && responseNode.has("transacoes")) {
                    JsonNode transacoes = responseNode.get("transacoes");
                    
                    // Adicionar transações à tabela
                    for (JsonNode transacao : transacoes) {
                        String data = transacao.get("criado_em").asText();
                        double valor = transacao.get("valor_enviado").asDouble();
                    
                    JsonNode enviador = transacao.get("usuario_enviador");
                    JsonNode recebedor = transacao.get("usuario_recebedor");
                    
                    String cpfEnviador = enviador.get("cpf").asText();
                    String nomeEnviador = enviador.get("nome").asText();
                    String cpfRecebedor = recebedor.get("cpf").asText();
                    String nomeRecebedor = recebedor.get("nome").asText();
                    
                    String tipo;
                    String origem_destino;
                    
                    if (cpfEnviador.equals(cpfUsuario)) {
                        tipo = "Enviado";
                        origem_destino = nomeRecebedor + " (" + cpfRecebedor + ")";
                    } else {
                        tipo = "Recebido";
                        origem_destino = nomeEnviador + " (" + cpfEnviador + ")";
                    }
                    
                    String dataFormatada = LocalDateTime.parse(data, DateTimeFormatter.ISO_DATE_TIME)
                                          .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    
                    tableModel.addRow(new Object[]{
                        dataFormatada,
                        tipo,
                        currencyFormat.format(valor),
                        origem_destino
                    });
                }
                } else {
                    // Nenhuma transação encontrada ou erro na resposta
                    JOptionPane.showMessageDialog(this, "Nenhuma transação encontrada",
                                                "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao processar extrato: " + e.getMessage(),
                                            "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            tableModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, "Erro ao carregar histórico",
                                        "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void realizarLogout() {
        int option = JOptionPane.showConfirmDialog(this, "Deseja realmente sair?", 
                                                  "Confirmar Logout", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            client.disconnect();
            
            // Voltar para tela de login
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
            this.dispose();
        }
    }
    
    private void setupDepositoAction() {
        depositoButton.addActionListener(e -> {
            try {
                String valorStr = valorDepositoField.getText().trim();
                
                if (valorStr.isEmpty()) {
                    showAlert("Por favor, digite o valor do depósito", "warning");
                    return;
                }
                
                // Normalizar formato (aceitar vírgula ou ponto)
                valorStr = valorStr.replace(",", ".");
                
                double valor;
                try {
                    valor = Double.parseDouble(valorStr);
                } catch (NumberFormatException ex) {
                    showAlert("Valor inválido. Use formato: 100.50", "error");
                    return;
                }
                
                if (valor <= 0) {
                    showAlert("O valor deve ser maior que zero", "warning");
                    return;
                }
                
                if (valor < 0.01) {
                    showAlert("Valor mínimo: R$ 0,01", "warning");
                    return;
                }
                
                // Desabilitar botão durante processamento
                depositoButton.setEnabled(false);
                depositoButton.setText("Processando...");
                
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        return client.realizarDepositoComMensagem(token, valor);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            String resultado = get();
                            if (resultado.contains("sucesso") || resultado.contains("realizado")) {
                                showAlert("Depósito realizado com sucesso!", "success");
                                valorDepositoField.setText("");
                                updateUserData(); // Atualizar saldo
                            } else {
                                showAlert("Erro no depósito: " + resultado, "error");
                            }
                        } catch (Exception ex) {
                            showAlert("Erro na conexão: " + ex.getMessage(), "error");
                        } finally {
                            depositoButton.setEnabled(true);
                            depositoButton.setText("Fazer Depósito");
                        }
                    }
                };
                worker.execute();
                
            } catch (Exception ex) {
                showAlert("Erro inesperado: " + ex.getMessage(), "error");
                depositoButton.setEnabled(true);
                depositoButton.setText("Fazer Depósito");
            }
        });
    }
    
    private void showAlert(String message, String type) {
        SwingUtilities.invokeLater(() -> {
            Color backgroundColor;
            Color textColor = Color.WHITE;
            
            switch(type.toLowerCase()) {
                case "success":
                    backgroundColor = new Color(76, 175, 80); // Verde
                    break;
                case "warning":
                    backgroundColor = new Color(255, 152, 0); // Laranja
                    break;
                case "error":
                    backgroundColor = new Color(244, 67, 54); // Vermelho
                    break;
                default:
                    backgroundColor = new Color(33, 150, 243); // Azul (info)
            }
            
            alertLabel.setText(message);
            alertLabel.setBackground(backgroundColor);
            alertLabel.setForeground(textColor);
            alertLabel.setVisible(true);
            
            // Auto-hide alert after 5 seconds
            Timer timer = new Timer(5000, e -> alertLabel.setVisible(false));
            timer.setRepeats(false);
            timer.start();
        });
    }
    
    private void updateUserData() {
        carregarDadosUsuario();
    }
    
    /**
     * Cria um campo de texto moderno.
     */
    private JTextField createModernTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(108, 117, 125), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    /**
     * Cria um botão moderno com gradiente e efeitos visuais.
     */
    private JButton createModernButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradiente vertical
                GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Texto centralizado
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBorder(null);
            }
        });
        
        return button;
    }
    
    /**
     * Cria um card moderno com aparência de cartão.
     */
    private JPanel createModernCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fundo branco com bordas arredondadas
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Sombra sutil
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 15, 15);
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }
    
    /**
     * Cria um JTabbedPane moderno com visual aprimorado.
     */
    private JTabbedPane createModernTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fundo com gradiente sutil
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(248, 249, 250),
                    0, getHeight(), new Color(233, 236, 239)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(248, 249, 250));
        tabbedPane.setForeground(Color.BLACK);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        return tabbedPane;
    }
}
