package com.newpix.client.gui;

import com.newpix.client.NewPixClient;
import com.newpix.util.ErrorHandler;
import com.newpix.util.ConnectionConfig;
import com.newpix.util.CpfUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Janela de Login - Apenas CPF e Senha
 */
public class LoginWindow extends JFrame {
    
    private NewPixClient client;
    private JTextField cpfField;
    private JPasswordField senhaField;
    private JButton loginButton;
    private JButton cadastroButton;
    private JButton conectarButton;
    private JTextField hostField;
    private JTextField portField;
    private JLabel statusLabel;
    private volatile boolean isConnecting = false;
    
    public LoginWindow() {
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
        conectarButton = new JButton("Conectar");
        
        // Campos de login
        cpfField = new JTextField(20);
        senhaField = new JPasswordField(20);
        
        loginButton = new JButton("Fazer Login");
        cadastroButton = new JButton("Criar Conta");
        
        statusLabel = new JLabel("Desconectado - Configure a conexão", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        
        // Desabilitar campos até conectar
        setFieldsEnabled(false);
        
        // Configurar tooltips
        setupTooltips();
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
        setLayout(new BorderLayout(10, 10));
        
        // Panel de conexão
        JPanel conexaoPanel = new JPanel(new GridBagLayout());
        conexaoPanel.setBorder(BorderFactory.createTitledBorder("Conexão com Servidor"));
        
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
        gbc.gridwidth = 2;
        conexaoPanel.add(conectarButton, gbc);
        
        gbc.gridy = 3;
        conexaoPanel.add(statusLabel, gbc);
        
        // Panel principal - Login
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder("Fazer Login"));
        
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
        
        // Layout final
        add(conexaoPanel, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        conectarButton.addActionListener(e -> conectar());
        loginButton.addActionListener(e -> realizarLogin());
        cadastroButton.addActionListener(e -> abrirJanelaCadastro());
        
        // Enter nos campos
        ActionListener loginAction = e -> realizarLogin();
        cpfField.addActionListener(loginAction);
        senhaField.addActionListener(loginAction);
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
                        statusLabel.setText("Conectado com sucesso!");
                        statusLabel.setForeground(Color.GREEN);
                        setFieldsEnabled(true);
                        cpfField.requestFocus();
                    } else {
                        statusLabel.setText("Falha na conexão - Verifique host/porta");
                        statusLabel.setForeground(Color.RED);
                        setFieldsEnabled(false);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Erro na conexão: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
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
        cadastroButton.setEnabled(enabled);
    }
    
    private void realizarLogin() {
        if (!validarDadosLogin()) {
            return;
        }
        
        String cpf = cpfField.getText().trim();
        String senha = new String(senhaField.getPassword());
        
        // Normalizar CPF (remover formatação)
        String cpfNormalizado = CpfUtil.normalizarCpf(cpf);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return client.loginSimple(cpfNormalizado, senha);
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(
                            LoginWindow.this,
                            "Login realizado com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        // Abrir janela principal e fechar login
                        SwingUtilities.invokeLater(() -> {
                            new MainGUI(client, "").setVisible(true);
                            dispose();
                        });
                    } else {
                        JOptionPane.showMessageDialog(LoginWindow.this, "CPF ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
                        senhaField.setText("");
                        senhaField.requestFocus();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Erro no login: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private boolean validarDadosLogin() {
        String cpf = cpfField.getText().trim();
        String senha = new String(senhaField.getPassword());
        
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe o CPF.", "Erro", JOptionPane.ERROR_MESSAGE);
            cpfField.requestFocus();
            return false;
        }
        
        if (!CpfUtil.validarFormatoCpf(cpf)) {
            JOptionPane.showMessageDialog(this, "CPF inválido. Use o formato 000.000.000-00 ou apenas números.", "Erro", JOptionPane.ERROR_MESSAGE);
            cpfField.requestFocus();
            return false;
        }
        
        if (senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe a senha.", "Erro", JOptionPane.ERROR_MESSAGE);
            senhaField.requestFocus();
            return false;
        }
        
        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 6 caracteres.", "Erro", JOptionPane.ERROR_MESSAGE);
            senhaField.requestFocus();
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
