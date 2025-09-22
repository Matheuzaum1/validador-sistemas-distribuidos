-- Script SQL para popular o banco de dados com usuários de teste
-- Este arquivo é usado automaticamente pelo DatabaseManager

-- Criação da tabela (caso não exista)
CREATE TABLE IF NOT EXISTS usuarios (
    cpf TEXT PRIMARY KEY,
    nome TEXT NOT NULL,
    senha TEXT NOT NULL,
    saldo REAL DEFAULT 0.0,
    criado_em TEXT NOT NULL,
    atualizado_em TEXT NOT NULL
);

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

-- Para adicionar novos usuários manualmente, use o formato:
-- INSERT INTO usuarios (cpf, nome, senha, criado_em, atualizado_em) 
-- VALUES ('000.000.000-00', 'Nome Completo', 'senha_hash_bcrypt', datetime('now'), datetime('now'));

-- Exemplo de consultas úteis:

-- Listar todos os usuários
-- SELECT cpf, nome, saldo, criado_em FROM usuarios ORDER BY nome;

-- Buscar usuário por CPF
-- SELECT * FROM usuarios WHERE cpf = '123.456.789-01';

-- Atualizar saldo de um usuário
-- UPDATE usuarios SET saldo = 1000.00, atualizado_em = datetime('now') WHERE cpf = '123.456.789-01';

-- Contar total de usuários
-- SELECT COUNT(*) as total_usuarios FROM usuarios;