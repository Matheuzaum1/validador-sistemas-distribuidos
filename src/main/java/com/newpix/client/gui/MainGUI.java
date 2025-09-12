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
    
    // Componentes para dispositivos conectados
    private JTable dispositivosTable;
    private DefaultTableModel dispositivosTableModel;
    private JButton atualizarDispositivosButton;
    
    // Componentes para configurações de conexão
    private JTextField configHostField;
    private JTextField configPortField;
    private JButton testarConexaoButton;
    private JButton salvarConfigButton;
    
    private DecimalFormat currencyFormat = new DecimalFormat("R$ #,##0.00");
    
    public MainGUI(NewPixClient client, String token) {
        this.client = client;
        this.token = token;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        carregarDadosUsuario();
    }
    
    private void initializeComponents() {
        // Aplicar tema
        NewPixTheme.applyTheme();
        
        setTitle(NewPixTheme.Icons.MONEY + " NewPix - Sistema Bancário");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setBackground(NewPixTheme.BACKGROUND_LIGHT);
        
        // Labels de informação do usuário
        nomeLabel = NewPixTheme.createTitle("Nome: Carregando...");
        cpfLabel = new JLabel("CPF: Carregando...");
        cpfLabel.setFont(NewPixTheme.FONT_BODY);
        cpfLabel.setForeground(NewPixTheme.TEXT_SECONDARY);
        
        saldoLabel = new JLabel("Saldo: Carregando...");
        saldoLabel.setFont(NewPixTheme.FONT_TITLE);
        saldoLabel.setForeground(NewPixTheme.SUCCESS_COLOR);
        
        // Campos para PIX
        valorPixField = new JTextField(15);
        cpfDestinoField = new JTextField(20);
        pixButton = NewPixTheme.createStyledButton(NewPixTheme.Icons.SEND + " Enviar PIX", NewPixTheme.ButtonStyle.PRIMARY);
        
        // Campos para depósito
        valorDepositoField = new JTextField(15);
        depositoButton = new JButton("Fazer Depósito");
        depositoButton.setBackground(new Color(34, 139, 34));
        depositoButton.setForeground(Color.WHITE);
        
        // Label para alertas (tipo PHP)
        alertLabel = new JLabel(" ");
        alertLabel.setHorizontalAlignment(SwingConstants.CENTER);
        alertLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        alertLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        alertLabel.setOpaque(true);
        alertLabel.setVisible(false);
        
        // Campos para atualização de dados
        novoNomeField = new JTextField(20);
        novaSenhaField = new JPasswordField(20);
        atualizarButton = NewPixTheme.createStyledButton(NewPixTheme.Icons.SETTINGS + " Atualizar Dados", NewPixTheme.ButtonStyle.SUCCESS);
        
        // Botões de ação
        extratoButton = NewPixTheme.createStyledButton(NewPixTheme.Icons.REFRESH + " Atualizar Extrato", NewPixTheme.ButtonStyle.PRIMARY);
        logoutButton = NewPixTheme.createStyledButton(NewPixTheme.Icons.LOGOUT + " Logout", NewPixTheme.ButtonStyle.ERROR);
        
        // Tabela de extrato
        String[] columns = {"Data", "Tipo", "Valor", "Origem/Destino"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        extratoTable = new JTable(tableModel);
        extratoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Tabela de dispositivos conectados
        String[] dispositivosColumns = {"IP", "Porta", "Nome/ID", "Status", "Último Acesso"};
        dispositivosTableModel = new DefaultTableModel(dispositivosColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dispositivosTable = new JTable(dispositivosTableModel);
        dispositivosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Botão para atualizar dispositivos
        atualizarDispositivosButton = new JButton("Atualizar Dispositivos");
        
        // Componentes de configuração de conexão
        configHostField = new JTextField(20);
        configPortField = new JTextField(8);
        testarConexaoButton = new JButton("Testar Conexão");
        salvarConfigButton = new JButton("Salvar Configuração");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel superior - informações do usuário
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));
        userPanel.setBackground(new Color(240, 248, 255));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        userPanel.add(nomeLabel, gbc);
        gbc.gridx = 1;
        userPanel.add(cpfLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        userPanel.add(saldoLabel, gbc);
        
        // Adicionar label de alertas
        gbc.gridy = 2;
        userPanel.add(alertLabel, gbc);
        
        // Panel central - operações
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Aba PIX
        JPanel pixPanel = createPixPanel();
        tabbedPane.addTab("Enviar PIX", pixPanel);
        
        // Aba Depósito
        JPanel depositoPanel = createDepositoPanel();
        tabbedPane.addTab("Fazer Depósito", depositoPanel);
        
        // Aba Extrato
        JPanel extratoPanel = createExtratoPanel();
        tabbedPane.addTab("Extrato", extratoPanel);
        
        // Aba Configurações
        JPanel configPanel = createConfigPanel();
        tabbedPane.addTab("Configurações", configPanel);
        
        // Aba Dispositivos Conectados
        JPanel dispositivosPanel = createDispositivosPanel();
        tabbedPane.addTab("Dispositivos", dispositivosPanel);
        
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
    
    private JPanel createDispositivosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Dispositivos Conectados ao Servidor");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Tabela de dispositivos
        JScrollPane scrollPane = new JScrollPane(dispositivosTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(atualizarDispositivosButton);
        
        // Botão para reconectar ao servidor
        JButton reconectarButton = new JButton("Reconectar");
        reconectarButton.addActionListener(e -> tentarReconectar());
        buttonPanel.add(reconectarButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Carregar dados iniciais
        carregarDispositivosConectados();
        
        return panel;
    }
    
    private void carregarDispositivosConectados() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Simular dados de dispositivos conectados
                    SwingUtilities.invokeLater(() -> {
                        dispositivosTableModel.setRowCount(0);
                        
                        // Adicionar dispositivo local (cliente atual)
                        String localIP = getLocalIP();
                        dispositivosTableModel.addRow(new Object[]{
                            localIP, 
                            "Cliente", 
                            "Este Cliente", 
                            "Conectado",
                            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
                        });
                        
                        // Simular outros dispositivos (em um sistema real, isso viria do servidor)
                        dispositivosTableModel.addRow(new Object[]{
                            "192.168.1.100", 
                            "8080", 
                            "Servidor NewPix", 
                            "Online",
                            "Sempre ativo"
                        });
                        
                        dispositivosTableModel.addRow(new Object[]{
                            "192.168.1.101", 
                            "Cliente", 
                            "Cliente-002", 
                            "Conectado",
                            "09:30:15"
                        });
                    });
                    
                } catch (Exception e) {
                    CLILogger.error("Erro ao carregar dispositivos: " + e.getMessage());
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void tentarReconectar() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    showAlert("Tentando reconectar...", "info");
                    
                    if (client.isConnected()) {
                        showAlert("Já conectado ao servidor", "success");
                    } else {
                        boolean reconnected = client.connect();
                        if (reconnected) {
                            showAlert("Reconectado com sucesso!", "success");
                            carregarDispositivosConectados();
                        } else {
                            showAlert("Falha na reconexão", "error");
                        }
                    }
                } catch (Exception e) {
                    showAlert("Erro na reconexão: " + e.getMessage(), "error");
                }
                return null;
            }
        };
        worker.execute();
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
        
        // Setup dispositivos action
        atualizarDispositivosButton.addActionListener(e -> carregarDispositivosConectados());
        
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
                    }
                    
                } else if (dadosNode.has("nome") && dadosNode.has("cpf") && dadosNode.has("saldo")) {
                    // Formato direto com dados
                    nomeUsuario = dadosNode.get("nome").asText();
                    cpfUsuario = dadosNode.get("cpf").asText();
                    saldoUsuario = dadosNode.get("saldo").asDouble();
                    
                    nomeLabel.setText("Nome: " + nomeUsuario);
                    cpfLabel.setText("CPF: " + cpfUsuario);
                    saldoLabel.setText("Saldo: " + currencyFormat.format(saldoUsuario));
                    
                } else {
                    // Erro na resposta
                    String errorMsg = "Dados do usuário não encontrados";
                    if (dadosNode.has("message")) {
                        errorMsg = dadosNode.get("message").asText();
                    } else if (dadosNode.has("info")) {
                        errorMsg = dadosNode.get("info").asText();
                    }
                    
                    CLILogger.error("Erro ao carregar dados: " + errorMsg);
                    
                    // Usar toast em vez de JOptionPane
                    AnimationUtils.showToast(this, 
                                           "❌ Erro ao carregar dados: " + errorMsg, 
                                           3000, 
                                           NewPixTheme.ERROR_COLOR);
                }
                
            } else {
                CLILogger.error("Resposta vazia do servidor para dados do usuário");
                AnimationUtils.showToast(this, 
                                       "❌ Erro: Resposta vazia do servidor", 
                                       3000, 
                                       NewPixTheme.ERROR_COLOR);
            }
            
        } catch (Exception e) {
            CLILogger.error("Erro ao processar dados do usuário: " + e.getMessage());
            AnimationUtils.showToast(this, 
                                   "❌ Erro ao processar dados: " + e.getMessage(), 
                                   3000, 
                                   NewPixTheme.ERROR_COLOR);
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
            LoginGUI loginGUI = new LoginGUI();
            loginGUI.setVisible(true);
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
}
