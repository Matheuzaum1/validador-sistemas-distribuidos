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
                "atualizado_em TEXT NOT NULL," +
                "conta_ativa INTEGER DEFAULT 1" +
                ")";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                
                // Create transactions table for traditional transfers/deposits
                String createTransacoes = "CREATE TABLE IF NOT EXISTS transacoes (" +
                    "id INTEGER PRIMARY KEY," +
                    "cpf_origem TEXT," +
                    "cpf_destino TEXT," +
                    "valor REAL NOT NULL," +
                    "timestamp TEXT NOT NULL," +
                    "tipo_transacao TEXT DEFAULT 'transferencia'" +
                    ")";
                stmt.execute(createTransacoes);
                
                // Create Pix keys table
                String createChavesPix = "CREATE TABLE IF NOT EXISTS chaves_pix (" +
                    "id INTEGER PRIMARY KEY," +
                    "cpf_dono TEXT NOT NULL," +
                    "tipo_chave TEXT NOT NULL," +
                    "valor_chave TEXT NOT NULL UNIQUE," +
                    "criada_em TEXT NOT NULL," +
                    "ativa INTEGER DEFAULT 1," +
                    "FOREIGN KEY (cpf_dono) REFERENCES usuarios(cpf)" +
                    ")";
                stmt.execute(createChavesPix);
                
                // Create Pix transactions table
                String createTransacoesPix = "CREATE TABLE IF NOT EXISTS transacoes_pix (" +
                    "id INTEGER PRIMARY KEY," +
                    "chave_pix_origem TEXT NOT NULL," +
                    "chave_pix_destino TEXT NOT NULL," +
                    "cpf_origem TEXT NOT NULL," +
                    "cpf_destino TEXT NOT NULL," +
                    "valor REAL NOT NULL," +
                    "timestamp TEXT NOT NULL," +
                    "status TEXT DEFAULT 'sucesso'," +
                    "identificador_pix TEXT UNIQUE," +
                    "FOREIGN KEY (cpf_origem) REFERENCES usuarios(cpf)," +
                    "FOREIGN KEY (cpf_destino) REFERENCES usuarios(cpf)" +
                    ")";
                stmt.execute(createTransacoesPix);
                
                // Create indices for performance
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_transacoes_origem ON transacoes(cpf_origem)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_transacoes_destino ON transacoes(cpf_destino)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_transacoes_timestamp ON transacoes(timestamp)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_chaves_pix_dono ON chaves_pix(cpf_dono)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_chaves_pix_valor ON chaves_pix(valor_chave)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_chaves_pix_tipo ON chaves_pix(tipo_chave)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_transacoes_pix_origem ON transacoes_pix(cpf_origem)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_transacoes_pix_destino ON transacoes_pix(cpf_destino)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_transacoes_pix_timestamp ON transacoes_pix(timestamp)");
                
                logger.info("Tabelas de usuarios, transacoes, chaves_pix e transacoes_pix criadas/verificadas com sucesso");
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
        // Para depósitos, tanto enviador quanto recebedor são o mesmo usuário (protocolo 4.9)
        return performAtomicTransfer(cpfDestino, cpfDestino, valor);
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

            boolean isDeposit = cpfOrigem != null && cpfOrigem.equals(cpfDestino);

            // Para transferências normais, verificar saldo da origem
            // Para depósitos, apenas adicionar ao saldo (origem = destino)
            double origemSaldo = 0.0;
            if (cpfOrigem != null && !isDeposit) {
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

            if (isDeposit) {
                // Para depósitos: apenas adicionar o valor ao saldo do usuário
                try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                    psSelect.setString(1, cpfDestino);
                    ResultSet rs = psSelect.executeQuery();
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    double currentSaldo = rs.getDouble("saldo");
                    double newSaldo = currentSaldo + valor;
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setDouble(1, newSaldo);
                        psUpdate.setString(2, java.time.LocalDateTime.now().toString());
                        psUpdate.setString(3, cpfDestino);
                        psUpdate.executeUpdate();
                    }
                }
            } else {
                // Para transferências: debitar origem e creditar destino
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
    
    public void resetDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Delete all Pix transactions first (has FK references)
            stmt.execute("DELETE FROM transacoes_pix");
            
            // Delete all Pix keys
            stmt.execute("DELETE FROM chaves_pix");
            
            // Delete all transactions
            stmt.execute("DELETE FROM transacoes");
            
            // Delete all users
            stmt.execute("DELETE FROM usuarios");
            
            // Reinitialize with default data
            populateDatabase();
            
            logger.info("Banco de dados foi resetado com sucesso");
        } catch (SQLException e) {
            logger.error("Erro ao resetar banco de dados", e);
            throw new RuntimeException("Erro ao resetar banco de dados: " + e.getMessage());
        }
    }
    
    // ============================================================================
    // MÉTODOS PARA GERENCIAR CHAVES PIX
    // ============================================================================
    
    /**
     * Registra uma nova chave Pix para um usuário
     * @param cpfDono CPF do proprietário da chave
     * @param tipoChave Tipo da chave (cpf, email, telefone, cnpj, aleatoria)
     * @param valorChave Valor da chave Pix
     * @return true se registrada com sucesso
     */
    public boolean registrarChavePix(String cpfDono, String tipoChave, String valorChave) {
        String sql = "INSERT INTO chaves_pix (cpf_dono, tipo_chave, valor_chave, criada_em, ativa) " +
                     "VALUES (?, ?, ?, ?, 1)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cpfDono);
            ps.setString(2, tipoChave);
            ps.setString(3, valorChave);
            ps.setString(4, LocalDateTime.now().toString());
            
            ps.executeUpdate();
            logger.info("Chave Pix registrada: {} para CPF {}", tipoChave, cpfDono);
            return true;
        } catch (SQLException e) {
            logger.error("Erro ao registrar chave Pix: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Busca o CPF do proprietário de uma chave Pix
     * @param valorChave Valor da chave Pix
     * @return CPF do proprietário ou null se não encontrado
     */
    public String buscarCpfPorChavePix(String valorChave) {
        String sql = "SELECT cpf_dono FROM chaves_pix WHERE valor_chave = ? AND ativa = 1";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, valorChave);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("cpf_dono");
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar chave Pix: {}", e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * Lista todas as chaves Pix ativas de um usuário
     * @param cpfDono CPF do proprietário
     * @return Lista de chaves Pix (formato: "tipo_chave: valor_chave")
     */
    public java.util.List<String> listarChavesPix(String cpfDono) {
        java.util.List<String> chaves = new java.util.ArrayList<>();
        String sql = "SELECT tipo_chave, valor_chave FROM chaves_pix WHERE cpf_dono = ? AND ativa = 1 ORDER BY criada_em DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cpfDono);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String tipo = rs.getString("tipo_chave");
                String valor = rs.getString("valor_chave");
                chaves.add(tipo + ": " + valor);
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar chaves Pix: {}", e.getMessage(), e);
        }
        
        return chaves;
    }
    
    /**
     * Desativa uma chave Pix
     * @param valorChave Valor da chave Pix a desativar
     * @return true se desativada com sucesso
     */
    public boolean desativarChavePix(String valorChave) {
        String sql = "UPDATE chaves_pix SET ativa = 0 WHERE valor_chave = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, valorChave);
            int updated = ps.executeUpdate();
            
            if (updated > 0) {
                logger.info("Chave Pix desativada: {}", valorChave);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Erro ao desativar chave Pix: {}", e.getMessage(), e);
        }
        
        return false;
    }
    
    /**
     * Registra uma transação Pix no banco de dados
     * @param chavePixOrigem Chave Pix do remetente
     * @param chavePixDestino Chave Pix do destinatário
     * @param cpfOrigem CPF do remetente
     * @param cpfDestino CPF do destinatário
     * @param valor Valor da transferência
     * @param identificadorPix Identificador único da transação
     * @return true se registrada com sucesso
     */
    public boolean registrarTransacaoPix(String chavePixOrigem, String chavePixDestino, 
                                        String cpfOrigem, String cpfDestino, 
                                        double valor, String identificadorPix) {
        String sql = "INSERT INTO transacoes_pix " +
                     "(chave_pix_origem, chave_pix_destino, cpf_origem, cpf_destino, valor, timestamp, status, identificador_pix) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'sucesso', ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, chavePixOrigem);
            ps.setString(2, chavePixDestino);
            ps.setString(3, cpfOrigem);
            ps.setString(4, cpfDestino);
            ps.setDouble(5, valor);
            ps.setString(6, LocalDateTime.now().toString());
            ps.setString(7, identificadorPix);
            
            ps.executeUpdate();
            logger.info("Transação Pix registrada: {} -> {} (R$ {})", chavePixOrigem, chavePixDestino, valor);
            return true;
        } catch (SQLException e) {
            logger.error("Erro ao registrar transação Pix: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Busca transações Pix de um usuário
     * @param cpf CPF do usuário (enviador ou recebedor)
     * @return Lista de transações Pix como JSON
     */
    public java.util.List<String> listarTransacoesPix(String cpf) {
        java.util.List<String> transacoes = new java.util.ArrayList<>();
        String sql = "SELECT * FROM transacoes_pix WHERE cpf_origem = ? OR cpf_destino = ? ORDER BY timestamp DESC LIMIT 50";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cpf);
            ps.setString(2, cpf);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String origem = rs.getString("cpf_origem");
                String destino = rs.getString("cpf_destino");
                double valor = rs.getDouble("valor");
                String timestamp = rs.getString("timestamp");
                String chavePixOrigem = rs.getString("chave_pix_origem");
                String chavePixDestino = rs.getString("chave_pix_destino");
                
                String transacao = String.format(
                    "{\"de\": \"%s (%s)\", \"para\": \"%s (%s)\", \"valor\": %.2f, \"data\": \"%s\"}",
                    chavePixOrigem, origem, chavePixDestino, destino, valor, timestamp
                );
                transacoes.add(transacao);
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar transações Pix: {}", e.getMessage(), e);
        }
        
        return transacoes;
    }
    
    /**
     * Conta o número total de chaves Pix ativas
     * @return Número de chaves Pix
     */
    public int countChavesPix() {
        String sql = "SELECT COUNT(*) FROM chaves_pix WHERE ativa = 1";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Erro ao contar chaves Pix", e);
        }
        
        return 0;
    }
    
    /**
     * Conta o número total de transações Pix
     * @return Número de transações Pix
     */
    public int countTransacoesPix() {
        String sql = "SELECT COUNT(*) FROM transacoes_pix";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Erro ao contar transações Pix", e);
        }
        
        return 0;
    }
}