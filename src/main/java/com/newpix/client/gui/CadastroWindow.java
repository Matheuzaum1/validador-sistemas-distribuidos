package com.newpix.client.gui;

import com.newpix.client.NewPixClient;
import com.newpix.util.CpfUtil;
import com.newpix.util.CLILogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Janela de Cadastro Moderna - Nome, CPF e Senha
 */
public class CadastroWindow extends JFrame {
    
    private NewPixClient client;
    private JTextField nomeField;
    private JTextField cpfField;
    private JPasswordField senhaField;
    private JPasswordField confirmarSenhaField;
    private JButton cadastrarButton;
    private JButton voltarButton;
    private LoginWindow loginParent; // Referência à janela de login pai
    
    public CadastroWindow(NewPixClient client) {
        this(client, null);
    }
    
    public CadastroWindow(NewPixClient client, LoginWindow loginParent) {
        this.client = client;
        this.loginParent = loginParent;
        initializeComponents();
        setupEventHandlers();
        setupWindowCloseHandler();
    }
    
    public CadastroWindow() {
        this(null, null);
    }
    
    private void initializeComponents() {
        setTitle("NewPix - Criar Conta");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(480, 650);  // Tamanho maior para acomodar design moderno
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Configurar fundo com gradiente
        getContentPane().setBackground(new Color(248, 249, 250));
        
        // Criar container principal com fundo gradiente
        JPanel mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fundo com gradiente
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 123, 255),
                    0, getHeight(), new Color(0, 86, 179)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        
        // Título principal
        JLabel titleLabel = new JLabel("Criar Nova Conta", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Campos de cadastro modernos
        nomeField = createModernTextField("Nome completo com pelo menos 6 caracteres");
        cpfField = createModernTextField("CPF no formato 000.000.000-00");
        senhaField = createModernPasswordField("Senha com pelo menos 6 caracteres");
        confirmarSenhaField = createModernPasswordField("Digite a senha novamente");
        
        // Botões modernos
        cadastrarButton = createModernButton("Criar Conta", new Color(40, 167, 69), new Color(33, 136, 56));
        voltarButton = createModernButton("Voltar ao Login", new Color(108, 117, 125), new Color(90, 98, 104));
        
        setupLayout(mainContainer, titleLabel);
    }
    
    /**
     * Cria um campo de texto moderno com aparência consistente.
     */
    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setToolTipText(placeholder);
        field.setPreferredSize(new Dimension(280, 40));
        field.setMaximumSize(new Dimension(280, 40));
        return field;
    }
    
    /**
     * Cria um campo de senha moderno.
     */
    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setToolTipText(placeholder);
        field.setPreferredSize(new Dimension(280, 40));
        field.setMaximumSize(new Dimension(280, 40));
        return field;
    }
    
    /**
     * Cria um botão moderno com aparência de cartão.
     */
    private JButton createModernButton(String text, Color primaryColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = getModel().isRollover() ? hoverColor : primaryColor;
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 35));
        button.setMaximumSize(new Dimension(140, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
                
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Sombra sutil
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(2, 2, getWidth(), getHeight(), 12, 12);
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return card;
    }
    
    /**
     * Configura o layout moderno da janela.
     */
    private void setupLayout(JPanel mainContainer, JLabel titleLabel) {
        // Card para o formulário de cadastro
        JPanel cadastroCard = createModernCard();
        cadastroCard.setLayout(new BoxLayout(cadastroCard, BoxLayout.Y_AXIS));
        
        // Labels e campos
        JLabel nomeLabel = new JLabel("Nome Completo:");
        nomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nomeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel cpfLabel = new JLabel("CPF:");
        cpfLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cpfLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cpfField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        senhaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        senhaField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel confirmarLabel = new JLabel("Confirmar Senha:");
        confirmarLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmarLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmarSenhaField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Adicionar componentes ao card
        cadastroCard.add(nomeLabel);
        cadastroCard.add(Box.createVerticalStrut(5));
        cadastroCard.add(nomeField);
        cadastroCard.add(Box.createVerticalStrut(15));
        
        cadastroCard.add(cpfLabel);
        cadastroCard.add(Box.createVerticalStrut(5));
        cadastroCard.add(cpfField);
        cadastroCard.add(Box.createVerticalStrut(15));
        
        cadastroCard.add(senhaLabel);
        cadastroCard.add(Box.createVerticalStrut(5));
        cadastroCard.add(senhaField);
        cadastroCard.add(Box.createVerticalStrut(15));
        
        cadastroCard.add(confirmarLabel);
        cadastroCard.add(Box.createVerticalStrut(5));
        cadastroCard.add(confirmarSenhaField);
        cadastroCard.add(Box.createVerticalStrut(20));
        
        // Panel para botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(voltarButton);
        buttonPanel.add(cadastrarButton);
        cadastroCard.add(buttonPanel);
        
        // Adicionar todos os componentes ao container principal
        mainContainer.add(titleLabel);
        mainContainer.add(Box.createVerticalStrut(25));
        mainContainer.add(cadastroCard);
        mainContainer.add(Box.createVerticalGlue());
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private void setupWindowCloseHandler() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                voltarParaLogin();
            }
        });
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
                    CLILogger.error("Erro durante o cadastro: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    
                    String errorMessage = "Erro durante o cadastro.\n\n";
                    
                    if (e.getMessage() != null) {
                        String msg = e.getMessage().toLowerCase();
                        if (msg.contains("connection") || msg.contains("conexão")) {
                            errorMessage += "Conexão com o servidor foi perdida.\n" +
                                          "Verifique sua conexão de internet e tente novamente.";
                        } else if (msg.contains("timeout")) {
                            errorMessage += "Timeout durante o cadastro.\n" +
                                          "O servidor pode estar sobrecarregado.\nTente novamente em alguns momentos.";
                        } else if (msg.contains("duplicate") || msg.contains("já existe") || msg.contains("duplicado")) {
                            errorMessage += "CPF já está cadastrado no sistema.\n" +
                                          "Tente fazer login ou use um CPF diferente.";
                        } else if (msg.contains("invalid") || msg.contains("inválido")) {
                            errorMessage += "Dados de cadastro inválidos.\n" +
                                          "Verifique se todas as informações estão corretas.";
                        } else {
                            errorMessage += "Detalhes: " + e.getMessage();
                        }
                    } else {
                        errorMessage += "Tipo de erro: " + e.getClass().getSimpleName();
                    }
                    
                    JOptionPane.showMessageDialog(CadastroWindow.this, 
                        errorMessage, 
                        "Erro no Cadastro", 
                        JOptionPane.ERROR_MESSAGE);
                        
                    // Focar no campo apropriado baseado no erro
                    if (errorMessage.toLowerCase().contains("cpf")) {
                        cpfField.requestFocus();
                        cpfField.selectAll();
                    } else {
                        nomeField.requestFocus();
                    }
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
            if (loginParent != null) {
                // Se temos referência à janela pai, apenas mostrá-la novamente
                loginParent.setVisible(true);
            } else {
                // Senão, criar uma nova janela de login
                new LoginWindow().setVisible(true);
            }
            dispose();
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CadastroWindow().setVisible(true);
        });
    }
}
