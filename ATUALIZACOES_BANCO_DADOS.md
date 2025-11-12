# Atualização do Banco de Dados - Fase NewPix

## 📋 Resumo das Alterações

O banco de dados foi atualizado para suportar a **nova fase do projeto: Sistema Pix**. Foram adicionadas novas tabelas, campos e índices para gerenciar chaves Pix e suas transações.

---

## 🗂️ Estrutura do Banco de Dados

### 1. Tabela `usuarios` (MODIFICADA)
**Adições:**
- `conta_ativa` (INTEGER DEFAULT 1) - Status da conta do usuário

**Campos existentes:**
- `cpf` (TEXT PRIMARY KEY) - Identificador único
- `nome` (TEXT NOT NULL) - Nome completo
- `senha` (TEXT NOT NULL) - Senha criptografada com BCrypt
- `saldo` (REAL DEFAULT 0.0) - Saldo em reais
- `criado_em` (TEXT NOT NULL) - Data de criação
- `atualizado_em` (TEXT NOT NULL) - Data da última atualização

---

### 2. Tabela `transacoes` (MODIFICADA)
**Adições:**
- `tipo_transacao` (TEXT DEFAULT 'transferencia') - Tipo: 'transferencia' ou 'deposito'

**Campos existentes:**
- `id` (INTEGER PRIMARY KEY) - Identificador único
- `cpf_origem` (TEXT) - CPF do remetente
- `cpf_destino` (TEXT) - CPF do destinatário
- `valor` (REAL NOT NULL) - Valor da transação
- `timestamp` (TEXT NOT NULL) - Data e hora

---

### 3. Tabela `chaves_pix` (NOVA)
Armazena as chaves Pix registradas pelos usuários.

```sql
CREATE TABLE chaves_pix (
    id INTEGER PRIMARY KEY,
    cpf_dono TEXT NOT NULL,
    tipo_chave TEXT NOT NULL,
    valor_chave TEXT NOT NULL UNIQUE,
    criada_em TEXT NOT NULL,
    ativa INTEGER DEFAULT 1,
    FOREIGN KEY (cpf_dono) REFERENCES usuarios(cpf)
);
```

**Campos:**
- `id` - Identificador único
- `cpf_dono` - CPF do proprietário da chave
- `tipo_chave` - Tipo de chave: `cpf`, `email`, `telefone`, `cnpj`, `aleatoria`
- `valor_chave` - Valor da chave (único)
- `criada_em` - Data de criação
- `ativa` - Status da chave (1 = ativa, 0 = desativada)

**Índices:**
- `idx_chaves_pix_dono` - Para buscar rápido por CPF do dono
- `idx_chaves_pix_valor` - Para buscar rápido pela chave
- `idx_chaves_pix_tipo` - Para filtrar por tipo de chave

---

### 4. Tabela `transacoes_pix` (NOVA)
Registra todas as transações realizadas via Pix com rastreabilidade completa.

```sql
CREATE TABLE transacoes_pix (
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
```

**Campos:**
- `id` - Identificador único
- `chave_pix_origem` - Chave Pix do remetente
- `chave_pix_destino` - Chave Pix do destinatário
- `cpf_origem` - CPF do remetente
- `cpf_destino` - CPF do destinatário
- `valor` - Valor transferido
- `timestamp` - Data e hora da transação
- `status` - Status: 'sucesso', 'pendente', 'falha'
- `identificador_pix` - Identificador único da transação (para rastreamento)

**Índices:**
- `idx_transacoes_pix_origem` - Para buscar por CPF do remetente
- `idx_transacoes_pix_destino` - Para buscar por CPF do destinatário
- `idx_transacoes_pix_timestamp` - Para ordenar por data

---

## 🔧 Novos Métodos no DatabaseManager

### Gerenciamento de Chaves Pix

1. **`registrarChavePix(String cpfDono, String tipoChave, String valorChave)`**
   - Registra uma nova chave Pix
   - Retorna `true` se sucesso

2. **`buscarCpfPorChavePix(String valorChave)`**
   - Busca o CPF do proprietário de uma chave Pix
   - Retorna o CPF ou `null` se não encontrado

3. **`listarChavesPix(String cpfDono)`**
   - Lista todas as chaves Pix ativas de um usuário
   - Retorna uma `List<String>` com as chaves

4. **`desativarChavePix(String valorChave)`**
   - Desativa uma chave Pix
   - Retorna `true` se sucesso

5. **`countChavesPix()`**
   - Conta o número total de chaves Pix ativas

### Gerenciamento de Transações Pix

6. **`registrarTransacaoPix(String chavePixOrigem, String chavePixDestino, String cpfOrigem, String cpfDestino, double valor, String identificadorPix)`**
   - Registra uma nova transação Pix
   - Retorna `true` se sucesso

7. **`listarTransacoesPix(String cpf)`**
   - Lista as transações Pix de um usuário (últimas 50)
   - Retorna uma `List<String>` em formato JSON

8. **`countTransacoesPix()`**
   - Conta o número total de transações Pix

---

## 📊 Exemplo de Uso

```java
DatabaseManager db = DatabaseManager.getInstance();

// Registrar uma chave Pix
db.registrarChavePix("123.456.789-01", "email", "joao@email.com");
db.registrarChavePix("123.456.789-01", "telefone", "+5511999999999");

// Buscar CPF por chave Pix
String cpf = db.buscarCpfPorChavePix("joao@email.com");

// Listar chaves do usuário
List<String> chaves = db.listarChavesPix("123.456.789-01");

// Registrar transação Pix
db.registrarTransacaoPix(
    "joao@email.com",
    "maria@email.com",
    "123.456.789-01",
    "987.654.321-02",
    150.00,
    "UUID-unico-da-transacao"
);

// Listar transações Pix do usuário
List<String> transacoes = db.listarTransacoesPix("123.456.789-01");
```

---

## 🔄 Migração de Dados Existentes

A migração é **automática**:
- Ao iniciar o sistema, o `DatabaseManager` verifica se as tabelas existem
- Se não existirem, cria automaticamente as novas tabelas
- Dados existentes de usuários e transações não são afetados
- As novas tabelas podem ser preenchidas conforme os usuários registram chaves Pix

---

## 📈 Melhorias de Performance

Foram adicionados índices nas seguintes colunas:

| Tabela | Coluna(s) | Índice |
|--------|-----------|--------|
| `transacoes` | `cpf_origem`, `cpf_destino`, `timestamp` | Busca rápida de transações |
| `chaves_pix` | `cpf_dono`, `valor_chave`, `tipo_chave` | Busca rápida de chaves |
| `transacoes_pix` | `cpf_origem`, `cpf_destino`, `timestamp` | Busca rápida de transações Pix |

---

## 🔐 Considerações de Segurança

1. **Chaves Únicas**: O campo `valor_chave` em `chaves_pix` é UNIQUE, evitando duplicatas
2. **Chaves Estrangeiras**: Referências de integridade entre tabelas
3. **Senhas**: Continua usando BCrypt para criptografia
4. **Auditoria**: Todas as transações são registradas com timestamp

---

## ✅ Verificação

Para verificar se as tabelas foram criadas corretamente:

```sql
-- Ver todas as tabelas
SELECT name FROM sqlite_master WHERE type='table';

-- Ver estrutura de uma tabela
PRAGMA table_info(chaves_pix);
PRAGMA table_info(transacoes_pix);

-- Ver índices
SELECT name FROM sqlite_master WHERE type='index';
```

---

## 📝 Arquivo de Setup

O arquivo `database_setup.sql` foi atualizado com:
- Definição completa das tabelas
- Índices para performance
- Exemplos de consultas úteis
- Documentação em SQL

---

## 🚀 Próximos Passos

1. Implementar a GUI de registro de chaves Pix no client
2. Implementar a interface de transferência por Pix
3. Adicionar validação de chaves Pix (email, telefone, etc)
4. Criar endpoints no servidor para operações Pix
5. Implementar confirmar/validar chaves Pix antes de usar

---

**Data da Atualização:** 12 de novembro de 2025  
**Versão:** 1.0.0 - NewPix  
**Status:** ✅ Compilação bem-sucedida
