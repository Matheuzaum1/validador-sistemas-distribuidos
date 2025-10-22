package com.distribuidos.client;

import com.distribuidos.common.MessageBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class ClientGUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(ClientGUI.class);
    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    
    // Componentes de conexão
    private JTextField serverHostField;
    private JTextField serverPortField;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel connectionStatusLabel;
    
    // Componentes de usuário
    private JTextField nomeField;
    private JTextField cpfField;
    private JPasswordField senhaField;
    private JButton loginButton;
    private JButton logoutButton;
    private JButton createUserButton;
    private JButton readUserButton;
    private JButton updateUserButton;
    private JButton deleteUserButton;
    
    // Componentes de informação
    private JLabel userInfoLabel;
    private JTextArea logArea;
    // private JTable clientsTable;
    // private DefaultTableModel clientsTableModel;
    
    // Estado
    private ClientConnection connection;
    private String currentToken;
    private boolean isLoggedIn = false;
    private final ObjectMapper mapper = new ObjectMapper();
    
    public ClientGUI() {
        connection = new ClientConnection(this);
        initializeGUI();
        updateUI();
    }
    
    private void initializeGUI() {
        setTitle("Cliente - Sistema Distribuído");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Painel principal com abas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Aba de Conexão e Usuário
        JPanel mainPanel = createMainPanel();
        tabbedPane.addTab("Principal", mainPanel);
        
    // Aba de Transações
    JPanel transactionsPanel = createTransactionsPanel();
    tabbedPane.addTab("Transações", transactionsPanel);
        
    // Aba de Clientes Conectados removida
        
        add(tabbedPane);
        
        // Listener para desconectar ao fechar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (connection.isConnected()) {
                    connection.disconnect();
                }
                System.exit(0);
            }
        });
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Painel superior - Conexão
        JPanel connectionPanel = createConnectionPanel();
        panel.add(connectionPanel, BorderLayout.NORTH);
        
        // Painel central - Usuário
        JPanel userPanel = createUserPanel();
        panel.add(userPanel, BorderLayout.CENTER);
        
        // Painel inferior - Log
        JPanel logPanel = createLogPanel();
        panel.add(logPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Conexão com Servidor"));
        
        JPanel fieldsPanel = new JPanel(new FlowLayout());
        
        fieldsPanel.add(new JLabel("Host:"));
        serverHostField = new JTextField("localhost", 15);
        fieldsPanel.add(serverHostField);
        
        fieldsPanel.add(new JLabel("Porta:"));
        serverPortField = new JTextField("8080", 8);
        fieldsPanel.add(serverPortField);
        
        connectButton = new JButton("Conectar");
        connectButton.addActionListener(e -> connectToServer());
        fieldsPanel.add(connectButton);
        
        disconnectButton = new JButton("Desconectar");
        disconnectButton.setEnabled(false);
        disconnectButton.addActionListener(e -> disconnectFromServer());
        fieldsPanel.add(disconnectButton);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
    connectionStatusLabel = new JLabel("Status: Desconectado");
    connectionStatusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    connectionStatusLabel.setForeground(Color.RED);
        panel.add(connectionStatusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Painel de campos
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        fieldsPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nomeField = new JTextField(20);
        fieldsPanel.add(nomeField, gbc);
        
        // CPF
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cpfField = new JTextField(20);
        cpfField.setDocument(new CpfFormatter()); // Formatação automática de CPF
        cpfField.setToolTipText("Digite apenas os números do CPF (ex: 12345678901)");
        fieldsPanel.add(cpfField, gbc);
        
        // Senha
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        senhaField = new JPasswordField(20);
        fieldsPanel.add(senhaField, gbc);
        
        panel.add(fieldsPanel, BorderLayout.NORTH);
        
        // Painel de botões
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Operações"));
        
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> performLogin());
        buttonsPanel.add(loginButton);
        
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> performLogout());
        buttonsPanel.add(logoutButton);
        
        createUserButton = new JButton("Criar Usuário");
        createUserButton.addActionListener(e -> performCreateUser());
        buttonsPanel.add(createUserButton);
        
        readUserButton = new JButton("Ler Usuário");
        readUserButton.addActionListener(e -> performReadUser());
        buttonsPanel.add(readUserButton);
        
        updateUserButton = new JButton("Alterar Dados");
        updateUserButton.addActionListener(e -> performUpdateUser());
        buttonsPanel.add(updateUserButton);
        
        deleteUserButton = new JButton("Deletar Conta");
        deleteUserButton.addActionListener(e -> performDeleteUser());
        buttonsPanel.add(deleteUserButton);
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        // Painel de informações do usuário
        userInfoLabel = new JLabel("Usuário: Não logado");
        userInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        userInfoLabel.setBorder(BorderFactory.createTitledBorder("Informações"));
        panel.add(userInfoLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Log de Comunicação"));
        
        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        // Configurar fonte que suporte caracteres UTF-8
        logArea.setFont(new Font("Dialog", Font.PLAIN, 11));
        // Garantir que a codificação seja UTF-8
        logArea.getDocument().putProperty("i18n", Boolean.TRUE);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton clearLogButton = new JButton("Limpar Log");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        panel.add(clearLogButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // createClientsPanel() removido porque a aba foi eliminada
    
    private void connectToServer() {
        try {
            String host = serverHostField.getText().trim();
            int port = Integer.parseInt(serverPortField.getText().trim());
            
            // Validação básica
            if (host.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Endereço do servidor não pode estar vazio", 
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Feedback visual durante conexão
            connectionStatusLabel.setText("Status: Conectando...");
            connectionStatusLabel.setForeground(Color.ORANGE);
            connectButton.setEnabled(false);
            addLogMessage("=== INICIANDO PROCESSO DE CONEXÃO ===");
            addLogMessage("Servidor destino: " + host + ":" + port);
            
            // Executar conexão em thread separada para não travar a UI
            new Thread(() -> {
                final boolean[] connectionResult = {false};
                
                try {
                    // Primeiro, testar conectividade básica
                    SwingUtilities.invokeLater(() -> {
                        connectionStatusLabel.setText("Status: Testando conectividade...");
                    });
                    
                    if (connection.testConnectivity(host, port)) {
                        // Se o teste passou, tentar conexão completa
                        SwingUtilities.invokeLater(() -> {
                            connectionStatusLabel.setText("Status: Estabelecendo conexão...");
                        });
                        connectionResult[0] = connection.connect(host, port);
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            addLogMessage("⚠ Teste de conectividade falhou - tentando conexão mesmo assim...");
                        });
                        // Mesmo que o teste falhe, tenta conectar (alguns firewalls bloqueiam ping)
                        connectionResult[0] = connection.connect(host, port);
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        addLogMessage("✗ Erro durante processo de conexão: " + e.getMessage());
                    });
                }
                
                // Atualizar UI na thread principal
                SwingUtilities.invokeLater(() -> {
                    connectButton.setEnabled(true);
                    
                    if (connectionResult[0]) {
                        connectionStatusLabel.setText("Status: Conectado a " + host + ":" + port);
                        connectionStatusLabel.setForeground(new Color(0, 128, 0)); // darker green
                        updateUI();
                        addLogMessage("🎉 Conexão estabelecida com sucesso!");
                    } else {
                        connectionStatusLabel.setText("Status: Falha na conexão");
                        connectionStatusLabel.setForeground(Color.RED);
                        addLogMessage("💥 Falha na conexão!");
                        
                        String diagnosticMessage = "Falha ao conectar ao servidor " + host + ":" + port + 
                            "\n\nVerificações realizadas:" +
                            "\n• Teste de conectividade" +
                            "\n• Tentativa de estabelecimento de socket TCP" +
                            "\n• Envio de mensagem de protocolo 'conectar'" +
                            "\n\nPossíveis causas:" +
                            "\n• Servidor não está rodando" +
                            "\n• Porta incorreta ou bloqueada" +
                            "\n• Problemas de rede/firewall" +
                            "\n• Servidor rejeitou a conexão" +
                            "\n\nConsulte o log detalhado para mais informações.";
                            
                        JOptionPane.showMessageDialog(this, diagnosticMessage, 
                            "Diagnóstico de Conexão", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }).start();
            
        } catch (NumberFormatException e) {
            connectButton.setEnabled(true);
            connectionStatusLabel.setText("Status: Desconectado");
            connectionStatusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Porta deve ser um número válido (ex: 8080)", 
                "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            connectButton.setEnabled(true);
            connectionStatusLabel.setText("Status: Erro");
            connectionStatusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void disconnectFromServer() {
        if (isLoggedIn) {
            performLogout();
        }
        
        connection.disconnect();
        connectionStatusLabel.setText("Status: Desconectado");
        connectionStatusLabel.setForeground(Color.RED);
        updateUI();
    }
    
    private void performLogin() {
        if (!validateCpf(cpfField.getText())) {
            JOptionPane.showMessageDialog(this, "CPF deve estar no formato 000.000.000-00", 
                "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String senha = new String(senhaField.getPassword());
        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(this, "Senha deve ter pelo menos 6 caracteres", 
                "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String response = connection.login(cpfField.getText(), senha);
            
            if (MessageBuilder.extractStatus(response)) {
                JsonNode node = mapper.readTree(response);
                currentToken = node.get("token").asText();
                isLoggedIn = true;
                
                userInfoLabel.setText("Usuário: " + cpfField.getText() + " (Logado)");
                userInfoLabel.setForeground(new Color(0, 70, 140)); // dark blue for readability
                
                JOptionPane.showMessageDialog(this, "Login realizado com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, MessageBuilder.extractInfo(response), 
                    "Erro de Login", JOptionPane.ERROR_MESSAGE);
            }
            
            updateUI();
            
        } catch (Exception e) {
            logger.error("Erro no login", e);
            JOptionPane.showMessageDialog(this, "Erro no login: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performLogout() {
        if (!isLoggedIn || currentToken == null) {
            return;
        }
        
        try {
            String response = connection.logout(currentToken);
            
            if (MessageBuilder.extractStatus(response)) {
                currentToken = null;
                isLoggedIn = false;
                
                userInfoLabel.setText("Usuário: Não logado");
                userInfoLabel.setForeground(Color.BLACK);
                
                JOptionPane.showMessageDialog(this, "Logout realizado com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, MessageBuilder.extractInfo(response), 
                    "Erro de Logout", JOptionPane.ERROR_MESSAGE);
            }
            
            updateUI();
            
        } catch (Exception e) {
            logger.error("Erro no logout", e);
            JOptionPane.showMessageDialog(this, "Erro no logout: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performCreateUser() {
        if (!validateUserFields()) {
            return;
        }
        
        try {
            String response = connection.createUser(nomeField.getText(), cpfField.getText(), 
                new String(senhaField.getPassword()));
            
            if (MessageBuilder.extractStatus(response)) {
                JOptionPane.showMessageDialog(this, "Usuário criado com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, MessageBuilder.extractInfo(response), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao criar usuário", e);
            JOptionPane.showMessageDialog(this, "Erro ao criar usuário: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performReadUser() {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Você precisa estar logado para ler dados do usuário", 
                "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String response = connection.readUser(currentToken);
            
            if (MessageBuilder.extractStatus(response)) {
                JsonNode node = mapper.readTree(response);
                JsonNode usuario = node.get("usuario");
                
                String info = String.format(
                    "CPF: %s\nNome: %s\nSaldo: R$ %.2f",
                    usuario.get("cpf").asText(),
                    usuario.get("nome").asText(),
                    usuario.get("saldo").asDouble()
                );
                
                JOptionPane.showMessageDialog(this, info, "Dados do Usuário", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, MessageBuilder.extractInfo(response), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao ler usuário", e);
            JOptionPane.showMessageDialog(this, "Erro ao ler usuário: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performUpdateUser() {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Você precisa estar logado para alterar dados", 
                "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nome = nomeField.getText().trim();
        String senha = new String(senhaField.getPassword());
        
        if (nome.isEmpty() && senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha pelo menos o nome ou a senha para atualizar", 
                "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!nome.isEmpty() && nome.length() < 6) {
            JOptionPane.showMessageDialog(this, "Nome deve ter pelo menos 6 caracteres", 
                "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!senha.isEmpty() && senha.length() < 6) {
            JOptionPane.showMessageDialog(this, "Senha deve ter pelo menos 6 caracteres", 
                "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String response = connection.updateUser(currentToken, 
                nome.isEmpty() ? null : nome, 
                senha.isEmpty() ? null : senha);
            
            if (MessageBuilder.extractStatus(response)) {
                JOptionPane.showMessageDialog(this, "Dados atualizados com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, MessageBuilder.extractInfo(response), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário", e);
            JOptionPane.showMessageDialog(this, "Erro ao atualizar usuário: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performDeleteUser() {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Você precisa estar logado para deletar a conta", 
                "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja deletar sua conta?\nEsta ação não pode ser desfeita!",
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            String response = connection.deleteUser(currentToken);
            
            if (MessageBuilder.extractStatus(response)) {
                currentToken = null;
                isLoggedIn = false;
                
                userInfoLabel.setText("Usuário: Não logado");
                userInfoLabel.setForeground(Color.BLACK);
                
                JOptionPane.showMessageDialog(this, "Conta deletada com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, MessageBuilder.extractInfo(response), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
            updateUI();
            
        } catch (Exception e) {
            logger.error("Erro ao deletar usuário", e);
            JOptionPane.showMessageDialog(this, "Erro ao deletar usuário: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateUserFields() {
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String senha = new String(senhaField.getPassword());
        
        if (nome.length() < 6) {
            JOptionPane.showMessageDialog(this, "Nome deve ter pelo menos 6 caracteres", 
                "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!validateCpf(cpf)) {
            JOptionPane.showMessageDialog(this, "CPF deve estar no formato 000.000.000-00", 
                "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(this, "Senha deve ter pelo menos 6 caracteres", 
                "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private boolean validateCpf(String cpf) {
        return cpf != null && CPF_PATTERN.matcher(cpf).matches();
    }
    
    private void clearFields() {
        nomeField.setText("");
        cpfField.setText("");
        senhaField.setText("");
    }
    
    private void updateUI() {
        boolean connected = connection.isConnected();
        
        // Botões de conexão
        connectButton.setEnabled(!connected);
        disconnectButton.setEnabled(connected);
        serverHostField.setEnabled(!connected);
        serverPortField.setEnabled(!connected);
        
        // Botões de usuário
        loginButton.setEnabled(connected && !isLoggedIn);
        logoutButton.setEnabled(connected && isLoggedIn);
        createUserButton.setEnabled(connected && !isLoggedIn);
        readUserButton.setEnabled(connected && isLoggedIn);
        updateUserButton.setEnabled(connected && isLoggedIn);
        deleteUserButton.setEnabled(connected && isLoggedIn);
        // Ativa/desativa controles de transação
        // (procura por componentes na aba de transações)
        // Habilitação será tratada pelos próprios botões ao executar
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Transações"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        form.add(new JLabel("CPF Destino:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField cpfDestinoField = new JTextField(20);
        cpfDestinoField.setDocument(new CpfFormatter()); // Formatação automática de CPF
        cpfDestinoField.setToolTipText("Digite apenas os números do CPF (ex: 12345678901)");
        form.add(cpfDestinoField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        form.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField valorField = new JTextField(12);
        form.add(valorField, gbc);

        panel.add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton transferButton = new JButton("Transferir");
        JButton depositButton = new JButton("Depositar");

        transferButton.addActionListener(e -> {
            if (!connection.isConnected()) {
                JOptionPane.showMessageDialog(this, "Você precisa estar conectado ao servidor", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isLoggedIn || currentToken == null) {
                JOptionPane.showMessageDialog(this, "Você precisa estar logado para realizar transações", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cpfDestino = cpfDestinoField.getText().trim();
            if (!validateCpf(cpfDestino)) {
                JOptionPane.showMessageDialog(this, "CPF destino inválido. Formato: 000.000.000-00", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double valor;
            try {
                valor = Double.parseDouble(valorField.getText().trim().replace(',', '.'));
                if (valor <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido. Informe um número maior que zero.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String response = connection.transfer(currentToken, cpfDestino, valor);
                if (MessageBuilder.extractStatus(response)) {
                    JOptionPane.showMessageDialog(this, "Transferência efetuada com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, MessageBuilder.extractInfo(response), "Erro", JOptionPane.ERROR_MESSAGE);
                }
                addLogMessage("Transação transferir -> " + response);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao realizar transferência: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        depositButton.addActionListener(e -> {
            if (!connection.isConnected()) {
                JOptionPane.showMessageDialog(this, "Você precisa estar conectado ao servidor", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isLoggedIn || currentToken == null) {
                JOptionPane.showMessageDialog(this, "Você precisa estar logado para realizar depósitos", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double valor;
            try {
                valor = Double.parseDouble(valorField.getText().trim().replace(',', '.'));
                if (valor <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido. Informe um número maior que zero.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String response = connection.deposit(currentToken, valor);
                if (MessageBuilder.extractStatus(response)) {
                    JOptionPane.showMessageDialog(this, "Depósito efetuado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, MessageBuilder.extractInfo(response), "Erro", JOptionPane.ERROR_MESSAGE);
                }
                addLogMessage("Transação depositar -> " + response);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao realizar depósito: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttons.add(transferButton);
        buttons.add(depositButton);
        panel.add(buttons, BorderLayout.CENTER);

        return panel;
    }
    
    // Método relacionado a lista de clientes removido
    
    public void addLogMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}