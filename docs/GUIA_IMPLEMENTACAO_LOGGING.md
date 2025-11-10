# Guia de Implementação - Melhorias de Logging e Tratamento de Erros

## ✅ O Que Foi Criado

### 1. **Hierarquia de Custom Exceptions** 
```
com/distribuidos/exception/
├── ErrorCode.java (enum com todos os códigos de erro)
├── ApplicationException.java (exceção base)
├── ValidationException.java
├── InsufficientBalanceException.java
├── DatabaseException.java
└── ConnectionException.java
```

**Uso:**
```java
// Antes (genérico)
throw new RuntimeException("Erro na validação");

// Agora (específico)
throw new ValidationException("CPF inválido: formato incorreto");
throw new InsufficientBalanceException(100.0, 50.0);
```

### 2. **MDC Manager** 
`com.distribuidos.common.MDCManager`

Gerencia contexto de correlação entre logs.

**Uso:**
```java
// No início da requisição
MDCManager.initializeRequestId();
MDCManager.setUserId(usuario.getCpf());

// Logs automaticamente incluem [requestId]
logger.info("Usuário autenticado");

// Na limpeza
MDCManager.clear();
```

### 3. **Global Exception Handler**
`com.distribuidos.handler.GlobalExceptionHandler`

Centraliza tratamento de exceções com logging estruturado.

**Uso:**
```java
try {
    // operação
} catch (Exception e) {
    String response = GlobalExceptionHandler.handleException(e, "autenticação");
    return response;
}
```

### 4. **Retry Utility**
`com.distribuidos.util.RetryUtil`

Implementa retry automático com backoff exponencial.

**Uso:**
```java
// Para operações que retornam valor
Connection conn = RetryUtil.executeWithRetry(
    () -> DriverManager.getConnection(DB_URL),
    "conexão ao banco de dados",
    3,  // max retries
    100 // delay inicial em ms
);

// Para operações void
RetryUtil.executeVoidWithRetry(
    () -> persistUser(user),
    "persistência de usuário"
);
```

### 5. **Logback Melhorado**
`src/main/resources/logback.xml`

- ✅ Async appenders (melhor performance)
- ✅ Arquivo separado para erros
- ✅ Correlação com RequestId
- ✅ Rolling policy otimizado
- ✅ Padrão de log com contexto

---

## 🔄 Como Integrar no Código Existente

### ClientHandler.java (Integração Recomendada)

```java
import com.distribuidos.common.MDCManager;
import com.distribuidos.handler.GlobalExceptionHandler;

public class ClientHandler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    
    @Override
    public void run() {
        // Inicializar contexto de correlação
        MDCManager.initializeRequestId();
        MDCManager.setUserId(username);
        
        try (Socket socket = new Socket(serverHost, serverPort)) {
            logger.info("Conectado ao servidor {}:{}", serverHost, serverPort);
            
            // processar requisição
            
        } catch (SocketException e) {
            String response = GlobalExceptionHandler.handleException(e, "conexão com servidor");
            logger.error("Falha na conexão: {}", response);
            
        } catch (Exception e) {
            String response = GlobalExceptionHandler.handleException(e, "processamento de requisição");
            logger.error("Erro na requisição: {}", response);
            
        } finally {
            MDCManager.clear();
        }
    }
}
```

### ServerHandler.java (Integração Recomendada)

```java
import com.distribuidos.common.MDCManager;
import com.distribuidos.handler.GlobalExceptionHandler;

public class ServerHandler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    
    @Override
    public void run() {
        MDCManager.initializeRequestId();
        
        try (Socket socket = new Socket(...)) {
            logger.info("Cliente conectado: {}", socket.getInetAddress());
            
            // processar cliente
            MDCManager.setUserId(clientUsername);
            
        } catch (DatabaseException e) {
            logger.error("Erro de BD: {}", e.getErrorCode());
            
        } catch (ValidationException e) {
            logger.warn("Validação falhou: {}", e.getMessage());
            
        } catch (Exception e) {
            String response = GlobalExceptionHandler.handleException(e, "handler do cliente");
            logger.error(response);
            
        } finally {
            MDCManager.clear();
        }
    }
}
```

### DatabaseManager.java (Já Iniciado)

```java
import com.distribuidos.exception.DatabaseException;

// ✅ Já implementado
private void initializeDatabase() {
    try (Connection conn = DriverManager.getConnection(DB_URL)) {
        // ...
    } catch (SQLException e) {
        logger.error("Erro ao inicializar BD: {}", e.getMessage(), e);
        throw new DatabaseException("Falha ao inicializar BD", e);
    }
}
```

---

## 📋 Checklist de Próximas Implementações

- [ ] **ClientMain.java** - Adicionar MDC e GlobalExceptionHandler
  ```java
  MDCManager.initializeRequestId();
  try {
      // GUI initialization
  } catch (Exception e) {
      GlobalExceptionHandler.handleException(e, "inicialização do cliente");
  } finally {
      MDCManager.clear();
  }
  ```

- [ ] **ServerMain.java** - Adicionar MDC em listeners
  ```java
  serverSocket = RetryUtil.executeWithRetry(
      () -> new ServerSocket(PORT),
      "inicialização do servidor",
      3,
      100
  );
  ```

- [ ] **ClientConnection.java** - Adicionar retry em operações de rede
  ```java
  Connection conn = RetryUtil.executeWithRetry(
      () -> connectToServer(host, port),
      "conexão ao servidor"
  );
  ```

- [ ] **ValidationHelper.java** - Usar ValidationException
  ```java
  if (!isValidCPF(cpf)) {
      throw new ValidationException("CPF inválido: " + cpf);
  }
  ```

---

## 📊 Exemplo Completo de Fluxo com Logs

```
[2025-11-10 11:15:23.456] [550e8400-e29b-41d4-a716-446655440000] [client-thread-1] INFO  
ClientMain - Aplicação iniciada

[2025-11-10 11:15:23.789] [550e8400-e29b-41d4-a716-446655440000] [client-thread-1] DEBUG  
MDCManager - Contexto inicializado: RequestId=550e8400-e29b-41d4-a716-446655440000, UserId=123.456.789-01

[2025-11-10 11:15:24.100] [550e8400-e29b-41d4-a716-446655440000] [socket-handler] INFO  
ServerHandler - Cliente conectado: /192.168.1.100

[2025-11-10 11:15:24.234] [550e8400-e29b-41d4-a716-446655440000] [socket-handler] DEBUG  
DatabaseManager - Consultando usuário: 123.456.789-01

[2025-11-10 11:15:24.356] [550e8400-e29b-41d4-a716-446655440000] [socket-handler] INFO  
GlobalExceptionHandler - Autenticação bem-sucedida

// Logs no arquivo: logs/aplicacao.log
// Erros no arquivo: logs/erro.log
```

---

## 🚀 Performance

**Melhorias com Async Appenders:**
- ❌ Antes: Thread bloqueada escrevendo disco
- ✅ Depois: Thread com fila de 512 items, write assíncrono

**Tamanho esperado de logs:**
- ~2-5 MB por dia em operação normal
- ~1 MB por dia apenas erros (arquivo separado)
- Limpeza automática após 30 dias

---

## 📞 Referência Rápida

| Classe | Uso | Importação |
|--------|-----|-----------|
| MDCManager | Correlação de logs | `com.distribuidos.common.MDCManager` |
| GlobalExceptionHandler | Tratamento centralizado | `com.distribuidos.handler.GlobalExceptionHandler` |
| RetryUtil | Retry automático | `com.distribuidos.util.RetryUtil` |
| ValidationException | Validações | `com.distribuidos.exception.ValidationException` |
| DatabaseException | BD erros | `com.distribuidos.exception.DatabaseException` |
| ConnectionException | Conexões | `com.distribuidos.exception.ConnectionException` |

---

## ⚠️ Importante

🔒 **Validator.java e RulesEnum.java NÃO FORAM ALTERADOS**
- Integridade com equipe mantida
- Sistema distribuído funciona como esperado
- Sem conflitos de merge

✅ **O que foi adicionado é independente e não quebra nada existente**
