# NewPix - Sistema Bancário

Sistema bancário moderno desenvolvido em Java com arquitetura cliente-servidor, interface gráfica Swing e gerenciamento unificado via PowerShell.

## ✨ Características

- **🔐 Autenticação Segura**: Login e cadastro com criptografia BCrypt
- **💳 Validação CPF**: Formatos 100.181.699-45 ou 10018169945
- **🏦 Transações PIX**: Transferências instantâneas entre contas
- **🖥️ Interface Moderna**: Janelas dedicadas para login/cadastro
- **⚡ Script Unificado**: Gerenciamento completo via `newpix.ps1`
- **📊 Servidor Multithreaded**: Múltiplos clientes simultâneos

## 🚀 Início Rápido

### Pré-requisitos
- Java 8+
- Maven 3.6+
- PowerShell (Windows)

### Execução
```powershell
# Compilar e executar (primeira vez)
.\newpix.ps1 build
.\newpix.ps1 server    # Terminal 1
.\newpix.ps1 client    # Terminal 2

# Menu interativo
.\newpix.ps1
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

### ⚡ Script Unificado
Anteriormente: 26+ scripts dispersos (`.bat`, `.ps1`, `.sh`)
**Agora**: 1 único script `newpix.ps1` com todas as funcionalidades

### 📊 Resultado
- **Antes**: 50+ arquivos de configuração/script
- **Depois**: Estrutura limpa e organizada
- **Manutenção**: Simplificada drasticamente
- **Uso**: Interface única e intuitiva

---
*Projeto reorganizado e otimizado - Use apenas `.\newpix.ps1` para todas as operações.*
