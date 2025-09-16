package com.newpix.server.gui;

import com.newpix.dao.DatabaseManager;
import com.newpix.dao.UsuarioDAO;
import com.newpix.dao.TransacaoDAO;
import com.newpix.model.Usuario;
import com.newpix.model.Transacao;
import com.newpix.server.NewPixServer;
import com.newpix.server.ClientHandler;
import com.newpix.client.gui.theme.NewPixTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.List;

/**
 * Interface gráfica do servidor NewPix.
 */
public class ServerGUI extends JFrame {
    
    private NewPixServer server;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea logArea;
    private JTextField portField;
    private JLabel statusLabel;
    private JLabel serverIpLabel;
    private boolean serverRunning = false;
    
    // Banco de dados
    private DatabaseManager dbManager;
    private UsuarioDAO usuarioDAO;
    private TransacaoDAO transacaoDAO;
    
    // Componentes da aba de dispositivos
    private JTabbedPane tabbedPane;
    private DefaultTableModel devicesTableModel;
    private JTable devicesTable;
    private javax.swing.Timer devicesRefreshTimer;
    
    // Componentes da aba de banco de dados
    private DefaultTableModel databaseTableModel;
    private JTable databaseTable;
    private JTextField searchField;
    private JComboBox<String> tableComboBox;
    private JButton refreshDbButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    
    public ServerGUI() {
        // Inicializar DAOs
        this.dbManager = DatabaseManager.getInstance();
        this.usuarioDAO = new UsuarioDAO();
        this.transacaoDAO = new TransacaoDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        initializeDatabase();
    }
    
    private void initializeComponents() {
        // Aplicar tema NewPix
        NewPixTheme.applyTheme();
        
        setTitle("[SERVER] NewPix Server - Sistema Bancário Distribuído");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setBackground(NewPixTheme.BACKGROUND_LIGHT);
        
        // Componentes com tema
        startButton = NewPixTheme.createStyledButton("INICIAR SERVIDOR", NewPixTheme.ButtonStyle.SECONDARY);
        startButton.setBackground(NewPixTheme.BACKGROUND_CARD);
        startButton.setForeground(Color.BLACK);
        startButton.setOpaque(true);
        startButton.setBorderPainted(true);
        startButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        stopButton = NewPixTheme.createStyledButton("PARAR SERVIDOR", NewPixTheme.ButtonStyle.SECONDARY);
        stopButton.setBackground(NewPixTheme.BACKGROUND_CARD);
        stopButton.setForeground(Color.BLACK);
        stopButton.setOpaque(true);
        stopButton.setBorderPainted(true);
        stopButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        stopButton.setEnabled(false);
        
        logArea = new JTextArea(25, 60);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12)); // Fonte monospaced para logs
        logArea.setBackground(NewPixTheme.BACKGROUND_CARD);
        logArea.setForeground(NewPixTheme.TEXT_PRIMARY);
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Configurar codificação UTF-8
        try {
            logArea.getDocument().putProperty("charset", "UTF-8");
        } catch (Exception e) {
            // Fallback silencioso
        }
        
        portField = new JTextField("8080", 10);
        portField.setFont(NewPixTheme.FONT_BODY);
        portField.setBackground(NewPixTheme.BACKGROUND_CARD);
        portField.setForeground(NewPixTheme.TEXT_PRIMARY);
        portField.setBorder(BorderFactory.createLineBorder(NewPixTheme.PRIMARY_COLOR, 1));
        
        statusLabel = NewPixTheme.createStatusLabel("STATUS: Servidor Parado", NewPixTheme.StatusType.ERROR);
        statusLabel.setForeground(NewPixTheme.ERROR_COLOR);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(NewPixTheme.BACKGROUND_CARD);
        
        // Label para mostrar IP do servidor
        serverIpLabel = new JLabel("IP do Servidor: " + getServerIPAddress());
        serverIpLabel.setFont(NewPixTheme.FONT_BODY);
        serverIpLabel.setForeground(NewPixTheme.TEXT_PRIMARY);
        serverIpLabel.setOpaque(true);
        serverIpLabel.setBackground(NewPixTheme.BACKGROUND_CARD);
        
        // Inicializar componentes da tabela de dispositivos
        initializeDevicesTable();
        
        // Redirecionar saída para a área de log
        redirectSystemOutput();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(NewPixTheme.BACKGROUND_LIGHT);
        
        // Panel superior - controles
        JPanel controlPanel = NewPixTheme.createCard();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(NewPixTheme.BACKGROUND_CARD);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "[CONFIG] Controles do Servidor", 
                0, 0, NewPixTheme.FONT_SUBTITLE, NewPixTheme.TEXT_PRIMARY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel portLabel = new JLabel("Porta:");
        portLabel.setFont(NewPixTheme.FONT_BODY);
        portLabel.setForeground(NewPixTheme.TEXT_PRIMARY);
        portLabel.setOpaque(true);
        portLabel.setBackground(NewPixTheme.BACKGROUND_CARD);
        
        controlPanel.add(portLabel);
        controlPanel.add(portField);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(statusLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(serverIpLabel);
        
        // Criar painel com abas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(NewPixTheme.FONT_BODY);
        tabbedPane.setBackground(NewPixTheme.BACKGROUND_LIGHT);
        tabbedPane.setForeground(NewPixTheme.TEXT_PRIMARY);
        
        // Aba 1: Logs do Sistema
        JPanel logPanel = createLogPanel();
        tabbedPane.addTab("[LOGS] Sistema", logPanel);
        
        // Aba 2: Dispositivos Conectados
        JPanel devicesPanel = createDevicesPanel();
        tabbedPane.addTab("[DEVICES] Conectados", devicesPanel);
        
        // Aba 3: Explorador de Banco de Dados
        JPanel databasePanel = createDatabasePanel();
        tabbedPane.addTab("[DATABASE] Banco de Dados", databasePanel);
        
        // Panel inferior - informações
        JPanel infoPanel = NewPixTheme.createCard();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "[INFO] Informações do Sistema", 
                0, 0, NewPixTheme.FONT_SUBTITLE, NewPixTheme.TEXT_PRIMARY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JTextArea infoArea = new JTextArea(
            "NEWPIX SERVER - Sistema Bancário Distribuído\n" +
            "Protocolo: JSON over TCP\n" +
            "Funcionalidades: Login/Logout, CRUD Usuários, Transações PIX\n" +
            "Banco de Dados: SQLite (newpix.db)\n" +
            "Segurança: Criptografia BCrypt, Validação de Token"
        );
        infoArea.setEditable(false);
        infoArea.setFont(NewPixTheme.FONT_BODY);
        infoArea.setBackground(NewPixTheme.BACKGROUND_CARD);
        infoArea.setForeground(NewPixTheme.TEXT_SECONDARY);
        infoArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        infoPanel.add(infoArea, BorderLayout.CENTER);
        
        // Adicionar painéis à janela principal
        add(controlPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createLogPanel() {
        JPanel logPanel = NewPixTheme.createCard();
        logPanel.setLayout(new BorderLayout());
        logPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "[LOGS] Logs do Sistema", 
                0, 0, NewPixTheme.FONT_SUBTITLE, NewPixTheme.TEXT_PRIMARY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        return logPanel;
    }
    
    private JPanel createDevicesPanel() {
        JPanel devicesPanel = NewPixTheme.createCard();
        devicesPanel.setLayout(new BorderLayout());
        devicesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "[CONEXÕES] Dispositivos Conectados", 
                0, 0, NewPixTheme.FONT_SUBTITLE, NewPixTheme.TEXT_PRIMARY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Painel superior com informações
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(NewPixTheme.BACKGROUND_CARD);
        
        JLabel connectedLabel = new JLabel("Dispositivos atualmente conectados ao servidor:");
        connectedLabel.setFont(NewPixTheme.FONT_BODY);
        connectedLabel.setForeground(NewPixTheme.TEXT_PRIMARY);
        headerPanel.add(connectedLabel);
        
        // Tabela com scroll
        JScrollPane tableScrollPane = new JScrollPane(devicesTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.setBackground(NewPixTheme.BACKGROUND_CARD);
        
        devicesPanel.add(headerPanel, BorderLayout.NORTH);
        devicesPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        return devicesPanel;
    }
    
    private JPanel createDatabasePanel() {
        JPanel databasePanel = NewPixTheme.createCard();
        databasePanel.setLayout(new BorderLayout());
        databasePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "[DATABASE] Explorador do Banco de Dados", 
                0, 0, NewPixTheme.FONT_SUBTITLE, NewPixTheme.TEXT_PRIMARY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Painel superior com controles
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBackground(NewPixTheme.BACKGROUND_CARD);
        
        // ComboBox para selecionar tabela
        JLabel tableLabel = new JLabel("Tabela:");
        tableLabel.setFont(NewPixTheme.FONT_BODY);
        tableLabel.setForeground(Color.BLACK);
        
        tableComboBox = new JComboBox<>(new String[]{"usuarios", "transacoes", "sessoes"});
        tableComboBox.setFont(NewPixTheme.FONT_BODY);
        tableComboBox.setBackground(NewPixTheme.BACKGROUND_CARD);
        tableComboBox.setForeground(Color.BLACK);
        tableComboBox.addActionListener(e -> loadTableData());
        
        // Campo de busca
        JLabel searchLabel = new JLabel("Buscar:");
        searchLabel.setFont(NewPixTheme.FONT_BODY);
        searchLabel.setForeground(Color.BLACK);
        
        searchField = new JTextField(15);
        searchField.setFont(NewPixTheme.FONT_BODY);
        searchField.setBackground(NewPixTheme.BACKGROUND_CARD);
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        // Botões de ação
        refreshDbButton = createDbButton("ATUALIZAR", Color.BLUE);
        addButton = createDbButton("ADICIONAR", Color.GREEN);
        editButton = createDbButton("EDITAR", Color.ORANGE);
        deleteButton = createDbButton("DELETAR", Color.RED);
        
        // Adicionar event listeners
        refreshDbButton.addActionListener(e -> loadTableData());
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteSelectedRow());
        
        controlsPanel.add(tableLabel);
        controlsPanel.add(tableComboBox);
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(searchLabel);
        controlsPanel.add(searchField);
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(refreshDbButton);
        controlsPanel.add(addButton);
        controlsPanel.add(editButton);
        controlsPanel.add(deleteButton);
        
        // Tabela do banco de dados
        initializeDatabaseTable();
        JScrollPane dbScrollPane = new JScrollPane(databaseTable);
        dbScrollPane.setBorder(BorderFactory.createEmptyBorder());
        dbScrollPane.setBackground(NewPixTheme.BACKGROUND_CARD);
        
        databasePanel.add(controlsPanel, BorderLayout.NORTH);
        databasePanel.add(dbScrollPane, BorderLayout.CENTER);
        
        return databasePanel;
    }
    
    private JButton createDbButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(NewPixTheme.FONT_BODY);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(color.darker(), 1));
        button.setFocusPainted(false);
        return button;
    }
    
    private void initializeDatabaseTable() {
        // Colunas iniciais (serão atualizadas dinamicamente)
        String[] columnNames = {"ID", "Dados", "Timestamp"};
        
        // Modelo da tabela não editável
        databaseTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Criar tabela
        databaseTable = new JTable(databaseTableModel);
        databaseTable.setFont(NewPixTheme.FONT_BODY);
        databaseTable.setBackground(NewPixTheme.BACKGROUND_CARD);
        databaseTable.setForeground(Color.BLACK);
        databaseTable.setGridColor(Color.LIGHT_GRAY);
        databaseTable.setSelectionBackground(NewPixTheme.PRIMARY_COLOR);
        databaseTable.setSelectionForeground(Color.WHITE);
        databaseTable.setRowHeight(25);
        
        // Configurar cabeçalho
        databaseTable.getTableHeader().setFont(NewPixTheme.FONT_SUBTITLE);
        databaseTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        databaseTable.getTableHeader().setForeground(Color.BLACK);
        databaseTable.getTableHeader().setOpaque(true);
        
        // Carregar dados iniciais
        loadTableData();
    }
    
    private void loadTableData() {
        if (databaseTableModel == null) return;
        
        String selectedTable = (String) tableComboBox.getSelectedItem();
        
        try {
            // Limpar tabela atual
            databaseTableModel.setRowCount(0);
            
            if ("usuarios".equals(selectedTable)) {
                loadUsersData();
            } else if ("transacoes".equals(selectedTable)) {
                loadTransactionsData();
            } else if ("sessoes".equals(selectedTable)) {
                loadSessionsData();
            }
            
        } catch (Exception e) {
            logArea.append("Erro ao carregar dados da tabela " + selectedTable + ": " + e.getMessage() + "\n");
        }
    }
    
    private void loadUsersData() {
        // Atualizar colunas para usuarios
        String[] userColumns = {"CPF", "Nome", "Saldo", "Criado em"};
        databaseTableModel.setColumnIdentifiers(userColumns);
        
        try {
            List<Usuario> users = usuarioDAO.listarTodos();
            for (Usuario user : users) {
                Object[] rowData = {
                    user.getCpf(),
                    user.getNome(),
                    String.format("R$ %.2f", user.getSaldo()),
                    user.getCriadoEm() != null ? user.getCriadoEm().toString() : "N/A"
                };
                databaseTableModel.addRow(rowData);
            }
            logArea.append("Carregados " + users.size() + " usuários do banco de dados.\n");
        } catch (Exception e) {
            logArea.append("Erro ao carregar usuários: " + e.getMessage() + "\n");
        }
    }
    
    private void loadTransactionsData() {
        // Atualizar colunas para transacoes
        String[] transactionColumns = {"ID", "CPF Origem", "CPF Destino", "Valor", "Data"};
        databaseTableModel.setColumnIdentifiers(transactionColumns);
        
        try {
            List<Transacao> transactions = transacaoDAO.listarTodas();
            for (Transacao transaction : transactions) {
                Object[] rowData = {
                    transaction.getId(),
                    transaction.getUsuarioEnviador() != null ? transaction.getUsuarioEnviador().getCpf() : "N/A",
                    transaction.getUsuarioRecebedor() != null ? transaction.getUsuarioRecebedor().getCpf() : "N/A",
                    String.format("R$ %.2f", transaction.getValorEnviado()),
                    transaction.getCriadoEm() != null ? transaction.getCriadoEm().toString() : "N/A"
                };
                databaseTableModel.addRow(rowData);
            }
            logArea.append("Carregadas " + transactions.size() + " transações do banco de dados.\n");
        } catch (Exception e) {
            logArea.append("Erro ao carregar transações: " + e.getMessage() + "\n");
        }
    }
    
    private void loadSessionsData() {
        // Atualizar colunas para sessoes (simulado)
        String[] sessionColumns = {"ID", "CPF", "Token", "IP", "Status", "Criado em"};
        databaseTableModel.setColumnIdentifiers(sessionColumns);
        
        // Dados simulados para sessões
        if (server != null && serverRunning && server.getActiveClientCount() > 0) {
            for (int i = 0; i < server.getActiveClientCount(); i++) {
                Object[] rowData = {
                    i + 1,
                    "XXX.XXX.XXX-XX",
                    "token_" + System.currentTimeMillis(),
                    "192.168.1." + (100 + i),
                    "ATIVA",
                    new java.util.Date().toString()
                };
                databaseTableModel.addRow(rowData);
            }
        }
        logArea.append("Carregadas " + databaseTableModel.getRowCount() + " sessões ativas.\n");
    }
    
    private void showAddDialog() {
        String selectedTable = (String) tableComboBox.getSelectedItem();
        
        if ("usuarios".equals(selectedTable)) {
            showAddUserDialog();
        } else if ("transacoes".equals(selectedTable)) {
            JOptionPane.showMessageDialog(this, "Transações são criadas automaticamente via PIX", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Não é possível adicionar sessões manualmente", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Adicionar Usuário", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(NewPixTheme.BACKGROUND_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // CPF
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel cpfLabel = new JLabel("CPF:");
        cpfLabel.setForeground(Color.BLACK);
        formPanel.add(cpfLabel, gbc);
        
        gbc.gridx = 1;
        JTextField cpfField = new JTextField(15);
        cpfField.setForeground(Color.BLACK);
        formPanel.add(cpfField, gbc);
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel nomeLabel = new JLabel("Nome:");
        nomeLabel.setForeground(Color.BLACK);
        formPanel.add(nomeLabel, gbc);
        
        gbc.gridx = 1;
        JTextField nomeField = new JTextField(15);
        nomeField.setForeground(Color.BLACK);
        formPanel.add(nomeField, gbc);
        
        // Senha
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setForeground(Color.BLACK);
        formPanel.add(senhaLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField senhaField = new JPasswordField(15);
        senhaField.setForeground(Color.BLACK);
        formPanel.add(senhaField, gbc);
        
        // Saldo inicial
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel saldoLabel = new JLabel("Saldo inicial:");
        saldoLabel.setForeground(Color.BLACK);
        formPanel.add(saldoLabel, gbc);
        
        gbc.gridx = 1;
        JTextField saldoField = new JTextField("0.00", 15);
        saldoField.setForeground(Color.BLACK);
        formPanel.add(saldoField, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(NewPixTheme.BACKGROUND_CARD);
        
        JButton saveButton = createDbButton("SALVAR", Color.GREEN);
        JButton cancelButton = createDbButton("CANCELAR", Color.GRAY);
        
        saveButton.addActionListener(e -> {
            try {
                String cpf = cpfField.getText().trim();
                String nome = nomeField.getText().trim();
                String senha = new String(senhaField.getPassword());
                double saldo = Double.parseDouble(saldoField.getText().trim());
                
                if (cpf.isEmpty() || nome.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos os campos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Criar o usuário
                Usuario novoUsuario = new Usuario();
                novoUsuario.setCpf(cpf);
                novoUsuario.setNome(nome);
                novoUsuario.setSenha(senha);
                novoUsuario.setSaldo(saldo);
                novoUsuario.setCriadoEm(java.time.LocalDateTime.now());
                novoUsuario.setAtualizadoEm(java.time.LocalDateTime.now());
                
                usuarioDAO.criar(novoUsuario);
                loadTableData();
                dialog.dispose();
                logArea.append("Usuário criado: " + nome + " (" + cpf + ")\n");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao criar usuário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditDialog() {
        int selectedRow = databaseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um registro para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedTable = (String) tableComboBox.getSelectedItem();
        
        if ("usuarios".equals(selectedTable)) {
            showEditUserDialog(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Edição não disponível para esta tabela", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showEditUserDialog(int selectedRow) {
        // Implementação similar ao showAddUserDialog, mas preenchendo com dados existentes
        JOptionPane.showMessageDialog(this, "Funcionalidade de edição em desenvolvimento", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteSelectedRow() {
        int selectedRow = databaseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um registro para deletar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedTable = (String) tableComboBox.getSelectedItem();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja deletar este registro?", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if ("usuarios".equals(selectedTable)) {
                    Object userId = databaseTableModel.getValueAt(selectedRow, 0);
                    usuarioDAO.deletar(userId.toString());
                    loadTableData();
                    logArea.append("Usuário deletado (CPF: " + userId + ")\n");
                } else {
                    JOptionPane.showMessageDialog(this, "Exclusão não disponível para esta tabela", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setupEventHandlers() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (serverRunning) {
                    stopServer();
                }
                System.exit(0);
            }
        });
    }
    
    private void initializeDatabase() {
        try {
            DatabaseManager.getInstance().initializeDatabase();
            logArea.append("Banco de dados inicializado com sucesso.\n");
        } catch (Exception e) {
            logArea.append("Erro ao inicializar banco de dados: " + e.getMessage() + "\n");
        }
    }
    
    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText());
            
            server = new NewPixServer(port);
            new Thread(() -> {
                try {
                    server.start();
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        logArea.append("Erro ao iniciar servidor: " + e.getMessage() + "\n");
                        updateServerStatus(false);
                    });
                }
            }).start();
            
            updateServerStatus(true);
            logArea.append("Servidor iniciado na porta " + port + "\n");
            
            // Iniciar timer para atualizar tabela de dispositivos
            devicesRefreshTimer.start();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Porta inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logArea.append("Erro ao iniciar servidor: " + e.getMessage() + "\n");
        }
    }
    
    private void stopServer() {
        if (server != null) {
            try {
                server.stop();
                updateServerStatus(false);
                logArea.append("Servidor parado.\n");
                
                // Parar timer e limpar tabela
                devicesRefreshTimer.stop();
                updateDevicesTable();
                
            } catch (Exception e) {
                logArea.append("Erro ao parar servidor: " + e.getMessage() + "\n");
            }
        }
    }
    
    private void updateServerStatus(boolean running) {
        serverRunning = running;
        startButton.setEnabled(!running);
        stopButton.setEnabled(running);
        portField.setEnabled(!running);
        
        if (running) {
            // Servidor ativo - botão iniciar fica verde
            startButton.setForeground(NewPixTheme.SUCCESS_COLOR);
            startButton.setBorder(BorderFactory.createLineBorder(NewPixTheme.SUCCESS_COLOR, 2));
            
            // Botão parar fica preto normal
            stopButton.setForeground(Color.BLACK);
            stopButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            
            statusLabel.setText("STATUS: Servidor Ativo");
            statusLabel.setForeground(NewPixTheme.SUCCESS_COLOR);
        } else {
            // Servidor parado - botão parar fica vermelho
            stopButton.setForeground(NewPixTheme.ERROR_COLOR);
            stopButton.setBorder(BorderFactory.createLineBorder(NewPixTheme.ERROR_COLOR, 2));
            
            // Botão iniciar fica preto normal
            startButton.setForeground(Color.BLACK);
            startButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            
            statusLabel.setText("STATUS: Servidor Parado");
            statusLabel.setForeground(NewPixTheme.ERROR_COLOR);
        }
        
        statusLabel.setOpaque(true);
        statusLabel.setBackground(NewPixTheme.BACKGROUND_CARD);
        
        // Atualizar o painel de controle
        Component parent = statusLabel.getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }
    
    private void redirectSystemOutput() {
        try {
            // Redirecionar System.out e System.err para a área de log com UTF-8
            PrintStream customOut = new PrintStream(new OutputStream() {
                private StringBuilder buffer = new StringBuilder();
                
                @Override
                public void write(int b) {
                    char ch = (char) b;
                    if (ch == '\n') {
                        SwingUtilities.invokeLater(() -> {
                            String text = buffer.toString();
                            // Limpar caracteres problemáticos
                            text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
                            logArea.append(text + "\n");
                            logArea.setCaretPosition(logArea.getDocument().getLength());
                        });
                        buffer.setLength(0);
                    } else if (ch != '\r') {
                        buffer.append(ch);
                    }
                }
            }, true, "UTF-8");
            
            System.setOut(customOut);
            System.setErr(customOut);
        } catch (Exception e) {
            logArea.append("Erro ao configurar redirecionamento: " + e.getMessage() + "\n");
        }
    }
    
    private String getServerIPAddress() {
        try {
            // Método 1: Tentar obter o IP preferencial (não loopback, IPv4)
            for (NetworkInterface netInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (netInterface.isUp() && !netInterface.isLoopback() && !netInterface.isVirtual()) {
                    for (InetAddress address : Collections.list(netInterface.getInetAddresses())) {
                        if (!address.isLoopbackAddress() && 
                            !address.isLinkLocalAddress() && 
                            !address.isMulticastAddress() &&
                            address.getHostAddress().indexOf(':') == -1) { // IPv4 apenas
                            
                            String ip = address.getHostAddress();
                            
                            // Priorizar IPs das redes comuns (172.x, 192.168.x, 10.x)
                            if (ip.startsWith("172.") || ip.startsWith("192.168.") || ip.startsWith("10.")) {
                                System.out.println("IP detectado (rede privada): " + ip + " em " + netInterface.getDisplayName());
                                return ip;
                            }
                        }
                    }
                }
            }
            
            // Método 2: Se não encontrou IP de rede privada, tentar qualquer IP válido
            for (NetworkInterface netInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (netInterface.isUp() && !netInterface.isLoopback()) {
                    for (InetAddress address : Collections.list(netInterface.getInetAddresses())) {
                        if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(':') == -1) {
                            String ip = address.getHostAddress();
                            System.out.println("IP detectado (qualquer): " + ip + " em " + netInterface.getDisplayName());
                            return ip;
                        }
                    }
                }
            }
            
            // Método 3: Fallback para localhost
            System.out.println("Usando IP localhost como fallback");
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.err.println("Erro ao detectar IP do servidor: " + e.getMessage());
            return "127.0.0.1";
        }
    }
    
    private void initializeDevicesTable() {
        // Colunas da tabela
        String[] columnNames = {"IP", "Porta", "Hostname", "Status", "Conectado em"};
        
        // Modelo da tabela não editável
        devicesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Criar tabela
        devicesTable = new JTable(devicesTableModel);
        devicesTable.setFont(NewPixTheme.FONT_BODY);
        devicesTable.setBackground(NewPixTheme.BACKGROUND_CARD);
        devicesTable.setForeground(Color.BLACK);
        devicesTable.setGridColor(Color.LIGHT_GRAY);
        devicesTable.setSelectionBackground(NewPixTheme.PRIMARY_COLOR);
        devicesTable.setSelectionForeground(Color.WHITE);
        devicesTable.setRowHeight(25);
        
        // Configurar largura das colunas
        TableColumnModel columnModel = devicesTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120); // IP
        columnModel.getColumn(1).setPreferredWidth(80);  // Porta
        columnModel.getColumn(2).setPreferredWidth(150); // Hostname
        columnModel.getColumn(3).setPreferredWidth(80);  // Status
        columnModel.getColumn(4).setPreferredWidth(150); // Conectado em
        
        // Configurar cabeçalho
        devicesTable.getTableHeader().setFont(NewPixTheme.FONT_SUBTITLE);
        devicesTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        devicesTable.getTableHeader().setForeground(Color.BLACK);
        devicesTable.getTableHeader().setOpaque(true);
        
        // Timer para atualizar tabela a cada 5 segundos
        devicesRefreshTimer = new javax.swing.Timer(5000, e -> updateDevicesTable());
    }
    
    private void updateDevicesTable() {
        if (server != null && serverRunning) {
            // Limpar tabela
            devicesTableModel.setRowCount(0);
            
            // Obter lista de clientes conectados
            List<ClientHandler> clients = server.getActiveClients();
            
            if (!clients.isEmpty()) {
                for (ClientHandler client : clients) {
                    // Verificar se o cliente ainda está conectado
                    if (client.isConnected()) {
                        Object[] rowData = {
                            client.getClientIP(),           // IP real do cliente
                            String.valueOf(client.getClientPort()), // Porta real do cliente
                            client.getClientHostname(),     // Hostname real
                            "Conectado",                    // Status
                            new Date().toString()           // Conectado em (pode ser melhorado)
                        };
                        devicesTableModel.addRow(rowData);
                    }
                }
                
                logArea.append("Tabela de dispositivos atualizada: " + clients.size() + " clientes conectados\n");
            } else {
                logArea.append("Nenhum cliente conectado no momento\n");
            }
        } else {
            // Servidor parado - limpar tabela
            devicesTableModel.setRowCount(0);
            if (devicesTableModel.getRowCount() == 0) {
                logArea.append("Servidor parado - tabela de dispositivos limpa\n");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ServerGUI().setVisible(true);
        });
    }
}
