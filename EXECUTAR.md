# Como Executar o Sistema

## Pré-requisitos

- Java 17 ou superior instalado
- Maven 3.6+ instalado

## Executar o Sistema

### Opção 1: Launcher Unificado (Recomendado)
```bash
# Execute o launcher e escolha a opção
.\launcher.bat
```

### Opção 2: Scripts Individuais
```bash
# Servidor
.\start-server.bat

# Cliente (em outro terminal)
.\start-client.bat
```

### Opção 3: Compilação Manual
```bash
# Definir Java 17 (se necessário)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"

# Compilar
mvn clean compile

# Executar servidor
mvn exec:java -Dexec.mainClass=com.distribuidos.server.ServerMain

# Executar cliente (em outro terminal)
mvn exec:java -Dexec.mainClass=com.distribuidos.client.ClientMain
```

## Teste do Sistema

1. Execute o servidor primeiro (opção 1 no launcher)
2. Execute o cliente (opção 2 no launcher)
3. No cliente:
   - Conecte ao servidor (localhost:8080)
   - Crie um usuário
   - Faça login
   - Teste as funcionalidades (depósito, transferência, extrato)

## Funcionalidades Implementadas

### Cliente

- ✅ Conexão com servidor
- ✅ Cadastro de usuário
- ✅ Login/Logout
- ✅ Depósito na conta
- ✅ Transferência entre contas
- ✅ Consulta de extrato
- ✅ Interface gráfica intuitiva

### Servidor

- ✅ Aceita múltiplas conexões
- ✅ Banco de dados SQLite
- ✅ Autenticação por token
- ✅ Validação de segurança
- ✅ Interface gráfica de administração
- ✅ Logs detalhados

### Validações

- ✅ Formatação de CPF
- ✅ Validação de campos obrigatórios
- ✅ Segurança de sessão
- ✅ Controle de acesso por usuário