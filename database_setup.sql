-- Script SQL para popular o banco de dados com usuários de teste
-- Este arquivo é usado automaticamente pelo DatabaseManager
-- Suporta operações bancárias tradicionais e sistema Pix

-- ============================================================================
-- TABELAS PRINCIPAIS
-- ============================================================================

-- Tabela de Usuários (com suporte a Pix)
CREATE TABLE IF NOT EXISTS usuarios (
    cpf TEXT PRIMARY KEY,
    nome TEXT NOT NULL,
    senha TEXT NOT NULL,
    saldo REAL DEFAULT 0.0,
    criado_em TEXT NOT NULL,
    atualizado_em TEXT NOT NULL,
    conta_ativa INTEGER DEFAULT 1
);

-- Tabela de Transações Tradicionais (transferências e depósitos)
CREATE TABLE IF NOT EXISTS transacoes (
    id INTEGER PRIMARY KEY,
    cpf_origem TEXT,
    cpf_destino TEXT,
    valor REAL NOT NULL,
    timestamp TEXT NOT NULL,
    tipo_transacao TEXT DEFAULT 'transferencia',
    FOREIGN KEY (cpf_origem) REFERENCES usuarios(cpf),
    FOREIGN KEY (cpf_destino) REFERENCES usuarios(cpf)
);

-- ============================================================================
-- TABELAS PIX
-- ============================================================================

-- Tabela de Chaves Pix (Email, Telefone, CNPJ, CPF, Chave Aleatória)
CREATE TABLE IF NOT EXISTS chaves_pix (
    id INTEGER PRIMARY KEY,
    cpf_dono TEXT NOT NULL,
    tipo_chave TEXT NOT NULL,
    valor_chave TEXT NOT NULL,
    criada_em TEXT NOT NULL,
    ativa INTEGER DEFAULT 1,
    FOREIGN KEY (cpf_dono) REFERENCES usuarios(cpf),
    UNIQUE(valor_chave)
);

-- Tabela de Transações Pix (rastreabilidade)
CREATE TABLE IF NOT EXISTS transacoes_pix (
    id INTEGER PRIMARY KEY,
    chave_pix_origem TEXT NOT NULL,
    chave_pix_destino TEXT NOT NULL,
    cpf_origem TEXT NOT NULL,
    cpf_destino TEXT NOT NULL,
    valor REAL NOT NULL,
    timestamp TEXT NOT NULL,
    status TEXT DEFAULT 'sucesso',
    identificador_pix TEXT UNIQUE,
    FOREIGN KEY (cpf_origem) REFERENCES usuarios(cpf),
    FOREIGN KEY (cpf_destino) REFERENCES usuarios(cpf)
);

-- ============================================================================
-- ÍNDICES PARA PERFORMANCE
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_transacoes_origem ON transacoes(cpf_origem);
CREATE INDEX IF NOT EXISTS idx_transacoes_destino ON transacoes(cpf_destino);
CREATE INDEX IF NOT EXISTS idx_transacoes_timestamp ON transacoes(timestamp);

CREATE INDEX IF NOT EXISTS idx_chaves_pix_dono ON chaves_pix(cpf_dono);
CREATE INDEX IF NOT EXISTS idx_chaves_pix_valor ON chaves_pix(valor_chave);
CREATE INDEX IF NOT EXISTS idx_chaves_pix_tipo ON chaves_pix(tipo_chave);

CREATE INDEX IF NOT EXISTS idx_transacoes_pix_origem ON transacoes_pix(cpf_origem);
CREATE INDEX IF NOT EXISTS idx_transacoes_pix_destino ON transacoes_pix(cpf_destino);
CREATE INDEX IF NOT EXISTS idx_transacoes_pix_timestamp ON transacoes_pix(timestamp);

-- ============================================================================
-- DADOS DE TESTE
-- ============================================================================

-- Inserção de usuários de teste
-- Nota: As senhas serão criptografadas automaticamente pelo sistema

-- Usuário 1: João Silva
-- CPF: 123.456.789-01, Senha: 123456

-- Usuário 2: Maria Santos  
-- CPF: 987.654.321-02, Senha: 654321

-- Usuário 3: Pedro Oliveira
-- CPF: 111.222.333-44, Senha: password

-- Usuário 4: Ana Costa
-- CPF: 555.666.777-88, Senha: 123abc

-- Os usuários são inseridos automaticamente pelo DatabaseManager.java
-- na primeira execução do sistema, se a tabela estiver vazia.

-- ============================================================================
-- EXEMPLOS DE CHAVES PIX
-- ============================================================================
-- Cada usuário poderá ter múltiplas chaves Pix de diferentes tipos:
-- - CPF: 12345678901 (formato sem formatação)
-- - Email: usuario@email.com
-- - Telefone: +5511999999999
-- - CNPJ: 12345678000195
-- - Chave Aleatória: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

-- ============================================================================
-- EXEMPLOS DE CONSULTAS ÚTEIS
-- ============================================================================

-- Listar todos os usuários com suas chaves Pix
-- SELECT u.cpf, u.nome, u.saldo, COUNT(cp.id) as num_chaves_pix
-- FROM usuarios u
-- LEFT JOIN chaves_pix cp ON u.cpf = cp.cpf_dono AND cp.ativa = 1
-- GROUP BY u.cpf
-- ORDER BY u.nome;

-- Buscar chaves Pix de um usuário
-- SELECT * FROM chaves_pix 
-- WHERE cpf_dono = '123.456.789-01' AND ativa = 1;

-- Buscar usuário pelo valor da chave Pix
-- SELECT u.*, cp.tipo_chave, cp.valor_chave
-- FROM usuarios u
-- INNER JOIN chaves_pix cp ON u.cpf = cp.cpf_dono
-- WHERE cp.valor_chave = 'joao@email.com' AND cp.ativa = 1;

-- Listar transações Pix de um usuário
-- SELECT * FROM transacoes_pix
-- WHERE cpf_origem = '123.456.789-01' OR cpf_destino = '123.456.789-01'
-- ORDER BY timestamp DESC;

-- Resumo de transações por tipo
-- SELECT 
--     DATE(timestamp) as data,
--     COUNT(*) as total_transacoes,
--     SUM(CASE WHEN tipo_transacao = 'transferencia' THEN 1 ELSE 0 END) as transferencias,
--     SUM(CASE WHEN tipo_transacao = 'deposito' THEN 1 ELSE 0 END) as depositos,
--     SUM(valor) as valor_total
-- FROM transacoes
-- GROUP BY DATE(timestamp)
-- ORDER BY data DESC;