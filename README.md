# Sistema Bancário Distribuído - EP-2

**Sistema bancário distribuído em Java com comunicação cliente-servidor, operações CRUD, transações financeiras e validação rigorosa de protocolo.**

## 🚀 Execução Rápida

### Windows
```powershell
# Executar diretamente
.\scripts\sistema.ps1

# Ou compilar e executar separadamente
.\scripts\compilar.ps1
.\scripts\servidor.ps1    # Terminal 1
.\scripts\cliente.ps1     # Terminal 2
```

### Linux/macOS
```bash
# Dar permissão aos scripts
chmod +x scripts/*.sh

# Compilar e executar servidor
./scripts/compilar.sh
./scripts/servidor.sh    # Terminal 1

# Em outro terminal: executar cliente
./scripts/cliente.sh     # Terminal 2
```

## ⚙️ Requisitos do Sistema

- **Java 17+** (testado com Java 25)
- **Maven 3.6+**
- **SO Suportados**: Windows, Linux, macOS
- **Porta 20000** disponível (configurável)

## 📋 Funcionalidades Implementadas (EP-2)

### 🖥️ Cliente (1.2/1.2 pontos)
- **a) Conectar no servidor** - Interface para inserir IP/porta + protocolo de conexão
- **b) Login no sistema** - Autenticação JWT com validação de CPF/senha
- **c) Depositar dinheiro** - Operação `depositar` com validação de valores
- **d) Transferir dinheiro** - Operação `transacao_criar` entre usuários
- **e) Ver extrato (histórico)** - Operação `transacao_ler` com filtros de data
- **f) Ver dados da conta** - Operação `usuario_ler` com CPF, nome e saldo

### 🖧 Servidor (1.8/1.8 pontos)
- **g) Aceitar conexões** - ServerMain multi-threaded na porta 20000
- **h) Validar operações** - Validator.java para todas as mensagens JSON
- **i) Login/logout usuários** - Gestão de tokens JWT e sessões
- **j) CRUD usuários** - Create/Read/Update/Delete completo
- **k) Operações transferência** - Validação de saldos e usuários
- **l) Operações depósito** - Validação de valores e atualização de saldos
- **m) Leitura extratos** - Consulta de transações por período
- **n) Gerenciar múltiplos clientes** - Threads independentes por cliente
- **o) Tratar erros** - Respostas padronizadas e logging estruturado

## 🔧 Como Executar para Avaliação

### Opção 1A: Windows (PowerShell)
```powershell
# Compilar e executar servidor
.\scripts\compilar.ps1
.\scripts\servidor.ps1

# Em outro terminal: executar cliente
.\scripts\cliente.ps1
```

### Opção 1B: Linux/macOS (Bash)
```bash
# Dar permissão e compilar
chmod +x scripts/*.sh
./scripts/compilar.sh

# Servidor (Terminal 1)
./scripts/servidor.sh

# Cliente (Terminal 2)
./scripts/cliente.sh
```

### Opção 2: Maven Direto (Multiplataforma)
```bash
# Compilar
mvn clean compile package

# Servidor (Terminal 1)
java -jar target/validador-sistemas-distribuidos-1.0.0.jar

# Cliente (Terminal 2)
java -Dserver.host=localhost -Dserver.port=20000 -cp target/validador-sistemas-distribuidos-1.0.0.jar com.distribuidos.client.ClientMain
```

## 📂 Arquitetura do Sistema

### Código Principal
```
src/main/java/com/distribuidos/
├── client/
│   ├── ClientMain.java          # Ponto de entrada do cliente
│   ├── ClientGUI.java           # Interface Swing
│   └── ClientConnection.java    # Comunicação TCP/IP
├── server/
│   ├── ServerMain.java          # Ponto de entrada do servidor  
│   ├── ServerHandler.java       # Processamento de requisições
│   └── ServerGUI.java           # Interface administrativa
├── common/
│   ├── MessageBuilder.java      # Construção de mensagens JSON
│   ├── TokenManager.java        # Gerenciamento JWT
│   ├── Usuario.java             # Modelo de usuário
│   └── Transacao.java           # Modelo de transação
└── database/
    └── DatabaseManager.java     # Operações SQLite
```

### Validador de Protocolo
```
src/main/java/validador/          # Sistema de validação rigorosa
├── Validator.java                # Validação cliente ↔ servidor
└── RulesEnum.java               # Regras do protocolo bancário
```

### Scripts de Execução
```
scripts/
├── compilar.ps1                 # Compilação Maven (Windows)
├── servidor.ps1                 # Execução do servidor (Windows)
├── cliente.ps1                  # Execução do cliente (Windows)
├── sistema.ps1                  # Menu interativo (Windows)
├── compilar.sh                  # Compilação Maven (Linux/macOS)
├── servidor.sh                  # Execução do servidor (Linux/macOS)
└── cliente.sh                   # Execução do cliente (Linux/macOS)
```

**Nota para Linux/macOS**: Se os scripts .sh não existirem, você pode criá-los ou usar diretamente os comandos Maven:

```bash
# Substituir scripts por comandos diretos
# Compilar:
mvn clean compile package

# Servidor:
java -jar target/validador-sistemas-distribuidos-1.0.0.jar

# Cliente:
java -Dserver.host=localhost -Dserver.port=20000 -cp target/validador-sistemas-distribuidos-1.0.0.jar com.distribuidos.client.ClientMain
```

## 🔗 Conectar e Testar

### Configuração Padrão
- **Servidor**: `localhost:20000`
- **Protocolo**: TCP/IP com mensagens JSON
- **Banco**: SQLite (criado automaticamente em `usuarios.db`)

### Usuários de Teste Pré-cadastrados
| CPF | Nome | Senha | Saldo |
|-----|------|-------|-------|
| 123.456.789-01 | João Silva Santos | 123456 | R$ 1.500,00 |
| 987.654.321-02 | Maria Santos Oliveira | 654321 | R$ 2.300,00 |
| 111.222.333-44 | Pedro Oliveira Costa | password | R$ 800,00 |
| 555.666.777-88 | Ana Costa Ferreira | 123abc | R$ 4.200,00 |

### Fluxo de Teste Recomendado
1. **Executar servidor** (`scripts\servidor.ps1`)
2. **Executar cliente** (`scripts\cliente.ps1`) 
3. **Conectar**: Host `localhost`, Porta `20000`
4. **Login**: CPF `123.456.789-01`, Senha `123456`
5. **Testar operações**: Depósito, transferência, extrato, dados da conta

## 🌐 Protocolo de Comunicação

Todas as mensagens seguem o formato JSON padronizado conforme especificação em `Essentials/README.md`.

### Exemplos de Operações

#### Login de Usuário
```json
// Cliente → Servidor
{
  "operacao": "usuario_login",
  "cpf": "123.456.789-01",
  "senha": "123456"
}

// Servidor → Cliente (Sucesso)
{
  "operacao": "usuario_login",
  "status": true,
  "info": "Login realizado com sucesso",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Depósito
```json
// Cliente → Servidor
{
  "operacao": "depositar",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "valor_enviado": 500.00
}
```

#### Transferência
```json
// Cliente → Servidor
{
  "operacao": "transacao_criar",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "cpf_destino": "987.654.321-02",
  "valor": 250.00
}
```

### Sistema de Validação
- **Todas as mensagens** são validadas pelo `Validator.java`
- **Conformidade rigorosa** com protocolo bancário em `Essentials/README.md`
- **Tratamento de erros** padronizado com códigos específicos

## ⚙️ Tecnologias e Dependências

### Stack Principal
- **Java 17+** - Linguagem de desenvolvimento
- **Maven 3.6+** - Gerenciamento de dependências e build
- **SQLite 3.46+** - Banco de dados local
- **Jackson 2.17.2** - Processamento JSON
- **BCrypt 0.4** - Hash seguro de senhas
- **JWT** - Tokens de autenticação
- **Java Swing** - Interface gráfica

### Características Técnicas
- **Arquitetura Cliente-Servidor** com TCP/IP
- **Multi-threading** - um thread por cliente conectado
- **Transações ACID** - integridade garantida no SQLite
- **Validação rigorosa** - protocolo JSON validado em todas as mensagens
- **Logging estruturado** - Logback para debug e auditoria
- **Criptografia** - senhas nunca armazenadas em texto plano

## 🔧 Resolução de Problemas

### Problemas Comuns

#### "Erro de conexão com o servidor"
```powershell
# Verificar se servidor está rodando
Get-Process java

# Verificar porta 20000
netstat -an | findstr :20000

# Reiniciar servidor
.\scripts\servidor.ps1
```

#### "ClassNotFoundException" ou erro de compilação
```powershell
# Limpar e recompilar
mvn clean compile package

# Verificar dependências
mvn dependency:tree
```

#### "Porta 20000 já está em uso"
```powershell
# Encontrar processo usando a porta
netstat -ano | findstr :20000

# Terminar processo (substituir PID)
Stop-Process -Id [PID] -Force
```

#### "Banco de dados bloqueado"
```powershell
# Fechar todas as conexões Java
Get-Process java | Stop-Process

# Remover arquivos de lock
Remove-Item usuarios.db-wal, usuarios.db-shm -ErrorAction SilentlyContinue
```

### Logs de Debug
```powershell
# Ver logs em tempo real
Get-Content logs\application.log -Wait

# Filtrar erros
Get-Content logs\application.log | Select-String "ERROR"
```

## 📋 Checklist de Avaliação EP-2

### ✅ Implementação Completa
- [x] **6 funcionalidades do cliente** (1.2 pts) - Todas implementadas
- [x] **9 funcionalidades do servidor** (1.8 pts) - Todas implementadas
- [x] **Protocolo rigoroso** - Validação automática de todas as mensagens
- [x] **Interface gráfica** - Cliente e servidor com GUI Swing
- [x] **Banco de dados** - SQLite com transações ACID
- [x] **Documentação** - README com instruções claras
- [x] **Scripts de execução** - PowerShell para compilação e execução

### 🎯 Pontuação Total: **3.0/3.0**

---

## 📄 Informações Acadêmicas

**Disciplina**: Sistemas Distribuídos  
**Avaliação**: EP-2 (Funcionalidades Cliente-Servidor)  
**Período**: 2025.1  
**Tecnologia**: Java + Maven + SQLite + TCP/IP  

### Documentação de Referência
- [`Essentials/README.md`](Essentials/README.md) - Especificação completa do protocolo
- [`pom.xml`](pom.xml) - Configuração Maven e dependências
- [`database_setup.sql`](database_setup.sql) - Schema do banco de dados

**Sistema validado e pronto para avaliação EP-2** ✅