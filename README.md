# NewPix Banking System

Sistema bancário moderno desenvolvido em Java com arquitetura cliente-servidor, interface gráfica Swing e scripts de automação cross-platform.

## ✨ Características

- **🔐 Autenticação Segura**: Login e cadastro com criptografia BCrypt
- **💳 Validação CPF**: Formatos 000.000.000-00 ou 00000000000
- **🏦 Transações PIX**: Transferências instantâneas entre contas
- **🖥️ Interface Moderna**: LoginWindow com design responsivo
- **⚡ Scripts Cross-Platform**: Windows (PowerShell) e Linux/macOS (Bash)
- **📊 Servidor Multithreaded**: Múltiplos clientes simultâneos

## 🚀 Início Rápido

### Pré-requisitos
- Java 17+
- Maven 3.6+

### Execução Rápida

**Windows:**
```powershell
.\newpix.ps1 both-gui    # Sistema completo
```

**Linux/macOS:**
```bash
./newpix.sh both-gui     # Sistema completo
```

### Execução Manual
```bash
# Compilar
mvn clean compile dependency:copy-dependencies

# Servidor (Terminal 1)
java -cp "target/classes:target/dependency/*" com.newpix.server.gui.ServerGUI

# Cliente (Terminal 2) 
java -cp "target/classes:target/dependency/*" com.newpix.client.gui.LoginWindow
```

## 📋 Comandos

| Comando | Descrição |
|---------|-----------|
| `build` | Compila o projeto |
| `server` | Inicia servidor (porta 8080) |
| `client` | Inicia cliente |
| `status` | Verifica servidor |
| `stop` | Para todos os processos |
| `clean` | Limpa arquivos compilados |
| `help` | Ajuda completa |

## 🏗️ Arquitetura

```
src/main/java/com/newpix/
├── client/
│   └── gui/           # LoginWindow, CadastroWindow, MainGUI
├── server/            # NewPixServer, ClientHandler, MessageProcessor
├── model/             # Usuario, Transacao, Sessao
├── dao/               # DatabaseManager, UsuarioDAO, TransacaoDAO
├── service/           # UsuarioService, TransacaoService
└── util/              # CpfUtil, ConnectionConfig, ErrorHandler
```

## 💡 Funcionalidades

### Sistema de Login/Cadastro
- **Cadastro**: Nome + CPF + Senha (janela dedicada)
- **Login**: CPF + Senha (validação segura)
- **Navegação**: Transição suave entre telas

### Validação CPF
- ✅ Formato: `100.181.699-45`
- ✅ Numérico: `10018169945`
- ✅ Dígitos verificadores
- ✅ Conversão automática

### Transações PIX
- 💰 Transferências instantâneas
- 📊 Histórico de transações
- 🔍 Consulta de saldo
- ⚡ Processamento em tempo real

## 📁 Estrutura Limpa

```
validador-sistemas-distribuidos/
├── src/               # Código fonte Java
├── target/            # Arquivos compilados
├── docs/              # Documentação técnica
├── .vscode/           # Configurações VS Code
├── newpix.db          # Banco SQLite
├── newpix.ps1         # Script unificado
├── pom.xml            # Dependências Maven
├── LICENSE            # Licença MIT
└── README.md          # Este arquivo
```

## 🔧 Dependências

```xml
<!-- Principais dependências do pom.xml -->
- Jackson 2.19.2 (JSON processing)
- SQLite JDBC 3.43.0.0 (Database)
- BCrypt 0.4 (Password hashing)
- JUnit Jupiter 5.9.2 (Tests)
```

## 🐛 Solução de Problemas

### Servidor não conecta
```powershell
.\newpix.ps1 clean && .\newpix.ps1 build && .\newpix.ps1 server
```

### Erro de dependências
```powershell
mvn clean install -U
```

### Processos travados
```powershell
.\newpix.ps1 stop
```

## 📖 Uso do Sistema

1. **Primeiro Acesso**
   - Compile: `.\newpix.ps1 build`
   - Inicie servidor: `.\newpix.ps1 server`
   - Abra cliente: `.\newpix.ps1 client`

2. **Cadastro de Usuário**
   - Clique em "Cadastrar"
   - Preencha: Nome, CPF, Senha
   - Confirme senha
   - Clique "Cadastrar"

3. **Login**
   - Digite CPF e senha
   - Clique "Login"
   - Acesse o sistema bancário

4. **Transações PIX**
   - Digite CPF do destinatário
   - Insira valor
   - Confirme transferência

## 🎯 Organização do Projeto

Este projeto passou por uma **reorganização completa**:

### ✅ Limpeza Realizada
- 🗑️ Removido diretório `.m2` local (26MB+ desnecessários)
- 🗑️ Eliminados 22+ scripts redundantes 
- 🗑️ Consolidados arquivos `.md` duplicados
- 🗑️ Removidos diretórios `scripts/` obsoletos
- 🗑️ Eliminado `validador-original/` duplicado

### ⚡ Scripts Cross-Platform
- **Windows**: `newpix.ps1` (PowerShell)
- **Linux/macOS**: `newpix.sh` (Bash)
- **Funcionalidades**: build, run, status, test, changelog

### 📊 Resultado
- **Antes**: 48+ arquivos MD redundantes
- **Depois**: 4 arquivos essenciais
- **Manutenção**: Simplificada drasticamente
- **Cross-Platform**: Suporte completo Windows/Linux/macOS

## 📚 Documentação

- **[SCRIPTS.md](SCRIPTS.md)** - Guia completo dos scripts de automação
- **[CHANGELOG.md](CHANGELOG.md)** - Histórico detalhado de mudanças
- **[docs/Requisitos.md](docs/Requisitos.md)** - Especificação original do projeto

---

*Desenvolvido com ❤️ por Matheuzaum1*
