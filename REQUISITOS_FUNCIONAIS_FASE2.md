# Análise de Cobertura de Requisitos Funcionais - Fase 2

**Data de Análise:** 5 de novembro de 2025  
**Versão do Projeto:** newpix-teste  
**Status:** ✅ TODOS OS REQUISITOS COBERTOS

---

## Resumo Executivo

Este documento valida a implementação de todos os 7 requisitos funcionais solicitados para a Fase 2 do projeto. Cada requisito foi mapeado para os componentes específicos do código responsáveis por sua implementação, garantindo funcionalidade e tratamento de erros adequados.

---

## 1. ✅ Só Usuário Logado Pode Fazer Transações

### Requisito
Validar que transações (transferências e depósitos) só podem ser realizadas por usuários autenticados.

### Implementação

#### Servidor - `ServerHandler.java`

**handleTransfer() - Linhas 327-362:**
```java
private String handleTransfer(String message) {
    // ... extração de dados ...
    String token = node.get("token").asText();
    String cpfOrigem = TokenManager.getCpfFromToken(token);
    
    if (cpfOrigem == null) {  // ← VALIDAÇÃO DE AUTENTICAÇÃO
        return MessageBuilder.buildErrorResponse("transacao_criar", 
            "Token inválido ou expirado");
    }
    // ... resto da transação ...
}
```

**handleDeposit() - Linhas 364-388:**
```java
private String handleDeposit(String message) {
    // ... extração de dados ...
    String token = node.get("token").asText();
    String cpf = TokenManager.getCpfFromToken(token);
    
    if (cpf == null) {  // ← VALIDAÇÃO DE AUTENTICAÇÃO
        return MessageBuilder.buildErrorResponse("transacao_depositar", 
            "Token inválido ou expirado");
    }
    // ... resto da transação ...
}
```

#### Fluxo de Autenticação
1. **Login** (`handleLogin()`) - Gera token válido via `TokenManager.generateToken(cpf)`
2. **Transação** - Verifica token via `TokenManager.getCpfFromToken(token)`
3. **Validação** - Se retorna `null`, nega acesso com mensagem de erro apropriada

#### Cliente - `ClientConnection.java`
- Métodos `transfer()` e `deposit()` executam `sendMessage()` que valida a mensagem antes de enviar
- `Validator.validateClient()` assegura que token está presente na mensagem

#### Tratamento de Erros
| Cenário | Resposta do Servidor |
|---------|---------------------|
| Token inválido | `{"operacao":"transacao_criar","status":false,"info":"Token inválido ou expirado"}` |
| Token expirado | `{"operacao":"depositar","status":false,"info":"Token inválido ou expirado"}` |
| Sem autenticação | Erro na validação do Validator |

---

## 2. ✅ Criação de Extratos da Conta

### Requisito
O sistema deve criar registros de extratos automaticamente para cada transação realizada.

### Implementação

#### Banco de Dados - `DatabaseManager.java`

**Schema - Linhas 42-48:**
```sql
CREATE TABLE IF NOT EXISTS transacoes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cpf_origem TEXT,
    cpf_destino TEXT,
    valor REAL NOT NULL,
    timestamp TEXT NOT NULL
)
```

#### Criação Automática de Extratos

**performAtomicTransfer() - Linhas 178-257:**
```java
private boolean performAtomicTransfer(String cpfOrigem, String cpfDestino, double valor) {
    // Dentro de transação ACID:
    
    // 1. Valida saldo de origem (se houver)
    if (cpfOrigem != null) {
        // Verifica se cpfOrigem existe e tem saldo suficiente
    }
    
    // 2. Debita da conta de origem
    if (cpfOrigem != null) {
        double newOrigem = origemSaldo - valor;
        // UPDATE usuarios SET saldo = ?, atualizado_em = ? WHERE cpf = ?
    }
    
    // 3. Credita na conta de destino
    double newDestino = destinoSaldo + valor;
    // UPDATE usuarios SET saldo = ?, atualizado_em = ? WHERE cpf = ?
    
    // 4. ← CRIA REGISTRO DE EXTRATO AUTOMATICAMENTE
    String insertTransSql = "INSERT INTO transacoes (cpf_origem, cpf_destino, valor, timestamp) VALUES (?, ?, ?, ?)";
    psTrans.setString(1, cpfOrigem);
    psTrans.setString(2, cpfDestino);
    psTrans.setDouble(3, valor);
    psTrans.setString(4, java.time.LocalDateTime.now().toString());
    psTrans.executeUpdate();
    
    conn.commit(); // ← GARANTE ATOMICIDADE
    return true;
}
```

#### Tipos de Transações Registradas

| Tipo | Descrição | Método |
|------|-----------|--------|
| Transferência | CPF origem → CPF destino | `createTransfer()` |
| Depósito | null → CPF destino | `createDeposit()` |

#### Campos Registrados
- ✅ `id` - Identificador único (auto-incrementado)
- ✅ `cpf_origem` - Conta que enviou (null para depósitos)
- ✅ `cpf_destino` - Conta que recebeu
- ✅ `valor` - Valor da transação
- ✅ `timestamp` - Data/hora exata da transação

#### Exemplos de Extratos Criados
```
Depósito: criar_Deposit("123.456.789-01", 500.00)
  → INSERT INTO transacoes: (cpf_origem=null, cpf_destino=123.456.789-01, valor=500.00, timestamp=2025-11-05T...)

Transferência: createTransfer("123.456.789-01", "987.654.321-02", 120.00)
  → INSERT INTO transacoes: (cpf_origem=123.456.789-01, cpf_destino=987.654.321-02, valor=120.00, timestamp=2025-11-05T...)
```

---

## 3. ✅ Pedido de Extrato da Conta

### Requisito
Cliente pode solicitar extratos filtrados por período (data inicial e data final).

### Implementação

#### Operação: `transacao_ler`

**Servidor - handleTransacaoLer() - Linhas 390-445:**
```java
private String handleTransacaoLer(String message) {
    try {
        // 1. Extrai token e valida autenticação
        String token = node.get("token").asText();
        String cpf = TokenManager.getCpfFromToken(token);
        if (cpf == null) {
            return MessageBuilder.buildErrorResponse("transacao_ler", 
                "Token inválido ou expirado");
        }

        // 2. Extrai datas
        String dataInicial = node.has("data_inicial") ? node.get("data_inicial").asText() : null;
        String dataFinal = node.has("data_final") ? node.get("data_final").asText() : null;

        if (dataInicial == null || dataFinal == null) {
            return MessageBuilder.buildErrorResponse("transacao_ler", 
                "data_inicial e data_final são obrigatórias");
        }

        // 3. Valida formato ISO 8601 UTC
        java.time.Instant inicio = java.time.Instant.parse(dataInicial);
        java.time.Instant fim = java.time.Instant.parse(dataFinal);

        // 4. Valida intervalo (máximo 31 dias)
        long days = java.time.Duration.between(inicio, fim).toDays();
        if (days < 0 || days > 31) {
            return MessageBuilder.buildErrorResponse("transacao_ler", 
                "Intervalo de data inválido (máximo 31 dias)");
        }

        // 5. Busca transações do usuário no período
        java.util.List<com.distribuidos.common.Transacao> filtered = new java.util.ArrayList<>();
        for (com.distribuidos.common.Transacao t : all) {
            // Filtra apenas transações onde o usuário é origem OU destino
            boolean isUserTransaction = cpf.equals(t.getCpfOrigem()) || cpf.equals(t.getCpfDestino());
            if (!isUserTransaction) continue;
            
            java.time.Instant ts = t.getTimestamp().atZone(java.time.ZoneId.systemDefault()).toInstant();
            if (!ts.isBefore(inicio) && !ts.isAfter(fim)) {
                filtered.add(t);
            }
        }

        // 6. Retorna resultado
        return MessageBuilder.buildTransacoesResponse("transacao_ler", 
            "Transações recuperadas com sucesso.", filtered);
    } catch (Exception e) {
        return MessageBuilder.buildErrorResponse("transacao_ler", 
            "Erro interno ao ler transações");
    }
}
```

#### Cliente - `ClientConnection.java`
```java
// Método de conveniência para ler transações
public String readTransactions(String token, String dataInicial, String dataFinal) {
    String message = MessageBuilder.buildReadTransacoesMessage(token, dataInicial, dataFinal);
    return sendMessage(message);
}
```

#### Validações Implementadas
| Validação | Descrição | Tratamento |
|-----------|-----------|-----------|
| Autenticação | Token deve ser válido | Erro se null |
| Datas obrigatórias | data_inicial e data_final | Erro se null |
| Formato ISO 8601 | `yyyy-MM-dd'T'HH:mm:ss'Z'` | Erro se inválido |
| Intervalo máximo | 31 dias | Erro se > 31 dias ou negativo |
| Filtro de usuário | Apenas transações do usuário logado | Aplicado na filtragem |

#### Exemplo de Resposta
```json
{
  "operacao": "transacao_ler",
  "status": true,
  "info": "Transações recuperadas com sucesso.",
  "transacoes": [
    {
      "valor_enviado": 120.00,
      "usuario_enviador": {
        "nome": "João Silva",
        "cpf": "123.456.789-01"
      },
      "usuario_recebedor": {
        "nome": "Maria Santos",
        "cpf": "987.654.321-02"
      },
      "criado_em": "2025-11-05T10:30:45Z",
      "atualizado_em": "2025-11-05T10:30:45Z"
    }
  ]
}
```

---

## 4. ✅ CR de Transações

### Requisito
Sistema deve suportar Create (C) e Read (R) de transações (sem update/delete).

### Implementação - Create (Create)

#### Transferência - `transacao_criar`

**Servidor - handleTransfer():**
```java
private String handleTransfer(String message) {
    // Valida token (autenticação)
    String cpfOrigem = TokenManager.getCpfFromToken(token);
    if (cpfOrigem == null) {
        return MessageBuilder.buildErrorResponse("transacao_criar", "Token inválido ou expirado");
    }

    // Valida CPF de destino existe
    if (!dbManager.userExists(cpfDestino)) {
        return MessageBuilder.buildErrorResponse("transacao_transferir", 
            "Usuário destino não encontrado");
    }

    // Executa transferência atomicamente
    boolean ok = dbManager.createTransfer(cpfOrigem, cpfDestino, valor);
    if (ok) {
        serverGUI.updateDatabaseView();
        return MessageBuilder.buildSuccessResponse("transacao_criar", 
            "Transferência realizada com sucesso");
    } else {
        return MessageBuilder.buildErrorResponse("transacao_criar", 
            "Erro ao realizar transferência (saldo insuficiente ou usuário inválido)");
    }
}
```

**Validações:**
- ✅ Token válido (autenticado)
- ✅ CPF destino existe
- ✅ Saldo suficiente (verificado em `performAtomicTransfer()`)
- ✅ Transação atômica (ACID)

#### Read (Leitura) - `transacao_ler`

**Implementação completa em seção anterior (Requisito 3)**

---

## 5. ✅ Depósito na Conta

### Requisito
Sistema deve suportar depósitos que aumentam o saldo da conta.

### Implementação

#### Operação: `depositar`

**Servidor - handleDeposit() - Linhas 364-388:**
```java
private String handleDeposit(String message) {
    try {
        // 1. Valida autenticação
        String token = node.get("token").asText();
        String cpf = TokenManager.getCpfFromToken(token);
        if (cpf == null) {
            return MessageBuilder.buildErrorResponse("transacao_depositar", 
                "Token inválido ou expirado");
        }

        // 2. Extrai valor do depósito
        double valor = node.has("valor_enviado") ? 
            node.get("valor_enviado").asDouble() : 
            node.get("valor").asDouble();

        // 3. Executa depósito
        boolean ok = dbManager.createDeposit(cpf, valor);
        if (ok) {
            serverGUI.updateDatabaseView();
            serverGUI.addLogMessage(String.format("Depósito: %s : R$ %.2f", cpf, valor));
            return MessageBuilder.buildSuccessResponse("depositar", 
                "Depósito realizado com sucesso");
        } else {
            return MessageBuilder.buildErrorResponse("depositar", 
                "Erro ao realizar depósito");
        }
    } catch (Exception e) {
        logger.error("Erro ao processar depósito", e);
        return MessageBuilder.buildErrorResponse("transacao_depositar", 
            "Erro interno ao processar depósito");
    }
}
```

#### Banco de Dados - `DatabaseManager.createDeposit()`
```java
public boolean createDeposit(String cpfDestino, double valor) {
    return performAtomicTransfer(null, cpfDestino, valor);
}
```

**Fluxo Atomicamente Garantido:**
1. ✅ Verifica se CPF destino existe
2. ✅ Busca saldo atual
3. ✅ Calcula novo saldo: `novoSaldo = saldoAtual + valor`
4. ✅ Atualiza saldo na tabela `usuarios`
5. ✅ Cria registro em `transacoes` com `cpf_origem = null`
6. ✅ Realiza COMMIT para garantir atomicidade

#### Exemplo de Depósito
```
ANTES: saldo = 500.00
DEPÓSITO: +250.00
DEPOIS: saldo = 750.00
REGISTRO: INSERT INTO transacoes (cpf_origem=null, cpf_destino=123.456.789-01, valor=250.00, ...)
```

#### Cliente - `ClientConnection.deposit()`
```java
public String deposit(String token, double valor) {
    String message = MessageBuilder.buildDepositMessage(token, valor);
    return sendMessage(message);
}
```

---

## 6. ✅ Cliente e Servidor Enviam Corretamente Mensagens de Erro (Cadastro)

### Requisito
Implementar tratamento robusto de erros para operações de cadastro (criar, atualizar, deletar usuário).

### Operação 1: `usuario_criar`

#### Servidor - handleCreateUser()

**Validações:**
```java
if (dbManager.userExists(cpf)) {
    return MessageBuilder.buildErrorResponse("usuario_criar", 
        "Usuário já existe com este CPF");  // ← ERRO: CPF duplicado
}

if (!dbManager.createUser(cpf, nome, senha)) {
    return MessageBuilder.buildErrorResponse("usuario_criar", 
        "Erro ao criar usuário");  // ← ERRO: Falha no BD
}
```

**Tratamento de Exceções:**
```java
catch (Exception e) {
    logger.error("Erro ao criar usuário", e);
    return MessageBuilder.buildErrorResponse("usuario_criar", 
        "Erro interno ao criar usuário");
}
```

**Mensagens de Erro Possíveis:**
| Cenário | Mensagem Enviada |
|---------|-----------------|
| CPF já existe | `"Usuário já existe com este CPF"` |
| Erro no banco | `"Erro ao criar usuário"` |
| Exceção não tratada | `"Erro interno ao criar usuário"` |

#### Cliente - ClientConnection.createUser()
```java
public String createUser(String nome, String cpf, String senha) {
    String message = MessageBuilder.buildCreateUserMessage(nome, cpf, senha);
    return sendMessage(message);  // → Valida com Validator.validateClient()
}
```

**Validação Cliente:**
- ✅ `Validator.validateClient()` - Verifica presença/tipo de campos
- ✅ Nome entre 6-120 caracteres
- ✅ CPF formato `000.000.000-00`
- ✅ Senha entre 6-120 caracteres

---

### Operação 2: `usuario_atualizar`

#### Servidor - handleUpdateUser()

**Validações:**
```java
String cpf = TokenManager.getCpfFromToken(token);
if (cpf == null) {
    return MessageBuilder.buildErrorResponse("usuario_atualizar", 
        "Token inválido ou expirado");  // ← ERRO: Auth
}

com.fasterxml.jackson.databind.JsonNode usuarioNode = node.get("usuario");
String nome = usuarioNode.has("nome") ? usuarioNode.get("nome").asText() : null;
String senha = usuarioNode.has("senha") ? usuarioNode.get("senha").asText() : null;

if (dbManager.updateUser(cpf, nome, senha)) {
    // ... sucesso
} else {
    return MessageBuilder.buildErrorResponse("usuario_atualizar", 
        "Erro ao atualizar usuário");  // ← ERRO: BD
}
```

**Mensagens de Erro Possíveis:**
| Cenário | Mensagem Enviada |
|---------|-----------------|
| Token inválido | `"Token inválido ou expirado"` |
| Nenhum campo atualizar | Erro na validação (cliente) |
| Erro no banco | `"Erro ao atualizar usuário"` |

---

### Operação 3: `usuario_deletar`

#### Servidor - handleDeleteUser()

**Validações:**
```java
String cpf = TokenManager.getCpfFromToken(token);
if (cpf == null) {
    return MessageBuilder.buildErrorResponse("usuario_deletar", 
        "Token inválido ou expirado");  // ← ERRO: Auth
}

if (dbManager.deleteUser(cpf)) {
    TokenManager.removeToken(token);
    // ... atualiza UI
    return MessageBuilder.buildSuccessResponse("usuario_deletar", 
        "Usuário deletado com sucesso");
} else {
    return MessageBuilder.buildErrorResponse("usuario_deletar", 
        "Erro ao deletar usuário");  // ← ERRO: BD
}
```

**Mensagens de Erro Possíveis:**
| Cenário | Mensagem Enviada |
|---------|-----------------|
| Token inválido | `"Token inválido ou expirado"` |
| Erro no banco | `"Erro ao deletar usuário"` |

---

#### Validação de Mensagens - Validator.java

**Chaves Esperadas Verificadas:**
```java
EXPECTED_CLIENT_KEYS.put(RulesEnum.USUARIO_CRIAR, 
    Set.of("operacao", "nome", "cpf", "senha"));

EXPECTED_CLIENT_KEYS.put(RulesEnum.USUARIO_ATUALIZAR, 
    Set.of("operacao", "token", "usuario"));

EXPECTED_CLIENT_KEYS.put(RulesEnum.USUARIO_DELETAR, 
    Set.of("operacao", "token"));
```

**Validações de Formato:**
- ✅ CPF: `\d{3}\.\d{3}\.\d{3}-\d{2}`
- ✅ Senha: 6-120 caracteres
- ✅ Nome: 6-120 caracteres
- ✅ Token: 3-200 caracteres

---

## 7. ✅ Cliente e Servidor Enviam Corretamente Mensagens de Erro (Login)

### Requisito
Implementar tratamento robusto de erros para operações de autenticação (login/logout).

### Operação 1: `usuario_login`

#### Servidor - handleLogin()

**Validações:**
```java
String cpf = node.get("cpf").asText();
String senha = node.get("senha").asText();

if (dbManager.authenticateUser(cpf, senha)) {
    // ... sucesso, gera token
    return MessageBuilder.buildSuccessResponse("usuario_login", 
        "Login realizado com sucesso", token);
} else {
    return MessageBuilder.buildErrorResponse("usuario_login", 
        "CPF ou senha inválidos");  // ← ERRO: Falha auth
}
```

**Fluxo de Autenticação:**
1. ✅ Busca usuário por CPF
2. ✅ Valida senha com BCrypt.checkpw()
3. ✅ Gera token se autenticação bem-sucedida
4. ✅ Retorna erro se CPF não existe ou senha incorreta

**Mensagens de Erro Possíveis:**
| Cenário | Mensagem Enviada |
|---------|-----------------|
| CPF não existe | `"CPF ou senha inválidos"` |
| Senha incorreta | `"CPF ou senha inválidos"` |
| Erro no banco | `"Erro interno no login"` |
| Exceção não tratada | `"Erro interno no login"` |

**Tratamento de Exceções:**
```java
catch (Exception e) {
    logger.error("Erro no login", e);
    return MessageBuilder.buildErrorResponse("usuario_login", 
        "Erro interno no login");
}
```

#### Cliente - ClientConnection.login()

**Validação Cliente:**
```java
public String login(String cpf, String senha) {
    String message = MessageBuilder.buildLoginMessage(cpf, senha);
    return sendMessage(message);
}
```

Validações via `Validator.validateClient()`:
- ✅ CPF formato `000.000.000-00`
- ✅ Senha entre 6-120 caracteres
- ✅ Ambos os campos obrigatórios

---

### Operação 2: `usuario_logout`

#### Servidor - handleLogout()

**Validações:**
```java
String token = node.get("token").asText();

if (TokenManager.isValidToken(token)) {
    TokenManager.removeToken(token);
    
    // Remove informações do usuário do cliente
    clientInfo.setCpfUsuario(null);
    clientInfo.setNomeUsuario(null);
    serverGUI.updateConnectedClients();
    
    return MessageBuilder.buildSuccessResponse("usuario_logout", 
        "Logout realizado com sucesso");
} else {
    return MessageBuilder.buildErrorResponse("usuario_logout", 
        "Token inválido ou expirado");  // ← ERRO: Token inválido
}
```

**Mensagens de Erro Possíveis:**
| Cenário | Mensagem Enviada |
|---------|-----------------|
| Token inválido | `"Token inválido ou expirado"` |
| Token expirado | `"Token inválido ou expirado"` |
| Erro no banco | `"Erro interno no logout"` |

**Tratamento de Exceções:**
```java
catch (Exception e) {
    logger.error("Erro no logout", e);
    return MessageBuilder.buildErrorResponse("usuario_logout", 
        "Erro interno no logout");
}
```

#### Cliente - ClientConnection.logout()

**Validação Cliente:**
```java
public String logout(String token) {
    String message = MessageBuilder.buildLogoutMessage(token);
    return sendMessage(message);
}
```

Validações via `Validator.validateClient()`:
- ✅ Token entre 3-200 caracteres
- ✅ Token obrigatório

---

### Tratamento de Erro de Validação - ClientConnection

**Tratamento de Erros de Validação:**
```java
catch (Exception e) {
    // Validation errors from Validator.java are surfaced here
    clientGUI.addLogMessage("Erro de validação: " + e.getMessage());

    String lower = e.getMessage().toLowerCase();
    if (lower.contains("token")) {
        // Show friendly dialog for token errors
        javax.swing.JOptionPane.showMessageDialog(null,
            "Token inválido — por favor reconecte ou efetue login novamente.",
            "Token inválido",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        
        return MessageBuilder.buildErrorResponse("validation_error",
            "Token inválido - reconecte ou faça login novamente");
    }
    
    throw new RuntimeException("Erro de validação: " + e.getMessage());
}
```

---

## Matriz de Cobertura de Requisitos

| Req # | Requisito | Implementado | Validação | Tratamento de Erro | Status |
|-------|-----------|-------------|-----------|-------------------|--------|
| 1 | Só usuário logado faz transações | ✅ ServerHandler | Token verificado | "Token inválido" | ✅ |
| 2 | Criação de extratos | ✅ DatabaseManager | Transação ACID | Rollback automático | ✅ |
| 3 | Pedido de extrato | ✅ handleTransacaoLer() | Filtro data 31d | 5 validações | ✅ |
| 4 | CR de Transações | ✅ handleTransfer() + handleTransacaoLer() | Autenticação | 3 validações | ✅ |
| 5 | Depósito na conta | ✅ handleDeposit() | Token + existência | Erro se falha | ✅ |
| 6 | Erros cadastro | ✅ criar/atualizar/deletar | Validator | 8 mensagens | ✅ |
| 7 | Erros login | ✅ login/logout | Validator | 5 mensagens | ✅ |

---

## Fluxos de Teste Recomendados

### Teste 1: Apenas Usuário Autenticado Faz Transações
```
1. Cliente tenta transferir SEM fazer login
   → Erro de validação do Validator (campo 'token' obrigatório)
   
2. Cliente faz login (recebe token)
   → Sucesso: token gerado
   
3. Cliente transfere com token válido
   → Sucesso: transferência realizada
   
4. Cliente logout
   → Sucesso: token removido
   
5. Cliente tenta transferir com token antigo
   → Erro: "Token inválido ou expirado"
```

### Teste 2: Extratos Criados e Consultáveis
```
1. Cliente faz login
2. Cliente realiza depósito (+500.00)
   → Extrato criado com timestamp
3. Cliente realiza transferência (200.00)
   → Extrato criado com timestamp
4. Cliente solicita extrato com data_inicial e data_final
   → Retorna 2 transações com todos os dados
```

### Teste 3: Tratamento de Erros de Cadastro
```
1. Cliente tenta criar usuário com CPF duplicado
   → Erro: "Usuário já existe com este CPF"
   
2. Cliente tenta criar com CPF formato inválido
   → Erro: "O campo 'cpf' deve estar no formato '000.000.000-00'"
   
3. Cliente tenta atualizar sem fazer login
   → Erro: "Token inválido ou expirado"
   
4. Cliente tenta deletar usuário
   → Sucesso: Usuário deletado, token removido
```

### Teste 4: Tratamento de Erros de Login
```
1. Cliente tenta login com CPF errado
   → Erro: "CPF ou senha inválidos"
   
2. Cliente tenta login com senha errada
   → Erro: "CPF ou senha inválidos"
   
3. Cliente faz login com credenciais válidas
   → Sucesso: Token gerado
   
4. Cliente logout com token válido
   → Sucesso: Logout realizado
   
5. Cliente tenta logout com token inválido
   → Erro: "Token inválido ou expirado"
```

---

## Componentes Críticos Envolvidos

### Cliente (`com.distribuidos.client`)
- ✅ `ClientConnection.java` - Comunicação e validação
- ✅ `ClientGUI.java` - Interface com usuário

### Servidor (`com.distribuidos.server`)
- ✅ `ServerHandler.java` - Processamento de requisições (8 handlers)
- ✅ `ServerMain.java` - Inicialização e gerenciamento
- ✅ `ServerGUI.java` - Interface com usuário

### Comum (`com.distribuidos.common`)
- ✅ `TokenManager.java` - Geração e validação de tokens
- ✅ `MessageBuilder.java` - Construção de mensagens JSON
- ✅ `Validator.java` - Validação de protocolo

### Banco de Dados (`com.distribuidos.database`)
- ✅ `DatabaseManager.java` - Operações ACID e transações

### Validação (`validador`)
- ✅ `RulesEnum.java` - Definição de operações (incluindo ERRO_SERVIDOR)
- ✅ `Validator.java` - Validação de chaves esperadas

---

## Conclusão

✅ **TODOS OS 7 REQUISITOS FUNCIONAIS ESTÃO IMPLEMENTADOS E TESTÁVEIS**

O sistema garante:
1. **Segurança** - Apenas usuários autenticados (via token) podem fazer transações
2. **Integridade** - Extratos criados automaticamente com transações ACID
3. **Disponibilidade** - API completa para criar, ler, atualizar e deletar usuários/transações
4. **Robustez** - Tratamento abrangente de erros em todos os módulos
5. **Validação** - Protocolo JSON validado em cliente e servidor

Cada componente foi mapeado com exemplos específicos de código e mensagens de erro esperadas.

---

**Documento preparado em:** 5 de novembro de 2025  
**Versão:** 1.0  
**Próximos passos:** Executar testes de integração e validação em ambiente de teste
