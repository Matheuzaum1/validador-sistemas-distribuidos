# Simple Error Injector v1.0

Proxy TCP simples e funcional para interceptar e injetar erros em comunicações cliente-servidor.

## Características

✅ **Interface Simples**: GUI minimalista e intuitiva  
✅ **Proxy TCP Funcional**: Relay completo bidirecional  
✅ **Injeção de Erros**: Checkboxes para ativar/desativar erros  
✅ **Logs em Tempo Real**: Monitoramento de todas as mensagens  
✅ **Thread-Safe**: Implementação robusta com threads  

## Como Usar

### 1. Compilar
```bash
mvn clean package
```

### 2. Executar
```bash
java -jar target/simple-error-injector-1.0.0.jar
```

### 3. Configurar
- **Porta Proxy**: 9999 (onde clientes conectam)
- **Host Servidor**: localhost (servidor real)  
- **Porta Servidor**: 8080 (onde servidor escuta)

### 4. Operar
1. Clique **🚀 INICIAR PROXY**
2. Marque checkboxes para ativar injeção de erros
3. Conecte seus clientes na porta do proxy
4. Monitore os logs em tempo real

## Funcionalidades

### Injeção de Erros
- **Cliente → Servidor**: Remove campo `operacao` do JSON
- **Servidor → Cliente**: Remove campo `status` do JSON

### Logs
- Timestamp de todas as mensagens
- Identificação da direção (CLIENTE→SERVIDOR / SERVIDOR→CLIENTE)
- Indicação clara quando erros são injetados (🔴)
- Status de conexões e desconexões

### Interface
- Botões coloridos (Verde=Start, Vermelho=Stop)
- Checkboxes para controle de injeção
- Área de logs com scroll automático
- Feedback visual imediato

## Arquitetura

```
Cliente → Proxy (porta 9999) → Servidor Real (porta 8080)
  ↑          ↓                      ↑          ↓
  ←─────── Relay Bidirecional ──────←
         (com injeção de erros)
```

## Vantagens

- **Simplicidade**: Uma única classe, fácil de entender
- **Robustez**: Tratamento adequado de threads e exceções  
- **Flexibilidade**: Fácil de modificar e estender
- **Performance**: Relay direto sem overhead desnecessário
- **Debugging**: Logs detalhados para troubleshooting