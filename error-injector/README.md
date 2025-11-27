# Error Injector - Sistema de Injeção de Erros

## 🎯 Visão Geral

O **Error Injector** é uma ferramenta completa para testar a robustez e conformidade de sistemas distribuídos através da injeção de erros controlados. Ele permite testar **TODAS as possibilidades de erros** tanto para cliente quanto para servidor, seguindo rigorosamente o protocolo 4.11.

## ✨ Funcionalidades

### 🖥️ Interface Gráfica Completa
- **3 Abas Principais:**
  - 📡 **Servidor Mock**: Simula servidor que responde com erros injetados
  - 💻 **Cliente Mock**: Simula cliente que envia mensagens malformadas  
  - 🧪 **Gerador de Testes**: Cria casos de teste automatizados

### 🔧 47 Tipos de Erro Cobertos
- **Campos Ausentes/Nulos**: operacao, token, usuario, valor, etc.
- **Erros de Login**: senha incorreta, usuario inexistente, etc.
- **Erros de Usuário**: CPF inválido, saldo insuficiente, etc.
- **Erros de Transação**: valor inválido, tipos incorretos, etc.
- **Status False**: Respostas com status:false para diferentes operações
- **Erros de Formato**: JSON malformado, encoding incorreto, etc.

### 🚀 Testes Automatizados
- **Sequência Completa**: Executa todos os tipos de erro automaticamente
- **Geração de Relatórios**: Exporta casos de teste e resultados
- **Análise de Respostas**: Detecta como o servidor lida com cada erro

## 🏃‍♂️ Como Usar

### 1. Executar o JAR
```bash
cd error-injector
java -jar target/error-injector-1.0.0.jar
```

### 2. Servidor Mock
1. **Configurar:**
   - Porta do servidor (ex: 8080)
   - Tipo de erro para injetar
   - Tipo de mensagem de resposta
2. **Iniciar:** Clique em "🎯 Iniciar Servidor"
3. **Conectar:** Use seu cliente real para conectar ao servidor mock

### 3. Cliente Mock  
1. **Configurar:**
   - Host e porta do servidor real (ex: localhost:8080)
   - Tipo de erro para injetar
   - Mensagem a ser enviada
2. **Conectar:** Clique em "🔗 Conectar ao Servidor"
3. **Enviar:** Clique em "📤 Enviar Mensagem com Erro"

### 4. Testes Automatizados
1. **Gerar Casos:** Clique em "🧪 Gerar Todos os Casos de Teste"
2. **Exportar:** Clique em "📁 Exportar Casos de Teste"
3. **Executar:** Use "🚀 Executar Sequência Completa" para testes automáticos

## 🎛️ Exemplos de Uso

### Testar Robustez do Servidor
```bash
1. Configure o Cliente Mock para seu servidor real
2. Selecione erro "JSON malformado" 
3. Digite mensagem: {"operacao": "conectar"}
4. Envie e verifique como o servidor responde
```

### Testar Robustez do Cliente
```bash  
1. Configure o Servidor Mock na porta 8080
2. Selecione erro "Operação ausente"
3. Configure resposta de conectar
4. Conecte seu cliente real ao servidor mock
```

## 📊 Logs e Monitoramento

- **Logs Detalhados**: Cada operação é logada com emojis para fácil identificação
- **Análise Automática**: Detecta se erros são tratados conforme protocolo 4.11
- **Estatísticas**: Conta mensagens enviadas, conexões aceitas, etc.

## 🔍 Tipos de Erro Destacados

### Mais Críticos para Testar:
- ❌ **MISSING_OPERACAO**: Mensagem sem campo "operacao"
- 🔒 **LOGIN_SENHA_INCORRETA**: Senha inválida no login
- 💰 **SALDO_INSUFICIENTE**: Valor maior que saldo disponível
- 📝 **MALFORMED_JSON**: Estrutura JSON inválida
- ⚠️ **WRONG_FIRST_OPERATION**: Primeira operação não é "conectar"

## 🛠️ Arquitetura Técnica

- **Java 21**: Linguagem principal
- **Swing**: Interface gráfica nativa  
- **Jackson**: Processamento JSON
- **SLF4J/Logback**: Sistema de logs estruturado
- **Maven**: Gerenciamento de dependências

## 📁 Estrutura do Projeto

```
error-injector/
├── src/main/java/com/distribuidos/errorinjector/
│   ├── core/           # ErrorInjector e ErrorType
│   ├── mockserver/     # Servidor mock com injeção de erros
│   ├── mockclient/     # Cliente mock para testes
│   └── ui/             # Interface gráfica principal
├── target/
│   └── error-injector-1.0.0.jar  # JAR executável
└── pom.xml             # Configuração Maven
```

## 🎯 Próximos Passos

Após executar os testes com o Error Injector:

1. **Analise os Logs**: Verifique se seu sistema detecta e reporta erros corretamente
2. **Implemente Melhorias**: Adicione tratamento para erros não detectados
3. **Automatize**: Integre os casos de teste ao seu pipeline de CI/CD
4. **Documente**: Use os relatórios gerados para documentar a robustez do sistema

---

**💡 Dica:** Execute testes regulares com o Error Injector para garantir que mudanças no código não quebrem o tratamento de erros existente.

**⚠️ Aviso:** Esta ferramenta é para testes em ambientes controlados. Não use em produção.