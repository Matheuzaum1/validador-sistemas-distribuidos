# 🚀 Como Executar o Sistema

Sistema cliente-servidor distribuído com operações CRUD de usuários e transações bancárias.

## Opção 1: Sistema Completo (Recomendado) ⭐

Inicia servidor e cliente automaticamente em janelas separadas:

```powershell
.\scripts\sistema.ps1
```

## Opção 2: Execução Manual

### 1️⃣ Compilar o Projeto
```powershell
.\scripts\compilar.ps1
```

### 2️⃣ Iniciar o Servidor (em um terminal)
```powershell
.\scripts\servidor.ps1
```

### 3️⃣ Iniciar o Cliente (em outro terminal)
```powershell
.\scripts\cliente.ps1
```

## Opção 3: Comandos Maven Diretos

```bash
# Compilar
mvn clean package -DskipTests

# Iniciar servidor
java -cp target/validador-sistemas-distribuidos-1.0.0.jar com.distribuidos.server.ServerMain

# Iniciar cliente
java -cp target/validador-sistemas-distribuidos-1.0.0.jar com.distribuidos.client.ClientMain
```

## 📋 Pré-requisitos

- **Java 17** ou superior
- **Maven 3.6+**
- **Windows 10/11** (para scripts .bat e .ps1)

## 🎯 Fluxo de Uso da Interface

### 1. Conexão com Servidor
Ao abrir o cliente, você verá um diálogo solicitando:
- **Host**: localhost (padrão)
- **Porta**: 8080 (padrão)

### 2. Autenticação
Após conectar, você terá duas opções:

#### **Criar Nova Conta**
- Preencha: Nome (mín. 6 caracteres)
- CPF (formatado automaticamente: 000.000.000-00)
- Senha (mín. 6 caracteres)
- O login é feito automaticamente após criação bem-sucedida

#### **Fazer Login**
- CPF (formatado automaticamente)
- Senha

### 3. Operações Disponíveis

Após login, você terá acesso a duas abas:

#### **Aba Conta** (Operações CRUD)
- **Consultar Dados**: Ver informações da conta e saldo
- **Atualizar Dados**: Alterar nome e/ou senha
- **Deletar Conta**: Remover conta permanentemente

#### **Aba Transações**
- **CPF Destino**: Formatado automaticamente (000.000.000-00)
- **Valor**: Formatado em reais (R$ 0.000,00)
- **Transferir**: Enviar dinheiro para outro usuário
- **Depositar**: Adicionar saldo à sua conta

## 📁 Estrutura do Projeto

```
validador-sistemas-distribuidos/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/distribuidos/
│   │   │       ├── client/        # Cliente GUI
│   │   │       ├── server/        # Servidor
│   │   │       ├── common/        # Classes compartilhadas
│   │   │       └── database/      # Gerenciador de BD
│   │   └── resources/
│   │       └── logback.xml        # Configuração de logs
│   └── test/                      # Testes unitários
├── scripts/
│   ├── build.bat                  # Compilação
│   ├── server.bat                 # Iniciar servidor
│   ├── client.bat                 # Iniciar cliente
│   └── sistema.bat                # Menu interativo
├── docs/                          # Documentação
├── compilar.bat                   # Script de compilação
├── compilar.ps1                   # Script PS de compilação
├── iniciar-servidor.bat          # Iniciar servidor
├── iniciar-servidor.ps1          # Iniciar servidor (PS)
├── iniciar-cliente.bat           # Iniciar cliente
├── iniciar-cliente.ps1           # Iniciar cliente (PS)
└── pom.xml                        # Configuração Maven
```

## 🔧 Resolução de Problemas

### Erro: "JAR não encontrado"
```cmd
compilar.bat
```

### Erro: "Porta 8080 já está em uso"
Pare outros processos Java ou altere a porta no código.

### Erro: "Conexão recusada"
Certifique-se de que o servidor está rodando antes de iniciar o cliente.

### Limpar e recompilar
```cmd
mvn clean package
```

## 📝 Notas Importantes

- **Campos Formatados**: CPF e valores monetários são formatados automaticamente durante a digitação
- **Login Automático**: Após criar conta, o login é feito automaticamente
- **Validações**: Todos os campos têm validação em tempo real
- **Banco de Dados**: SQLite (usuarios.db) é criado automaticamente

## 🎨 Recursos da Interface

- ✅ Formatação automática de CPF (000.000.000-00)
- ✅ Formatação automática de valores monetários (R$ 0.000,00)
- ✅ Fluxo intuitivo: Conexão → Autenticação → Operações
- ✅ Login automático após criação de conta
- ✅ Validação em tempo real de campos
- ✅ Mensagens toast para feedback visual
- ✅ Log detalhado de todas as operações

## 📄 Licença

Projeto acadêmico - Sistemas Distribuídos
