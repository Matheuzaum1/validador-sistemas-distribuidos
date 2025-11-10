package com.distribuidos.database;

import com.distribuidos.common.Usuario;
import com.distribuidos.exception.DatabaseException;
import com.distribuidos.exception.ErrorCode;
import com.distribuidos.util.RetryUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_URL = "jdbc:sqlite:usuarios.db";
    private static DatabaseManager instance;
    
    private DatabaseManager() {
        initializeDatabase();
        populateDatabase();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "cpf TEXT PRIMARY KEY," +
                "nome TEXT NOT NULL," +
                "senha TEXT NOT NULL," +
                "saldo REAL DEFAULT 0.0," +
                "criado_em TEXT NOT NULL," +
                "atualizado_em TEXT NOT NULL" +
                ")";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                // Create transactions table
                String createTransacoes = "CREATE TABLE IF NOT EXISTS transacoes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "cpf_origem TEXT," +
                    "cpf_destino TEXT," +
                    "valor REAL NOT NULL," +
                    "timestamp TEXT NOT NULL" +
                    ")";
                stmt.execute(createTransacoes);
                logger.info("Tabelas de usuarios e transacoes criadas/verificadas com sucesso");
            }
        } catch (SQLException e) {
            logger.error("Erro ao inicializar banco de dados: {}", e.getMessage(), e);
            throw new DatabaseException("Falha ao inicializar banco de dados", e);
        }
    }
    
    private void populateDatabase() {
        try {
            // Verifica se já existem usuários
            if (countUsers() == 0) {
                logger.info("Populando banco de dados com usuários de teste...");
                
                // Usuários de teste expandidos para melhor cobertura de testes
                createUser("123.456.789-01", "João Silva Santos", "123456");
                createUser("987.654.321-02", "Maria Santos Oliveira", "654321");
                createUser("111.222.333-44", "Pedro Oliveira Costa", "password");
                createUser("555.666.777-88", "Ana Costa Ferreira", "123abc");
                createUser("444.555.666-77", "Carlos Eduardo Lima", "carlos123");
                createUser("333.444.555-66", "Fernanda Alves Souza", "fernanda456");
                createUser("222.333.444-55", "Roberto Silva Junior", "roberto789");
                createUser("666.777.888-99", "Juliana Pereira Rocha", "juliana321");
                createUser("777.888.999-00", "Marcos Antonio Dias", "marcos654");
                createUser("888.999.000-11", "Luciana Martins Cruz", "luciana987");
                createUser("999.000.111-22", "Rafael Santos Barbosa", "rafael147");
                createUser("000.111.222-33", "Camila Rodrigues Silva", "camila258");
                createUser("147.258.369-12", "Bruno Henrique Gomes", "bruno369");
                createUser("258.369.147-23", "Patrícia Lima Nascimento", "patricia741");
                createUser("369.147.258-34", "Diego Fernandes Costa", "diego852");
                createUser("741.852.963-45", "Vanessa Almeida Santos", "vanessa963");
                createUser("852.963.741-56", "Thiago Oliveira Pereira", "thiago159");
                createUser("963.741.852-67", "Priscila Santos Moreira", "priscila753");
                createUser("159.753.486-78", "Leonardo Silva Cardoso", "leonardo486");
                createUser("753.486.159-89", "Gabriela Costa Ribeiro", "gabriela159");
                
                logger.info("Banco de dados populado com {} usuários de teste", countUsers());
                // Seed a richer set of sample transactions for demonstration
                try {
                    // some deposits
                    createDeposit("123.456.789-01", 500.00);
                    createDeposit("987.654.321-02", 300.00);
                    createDeposit("111.222.333-44", 200.00);
                    createDeposit("555.666.777-88", 150.00);

                    // a sequence of transfers between users
                    createTransfer("123.456.789-01", "987.654.321-02", 120.00);
                    createTransfer("987.654.321-02", "111.222.333-44", 50.00);
                    createTransfer("111.222.333-44", "555.666.777-88", 25.50);
                    createTransfer("555.666.777-88", "444.555.666-77", 10.00);
                    createTransfer("444.555.666-77", "333.444.555-66", 5.75);
                    createTransfer("333.444.555-66", "222.333.444-55", 30.00);
                    createTransfer("222.333.444-55", "666.777.888-99", 15.00);

                    // additional mixed activity
                    createTransfer("666.777.888-99", "777.888.999-00", 45.00);
                    createTransfer("777.888.999-00", "888.999.000-11", 60.00);
                    createDeposit("999.000.111-22", 250.00);
                    createTransfer("999.000.111-22", "000.111.222-33", 100.00);
                    createTransfer("000.111.222-33", "147.258.369-12", 20.00);
                    createTransfer("147.258.369-12", "258.369.147-23", 10.00);
                    createTransfer("258.369.147-23", "369.147.258-34", 7.00);
                    createTransfer("369.147.258-34", "741.852.963-45", 12.34);
                    createTransfer("741.852.963-45", "852.963.741-56", 9.99);
                    createTransfer("852.963.741-56", "963.741.852-67", 4.00);
                    createTransfer("963.741.852-67", "159.753.486-78", 3.50);
                    createTransfer("159.753.486-78", "753.486.159-89", 2.25);

                    logger.info("Transações semeadas com sucesso");
                } catch (Exception e) {
                    logger.warn("Erro ao semear transações: {}", e.getMessage());
                }
            }

            // If there are no transactions yet, seed them (covers cases where users exist but transacoes is empty)
            if (countTransacoes() == 0) {
                logger.info("Nenhuma transação encontrada; semeando transações de exemplo...");
                seedTransacoes();
            }
        } catch (Exception e) {
            logger.warn("Erro ao popular banco de dados: " + e.getMessage());
        }
    }

    private void seedTransacoes() {
        try {
            // some deposits
            createDeposit("123.456.789-01", 500.00);
            createDeposit("987.654.321-02", 300.00);
            createDeposit("111.222.333-44", 200.00);
            createDeposit("555.666.777-88", 150.00);

            // a sequence of transfers between users
            createTransfer("123.456.789-01", "987.654.321-02", 120.00);
            createTransfer("987.654.321-02", "111.222.333-44", 50.00);
            createTransfer("111.222.333-44", "555.666.777-88", 25.50);
            createTransfer("555.666.777-88", "444.555.666-77", 10.00);
            createTransfer("444.555.666-77", "333.444.555-66", 5.75);
            createTransfer("333.444.555-66", "222.333.444-55", 30.00);
            createTransfer("222.333.444-55", "666.777.888-99", 15.00);

            // additional mixed activity
            createTransfer("666.777.888-99", "777.888.999-00", 45.00);
            createTransfer("777.888.999-00", "888.999.000-11", 60.00);
            createDeposit("999.000.111-22", 250.00);
            createTransfer("999.000.111-22", "000.111.222-33", 100.00);
            createTransfer("000.111.222-33", "147.258.369-12", 20.00);
            createTransfer("147.258.369-12", "258.369.147-23", 10.00);
            createTransfer("258.369.147-23", "369.147.258-34", 7.00);
            createTransfer("369.147.258-34", "741.852.963-45", 12.34);
            createTransfer("741.852.963-45", "852.963.741-56", 9.99);
            createTransfer("852.963.741-56", "963.741.852-67", 4.00);
            createTransfer("963.741.852-67", "159.753.486-78", 3.50);
            createTransfer("159.753.486-78", "753.486.159-89", 2.25);

            logger.info("Transações semeadas com sucesso");
        } catch (Exception e) {
            logger.warn("Erro ao semear transações: {}", e.getMessage());
        }
    }

    // Transaction-related methods
    public boolean createDeposit(String cpfDestino, double valor) {
        return performAtomicTransfer(null, cpfDestino, valor);
    }

    public boolean createTransfer(String cpfOrigem, String cpfDestino, double valor) {
        return performAtomicTransfer(cpfOrigem, cpfDestino, valor);
    }

    private boolean performAtomicTransfer(String cpfOrigem, String cpfDestino, double valor) {
        String selectSql = "SELECT saldo FROM usuarios WHERE cpf = ?";
        String updateSql = "UPDATE usuarios SET saldo = ?, atualizado_em = ? WHERE cpf = ?";
        String insertTransSql = "INSERT INTO transacoes (cpf_origem, cpf_destino, valor, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);

            // If origem provided, check balance
            double origemSaldo = 0.0;
            if (cpfOrigem != null) {
                try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                    ps.setString(1, cpfOrigem);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    origemSaldo = rs.getDouble("saldo");
                    if (origemSaldo < valor) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Deduct origem
            if (cpfOrigem != null) {
                double newOrigem = origemSaldo - valor;
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setDouble(1, newOrigem);
                    ps.setString(2, java.time.LocalDateTime.now().toString());
                    ps.setString(3, cpfOrigem);
                    ps.executeUpdate();
                }
            }

            // Credit destino
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setString(1, cpfDestino);
                ResultSet rs = psSelect.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                double destinoSaldo = rs.getDouble("saldo");
                double newDestino = destinoSaldo + valor;
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setDouble(1, newDestino);
                    psUpdate.setString(2, java.time.LocalDateTime.now().toString());
                    psUpdate.setString(3, cpfDestino);
                    psUpdate.executeUpdate();
                }
            }

            // Insert transaction record
            try (PreparedStatement psTrans = conn.prepareStatement(insertTransSql)) {
                psTrans.setString(1, cpfOrigem);
                psTrans.setString(2, cpfDestino);
                psTrans.setDouble(3, valor);
                psTrans.setString(4, java.time.LocalDateTime.now().toString());
                psTrans.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            logger.error("Erro ao realizar transação", e);
            return false;
        }
    }

    public java.util.List<com.distribuidos.common.Transacao> getAllTransacoes() {
        java.util.List<com.distribuidos.common.Transacao> list = new java.util.ArrayList<>();
        // Use LEFT JOINs to fetch sender/receiver names in a single query to avoid N+1 lookups
        String sql = "SELECT t.id, t.cpf_origem, t.cpf_destino, t.valor, t.timestamp, " +
                "uo.nome AS nome_origem, ud.nome AS nome_destino " +
                "FROM transacoes t " +
                "LEFT JOIN usuarios uo ON t.cpf_origem = uo.cpf " +
                "LEFT JOIN usuarios ud ON t.cpf_destino = ud.cpf " +
                "ORDER BY t.id DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String origem = rs.getString("cpf_origem");
                String destino = rs.getString("cpf_destino");
                double valor = rs.getDouble("valor");
                java.time.LocalDateTime ts = java.time.LocalDateTime.parse(rs.getString("timestamp"));
                String nomeOrigem = rs.getString("nome_origem");
                String nomeDestino = rs.getString("nome_destino");

                com.distribuidos.common.Transacao t = new com.distribuidos.common.Transacao(id, origem, destino, valor, ts);
                t.setNomeEnviador(nomeOrigem);
                t.setNomeRecebedor(nomeDestino);
                list.add(t);
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar transações", e);
        }

        return list;
    }
    
    public boolean createUser(String cpf, String nome, String senha) {
        String sql = "INSERT INTO usuarios (cpf, nome, senha, criado_em, atualizado_em) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String hashedPassword = BCrypt.hashpw(senha, BCrypt.gensalt());
            String now = LocalDateTime.now().toString();
            
            pstmt.setString(1, cpf);
            pstmt.setString(2, nome);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, now);
            pstmt.setString(5, now);
            
            int result = pstmt.executeUpdate();
            logger.info("Usuário criado: CPF={}, Nome={}", cpf, nome);
            return result > 0;
            
        } catch (SQLException e) {
            logger.error("Erro ao criar usuário: CPF={}", cpf, e);
            return false;
        }
    }
    
    public Usuario getUser(String cpf) {
        String sql = "SELECT * FROM usuarios WHERE cpf = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cpf);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Usuario user = new Usuario();
                user.setCpf(rs.getString("cpf"));
                user.setNome(rs.getString("nome"));
                user.setSenha(rs.getString("senha"));
                user.setSaldo(rs.getDouble("saldo"));
                user.setCriadoEm(LocalDateTime.parse(rs.getString("criado_em")));
                user.setAtualizadoEm(LocalDateTime.parse(rs.getString("atualizado_em")));
                return user;
            }
            
        } catch (SQLException e) {
            logger.error("Erro ao buscar usuário: CPF={}", cpf, e);
        }
        
        return null;
    }
    
    public List<Usuario> getAllUsers() {
        List<Usuario> users = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario user = new Usuario();
                user.setCpf(rs.getString("cpf"));
                user.setNome(rs.getString("nome"));
                user.setSenha(rs.getString("senha"));
                user.setSaldo(rs.getDouble("saldo"));
                user.setCriadoEm(LocalDateTime.parse(rs.getString("criado_em")));
                user.setAtualizadoEm(LocalDateTime.parse(rs.getString("atualizado_em")));
                users.add(user);
            }
            
        } catch (SQLException e) {
            logger.error("Erro ao buscar todos os usuários", e);
        }
        
        return users;
    }
    
    public boolean updateUser(String cpf, String nome, String senha) {
        StringBuilder sql = new StringBuilder("UPDATE usuarios SET atualizado_em = ?");
        List<Object> params = new ArrayList<>();
        params.add(LocalDateTime.now().toString());
        
        if (nome != null && !nome.trim().isEmpty()) {
            sql.append(", nome = ?");
            params.add(nome);
        }
        
        if (senha != null && !senha.trim().isEmpty()) {
            sql.append(", senha = ?");
            params.add(BCrypt.hashpw(senha, BCrypt.gensalt()));
        }
        
        sql.append(" WHERE cpf = ?");
        params.add(cpf);
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            int result = pstmt.executeUpdate();
            logger.info("Usuário atualizado: CPF={}", cpf);
            return result > 0;
            
        } catch (SQLException e) {
            logger.error("Erro ao atualizar usuário: CPF={}", cpf, e);
            return false;
        }
    }
    
    public boolean deleteUser(String cpf) {
        String sql = "DELETE FROM usuarios WHERE cpf = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cpf);
            int result = pstmt.executeUpdate();
            logger.info("Usuário deletado: CPF={}", cpf);
            return result > 0;
            
        } catch (SQLException e) {
            logger.error("Erro ao deletar usuário: CPF={}", cpf, e);
            return false;
        }
    }
    
    public boolean authenticateUser(String cpf, String senha) {
        Usuario user = getUser(cpf);
        if (user != null) {
            return BCrypt.checkpw(senha, user.getSenha());
        }
        return false;
    }
    
    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM usuarios";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            logger.error("Erro ao contar usuários", e);
        }
        
        return 0;
    }

    public int countTransacoes() {
        String sql = "SELECT COUNT(*) FROM transacoes";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Erro ao contar transações", e);
        }
        return 0;
    }
    
    public boolean userExists(String cpf) {
        return getUser(cpf) != null;
    }
}