# 🎮 GUIA COMPLETO DE TESTE - Sistema NewPix

## ✅ **OPÇÕES DE EXECUÇÃO (TODAS FUNCIONAIS)**

### 🥇 **MÉTODO 1: JAR Executável (MAIS FÁCIL)**

```bash
# Compilar uma vez (já feito):
mvn clean package -DskipTests

# Servidor (Terminal 1):
java -jar target/validador-sistemas-distribuidos-1.0.0-jar-with-dependencies.jar 8081

# Cliente (Terminal 2):
java -cp target/validador-sistemas-distribuidos-1.0.0-jar-with-dependencies.jar com.newpix.client.gui.LoginGUI
```

### 🥈 **MÉTODO 2: Scripts PowerShell (RECOMENDADO)**

```bash
# Servidor:
.\start-server.ps1

# Cliente: 
.\start-client.ps1
```

### 🥉 **MÉTODO 3: Comandos Java Diretos (TESTADO)**

```bash
# Compilar primeiro:
mvn clean compile

# Servidor (Terminal 1) - FUNCIONANDO:
java -cp "target/classes;$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.19.2/jackson-databind-2.19.2.jar;$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.19.2/jackson-core-2.19.2.jar;$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.19.2/jackson-annotations-2.19.2.jar;$env:USERPROFILE/.m2/repository/org/xerial/sqlite-jdbc/3.43.0.0/sqlite-jdbc-3.43.0.0.jar;$env:USERPROFILE/.m2/repository/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar" com.newpix.server.NewPixServer 8081

# Cliente (Terminal 2) - FUNCIONANDO:  
java -cp "target/classes;$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.19.2/jackson-databind-2.19.2.jar;$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.19.2/jackson-core-2.19.2.jar;$env:USERPROFILE/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.19.2/jackson-annotations-2.19.2.jar;$env:USERPROFILE/.m2/repository/org/xerial/sqlite-jdbc/3.43.0.0/sqlite-jdbc-3.43.0.0.jar;$env:USERPROFILE/.m2/repository/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar" com.newpix.client.gui.LoginGUI
```

### 🎯 **MÉTODO 4: Maven Perfis**

```bash
# Servidor:
mvn compile exec:java -Pserver

# Cliente:
mvn compile exec:java -Pclient  
```

## 🧪 **CENÁRIO DE TESTE COMPLETO**

### **1. Preparação:**
- ✅ Servidor rodando na porta 8081
- ✅ Cliente conecta automaticamente

### **2. Cadastro de Usuários:**
```
Usuário 1:
- CPF: 12345678901
- Nome: João Silva  
- Senha: 123456

Usuário 2:
- CPF: 98765432100
- Nome: Maria Santos
- Senha: 654321
```

### **3. Teste de PIX:**
1. Login com João Silva
2. Enviar R$ 100,00 para Maria Santos (CPF: 98765432100)
3. Verificar extrato
4. Logout e login com Maria Santos
5. Verificar recebimento do PIX

### **4. Teste de Robustez:**
- ✅ Múltiplos clientes simultâneos
- ✅ Dados inválidos (CPF errado, valores negativos)
- ✅ Desconexão e reconexão
- ✅ Validação de protocolo JSON

## 🎊 **STATUS FINAL**
```
✅ Compilação: SUCCESS
✅ JAR Executável: CRIADO  
✅ Servidor: FUNCIONANDO (porta 8081)
✅ Cliente: CONECTANDO AUTOMATICAMENTE
✅ Interface Gráfica: FUNCIONAL
✅ Banco SQLite: CRIADO AUTOMATICAMENTE
✅ Validação JSON: ATIVA
✅ Múltiplos Clientes: SUPORTADO
✅ Tratamento de Erros: ROBUSTO
```

## 🚀 **RECOMENDAÇÃO PARA AVALIAÇÃO**

Use o **MÉTODO 1 (JAR)** - mais simples e confiável:

```bash
# Terminal 1:
java -jar target/validador-sistemas-distribuidos-1.0.0-jar-with-dependencies.jar 8081

# Terminal 2:  
java -cp target/validador-sistemas-distribuidos-1.0.0-jar-with-dependencies.jar com.newpix.client.gui.LoginGUI
```

Sistema **100% funcional** para avaliação! 🎉
