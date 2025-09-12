package com.newpix.client.gui;

import com.newpix.client.NewPixClient;
import com.newpix.util.ConnectionConfig;
import com.newpix.util.CLILogger;
import com.newpix.util.UserPreferences;
import com.newpix.util.ServerScanner;
import com.newpix.client.gui.components.CpfField;
import com.newpix.client.gui.components.PasswordFieldWithToggle;
import com.newpix.client.gui.theme.NewPixTheme;
import com.newpix.client.gui.animations.AnimationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Janela de Login - Apenas CPF e Senha
 */
public class LoginWindow extends JFrame {
    
    private NewPixClient client;
    private CpfField cpfField;
    private PasswordFieldWithToggle senhaField;
    private JCheckBox rememberMeCheckBox;
    private JButton loginButton;
    private JButton cadastroButton;
    private JButton conectarButton;
    private JButton scanButton;
    private JTextField hostField;
    private JTextField portField;
    private JLabel statusLabel;
    private JLabel ipLocalLabel;
    private volatile boolean isConnecting = false;
    
    public LoginWindow() {
        // Mostrar banner do sistema no console
        CLILogger.showBanner();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindowCloseHandler();
    }
    
    private void initializeComponents() {
        setTitle("NewPix - Login");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Campos de conexão
        hostField = new JTextField(ConnectionConfig.DEFAULT_HOST, 15);
        portField = new JTextField(String.valueOf(ConnectionConfig.DEFAULT_PORT), 8);
        conectarButton = NewPixTheme.createStyledButton("▶ Conectar", NewPixTheme.ButtonStyle.PRIMARY);
        scanButton = NewPixTheme.createStyledButton("🔍 Escanear", NewPixTheme.ButtonStyle.SECONDARY);
        
        // Campos de login
        cpfField = new CpfField();
        senhaField = new PasswordFieldWithToggle(20);
        rememberMeCheckBox = new JCheckBox("Lembrar-me", UserPreferences.getRememberMe());
        
        loginButton = NewPixTheme.createStyledButton("🔐 Fazer Login", NewPixTheme.ButtonStyle.SUCCESS);
        cadastroButton = NewPixTheme.createStyledButton("+ Criar Conta", NewPixTheme.ButtonStyle.WARNING);
        
        statusLabel = NewPixTheme.createStatusLabel("Desconectado - Configure a conexão", NewPixTheme.StatusType.ERROR);
        
        // Label para IP local
        ipLocalLabel = new JLabel("IP Local: " + getLocalIP(), SwingConstants.CENTER);
        ipLocalLabel.setForeground(NewPixTheme.TEXT_SECONDARY);
        ipLocalLabel.setFont(NewPixTheme.FONT_SMALL);
        
        // Desabilitar campos até conectar
        setFieldsEnabled(false);
        
        // Configurar tooltips
        setupTooltips();
        
        // Carregar configurações salvas
        loadSavedPreferences();
    }
    
    private String getLocalIP() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
    
    private void loadSavedPreferences() {
        // Carregar host e porta salvos
        hostField.setText(UserPreferences.getServerHost());
        portField.setText(String.valueOf(UserPreferences.getServerPort()));
        
        // Carregar último CPF se remember me estiver ativo
        if (UserPreferences.getRememberMe()) {
            String lastCpf = UserPreferences.getLastCpf();
            if (!lastCpf.isEmpty()) {
                cpfField.setCpf(lastCpf);
            }
        }
    }
    
    private void setupTooltips() {
        hostField.setToolTipText("Endereço do servidor (ex: localhost, 192.168.1.100)");
        portField.setToolTipText("Porta do servidor (1-65535)");
        cpfField.setToolTipText("CPF no formato 000.000.000-00 ou apenas números");
        senhaField.setToolTipText("Digite sua senha");
    }
    
    private void setupWindowCloseHandler() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente sair do sistema?",
            "Confirmar Saída",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            if (client != null) {
                client.disconnect();
            }
            System.exit(0);
        }
    }
    
    private void setupLayout() {
        // Aplicar tema
        NewPixTheme.applyTheme();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(NewPixTheme.BACKGROUND_LIGHT);
        
        // Panel de conexão
        JPanel conexaoPanel = NewPixTheme.createCard();
        conexaoPanel.setLayout(new GridBagLayout());
        conexaoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "[REDE] Conexão com Servidor", 
                0, 0, NewPixTheme.FONT_SUBTITLE, NewPixTheme.TEXT_PRIMARY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        conexaoPanel.add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        conexaoPanel.add(hostField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        conexaoPanel.add(new JLabel("Porta:"), gbc);
        gbc.gridx = 1;
        conexaoPanel.add(portField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        conexaoPanel.add(conectarButton, gbc);
        
        gbc.gridx = 1;
        scanButton.setToolTipText("Escanear servidores na rede local");
        conexaoPanel.add(scanButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        conexaoPanel.add(statusLabel, gbc);
        
        // Panel principal - Login
        JPanel loginPanel = NewPixTheme.createCard();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "[LOGIN] Fazer Login", 
                0, 0, NewPixTheme.FONT_SUBTITLE, NewPixTheme.TEXT_PRIMARY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(cpfField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(senhaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(rememberMeCheckBox, gbc);
        
        // Panel de botões
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        loginButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(loginButton, gbc);
        
        gbc.gridx = 1;
        cadastroButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(cadastroButton, gbc);
        
        // Panel de status (incluindo IP local)
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(ipLocalLabel, BorderLayout.SOUTH);
        
        // Layout final
        add(conexaoPanel, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    private void setupEventHandlers() {
        conectarButton.addActionListener(e -> conectar());
        loginButton.addActionListener(e -> realizarLogin());
        cadastroButton.addActionListener(e -> abrirJanelaCadastro());
        scanButton.addActionListener(e -> escanearServidores());
        
        // Remember me checkbox
        rememberMeCheckBox.addActionListener(e -> {
            UserPreferences.setRememberMe(rememberMeCheckBox.isSelected());
            if (!rememberMeCheckBox.isSelected()) {
                // Se desmarcou, limpar CPF salvo
                UserPreferences.saveLastCpf("");
            }
        });
        
        // Enter nos campos
        ActionListener loginAction = e -> realizarLogin();
        cpfField.addActionListener(loginAction);
        senhaField.addActionListener(loginAction);
        
        // Salvar posição da janela ao fechar
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentMoved(java.awt.event.ComponentEvent e) {
                saveWindowPosition();
            }
            
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                saveWindowPosition();
            }
        });
    }
    
    private void saveWindowPosition() {
        UserPreferences.saveWindowBounds(getX(), getY(), getWidth(), getHeight());
    }
    
    private void escanearServidores() {
        JDialog scanDialog = ServerScanner.createScanDialog(this, new ServerScanner.ScanCallback() {
            @Override
            public void onServerFound(ServerScanner.ServerInfo serverInfo) {
                CLILogger.info("Servidor encontrado: " + serverInfo);
            }
            
            @Override
            public void onScanProgress(int current, int total) {
                // Progress já é mostrado no diálogo
            }
            
            @Override
            public void onScanComplete(List<ServerScanner.ServerInfo> servers) {
                if (!servers.isEmpty()) {
                    // Mostrar diálogo para selecionar servidor
                    mostrarDialogoSelecaoServidor(servers);
                }
            }
        });
        
        scanDialog.setVisible(true);
    }
    
    private void mostrarDialogoSelecaoServidor(List<ServerScanner.ServerInfo> servers) {
        String[] options = servers.stream()
            .map(s -> s.host + ":" + s.port + " (" + s.name + ")")
            .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Selecione um servidor para conectar:",
            "Servidores Encontrados",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (selected != null) {
            // Extrair host e porta
            String[] parts = selected.split(":");
            String host = parts[0];
            String portStr = parts[1].split(" ")[0];
            
            // Atualizar campos
            hostField.setText(host);
            portField.setText(portStr);
            
            // Conectar automaticamente
            conectar();
        }
    }
    
    private void conectar() {
        if (isConnecting) {
            return;
        }
        
        String host = hostField.getText().trim();
        String portText = portField.getText().trim();
        
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe o host do servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
            hostField.requestFocus();
            return;
        }
        
        int port;
        try {
            port = Integer.parseInt(portText);
            if (port < 1 || port > 65535) {
                throw new NumberFormatException("Porta fora do range válido");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Porta inválida. Deve ser um número entre 1 e 65535.", "Erro", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            return;
        }
        
        isConnecting = true;
        conectarButton.setEnabled(false);
        statusLabel.setText("Conectando...");
        statusLabel.setForeground(Color.ORANGE);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    client = new NewPixClient(host, port);
                    return client.connect();
                } catch (Exception e) {
                    return false;
                }
            }
            
            @Override
            protected void done() {
                isConnecting = false;
                conectarButton.setEnabled(true);
                
                try {
                    if (get()) {
                        statusLabel = NewPixTheme.createStatusLabel("✅ Conectado com sucesso!", NewPixTheme.StatusType.SUCCESS);
                        
                        // Animar o botão de conexão
                        AnimationUtils.animateBackgroundColor(conectarButton, 
                                                            conectarButton.getBackground(), 
                                                            NewPixTheme.SUCCESS_COLOR, 
                                                            500);
                        
                        // Mostrar toast de sucesso
                        AnimationUtils.showToast(LoginWindow.this, 
                                               "🌐 Conectado ao servidor!", 
                                               2000, 
                                               NewPixTheme.SUCCESS_COLOR);
                        
                        setFieldsEnabled(true);
                        cpfField.requestFocus();
                    } else {
                        statusLabel = NewPixTheme.createStatusLabel("❌ Falha na conexão - Verifique host/porta", NewPixTheme.StatusType.ERROR);
                        
                        // Animar erro no botão de conexão
                        AnimationUtils.animateBackgroundColor(conectarButton, 
                                                            conectarButton.getBackground(), 
                                                            NewPixTheme.ERROR_COLOR, 
                                                            500);
                        
                        setFieldsEnabled(false);
                    }
                } catch (Exception e) {
                    statusLabel = NewPixTheme.createStatusLabel("❌ Erro na conexão: " + e.getMessage(), NewPixTheme.StatusType.ERROR);
                    
                    // Animar erro no botão de conexão
                    AnimationUtils.animateBackgroundColor(conectarButton, 
                                                        conectarButton.getBackground(), 
                                                        NewPixTheme.ERROR_COLOR, 
                                                        500);
                    
                    setFieldsEnabled(false);
                }
            }
        };
        
        worker.execute();
    }
    
    private void setFieldsEnabled(boolean enabled) {
        cpfField.setEnabled(enabled);
        senhaField.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        // Botão de cadastro sempre habilitado - não precisa estar conectado
        cadastroButton.setEnabled(true);
    }
    
    private void realizarLogin() {
        if (!validarDadosLogin()) {
            return;
        }
        
        String cpf = cpfField.getCleanCpf();
        String senha = senhaField.getPasswordText();
        
        // Normalizar CPF (remover formatação)
        String cpfNormalizado = cpf;
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return client.loginComToken(cpfNormalizado, senha);
            }
            
            @Override
            protected void done() {
                try {
                    String token = get();
                    if (token != null && !token.isEmpty()) {
                        // Salvar dados se remember me estiver marcado
                        if (rememberMeCheckBox.isSelected()) {
                            UserPreferences.saveLastCpf(cpfNormalizado);
                        }
                        
                        // Salvar configuração do servidor
                        UserPreferences.saveServerConfig(hostField.getText().trim(), 
                                                        Integer.parseInt(portField.getText().trim()));
                        
                        // Mostrar notificação de sucesso
                        AnimationUtils.showToast(LoginWindow.this, 
                                               "✅ Login realizado com sucesso!", 
                                               2000, 
                                               NewPixTheme.SUCCESS_COLOR);
                        
                        // Animar o botão de login
                        AnimationUtils.animateBackgroundColor(loginButton, 
                                                            loginButton.getBackground(), 
                                                            NewPixTheme.SUCCESS_COLOR, 
                                                            500);
                        
                        // Abrir janela principal com token correto e fechar login
                        Timer delayTimer = new Timer(1500, e -> {
                            SwingUtilities.invokeLater(() -> {
                                new MainGUI(client, token).setVisible(true);
                                dispose();
                            });
                            ((Timer) e.getSource()).stop();
                        });
                        delayTimer.setRepeats(false);
                        delayTimer.start();
                    } else {
                        // Animar erro no botão de login
                        AnimationUtils.animateBackgroundColor(loginButton, 
                                                            loginButton.getBackground(), 
                                                            NewPixTheme.ERROR_COLOR, 
                                                            500);
                        
                        // Mostrar notificação de erro
                        AnimationUtils.showToast(LoginWindow.this, 
                                               "❌ CPF ou senha incorretos!", 
                                               3000, 
                                               NewPixTheme.ERROR_COLOR);
                        
                        senhaField.clear();
                        senhaField.requestFocusInWindow();
                    }
                } catch (Exception e) {
                    // Animar erro no botão de login
                    AnimationUtils.animateBackgroundColor(loginButton, 
                                                        loginButton.getBackground(), 
                                                        NewPixTheme.ERROR_COLOR, 
                                                        500);
                    
                    // Mostrar notificação de erro
                    AnimationUtils.showToast(LoginWindow.this, 
                                           "❌ Erro no login: " + e.getMessage(), 
                                           3000, 
                                           NewPixTheme.ERROR_COLOR);
                }
            }
        };
        
        worker.execute();
    }
    
    private boolean validarDadosLogin() {
        String cpf = cpfField.getCleanCpf();
        String senha = senhaField.getPasswordText();
        
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe o CPF.", "Erro", JOptionPane.ERROR_MESSAGE);
            cpfField.requestFocusInWindow();
            return false;
        }
        
        if (!cpfField.isValidCpf()) {
            JOptionPane.showMessageDialog(this, "CPF inválido. Verifique o formato.", "Erro", JOptionPane.ERROR_MESSAGE);
            cpfField.requestFocusInWindow();
            return false;
        }
        
        if (senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe a senha.", "Erro", JOptionPane.ERROR_MESSAGE);
            senhaField.requestFocusInWindow();
            return false;
        }
        
        if (!senhaField.isValidPassword()) {
            JOptionPane.showMessageDialog(this, "A senha deve ter entre 6 e 120 caracteres.", "Erro", JOptionPane.ERROR_MESSAGE);
            senhaField.requestFocusInWindow();
            return false;
        }
        
        return true;
    }
    
    private void abrirJanelaCadastro() {
        // Fechar janela de login e abrir cadastro
        SwingUtilities.invokeLater(() -> {
            new CadastroWindow(client).setVisible(true);
            dispose();
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
    }
}
