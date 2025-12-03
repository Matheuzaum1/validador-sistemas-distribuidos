# Sistema Bancário Distribuído - Validador

**Sistema bancário distribuído em Java com comunicação cliente-servidor, operações CRUD, transações financeiras e validação rigorosa de protocolo.**

## 🚀 Execução Rápida

### 📋 Opção 1: Menu Interativo (Recomendado)
```powershell
# Windows
.\scripts\menu.ps1

# Linux/macOS
chmod +x scripts/*.sh
./scripts/menu.sh
```

### ⚡ Opção 2: Execução Direta

#### Windows (PowerShell)
```powershell
# Compilar e executar automaticamente
.\scripts\sistema.ps1

# Ou separadamente:
.\scripts\compilar.ps1    # Compila o projeto
.\scripts\servidor.ps1    # Terminal 1 - Inicia servidor
.\scripts\cliente.ps1     # Terminal 2 - Inicia cliente

# Error Injector (teste de erros)
.\scripts\error-injector.ps1

# Sistema completo com proxy de teste
.\scripts\sistema-proxy.ps1

# Limpeza de builds
.\scripts\limpeza.ps1
```

#### Linux/macOS (Bash)
```bash
# Dar permissões (primeira vez)
chmod +x scripts/*.sh

# Compilar e executar automaticamente
./scripts/sistema.sh

# Ou separadamente:
./scripts/compilar.sh     # Compila o projeto
./scripts/servidor.sh     # Terminal 1 - Inicia servidor
./scripts/cliente.sh      # Terminal 2 - Inicia cliente

# Error Injector (se compilado)
cd error-injector && java -jar target/simple-error-injector-1.0.0.jar

# Limpeza
mvn clean
```

### 🔧 Opção 3: Maven Direto
```bash
# Compilar
mvn clean compile package

# Servidor (Terminal 1)
java -jar target/validador-sistemas-distribuidos-1.0.0.jar

# Cliente (Terminal 2)
java -cp target/validador-sistemas-distribuidos-1.0.0.jar com.distribuidos.client.ClientMain
```

### 💻 Opção 4: Via IDE (Eclipse/IntelliJ/VSCode)
1. **Importar projeto Maven**: `File > Import > Maven > Existing Maven Projects`
2. **Compilar**: `mvn clean compile` ou botão de build da IDE
3. **Executar Servidor**: Run `com.distribuidos.server.ServerMain`
4. **Executar Cliente**: Run `com.distribuidos.client.ClientMain`

## 🚨 Sistema de Relatório de Erros (Protocolo 4.11)

O sistema implementa completamente o protocolo 4.11 para relatório automático de erros do servidor.

### ✅ Funcionamento
1. **Cliente detecta erro** (campo ausente/nulo, status:false)
2. **Cliente envia `erro_servidor`** automaticamente
3. **Servidor registra** nos logs com JSON completo
4. **Cliente exibe pop-up** informando o problema
5. **Logs mostram** envio e confirmação

### 🔍 Cenários de Erro Detectados
- **Campo `operacao` ausente/nulo** na resposta
- **Campo `token` ausente/nulo** em login bem-sucedido
- **Campo `usuario` ausente/nulo** em usuario_ler
- **Campo `transacoes` ausente/nulo** em transacao_ler
- **Status false** em qualquer operação

### 📋 Exemplo de Log de Erro
```
❌ 🔴 PROTOCOLO VIOLATION: usuario_login sem token (seção 4.11)
📄 JSON recebido: {"operacao":"usuario_login","status":true,"info":"Login realizado","token":null}
📤 ERRO_SERVIDOR enviado: {"operacao":"erro_servidor","operacao_enviada":"usuario_login","info":"Resposta usuario_login chegou sem campo 'token' ou token é nulo"}
📥 ERRO_SERVIDOR confirmado: {"operacao":"erro_servidor","status":true,"info":"Erro reportado e registrado com sucesso"}
```

### 🧪 Ferramentas de Teste

#### Error Injector - Proxy de Teste
Proxy TCP que intercepta comunicações e injeta erros em tempo real:

```bash
# Compilar e executar
cd error-injector
mvn clean package
java -jar target/simple-error-injector-1.0.0.jar

# Configuração:
# - Porta Proxy: 9999 (clientes conectam aqui)
# - Host Servidor: localhost
# - Porta Servidor: 20000 (servidor real)
```

**Características do Error Injector:**
- ✅ Interface gráfica simples e intuitiva
- ✅ Proxy TCP funcional bidirecional
- ✅ Injeção de erros por checkboxes
- ✅ Logs em tempo real
- ✅ Primeira mensagem nunca é modificada (garante conexão)

**Tipos de erro injetados:**
- **Cliente → Servidor**: Remove campo `operacao`
- **Servidor → Cliente**: Remove campo `status`

#### Testes Automatizados
```bash
# Teste via linha de comando
java -cp target/classes com.distribuidos.tools.ServerResponseInjector

# Interface gráfica para testes
java -cp target/classes com.distribuidos.test.ErrorReportTestRunner
```

## 📋 Funcionalidades (Especificação EP-2)

### 🖥️ Cliente (6 funcionalidades)
- **a) Conectar servidor** - Interface IP/porta + protocolo conexão
- **b) Login sistema** - Autenticação JWT com CPF/senha
- **c) Depositar dinheiro** - Operação `depositar` com validação
- **d) Transferir dinheiro** - Operação `transacao_criar`
- **e) Ver extrato** - Operação `transacao_ler` com filtros
- **f) Ver dados conta** - Operação `usuario_ler`

### 🖧 Servidor (9 funcionalidades)
- **g) Aceitar conexões** - ServerMain multi-threaded porta 20000
- **h) Validar operações** - Validator.java para mensagens JSON
- **i) Login/logout** - Gestão tokens JWT e sessões
- **j) CRUD usuários** - Create/Read/Update/Delete completo
- **k) Operações transferência** - Validação saldos e usuários
- **l) Operações depósito** - Validação valores e saldos
- **m) Leitura extratos** - Consulta transações por período
- **n) Múltiplos clientes** - Threads independentes por cliente
- **o) Tratar erros** - Respostas padronizadas e logging

**Pontuação Total: 15 funcionalidades implementadas** ✅

## 🔧 Resolução de Problemas

### Problemas Comuns

#### "Erro de conexão com servidor"
```powershell
# Windows - Verificar se servidor está rodando
Get-Process java
netstat -an | findstr :20000

# Linux/macOS
ps aux | grep java
netstat -an | grep :20000

# Reiniciar servidor
.\scripts\servidor.ps1  # Windows
./scripts/servidor.sh   # Linux/macOS
```

#### "Erro de compilação" ou dependências
```bash
# Limpar e recompilar
mvn clean compile package

# Verificar dependências
mvn dependency:tree

# Forçar download de dependências
mvn dependency:resolve
```

#### "Porta já está em uso"
```powershell
# Windows - Encontrar processo
netstat -ano | findstr :20000
Stop-Process -Id [PID] -Force

# Linux/macOS
lsof -ti:20000 | xargs kill -9
```

#### "Banco de dados bloqueado"
```powershell
# Fechar conexões Java
Get-Process java | Stop-Process  # Windows
pkill java                       # Linux/macOS

# Remover arquivos de lock
rm usuarios.db-wal usuarios.db-shm
```

#### Problemas com Maven/Java
```bash
# Verificar versão Java
java -version

# Definir JAVA_HOME (se necessário)
export JAVA_HOME=/path/to/java  # Linux/macOS
$env:JAVA_HOME="C:\path\to\java" # Windows PowerShell

# Verificar Maven
mvn -version
```

### Logs e Debug
```bash
# Ver logs em tempo real
tail -f logs/application.log        # Linux/macOS
Get-Content logs\application.log -Wait  # Windows

# Filtrar erros
grep "ERROR" logs/application.log   # Linux/macOS
Select-String "ERROR" logs\application.log  # Windows

# Executar com debug detalhado
java -Dlogback.configurationFile=src/main/resources/logback.xml -jar target/validador-sistemas-distribuidos-1.0.0.jar
```

### Configuração de Ambiente

#### Instalação Java 17+
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# Windows (usando Chocolatey)
choco install openjdk17

# macOS (usando Homebrew)
brew install openjdk@17
```

#### Instalação Maven
```bash
# Ubuntu/Debian
sudo apt install maven

# Windows (usando Chocolatey)
choco install maven

# macOS (usando Homebrew)
brew install maven
```

## 📂 Arquitetura e Tecnologias

### Estrutura do Projeto
```
src/main/java/com/distribuidos/
├── client/              # Cliente com interface gráfica
│   ├── ClientMain.java     # Ponto de entrada
│   ├── ClientGUI.java      # Interface Swing
│   └── ClientConnection.java # Comunicação TCP/IP
├── server/              # Servidor multi-threaded
│   ├── ServerMain.java     # Ponto de entrada
│   ├── ServerHandler.java  # Processamento de requisições
│   └── ServerGUI.java      # Interface administrativa
├── common/              # Componentes compartilhados
│   ├── MessageBuilder.java # Construção de mensagens JSON
│   ├── TokenManager.java   # Gerenciamento JWT
│   ├── Usuario.java        # Modelo de usuário
│   └── Transacao.java      # Modelo de transação
├── database/            # Persistência de dados
│   └── DatabaseManager.java # Operações SQLite
└── validador/           # Validação de protocolo
    ├── Validator.java      # Validação cliente ↔ servidor
    └── RulesEnum.java      # Regras do protocolo bancário
```

### Stack Tecnológico
- **Java 17+** - Linguagem de desenvolvimento
- **Maven 3.6+** - Gerenciamento de dependências
- **SQLite 3.46+** - Banco de dados local
- **Jackson 2.19.2** - Processamento JSON
- **BCrypt 0.4** - Hash seguro de senhas
- **JWT** - Tokens de autenticação
- **Swing** - Interface gráfica
- **Logback** - Sistema de logs

### Características Técnicas
- **Arquitetura Cliente-Servidor** com TCP/IP
- **Multi-threading** - um thread por cliente conectado
- **Transações ACID** - integridade garantida no SQLite
- **Validação rigorosa** - protocolo JSON validado
- **Logging estruturado** - auditoria e debug
- **Criptografia** - senhas nunca em texto plano

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

## 🔌 Configuração e Conexão

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

### Fluxo de Teste Básico
1. **Executar servidor** (porta 20000)
2. **Executar cliente** e conectar em `localhost:20000`
3. **Fazer login**: CPF `123.456.789-01`, Senha `123456`
4. **Testar operações**: Depósito, transferência, extrato, consulta de dados

## 🌐 Protocolo de Comunicação

Todas as mensagens seguem formato JSON padronizado com validação rigorosa.

### Estrutura Base das Mensagens
```json
{
  "operacao": "nome_da_operacao",
  "status": true/false,
  "info": "mensagem_descritiva",
  // campos específicos por operação
}
```

### Operações Principais

#### 1. Conectar ao Servidor
```json
// Cliente → Servidor
{"operacao": "conectar"}

// Servidor → Cliente
{"operacao": "conectar", "status": true, "info": "Conexão estabelecida"}
```

#### 2. Login de Usuário
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

#### 3. Depósito
```json
// Cliente → Servidor
{
  "operacao": "depositar",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "valor_enviado": 500.00
}

// Servidor → Cliente
{
  "operacao": "depositar",
  "status": true,
  "info": "Depósito realizado com sucesso",
  "saldo": 2000.00
}
```

#### 4. Transferência
```json
// Cliente → Servidor
{
  "operacao": "transacao_criar",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "cpf_destino": "987.654.321-02",
  "valor": 250.00
}

// Servidor → Cliente
{
  "operacao": "transacao_criar",
  "status": true,
  "info": "Transferência realizada com sucesso",
  "saldo": 1750.00
}
```

#### 5. Consultar Extrato
```json
// Cliente → Servidor
{
  "operacao": "transacao_ler",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "data_inicio": "2025-01-01",
  "data_fim": "2025-12-31"
}

// Servidor → Cliente
{
  "operacao": "transacao_ler",
  "status": true,
  "info": "Transações encontradas",
  "transacoes": [...]
}
```

#### 6. Dados da Conta
```json
// Cliente → Servidor
{
  "operacao": "usuario_ler",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}

// Servidor → Cliente
{
  "operacao": "usuario_ler",
  "status": true,
  "info": "Dados encontrados",
  "usuario": {
    "cpf": "123.456.789-01",
    "nome": "João Silva Santos",
    "saldo": 1750.00
  }
}
```

## 📁 Estrutura do Projeto

```
validador-sistemas-distribuidos/
├── src/main/java/com/distribuidos/    # Código-fonte principal
├── error-injector/                    # Ferramenta de teste de erros
├── scripts/                          # Scripts de automação
├── Essentials/                       # Especificação do protocolo
├── logs/                            # Arquivos de log
├── database_setup.sql               # Schema do banco de dados
├── pom.xml                          # Configuração Maven
└── README.md                        # Este arquivo
```

## 📋 Requisitos do Sistema

### Software Necessário
- **Java 17+** (JDK)
- **Maven 3.6+** 
- **Sistema Operacional**: Windows, Linux ou macOS

### Dependências (gerenciadas pelo Maven)
- **Jackson 2.19.2** - Processamento JSON
- **SQLite JDBC 3.46.1** - Banco de dados
- **BCrypt 0.4** - Criptografia de senhas
- **Logback 1.4.14** - Sistema de logs
- **SLF4J 2.0.16** - API de logging

### Portas Utilizadas
- **Servidor Principal**: `20000` (TCP)
- **Error Injector**: `9999` (TCP, opcional)

---

## 📄 Arquivos de Referência

### Especificação do Protocolo
As regras completas do protocolo de comunicação estão implementadas no validador (`src/main/java/validador/`) baseadas na especificação oficial.

### Configuração do Banco
Schema e dados iniciais definidos em `database_setup.sql`.

### Build e Dependências
Configuração completa no arquivo `pom.xml`.

---

## 🎯 Status do Projeto

**Sistema validado e funcional** ✅

- ✅ **15 funcionalidades** implementadas (6 cliente + 9 servidor)  
- ✅ **Protocolo 4.11** para relatório de erros
- ✅ **Interface gráfica** para cliente e servidor
- ✅ **Validação rigorosa** de todas as mensagens
- ✅ **Scripts automatizados** para execução
- ✅ **Sistema de logs** completo
- ✅ **Ferramenta de teste** (Error Injector)

**Disciplina**: Sistemas Distribuídos  
**Tecnologia**: Java + Maven + SQLite + TCP/IP + JSON