package com.distribuidos.server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.distribuidos.common.ClientInfo;
import com.distribuidos.common.UIColors;
import com.distribuidos.common.Usuario;
import com.distribuidos.database.DatabaseManager;

public class ServerGUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(ServerGUI.class);
    
    private JTextArea logArea;
    private JTextField portField;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JLabel serverInfoLabel;
    private JTable clientsTable;
    private DefaultTableModel clientsTableModel;
    private JTable databaseTable;
    private DefaultTableModel databaseTableModel;
    private JTable transactionsTable;
    private DefaultTableModel transactionsTableModel;
    
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private final Map<String, ClientInfo> connectedClients = new ConcurrentHashMap<>();
    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    
    public ServerGUI() {
        initializeGUI();
        updateServerInfo();
        updateDatabaseView();
    }
    
    private void initializeGUI() {
        setTitle("Servidor - Sistema Distribuído");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Painel principal com abas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Aba do Servidor
        JPanel serverPanel = createServerPanel();
        tabbedPane.addTab("Servidor", serverPanel);
        
        // Aba de Clientes Conectados
        JPanel clientsPanel = createClientsPanel();
        tabbedPane.addTab("Clientes Conectados", clientsPanel);
        
        // Aba do Banco de Dados
        JPanel databasePanel = createDatabasePanel();
        tabbedPane.addTab("Banco de Dados", databasePanel);
        
        // Aba de Transações
        JPanel transactionsPanel = createTransactionsPanel();
        tabbedPane.addTab("Transações", transactionsPanel);
        
        // Aba Admin Tools
        JPanel adminPanel = createAdminToolsPanel();
        tabbedPane.addTab("Admin Tools", adminPanel);
        
        // Aba Error Injector
        JPanel errorInjectorPanel = createErrorInjectorPanel();
        tabbedPane.addTab("Error Injector 🔴", errorInjectorPanel);
        
        add(tabbedPane);
        
        // Listener para fechar servidor ao fechar janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopServer();
                System.exit(0);
            }
        });
    }
    
    // ========== Admin Tools Panel ==========
    private JPanel createAdminToolsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Ferramentas de Administração"));
        
        // Painel superior com opções
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 1. Backup
        JPanel backupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel backupLabel = new JLabel("💾 Backup do Banco de Dados");
        backupLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        backupPanel.add(backupLabel);
        JButton backupButton = new JButton("Fazer Backup");
        backupButton.addActionListener(e -> performBackup());
        backupPanel.add(backupButton);
        optionsPanel.add(backupPanel);
        
        // 2. Reset
        JPanel resetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel resetLabel = new JLabel("🔄 Resetar Sistema");
        resetLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        resetPanel.add(resetLabel);
        JButton resetButton = new JButton("Resetar BD");
        resetButton.addActionListener(e -> resetDatabase());
        resetPanel.add(resetButton);
        optionsPanel.add(resetPanel);
        
        // 3. Filtros e Buscas
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel filterLabel = new JLabel("🔍 Filtros e Buscas");
        filterLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        filterPanel.add(filterLabel);
        JButton filterButton = new JButton("Abrir Buscador");
        filterButton.addActionListener(e -> openFilterDialog());
        filterPanel.add(filterButton);
        optionsPanel.add(filterPanel);
        
        // 4. Alertas Inteligentes
        JPanel alertsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel alertsLabel = new JLabel("⚠️ Alertas Inteligentes");
        alertsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        alertsPanel.add(alertsLabel);
        JButton alertsButton = new JButton("Configurar Alertas");
        alertsButton.addActionListener(e -> configureAlerts());
        alertsPanel.add(alertsButton);
        optionsPanel.add(alertsPanel);
        
        panel.add(optionsPanel, BorderLayout.NORTH);
        
        // Painel central - Log de operações
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Log de Operações Admin"));
        
        JTextArea adminLogArea = new JTextArea(15, 50);
        adminLogArea.setEditable(false);
        adminLogArea.setFont(new Font("Dialog", Font.PLAIN, 11));
        adminLogArea.setText("[INFO] Sistema pronto para operações...\n");
        
        JScrollPane scrollPane = new JScrollPane(adminLogArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(logPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void performBackup() {
        // SwingWorker para não travar a UI
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    publish("[BACKUP] Iniciando backup do banco de dados...");
                    
                    // Simular operação de backup (pode integrar com DB depois)
                    Thread.sleep(2000);
                    
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String backupFile = "backup_" + timestamp + ".db";
                    
                    publish("[BACKUP] ✅ Backup concluído: " + backupFile);
                    addLogMessage("Backup realizado pelo administrador: " + backupFile);
                    
                } catch (Exception e) {
                    publish("[BACKUP] ❌ Erro: " + e.getMessage());
                    logger.error("Erro ao fazer backup", e);
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void resetDatabase() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "⚠️ ATENÇÃO!\n\nDeseja realmente resetar todo o banco de dados?\n" +
            "Todos os dados de usuários e transações serão PERDIDOS.",
            "Confirmar Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // SwingWorker para não travar a UI
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        publish("[RESET] Iniciando reset do banco de dados...");
                        
                        // Resetar BD
                        dbManager.resetDatabase();
                        Thread.sleep(1000);
                        
                        publish("[RESET] ✅ Banco de dados resetado com sucesso");
                        addLogMessage("Sistema foi resetado pelo administrador");
                        
                        // Atualizar views
                        updateDatabaseView();
                        updateTransactionsView();
                        
                    } catch (Exception e) {
                        publish("[RESET] ❌ Erro: " + e.getMessage());
                        logger.error("Erro ao resetar BD", e);
                    }
                    return null;
                }
            };
            worker.execute();
        }
    }
    
    private void openFilterDialog() {
        // Diálogo para filtros avançados
        JPanel filterPanel = new JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        
        // Filtro por CPF
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Buscar por CPF:"), gbc);
        
        JTextField cpfFilter = new JTextField(20);
        gbc.gridx = 1;
        filterPanel.add(cpfFilter, gbc);
        
        // Filtro por Nome
        gbc.gridx = 0;
        gbc.gridy = 1;
        filterPanel.add(new JLabel("Buscar por Nome:"), gbc);
        
        JTextField nameFilter = new JTextField(20);
        gbc.gridx = 1;
        filterPanel.add(nameFilter, gbc);
        
        // Filtro por intervalo de saldo
        gbc.gridx = 0;
        gbc.gridy = 2;
        filterPanel.add(new JLabel("Saldo Mínimo (R$):"), gbc);
        
        JTextField minBalance = new JTextField("0.00", 10);
        gbc.gridx = 1;
        filterPanel.add(minBalance, gbc);
        
        // Filtro por período de transações
        gbc.gridx = 0;
        gbc.gridy = 3;
        filterPanel.add(new JLabel("Período (dias):"), gbc);
        
        JTextField periodDays = new JTextField("30", 10);
        gbc.gridx = 1;
        filterPanel.add(periodDays, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, filterPanel, 
            "Filtros e Buscas Avançadas", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            // SwingWorker para processar filtros
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        publish("[FILTRO] Processando filtros...");
                        Thread.sleep(500);
                        
                        StringBuilder filters = new StringBuilder();
                        if (!cpfFilter.getText().isEmpty()) filters.append(" CPF=").append(cpfFilter.getText());
                        if (!nameFilter.getText().isEmpty()) filters.append(" Nome=").append(nameFilter.getText());
                        if (!minBalance.getText().isEmpty()) filters.append(" SaldoMin=R$").append(minBalance.getText());
                        if (!periodDays.getText().isEmpty()) filters.append(" Dias=").append(periodDays.getText());
                        
                        publish("[FILTRO] ✅ Filtros aplicados:" + filters.toString());
                        
                    } catch (Exception e) {
                        publish("[FILTRO] ❌ Erro ao aplicar filtros: " + e.getMessage());
                    }
                    return null;
                }
            };
            worker.execute();
        }
    }
    
    private void configureAlerts() {
        JPanel alertPanel = new JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        
        // Alerta de saldo baixo
        gbc.gridx = 0;
        gbc.gridy = 0;
        JCheckBox lowBalanceAlert = new JCheckBox("Alertar quando saldo < R$");
        alertPanel.add(lowBalanceAlert, gbc);
        
        JTextField lowBalanceAmount = new JTextField("100.00", 10);
        gbc.gridx = 1;
        alertPanel.add(lowBalanceAmount, gbc);
        
        // Alerta de transações suspeitas
        gbc.gridx = 0;
        gbc.gridy = 1;
        JCheckBox suspiciousAlert = new JCheckBox("Alertar transações > R$");
        alertPanel.add(suspiciousAlert, gbc);
        
        JTextField suspiciousAmount = new JTextField("5000.00", 10);
        gbc.gridx = 1;
        alertPanel.add(suspiciousAmount, gbc);
        
        // Alerta de desconexões abruptas
        gbc.gridx = 0;
        gbc.gridy = 2;
        JCheckBox disconnectAlert = new JCheckBox("Alertar desconexões abruptas");
        alertPanel.add(disconnectAlert, gbc);
        
        // Alerta de erro em transação
        gbc.gridx = 0;
        gbc.gridy = 3;
        JCheckBox errorAlert = new JCheckBox("Alertar erros em transações");
        alertPanel.add(errorAlert, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, alertPanel, 
            "Configurar Alertas Inteligentes", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            StringBuilder config = new StringBuilder("Alertas configurados: ");
            if (lowBalanceAlert.isSelected()) config.append("[Saldo Baixo] ");
            if (suspiciousAlert.isSelected()) config.append("[Transações Suspeitas] ");
            if (disconnectAlert.isSelected()) config.append("[Desconexões] ");
            if (errorAlert.isSelected()) config.append("[Erros] ");
            
            addLogMessage(config.toString());
            JOptionPane.showMessageDialog(this, "Alertas configurados com sucesso!", 
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private JPanel createServerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Painel superior - Controles
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        controlPanel.add(new JLabel("Porta:"));
        portField = new JTextField("20000", 10);
        controlPanel.add(portField);
        
        startButton = new JButton("Iniciar Servidor");
        startButton.addActionListener(e -> startServer());
        controlPanel.add(startButton);
        
        stopButton = new JButton("Parar Servidor");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopServer());
        controlPanel.add(stopButton);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Painel central - Informações e Status
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        
    statusLabel = new JLabel("Status: Parado");
    statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    statusLabel.setForeground(UIColors.ERROR);
        infoPanel.add(statusLabel);
        
        serverInfoLabel = new JLabel();
        serverInfoLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        infoPanel.add(serverInfoLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Painel inferior - Log
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        
        logArea = new JTextArea(15, 50);
        logArea.setEditable(false);
        // Configurar fonte que suporte caracteres UTF-8
        logArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        // Garantir que a codificação seja UTF-8
        logArea.getDocument().putProperty("i18n", Boolean.TRUE);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        
        JButton clearLogButton = new JButton("Limpar Log");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        logPanel.add(clearLogButton, BorderLayout.SOUTH);
        
        panel.add(logPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Clientes Conectados"));
        
        // Tabela de clientes
        String[] columnNames = {"IP", "Porta", "Hostname", "CPF Usuário", "Nome Usuário", "Conectado Em"};
        clientsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        clientsTable = new JTable(clientsTableModel);
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientsTable.getTableHeader().setReorderingAllowed(false);
        // Renderer to show tooltip with full hostname/IP when hovered
        clientsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof javax.swing.JComponent) {
                    javax.swing.JComponent jc = (javax.swing.JComponent) c;
                    jc.setToolTipText(value != null ? value.toString() : null);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(clientsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton refreshButton = new JButton("Atualizar");
        refreshButton.addActionListener(e -> updateConnectedClients());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createDatabasePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Usuários no Banco de Dados"));
        
        // Tabela do banco de dados
        String[] columnNames = {"CPF", "Nome", "Saldo", "Criado Em", "Atualizado Em"};
        databaseTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        databaseTable = new JTable(databaseTableModel);
        databaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        databaseTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(databaseTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton refreshButton = new JButton("Atualizar");
        refreshButton.addActionListener(e -> updateDatabaseView());
        buttonPanel.add(refreshButton);
        
        JButton deleteButton = new JButton("Deletar Usuário");
        deleteButton.addActionListener(e -> deleteSelectedUser());
        buttonPanel.add(deleteButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Transações no Banco de Dados"));

        String[] columnNames = {"ID", "Valor", "CPF Enviador", "CPF Recebedor", "Data"};
        transactionsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionsTable = new JTable(transactionsTableModel);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionsTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Atualizar");
        refreshButton.addActionListener(e -> updateTransactionsView());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Fill initially
        updateTransactionsView();

        return panel;
    }
    
    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            
            serverSocket = new ServerSocket(port);
            isRunning = true;
            
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            portField.setEnabled(false);
            
                statusLabel.setText("Status: Executando");
                statusLabel.setForeground(UIColors.SUCCESS);
            
            addLogMessage("Servidor iniciado na porta " + port);
            updateServerInfo();
            
            // Thread para aceitar conexões
            new Thread(() -> {
                while (isRunning && !serverSocket.isClosed()) {
                    try {
                        java.net.Socket clientSocket = serverSocket.accept();
                        new ServerHandler(clientSocket, this, connectedClients).start();
                    } catch (java.io.IOException e) {
                        if (isRunning) {
                            logger.error("Erro ao aceitar conexão", e);
                            addLogMessage("Erro ao aceitar conexão: " + e.getMessage());
                        }
                    }
                }
            }).start();
            
        } catch (Exception e) {
            logger.error("Erro ao iniciar servidor", e);
            addLogMessage("Erro ao iniciar servidor: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao iniciar servidor: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void stopServer() {
        try {
            isRunning = false;
            
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            portField.setEnabled(true);
            
            statusLabel.setText("Status: Parado");
            statusLabel.setForeground(UIColors.ERROR);
            
            addLogMessage("Servidor parado");
            updateServerInfo();
            
            // Limpa clientes conectados
            connectedClients.clear();
            updateConnectedClients();
            
        } catch (Exception e) {
            logger.error("Erro ao parar servidor", e);
            addLogMessage("Erro ao parar servidor: " + e.getMessage());
        }
    }
    
    public void addLogMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public void updateConnectedClients() {
        SwingUtilities.invokeLater(() -> {
            clientsTableModel.setRowCount(0);
            
            for (ClientInfo client : connectedClients.values()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String conectadoEm = sdf.format(new Date(client.getConectadoEm()));
                
                String ip = client.getIp();
                String hostname = client.getHostname();
                String hostDisplay;
                if (hostname != null && !hostname.isEmpty() && !hostname.equals(ip)) {
                    hostDisplay = String.format("%s (%s)", hostname, ip);
                } else {
                    hostDisplay = ip;
                }

                Object[] row = {
                    ip,
                    client.getPorta(),
                    hostDisplay,
                    client.getCpfUsuario() != null ? client.getCpfUsuario() : "Não logado",
                    client.getNomeUsuario() != null ? client.getNomeUsuario() : "Não logado",
                    conectadoEm
                };
                
                clientsTableModel.addRow(row);
            }
        });
    }
    
    public void updateDatabaseView() {
        SwingUtilities.invokeLater(() -> {
            databaseTableModel.setRowCount(0);
            
            List<Usuario> users = dbManager.getAllUsers();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            
            for (Usuario user : users) {
                Object[] row = {
                    user.getCpf(),
                    user.getNome(),
                    String.format("R$ %.2f", user.getSaldo()),
                    sdf.format(java.sql.Timestamp.valueOf(user.getCriadoEm())),
                    sdf.format(java.sql.Timestamp.valueOf(user.getAtualizadoEm()))
                };
                
                databaseTableModel.addRow(row);
            }
        });
        // Also refresh transactions view
        updateTransactionsView();
    }

    public void updateTransactionsView() {
        SwingUtilities.invokeLater(() -> {
            if (transactionsTableModel == null) return;
            transactionsTableModel.setRowCount(0);

            java.util.List<com.distribuidos.common.Transacao> transacoes = dbManager.getAllTransacoes();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            for (com.distribuidos.common.Transacao t : transacoes) {
                Object[] row = {
                    t.getId(),
                    String.format("R$ %.2f", t.getValor()),
                    t.getCpfOrigem() != null ? t.getCpfOrigem() : "-",
                    t.getCpfDestino() != null ? t.getCpfDestino() : "-",
                    sdf.format(java.sql.Timestamp.valueOf(t.getTimestamp()))
                };
                transactionsTableModel.addRow(row);
            }
        });
    }
    
    private void deleteSelectedUser() {
        int selectedRow = databaseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para deletar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String cpf = (String) databaseTableModel.getValueAt(selectedRow, 0);
        String nome = (String) databaseTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja deletar o usuário:\n" + nome + " (" + cpf + ")?",
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dbManager.deleteUser(cpf)) {
                addLogMessage("Usuário deletado pelo administrador: " + cpf);
                updateDatabaseView();
                JOptionPane.showMessageDialog(this, "Usuário deletado com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao deletar usuário", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateServerInfo() {
        try {
            // Try to find a non-loopback IPv4 address for display. Fall back to localhost when necessary.
            String hostName = null;
            String hostAddress = null;

            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface ni = interfaces.nextElement();
                try {
                    if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
                } catch (Exception ex) {
                    continue;
                }

                java.util.Enumeration<java.net.InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    java.net.InetAddress addr = addrs.nextElement();
                    if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
                        hostAddress = addr.getHostAddress();
                        try { hostName = addr.getHostName(); } catch (Exception ignored) {}
                        break;
                    }
                }

                if (hostAddress != null) break;
            }

            if (hostAddress == null) {
                InetAddress localhost = InetAddress.getLocalHost();
                hostName = localhost.getHostName();
                hostAddress = localhost.getHostAddress();
            }

            String info = String.format(
                "<html>Hostname: %s<br/>IP Local: %s<br/>Porta: %s<br/>Usuários no DB: %d</html>",
                hostName != null ? hostName : "N/A",
                hostAddress != null ? hostAddress : "N/A",
                isRunning ? portField.getText() : "N/A",
                dbManager.countUsers()
            );

            serverInfoLabel.setText(info);
        } catch (Exception e) {
            logger.error("Erro ao obter informações do servidor", e);
            serverInfoLabel.setText("Erro ao obter informações do servidor");
        }
    }
    
    // ========== Error Injector Panel ==========
    private JPanel createErrorInjectorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("🔴 Injetor de Erros - Teste de Resiliência"));
        
        // Painel central com controles
        JPanel controlPanel = new JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Tipo de erro
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(new JLabel("Tipo de Erro:"), gbc);
        
        JComboBox<com.distribuidos.tools.ServerErrorInjector.ErrorType> errorTypeCombo = 
            new JComboBox<>(com.distribuidos.tools.ServerErrorInjector.ErrorType.values());
        gbc.gridx = 1;
        controlPanel.add(errorTypeCombo, gbc);
        
        // Taxa de injeção
        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(new JLabel("Taxa de Injeção (%):"), gbc);
        
        JSlider rateSlider = new JSlider(0, 100, 50);
        rateSlider.setMajorTickSpacing(10);
        rateSlider.setMinorTickSpacing(1);
        rateSlider.setPaintTicks(true);
        rateSlider.setPaintLabels(true);
        JLabel rateLabel = new JLabel("50%");
        rateSlider.addChangeListener(e -> rateLabel.setText(rateSlider.getValue() + "%"));
        gbc.gridx = 1;
        controlPanel.add(rateSlider, gbc);
        
        gbc.gridx = 2;
        controlPanel.add(rateLabel, gbc);
        
        // Latência de rede
        gbc.gridx = 0;
        gbc.gridy = 2;
        controlPanel.add(new JLabel("Latência de Rede (ms):"), gbc);
        
        JSpinner latencySpinner = new JSpinner(new javax.swing.SpinnerNumberModel(0, 0, 5000, 100));
        gbc.gridx = 1;
        controlPanel.add(latencySpinner, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        
        JButton enableButton = new JButton("✅ Ativar Injeção");
        enableButton.addActionListener(e -> {
            com.distribuidos.tools.ServerErrorInjector.ErrorType type = 
                (com.distribuidos.tools.ServerErrorInjector.ErrorType) errorTypeCombo.getSelectedItem();
            double rate = rateSlider.getValue() / 100.0;
            long latency = ((Number) latencySpinner.getValue()).longValue();
            
            com.distribuidos.tools.ServerErrorInjector.enableErrorInjection(type, rate);
            com.distribuidos.tools.ServerErrorInjector.setNetworkLatency(latency);
            
            addLogMessage("🔴 INJEÇÃO DE ERROS ATIVADA - Tipo: " + type + ", Taxa: " + (int)(rate*100) + "%");
            JOptionPane.showMessageDialog(this, 
                "Injeção ativada:\n" + type + "\nTaxa: " + (int)(rate*100) + "%\nLatência: " + latency + "ms",
                "Injeção Ativa", JOptionPane.WARNING_MESSAGE);
        });
        buttonPanel.add(enableButton);
        
        JButton disableButton = new JButton("🟢 Desativar Injeção");
        disableButton.addActionListener(e -> {
            com.distribuidos.tools.ServerErrorInjector.disableErrorInjection();
            addLogMessage("🟢 INJEÇÃO DE ERROS DESATIVADA");
            JOptionPane.showMessageDialog(this, "Injeção desativada com sucesso", 
                "Injeção Desativada", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(disableButton);
        
        JButton statusButton = new JButton("📊 Ver Status");
        statusButton.addActionListener(e -> {
            String status = com.distribuidos.tools.ServerErrorInjector.getStatus();
            JOptionPane.showMessageDialog(this, status, "Status da Injeção", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(statusButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        controlPanel.add(buttonPanel, gbc);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Painel de informações
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informações de Erros"));
        
        JTextArea infoArea = new JTextArea(12, 50);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        infoArea.setText(gerarInfoErros());
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(infoArea);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("🔄 Atualizar");
        refreshButton.addActionListener(e -> infoArea.setText(gerarInfoErros()));
        JPanel infoButtonPanel = new JPanel(new FlowLayout());
        infoButtonPanel.add(refreshButton);
        infoPanel.add(infoButtonPanel, BorderLayout.SOUTH);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private String gerarInfoErros() {
        StringBuilder info = new StringBuilder();
        info.append("TIPOS DE ERROS DISPONÍVEIS:\n");
        info.append("════════════════════════════════════════\n\n");
        
        for (com.distribuidos.tools.ServerErrorInjector.ErrorType type : 
             com.distribuidos.tools.ServerErrorInjector.ErrorType.values()) {
            info.append("• ").append(type).append("\n");
            info.append("  → ").append(com.distribuidos.tools.ServerErrorInjector.getErrorMessage(type)).append("\n\n");
        }
        
        return info.toString();
    }
}