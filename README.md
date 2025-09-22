# Sistema Distribuído - Validador de Usuários

Este é um sistema distribuído completo desenvolvido em Java com Maven, implementando um servidor e cliente com interface gráfica para gerenciamento de usuários.

## Funcionalidades Implementadas

### Servidor
- ✅ **Interface Gráfica (GUI)** com abas organizadas
- ✅ **Log em tempo real** de todos os eventos
- ✅ **Seleção de porta** para o servidor
- ✅ **Informações do servidor**: IP real, hostname (DNS)
- ✅ **Visualização de clientes conectados** com detalhes (IP, porta, hostname, usuário logado)
- ✅ **CRUD completo do banco de dados** com interface visual
- ✅ **Tratamento avançado de erros** com validação usando classes Essentials

### Cliente
- ✅ **Interface Gráfica (GUI)** intuitiva
- ✅ **Campos de preenchimento**: nome, CPF, senha
- ✅ **Operações completas**:
  - Login/Logout
  - Criar usuário
  - Ler dados do usuário
  - Alterar dados do usuário
  - Deletar conta
- ✅ **Log em tempo real** de comunicação
- ✅ **Tabela de clientes conectados** (simulada)
- ✅ **Validação de dados** seguindo padrões das classes Essentials

### Características Técnicas
- ✅ **Arquitetura cliente-servidor** com sockets TCP
- ✅ **Protocolo JSON** validado pelas classes Essentials
- ✅ **Banco de dados SQLite** com dados populados
- ✅ **Autenticação por tokens** com expiração
- ✅ **Criptografia de senhas** com BCrypt
- ✅ **Logging estruturado** com SLF4J/Logback
- ✅ **Tratamento robusto de erros** e exceções
- ✅ **Interface responsiva** com Swing

## Estrutura do Projeto

```
├── Essentials/                 # Pasta preservada com validadores
│   ├── README.md
│   ├── RulesEnum.java
│   └── Validator.java
├── src/main/java/com/distribuidos/
│   ├── client/                 # Classes do cliente
│   │   ├── ClientConnection.java
│   │   ├── ClientGUI.java
│   │   └── ClientMain.java
│   ├── server/                 # Classes do servidor
│   │   ├── ServerHandler.java
│   │   ├── ServerGUI.java
│   │   └── ServerMain.java
│   ├── common/                 # Classes compartilhadas
│   │   ├── Usuario.java
│   │   ├── ClientInfo.java
│   │   ├── MessageBuilder.java
│   │   └── TokenManager.java
│   └── database/               # Gerenciamento do banco
│       └── DatabaseManager.java
├── src/main/resources/
│   └── logback.xml            # Configuração de logging
├── logs/                      # Diretório de logs
├── usuarios.db               # Banco SQLite (criado automaticamente)
└── pom.xml                   # Configuração Maven
```

## Como Executar

### Pré-requisitos
- Java 11 ou superior
- Maven 3.6 ou superior

### Compilação
```bash
mvn clean compile
```

### Executar Servidor
```bash
mvn exec:java -Dexec.mainClass="com.distribuidos.server.ServerMain"
```

### Executar Cliente
```bash
mvn exec:java -Dexec.mainClass="com.distribuidos.client.ClientMain"
```

### Executar com Maven (alternativo)
```bash
# Servidor
mvn exec:java@run-server

## Usuários Pré-cadastrados

O sistema vem com 20 usuários de teste já cadastrados para facilitar os testes:

| CPF | Nome | Senha |
|-----|------|-------|
| 123.456.789-01 | João Silva Santos | 123456 |
| 987.654.321-02 | Maria Santos Oliveira | 654321 |
| 111.222.333-44 | Pedro Oliveira Costa | password |
| 555.666.777-88 | Ana Costa Ferreira | 123abc |
| 444.555.666-77 | Carlos Eduardo Lima | carlos123 |
| 333.444.555-66 | Fernanda Alves Souza | fernanda456 |
| 222.333.444-55 | Roberto Silva Junior | roberto789 |
| 666.777.888-99 | Juliana Pereira Rocha | juliana321 |
| 777.888.999-00 | Marcos Antonio Dias | marcos654 |
| 888.999.000-11 | Luciana Martins Cruz | luciana987 |
| 999.000.111-22 | Rafael Santos Barbosa | rafael147 |
| 000.111.222-33 | Camila Rodrigues Silva | camila258 |
| 147.258.369-12 | Bruno Henrique Gomes | bruno369 |
| 258.369.147-23 | Patrícia Lima Nascimento | patricia741 |
| 369.147.258-34 | Diego Fernandes Costa | diego852 |
| 741.852.963-45 | Vanessa Almeida Santos | vanessa963 |
| 852.963.741-56 | Thiago Oliveira Pereira | thiago159 |
| 963.741.852-67 | Priscila Santos Moreira | priscila753 |
| 159.753.486-78 | Leonardo Silva Cardoso | leonardo486 |
| 753.486.159-89 | Gabriela Costa Ribeiro | gabriela159 |
```

## Usuários Pré-cadastrados

O sistema vem com usuários de teste já cadastrados:

| CPF | Nome | Senha |
|-----|------|-------|
| 123.456.789-01 | João Silva | 123456 |
| 987.654.321-02 | Maria Santos | 654321 |
| 111.222.333-44 | Pedro Oliveira | password |
| 555.666.777-88 | Ana Costa | 123abc |

## Protocolo de Comunicação

O sistema utiliza mensagens JSON validadas pelas classes Essentials:

### Exemplo de Login
**Cliente → Servidor:**
```json
{
  "operacao": "usuario_login",
  "cpf": "123.456.789-01",
  "senha": "123456"
}
```

**Servidor → Cliente:**
```json
{
  "operacao": "usuario_login",
  "status": true,
  "info": "Login realizado com sucesso",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

## Validações Implementadas

- **CPF**: Formato 000.000.000-00
- **Nome**: Mínimo 6 caracteres, máximo 120
- **Senha**: Mínimo 6 caracteres, máximo 120
- **Tokens**: Validação de expiração (30 minutos)
- **Mensagens JSON**: Validação completa usando classes Essentials

## Tratamento de Erros

O sistema implementa tratamento robusto de erros:

- **Validação de entrada** em todos os campos
- **Verificação de conectividade** antes das operações
- **Timeout de tokens** com limpeza automática
- **Mensagens de erro descritivas** para o usuário
- **Logging detalhado** para debugging
- **Recuperação graceful** de falhas de conexão

## Segurança

- **Senhas criptografadas** com BCrypt
- **Tokens seguros** com expiração automática
- **Validação rigorosa** de todas as entradas
- **Prevenção de SQL injection** com PreparedStatements
- **Logs de auditoria** de todas as operações

## Logs

Os logs são salvos em:
- **Console**: Para desenvolvimento
- **Arquivo**: `logs/sistema-distribuido.log` com rotação diária

## Banco de Dados

- **SQLite** para simplicidade e portabilidade
- **Arquivo**: `usuarios.db` (criado automaticamente)
- **Tabela usuarios** com campos: cpf, nome, senha, saldo, criado_em, atualizado_em

## Compatibilidade

Este sistema foi desenvolvido para ser compatível com outros projetos de sistemas distribuídos, seguindo rigorosamente as especificações das classes Essentials para garantir interoperabilidade.

## Branch

Este projeto está na branch `newpix-teste` conforme solicitado.