package com.newpix.server.gui;

import com.newpix.dao.DatabaseManager;
import com.newpix.server.NewPixServer;
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
    
    // Componentes da aba de dispositivos
    private JTabbedPane tabbedPane;
    private DefaultTableModel devicesTableModel;
    private JTable devicesTable;
    private javax.swing.Timer devicesRefreshTimer;
    
    public ServerGUI() {
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
            // Tentar obter o IP local preferencial (não loopback)
            for (NetworkInterface netInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (netInterface.isUp() && !netInterface.isLoopback()) {
                    for (InetAddress address : Collections.list(netInterface.getInetAddresses())) {
                        if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(':') == -1) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
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
            
            // Adicionar dados dos clientes conectados
            // Por enquanto, vamos simular alguns dados
            // TODO: Implementar coleta real de dados dos clientes
            
            if (server.getActiveClientCount() > 0) {
                for (int i = 0; i < server.getActiveClientCount(); i++) {
                    Object[] rowData = {
                        "192.168.1." + (100 + i),  // IP simulado
                        "Cliente" + (i + 1),        // Porta/Cliente
                        "DESKTOP-CLIENT-" + (i + 1), // Hostname simulado
                        "Conectado",                 // Status
                        new Date().toString()        // Conectado em
                    };
                    devicesTableModel.addRow(rowData);
                }
            }
        } else {
            // Servidor parado - limpar tabela
            devicesTableModel.setRowCount(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ServerGUI().setVisible(true);
        });
    }
}
