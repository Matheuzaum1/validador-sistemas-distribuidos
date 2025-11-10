package com.distribuidos.client;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.*;
import org.slf4j.*;
import com.distribuidos.common.*;
import com.fasterxml.jackson.databind.*;

public class ClientGUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(ClientGUI.class);
    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private static final String CARD_AUTH = "auth";
    private static final String CARD_MAIN = "main";
    
    private ClientConnection connection;
    private String currentToken;
    private String currentUserCpf;
    private boolean isLoggedIn = false;
    private final ObjectMapper mapper = new ObjectMapper();
    private JTextArea logArea;
    
    public ClientGUI() {
        connection = new ClientConnection(this);
        initializeGUI();
        SwingUtilities.invokeLater(this::showConnectionDialog);
    }
    
    private void initializeGUI() {
        setTitle("Cliente - Sistema Distribuído");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        JPanel authPanel = createAuthPanel();
        JPanel mainPanel = createMainPanel();
        
        mainContainer.add(authPanel, CARD_AUTH);
        mainContainer.add(mainPanel, CARD_MAIN);
        
        JPanel logPanel = createLogPanel();
        
        setLayout(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (connection.isConnected()) connection.disconnect();
                System.exit(0);
            }
        });
        
        cardLayout.show(mainContainer, CARD_AUTH);
    }
    
    private void showConnectionDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Host do Servidor:"), gbc);
        gbc.gridx = 1;
        JTextField hostField = new JTextField("localhost", 15);
        panel.add(hostField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Porta:"), gbc);
        gbc.gridx = 1;
        JTextField portField = new JTextField("8080", 15);
        panel.add(portField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Conectar ao Servidor", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String host = hostField.getText().trim();
            String portStr = portField.getText().trim();
            
            if (host.isEmpty() || portStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o host e a porta!", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return;
            }
            
            try {
                int port = Integer.parseInt(portStr);
                connectToServer(host, port);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Porta inválida! Use um número entre 1 e 65535.", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }
    
    private void connectToServer(String host, int port) {
        addLogMessage("=== TENTANDO CONECTAR AO SERVIDOR ===");
        addLogMessage("Host: " + host + " | Porta: " + port);
        
        new Thread(() -> {
            boolean success = connection.connect(host, port);
            
            SwingUtilities.invokeLater(() -> {
                if (success) {
                    addLogMessage("✓ Conectado com sucesso!");
                    ToastNotification.showSuccess("Conexão", "Conectado ao servidor com sucesso!");
                    cardLayout.show(mainContainer, CARD_AUTH);
                } else {
                    addLogMessage("✗ Falha na conexão!");
                    JOptionPane.showMessageDialog(this,
                        "Não foi possível conectar ao servidor.\\nVerifique se o servidor está rodando e tente novamente.",
                        "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            });
        }).start();
    }
    
    private JPanel createAuthPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Bem-vindo ao Sistema Distribuído", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        
        JButton createAccountButton = new JButton("Criar Nova Conta");
        createAccountButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        createAccountButton.addActionListener(e -> showCreateAccountDialog());
        gbc.gridy = 0;
        centerPanel.add(createAccountButton, gbc);
        
        JButton loginButton = new JButton("Fazer Login");
        loginButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        loginButton.addActionListener(e -> showLoginDialog());
        gbc.gridy = 1;
        centerPanel.add(loginButton, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void showCreateAccountDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        JTextField nomeField = new JTextField(20);
        nomeField.setToolTipText("Mínimo 6 caracteres");
        panel.add(nomeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        JTextField cpfField = new JTextField(20);
        cpfField.setDocument(new CpfFormatter());
        cpfField.setToolTipText("Digite apenas números (11 dígitos)");
        panel.add(cpfField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        JPasswordField senhaField = new JPasswordField(20);
        senhaField.setToolTipText("Mínimo 6 caracteres");
        panel.add(senhaField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Criar Nova Conta",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText().trim();
            String cpf = cpfField.getText().trim();
            String senha = new String(senhaField.getPassword());
            
            if (nome.length() < 6) {
                ToastNotification.showError("Validação", "Nome deve ter pelo menos 6 caracteres");
                return;
            }
            
            if (!validateCpf(cpf)) {
                ToastNotification.showError("Validação", "CPF deve estar no formato 000.000.000-00");
                return;
            }
            
            if (senha.length() < 6) {
                ToastNotification.showError("Validação", "Senha deve ter pelo menos 6 caracteres");
                return;
            }
            
            performCreateAccount(nome, cpf, senha);
        }
    }
    
    private void performCreateAccount(String nome, String cpf, String senha) {
        try {
            String response = connection.createUser(nome, cpf, senha);
            
            if (MessageBuilder.extractStatus(response)) {
                ToastNotification.showSuccess("Sucesso", "Conta criada com sucesso!");
                addLogMessage("✓ Conta criada: " + cpf);
                performAutoLogin(cpf, senha);
            } else {
                String errorMsg = MessageBuilder.extractInfo(response);
                ToastNotification.showError("Erro", errorMsg);
                addLogMessage("✗ Falha ao criar conta: " + errorMsg);
            }
        } catch (Exception e) {
            logger.error("Erro ao criar conta", e);
            ToastNotification.showError("Erro", "Erro ao criar conta: " + e.getMessage());
        }
    }
    
    private void performAutoLogin(String cpf, String senha) {
        addLogMessage("→ Fazendo login automático...");
        try {
            String response = connection.login(cpf, senha);
            
            if (MessageBuilder.extractStatus(response)) {
                JsonNode node = mapper.readTree(response);
                currentToken = node.get("token").asText();
                currentUserCpf = cpf;
                isLoggedIn = true;
                
                addLogMessage("✓ Login automático realizado com sucesso!");
                ToastNotification.showSuccess("Bem-vindo", "Login realizado com sucesso!");
                
                cardLayout.show(mainContainer, CARD_MAIN);
                updateMainPanelUI();
            } else {
                ToastNotification.showError("Login", "Conta criada, mas falha no login automático. Por favor, faça login manualmente.");
            }
        } catch (Exception e) {
            logger.error("Erro no login automático", e);
            ToastNotification.showError("Erro", "Conta criada, mas erro no login automático.");
        }
    }
    
    private void showLoginDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        JTextField cpfField = new JTextField(20);
        cpfField.setDocument(new CpfFormatter());
        cpfField.setToolTipText("Digite apenas números (11 dígitos)");
        panel.add(cpfField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        JPasswordField senhaField = new JPasswordField(20);
        panel.add(senhaField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Fazer Login",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String cpf = cpfField.getText().trim();
            String senha = new String(senhaField.getPassword());
            
            if (!validateCpf(cpf)) {
                ToastNotification.showError("Validação", "CPF deve estar no formato 000.000.000-00");
                return;
            }
            
            if (senha.length() < 6) {
                ToastNotification.showError("Validação", "Senha deve ter pelo menos 6 caracteres");
                return;
            }
            
            performLogin(cpf, senha);
        }
    }
    
    private void performLogin(String cpf, String senha) {
        try {
            String response = connection.login(cpf, senha);
            
            if (MessageBuilder.extractStatus(response)) {
                JsonNode node = mapper.readTree(response);
                currentToken = node.get("token").asText();
                currentUserCpf = cpf;
                isLoggedIn = true;
                
                addLogMessage("✓ Login realizado: " + cpf);
                ToastNotification.showSuccess("Sucesso", "Login realizado com sucesso!");
                
                cardLayout.show(mainContainer, CARD_MAIN);
                updateMainPanelUI();
            } else {
                String errorMsg = MessageBuilder.extractInfo(response);
                ToastNotification.showError("Login", errorMsg);
                addLogMessage("✗ Falha no login: " + errorMsg);
            }
        } catch (Exception e) {
            logger.error("Erro no login", e);
            ToastNotification.showError("Erro", "Erro no login: " + e.getMessage());
        }
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel userInfoLabel = new JLabel("Usuário: Carregando...");
        userInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        userInfoLabel.setName("userInfoLabel");
        topPanel.add(userInfoLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Sair");
        logoutButton.addActionListener(e -> performLogout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Conta", createAccountPanel());
        tabbedPane.addTab("Transações", createTransactionsPanel());
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados da Conta"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField nomeField = new JTextField(20);
        nomeField.setToolTipText("Mínimo 6 caracteres");
        nomeField.setName("mainNomeField");
        formPanel.add(nomeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Nova Senha:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JPasswordField senhaField = new JPasswordField(20);
        senhaField.setToolTipText("Mínimo 6 caracteres (deixe vazio para não alterar)");
        senhaField.setName("mainSenhaField");
        formPanel.add(senhaField, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton readButton = new JButton("Consultar Dados");
        readButton.addActionListener(e -> performReadUser());
        buttonsPanel.add(readButton);
        
        JButton updateButton = new JButton("Atualizar Dados");
        updateButton.addActionListener(e -> {
            JTextField nameField = findComponentByName(this, "mainNomeField", JTextField.class);
            JPasswordField passField = findComponentByName(this, "mainSenhaField", JPasswordField.class);
            performUpdateUser(nameField, passField);
        });
        buttonsPanel.add(updateButton);
        
        JButton deleteButton = new JButton("Deletar Conta");
        deleteButton.addActionListener(e -> performDeleteUser());
        buttonsPanel.add(deleteButton);
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Realizar Transação"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("CPF Destino:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField cpfDestinoField = new JTextField(20);
        cpfDestinoField.setDocument(new CpfFormatter());
        cpfDestinoField.setToolTipText("Digite apenas números (11 dígitos)");
        cpfDestinoField.setName("cpfDestinoField");
        formPanel.add(cpfDestinoField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField valorField = new JTextField(15);
        valorField.setDocument(new MoneyFormatter());
        valorField.setToolTipText("Digite o valor em reais");
        valorField.setName("valorField");
        formPanel.add(valorField, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton transferButton = new JButton("Transferir");
        transferButton.addActionListener(e -> {
            JTextField cpfField = findComponentByName(this, "cpfDestinoField", JTextField.class);
            JTextField valField = findComponentByName(this, "valorField", JTextField.class);
            performTransfer(cpfField, valField);
        });
        buttonsPanel.add(transferButton);
        
        JButton depositButton = new JButton("Depositar");
        depositButton.addActionListener(e -> {
            JTextField valField = findComponentByName(this, "valorField", JTextField.class);
            performDeposit(valField);
        });
        buttonsPanel.add(depositButton);
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Log de Comunicação"));
        
        logArea = new JTextArea(6, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton clearButton = new JButton("Limpar Log");
        clearButton.addActionListener(e -> logArea.setText(""));
        panel.add(clearButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void performReadUser() {
        if (!isLoggedIn) {
            ToastNotification.showWarning("Aviso", "Você precisa estar logado");
            return;
        }
        
        try {
            String response = connection.readUser(currentToken);
            
            if (MessageBuilder.extractStatus(response)) {
                JsonNode node = mapper.readTree(response);
                JsonNode usuario = node.get("usuario");
                
                String info = String.format("CPF: %s\\nNome: %s\\nSaldo: R$ %.2f",
                    usuario.get("cpf").asText(),
                    usuario.get("nome").asText(),
                    usuario.get("saldo").asDouble());
                
                JOptionPane.showMessageDialog(this, info, "Dados da Conta", JOptionPane.INFORMATION_MESSAGE);
                addLogMessage("✓ Dados consultados com sucesso");
            } else {
                String errorMsg = MessageBuilder.extractInfo(response);
                ToastNotification.showError("Erro", errorMsg);
                addLogMessage("✗ Erro ao consultar dados: " + errorMsg);
            }
        } catch (Exception e) {
            logger.error("Erro ao ler usuário", e);
            ToastNotification.showError("Erro", "Erro ao ler usuário: " + e.getMessage());
        }
    }
    
    private void performUpdateUser(JTextField nomeField, JPasswordField senhaField) {
        if (!isLoggedIn) {
            ToastNotification.showWarning("Aviso", "Você precisa estar logado");
            return;
        }
        
        String nome = nomeField.getText().trim();
        String senha = new String(senhaField.getPassword());
        
        if (nome.isEmpty() && senha.isEmpty()) {
            ToastNotification.showWarning("Validação", "Preencha pelo menos o nome ou a senha para atualizar");
            return;
        }
        
        if (!nome.isEmpty() && nome.length() < 6) {
            ToastNotification.showError("Validação", "Nome deve ter pelo menos 6 caracteres");
            return;
        }
        
        if (!senha.isEmpty() && senha.length() < 6) {
            ToastNotification.showError("Validação", "Senha deve ter pelo menos 6 caracteres");
            return;
        }
        
        try {
            String response = connection.updateUser(currentToken, 
                nome.isEmpty() ? null : nome, 
                senha.isEmpty() ? null : senha);
            
            if (MessageBuilder.extractStatus(response)) {
                ToastNotification.showSuccess("Sucesso", "Dados atualizados com sucesso!");
                addLogMessage("✓ Dados atualizados");
                nomeField.setText("");
                senhaField.setText("");
            } else {
                String errorMsg = MessageBuilder.extractInfo(response);
                ToastNotification.showError("Erro", errorMsg);
                addLogMessage("✗ Erro ao atualizar: " + errorMsg);
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário", e);
            ToastNotification.showError("Erro", "Erro ao atualizar: " + e.getMessage());
        }
    }
    
    private void performDeleteUser() {
        if (!isLoggedIn) {
            ToastNotification.showWarning("Aviso", "Você precisa estar logado");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja deletar sua conta?\\nEsta ação NÃO pode ser desfeita!",
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try {
            String response = connection.deleteUser(currentToken);
            
            if (MessageBuilder.extractStatus(response)) {
                ToastNotification.showSuccess("Sucesso", "Conta deletada com sucesso!");
                addLogMessage("✓ Conta deletada");
                performLogout();
            } else {
                String errorMsg = MessageBuilder.extractInfo(response);
                ToastNotification.showError("Erro", errorMsg);
                addLogMessage("✗ Erro ao deletar conta: " + errorMsg);
            }
        } catch (Exception e) {
            logger.error("Erro ao deletar usuário", e);
            ToastNotification.showError("Erro", "Erro ao deletar: " + e.getMessage());
        }
    }
    
    private void performLogout() {
        if (isLoggedIn && currentToken != null) {
            try {
                String response = connection.logout(currentToken);
                if (MessageBuilder.extractStatus(response)) {
                    addLogMessage("✓ Logout realizado com sucesso");
                    ToastNotification.showSuccess("Logout", "Desconectado com sucesso!");
                } else {
                    addLogMessage("⚠ Logout com aviso: " + MessageBuilder.extractInfo(response));
                }
            } catch (Exception e) {
                logger.error("Erro no logout", e);
                addLogMessage("⚠ Erro no logout: " + e.getMessage());
            }
        }
        
        isLoggedIn = false;
        currentToken = null;
        currentUserCpf = null;
        cardLayout.show(mainContainer, CARD_AUTH);
    }
    
    private void performTransfer(JTextField cpfDestinoField, JTextField valorField) {
        if (!isLoggedIn) {
            ToastNotification.showWarning("Aviso", "Você precisa estar logado");
            return;
        }
        
        String cpfDestino = cpfDestinoField.getText().trim();
        if (!validateCpf(cpfDestino)) {
            ToastNotification.showError("Validação", "CPF destino inválido. Formato: 000.000.000-00");
            return;
        }
        
        double valor;
        try {
            valor = MoneyFormatter.parseValue(valorField.getText());
            if (valor <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            ToastNotification.showError("Validação", "Valor inválido. Informe um número maior que zero.");
            return;
        }
        
        try {
            String response = connection.transfer(currentToken, cpfDestino, valor);
            
            if (MessageBuilder.extractStatus(response)) {
                ToastNotification.showSuccess("Sucesso", "Transferência efetuada com sucesso!");
                addLogMessage("✓ Transferência de R$ " + String.format("%.2f", valor) + " para " + cpfDestino);
                cpfDestinoField.setText("");
                valorField.setText("");
            } else {
                String errorMsg = MessageBuilder.extractInfo(response);
                ToastNotification.showError("Erro", errorMsg);
                addLogMessage("✗ Erro na transferência: " + errorMsg);
            }
        } catch (Exception ex) {
            logger.error("Erro ao transferir", ex);
            ToastNotification.showError("Erro", "Erro ao realizar transferência: " + ex.getMessage());
        }
    }
    
    private void performDeposit(JTextField valorField) {
        if (!isLoggedIn) {
            ToastNotification.showWarning("Aviso", "Você precisa estar logado");
            return;
        }
        
        double valor;
        try {
            valor = MoneyFormatter.parseValue(valorField.getText());
            if (valor <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            ToastNotification.showError("Validação", "Valor inválido. Informe um número maior que zero.");
            return;
        }
        
        try {
            String response = connection.deposit(currentToken, valor);
            
            if (MessageBuilder.extractStatus(response)) {
                ToastNotification.showSuccess("Sucesso", "Depósito efetuado com sucesso!");
                addLogMessage("✓ Depósito de R$ " + String.format("%.2f", valor));
                valorField.setText("");
            } else {
                String errorMsg = MessageBuilder.extractInfo(response);
                ToastNotification.showError("Erro", errorMsg);
                addLogMessage("✗ Erro no depósito: " + errorMsg);
            }
        } catch (Exception ex) {
            logger.error("Erro ao depositar", ex);
            ToastNotification.showError("Erro", "Erro ao realizar depósito: " + ex.getMessage());
        }
    }
    
    private boolean validateCpf(String cpf) {
        return cpf != null && CPF_PATTERN.matcher(cpf).matches();
    }
    
    private void updateMainPanelUI() {
        JLabel userInfoLabel = findComponentByName(this, "userInfoLabel", JLabel.class);
        if (userInfoLabel != null && currentUserCpf != null) {
            userInfoLabel.setText("Usuário: " + currentUserCpf);
            userInfoLabel.setForeground(UIColors.SUCCESS);
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T> T findComponentByName(Container container, String name, Class<T> type) {
        for (Component comp : container.getComponents()) {
            if (name.equals(comp.getName()) && type.isInstance(comp)) {
                return (T) comp;
            }
            if (comp instanceof Container) {
                T found = findComponentByName((Container) comp, name, type);
                if (found != null) return found;
            }
        }
        return null;
    }
    
    public void addLogMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
