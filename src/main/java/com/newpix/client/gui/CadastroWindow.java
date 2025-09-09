package com.newpix.client.gui;

import com.newpix.client.NewPixClient;
import com.newpix.util.CpfUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Janela de Cadastro - Nome, CPF e Senha
 */
public class CadastroWindow extends JFrame {
    
    private NewPixClient client;
    private JTextField nomeField;
    private JTextField cpfField;
    private JPasswordField senhaField;
    private JPasswordField confirmarSenhaField;
    private JButton cadastrarButton;
    private JButton voltarButton;
    
    public CadastroWindow(NewPixClient client) {
        this.client = client;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindowCloseHandler();
    }
    
    public CadastroWindow() {
        this.client = null;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindowCloseHandler();
    }
    
    private void initializeComponents() {
        setTitle("NewPix - Criar Conta");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Campos de cadastro
        nomeField = new JTextField(25);
        cpfField = new JTextField(25);
        senhaField = new JPasswordField(25);
        confirmarSenhaField = new JPasswordField(25);
        
        cadastrarButton = new JButton("Criar Conta");
        voltarButton = new JButton("Voltar ao Login");
        
        // Configurar tooltips
        setupTooltips();
    }
    
    private void setupTooltips() {
        nomeField.setToolTipText("Nome completo com pelo menos 6 caracteres");
        cpfField.setToolTipText("CPF no formato 000.000.000-00 ou apenas números");
        senhaField.setToolTipText("Senha com pelo menos 6 caracteres");
        confirmarSenhaField.setToolTipText("Digite a senha novamente para confirmar");
    }
    
    private void setupWindowCloseHandler() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                voltarParaLogin();
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel principal - Cadastro
        JPanel cadastroPanel = new JPanel(new GridBagLayout());
        cadastroPanel.setBorder(BorderFactory.createTitledBorder("Criar Nova Conta"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        cadastroPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1;
        cadastroPanel.add(nomeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        cadastroPanel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        cadastroPanel.add(cpfField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        cadastroPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        cadastroPanel.add(senhaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        cadastroPanel.add(new JLabel("Confirmar Senha:"), gbc);
        gbc.gridx = 1;
        cadastroPanel.add(confirmarSenhaField, gbc);
        
        // Panel de botões
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        voltarButton.setPreferredSize(new Dimension(140, 30));
        buttonPanel.add(voltarButton, gbc);
        
        gbc.gridx = 1;
        cadastrarButton.setPreferredSize(new Dimension(140, 30));
        buttonPanel.add(cadastrarButton, gbc);
        
        // Layout final
        add(cadastroPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Adicionar informações sobre o sistema
        JLabel infoLabel = new JLabel("<html><center><b>Sistema NewPix</b><br/>Crie sua conta para acessar o sistema bancário</center></html>", SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);
    }
    
    private void setupEventHandlers() {
        cadastrarButton.addActionListener(e -> realizarCadastro());
        voltarButton.addActionListener(e -> voltarParaLogin());
        
        // Enter nos campos
        ActionListener cadastroAction = e -> realizarCadastro();
        nomeField.addActionListener(cadastroAction);
        cpfField.addActionListener(cadastroAction);
        senhaField.addActionListener(cadastroAction);
        confirmarSenhaField.addActionListener(cadastroAction);
    }
    
    private void realizarCadastro() {
        if (!validarDadosCadastro()) {
            return;
        }
        
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Conecte-se ao servidor primeiro através da janela de login.", "Erro", JOptionPane.ERROR_MESSAGE);
            voltarParaLogin();
            return;
        }
        
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String senha = new String(senhaField.getPassword());
        
        // Normalizar CPF (remover formatação)
        String cpfNormalizado = CpfUtil.normalizarCpf(cpf);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return client.cadastroSimple(cpfNormalizado, nome, senha);
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(
                            CadastroWindow.this,
                            "Conta criada com sucesso!\nAgora você pode fazer login.",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        // Voltar para login após sucesso
                        voltarParaLogin();
                    } else {
                        JOptionPane.showMessageDialog(CadastroWindow.this, "Erro ao criar conta. CPF pode já estar cadastrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                        cpfField.requestFocus();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CadastroWindow.this, "Erro no cadastro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private boolean validarDadosCadastro() {
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String senha = new String(senhaField.getPassword());
        String confirmarSenha = new String(confirmarSenhaField.getPassword());
        
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe o nome completo.", "Erro", JOptionPane.ERROR_MESSAGE);
            nomeField.requestFocus();
            return false;
        }
        
        if (nome.length() < 6) {
            JOptionPane.showMessageDialog(this, "O nome deve ter pelo menos 6 caracteres.", "Erro", JOptionPane.ERROR_MESSAGE);
            nomeField.requestFocus();
            return false;
        }
        
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
        
        if (!senha.equals(confirmarSenha)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem. Digite novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            confirmarSenhaField.setText("");
            senhaField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void voltarParaLogin() {
        // Voltar para a janela de login
        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
            dispose();
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CadastroWindow().setVisible(true);
        });
    }
}
