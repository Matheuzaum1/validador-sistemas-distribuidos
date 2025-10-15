# Protocolo de Comunicação

O sistema utiliza um protocolo JSON personalizado validado pelas classes Essentials.

## Estrutura das Mensagens

Todas as mensagens seguem o formato JSON com validação obrigatória:

```json
{
  "operacao": "nome_da_operacao",
  "status": true/false,
  "info": "Mensagem informativa",
  // campos específicos da operação
}
```

## Operações Disponíveis

### 1. Conexão Inicial

**Cliente → Servidor:**
```json
{
  "operacao": "conectar"
}
```

**Servidor → Cliente:**
```json
{
  "operacao": "conectar",
  "status": true,
  "info": "Conexão estabelecida com sucesso"
}
```

### 2. Login de Usuário

**Cliente → Servidor:**
```json
{
  "operacao": "usuario_login",
  "cpf": "123.456.789-01",
  "senha": "123456"
}
```

**Servidor → Cliente (Sucesso):**
```json
{
  "operacao": "usuario_login",
  "status": true,
  "info": "Login realizado com sucesso",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "nome": "João Silva Santos",
  "saldo": 1500.50
}
```

**Servidor → Cliente (Erro):**
```json
{
  "operacao": "usuario_login",
  "status": false,
  "info": "CPF ou senha inválidos"
}
```

### 3. Logout

**Cliente → Servidor:**
```json
{
  "operacao": "usuario_logout",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Servidor → Cliente:**
```json
{
  "operacao": "usuario_logout",
  "status": true,
  "info": "Logout realizado com sucesso"
}
```

### 4. Criar Usuário

**Cliente → Servidor:**
```json
{
  "operacao": "usuario_criar",
  "cpf": "111.222.333-44",
  "nome": "Pedro Oliveira Costa",
  "senha": "password123"
}
```

**Servidor → Cliente:**
```json
{
  "operacao": "usuario_criar",
  "status": true,
  "info": "Usuário criado com sucesso"
}
```

### 5. Ler Dados do Usuário

**Cliente → Servidor:**
```json
{
  "operacao": "usuario_ler",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Servidor → Cliente:**
```json
{
  "operacao": "usuario_ler",
  "status": true,
  "info": "Dados do usuário recuperados",
  "nome": "João Silva Santos",
  "cpf": "123.456.789-01",
  "saldo": 1500.50,
  "criado_em": "2024-01-15T10:30:00",
  "atualizado_em": "2024-01-20T14:45:00"
}
```

### 6. Atualizar Usuário

**Cliente → Servidor:**
```json
{
  "operacao": "usuario_atualizar",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "nome": "João Silva Santos Junior",
  "senha": "nova_senha123"
}
```

**Servidor → Cliente:**
```json
{
  "operacao": "usuario_atualizar",
  "status": true,
  "info": "Dados atualizados com sucesso"
}
```

### 7. Deletar Usuário

**Cliente → Servidor:**
```json
{
  "operacao": "usuario_deletar",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Servidor → Cliente:**
```json
{
  "operacao": "usuario_deletar",
  "status": true,
  "info": "Conta deletada com sucesso"
}
```

### 8. Transferência entre Usuários

**Cliente → Servidor:**
```json
{
  "operacao": "transferencia",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "cpf_destino": "987.654.321-02",
  "valor": 250.75,
  "descricao": "Pagamento de serviços"
}
```

**Servidor → Cliente:**
```json
{
  "operacao": "transferencia",
  "status": true,
  "info": "Transferência realizada com sucesso",
  "saldo_atual": 1249.75,
  "id_transacao": "TXN_12345"
}
```

### 9. Depósito

**Cliente → Servidor:**
```json
{
  "operacao": "deposito",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "valor": 500.00,
  "descricao": "Depósito via PIX"
}
```

**Servidor → Cliente:**
```json
{
  "operacao": "deposito",
  "status": true,
  "info": "Depósito realizado com sucesso",
  "saldo_atual": 2000.50,
  "id_transacao": "DEP_67890"
}
```

### 10. Histórico de Transações

**Cliente → Servidor:**
```json
{
  "operacao": "historico_transacoes",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "data_inicio": "2024-01-01",
  "data_fim": "2024-01-31",
  "limite": 50
}
```

**Servidor → Cliente:**
```json
{
  "operacao": "historico_transacoes",
  "status": true,
  "info": "Histórico recuperado com sucesso",
  "transacoes": [
    {
      "id": "TXN_12345",
      "tipo": "transferencia_enviada",
      "valor": -250.75,
      "cpf_outro": "987.654.321-02",
      "descricao": "Pagamento de serviços",
      "data": "2024-01-20T14:30:00",
      "saldo_apos": 1249.75
    },
    {
      "id": "DEP_67890",
      "tipo": "deposito",
      "valor": 500.00,
      "descricao": "Depósito via PIX",
      "data": "2024-01-21T09:15:00",
      "saldo_apos": 1749.75
    }
  ],
  "total_transacoes": 2,
  "saldo_atual": 1749.75
}
```

## Validações

### CPF
- Formato: `000.000.000-00`
- Validação de dígitos verificadores
- Não pode ser sequência (111.111.111-11)

### Nome
- Mínimo: 6 caracteres
- Máximo: 120 caracteres
- Somente letras, espaços e acentos

### Senha
- Mínimo: 6 caracteres
- Máximo: 120 caracteres
- Criptografada com BCrypt no servidor

### Token
- JWT assinado
- Expiração: 30 minutos
- Invalidado no logout

### Valores Monetários
- Formato decimal (0.00)
- Valores positivos
- Máximo 2 casas decimais

## Códigos de Erro

| Código | Descrição |
|--------|-----------|
| `INVALID_OPERATION` | Operação não reconhecida |
| `INVALID_FORMAT` | Formato de dados inválido |
| `AUTHENTICATION_FAILED` | Falha na autenticação |
| `TOKEN_EXPIRED` | Token expirado |
| `TOKEN_INVALID` | Token inválido |
| `USER_NOT_FOUND` | Usuário não encontrado |
| `USER_ALREADY_EXISTS` | Usuário já existe |
| `INSUFFICIENT_FUNDS` | Saldo insuficiente |
| `INVALID_AMOUNT` | Valor inválido |
| `OPERATION_FAILED` | Falha na operação |
| `SERVER_ERROR` | Erro interno do servidor |

## Timeouts

- **Conexão**: 5 segundos
- **Leitura**: 10 segundos
- **Token**: 30 minutos
- **Sessão inativa**: 60 minutos

## Segurança

### Autenticação
- Tokens JWT com assinatura HMAC-SHA256
- Expiração automática de tokens
- Invalidação manual no logout

### Criptografia
- Senhas hasheadas com BCrypt (custo 12)
- Tokens assinados digitalmente
- Comunicação em texto claro (TCP)

### Validação
- Todas as mensagens validadas pelas classes Essentials
- Sanitização de entradas
- Prevenção de injection attacks

## Logs

Todas as operações são registradas com:
- Timestamp ISO 8601
- IP do cliente
- Operação realizada
- Status da operação
- Dados relevantes (sem senhas)

Exemplo:
```
2024-01-21T10:15:30.123 [INFO] 192.168.1.100:54321 - usuario_login - SUCCESS - CPF: 123.456.789-01
2024-01-21T10:16:45.456 [INFO] 192.168.1.100:54321 - transferencia - SUCCESS - Valor: 250.75, Destino: 987.654.321-02
```

## Interoperabilidade

Este protocolo foi desenvolvido para ser compatível com outros sistemas distribuídos que utilizem as classes Essentials, garantindo:

- Validação consistente de mensagens
- Formato padronizado de dados
- Códigos de erro unificados
- Comportamento previsível

Para implementar este protocolo em outro sistema, utilize as classes `Validator.java` e `RulesEnum.java` disponíveis na pasta `Essentials/`.
