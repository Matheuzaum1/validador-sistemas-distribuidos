# Guia de Implantação - NewPix Banking System

## 📋 Pré-requisitos

### 1. Java 17+ 
```bash
# Verificar versão do Java
java -version

# Deve mostrar Java 17 ou superior
```

### 2. Maven 3.6+
```bash
# Verificar versão do Maven  
mvn -version

# Deve mostrar Maven 3.6.0 ou superior
```

### 3. Git (opcional, para clonar o repositório)
```bash
git --version
```

## 🚀 Setup Automático

### Método 1: Script PowerShell (Recomendado)
```powershell
# 1. Navegue para o diretório do projeto
cd caminho\para\validador-sistemas-distribuidos

# 2. Execute o setup automático
powershell -ExecutionPolicy Bypass -File "scripts\setup-inicial.ps1"
```

### Método 2: Script Batch
```batch
# 1. Navegue para o diretório do projeto
cd caminho\para\validador-sistemas-distribuidos

# 2. Execute o setup automático
scripts\setup-inicial.bat
```

## 🔧 Setup Manual (Caso os scripts não funcionem)

### 1. Baixar Dependências
```bash
mvn clean compile
```

### 2. Testar Compilação
```bash
mvn test-compile
```

### 3. Executar Sistema
```bash
# Servidor
java -cp "target/classes;.m2/repository/com/fasterxml/jackson/core/jackson-core/2.19.2/jackson-core-2.19.2.jar;.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.19.2/jackson-databind-2.19.2.jar;.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.19.2/jackson-annotations-2.19.2.jar;.m2/repository/org/xerial/sqlite-jdbc/3.43.0.0/sqlite-jdbc-3.43.0.0.jar;.m2/repository/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar" com.newpix.server.BankServer

# Cliente
java -cp "target/classes;.m2/repository/com/fasterxml/jackson/core/jackson-core/2.19.2/jackson-core-2.19.2.jar;.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.19.2/jackson-databind-2.19.2.jar;.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.19.2/jackson-annotations-2.19.2.jar;.m2/repository/org/xerial/sqlite-jdbc/3.43.0.0/sqlite-jdbc-3.43.0.0.jar;.m2/repository/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar" com.newpix.client.BankClient
```

## 🏃‍♂️ Executar Sistema

### Scripts Automáticos
```bash
# Iniciar Servidor
.\scripts\start-server.ps1

# Iniciar Cliente (em outro terminal)  
.\scripts\start-client.ps1
```

### Execução Manual
```bash
# Servidor (Terminal 1)
java -cp "target/classes;..." com.newpix.server.BankServer

# Cliente (Terminal 2)
java -cp "target/classes;..." com.newpix.client.BankClient
```

## ⚠️ Solução de Problemas

### Problema: "Illegal character: '\ufeff'"
**Solução**: Os arquivos Java foram salvos com BOM UTF-8. Use o script de correção:
```bash
powershell -ExecutionPolicy Bypass -File "scripts\diagnostico-dependencias.ps1"
```

### Problema: Dependências não baixam
**Soluções**:
1. Verificar conexão com internet
2. Limpar cache Maven: `mvn clean`
3. Usar settings.xml local (já incluído no projeto)
4. Executar diagnóstico: `.\scripts\diagnostico-dependencias.ps1`

### Problema: "ClassNotFoundException"
**Soluções**:
1. Compilar primeiro: `mvn clean compile`
2. Verificar classpath nos scripts
3. Recriar dependências: `mvn dependency:resolve`

### Problema: Porta 8080 em uso
**Solução**: Modificar porta no arquivo `BankServer.java`:
```java
private static final int PORT = 8081; // Alterar aqui
```

### Problema: GUI não aparece
**Solução**: Scripts já incluem `-Djava.awt.headless=false`. Se persistir:
```bash
java -Djava.awt.headless=false -Djava.awt.Window.locationByPlatform=true ...
```

## 📁 Estrutura do Projeto

```
validador-sistemas-distribuidos/
├── src/main/java/              # Código fonte
├── target/classes/             # Classes compiladas  
├── .m2/repository/             # Dependências locais
├── docs/                       # Documentação
├── scripts/                    # Scripts de automação
├── validador-original/         # Código original
├── pom.xml                     # Configuração Maven
└── .m2/settings.xml           # Configurações Maven locais
```

## 🔒 Segurança

### Senhas Padrão (ALTERAR EM PRODUÇÃO)
- Admin: `admin123`
- Usuários de teste: `123456`

### Criptografia
- Senhas: BCrypt
- Comunicação: JSON sobre TCP (adicionar TLS em produção)

## 📊 Monitoramento

### Logs
- Servidor: Console e arquivo `bank_server.log`
- Cliente: Console

### Banco de Dados  
- Arquivo: `banking_system.db` (SQLite)
- Localização: Diretório do projeto

### Portas
- Servidor: 8080 (TCP)
- Cliente: Conexão automática

## 🚀 Funcionalidades

### Operações Bancárias
- ✅ Criar conta
- ✅ Login/Logout
- ✅ Depósito
- ✅ Saque
- ✅ Transferência
- ✅ PIX (Chave CPF)
- ✅ Extrato
- ✅ Histórico completo

### Interface
- ✅ GUI Swing
- ✅ Multi-usuário simultâneo
- ✅ Validações completas
- ✅ Tratamento de erros

## 🆘 Suporte

Em caso de problemas:

1. **Execute o diagnóstico**:
   ```bash
   .\scripts\diagnostico-dependencias.ps1
   ```

2. **Verifique os logs** nos consoles do servidor e cliente

3. **Limpe e recompile**:
   ```bash
   mvn clean compile
   ```

4. **Recrie o setup**:
   ```bash
   .\scripts\setup-inicial.ps1
   ```

---

**Versão**: 1.0.0  
**Autor**: NewPix Banking Team  
**Licença**: MIT  
**Suporte**: Documentação completa em `docs/`
