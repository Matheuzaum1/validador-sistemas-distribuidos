# Sistema Validador - Distribuído

Um sistema bancário distribuído em Java para comunicação cliente-servidor com protocolo JSON personalizado.

## 🚀 Início Rápido

### Pré-requisitos
- Java 11+ (testado com Java 25)
- Maven 3.6+

### Executar o Sistema

```bash
# Método 1: Script principal com menu interativo
.\sistema.bat

# Método 2: Scripts individuais
.\scripts\build.bat     # Compilar
.\scripts\server.bat    # Servidor
.\scripts\client.bat    # Cliente
```

### Conexão de Teste
- **Servidor local**: `localhost:8080`
- **CPF teste**: `123.456.789-01`
- **Senha teste**: `123456`

## 📁 Estrutura do Projeto

```
validador-sistemas-distribuidos/
├── src/                          # Código fonte
│   ├── main/java/com/distribuidos/
│   │   ├── client/              # Cliente GUI
│   │   ├── server/              # Servidor
│   │   ├── common/              # Classes compartilhadas
│   │   └── database/            # Gerenciamento SQLite
│   └── test/                    # Testes unitários
├── scripts/                     # Scripts de execução
│   ├── server.bat              # Iniciar servidor
│   ├── client.bat              # Iniciar cliente
│   ├── build.bat               # Compilar projeto
│   └── clean.bat               # Limpar e recompilar
├── docs/                        # Documentação
│   ├── protocol.md             # Protocolo de comunicação
│   └── development.md          # Guia de desenvolvimento
├── Essentials/                  # Validador de protocolo
│   ├── Validator.java
│   ├── RulesEnum.java
│   └── README.md               # Protocolo bancário
└── target/                      # Arquivos compilados
```

## 🔧 Scripts Disponíveis

| Script | Descrição |
|--------|-----------|
| `sistema.bat` | Script principal com menu interativo |
| `scripts\build.bat` | Compilar projeto |
| `scripts\server.bat` | Iniciar servidor |
| `scripts\client.bat` | Iniciar cliente |

## 🌐 Protocolo de Comunicação

O sistema usa um protocolo JSON personalizado. Consulte [docs/protocol.md](docs/protocol.md) para detalhes completos.

### Exemplo de Login
```json
// Cliente → Servidor
{
  "operacao": "usuario_login",
  "cpf": "123.456.789-01",
  "senha": "123456"
}

// Servidor → Cliente
{
  "operacao": "usuario_login",
  "token": "abc123...",
  "status": true,
  "info": "Login bem-sucedido."
}
```

## 🏗️ Desenvolvimento

### Compilar Manualmente
```bash
mvn clean compile package
```

### Executar Testes
```bash
mvn test
```

### Gerar JAR Executável
```bash
mvn package
# Gera: target/validador-sistemas-distribuidos-1.0.0-server.jar
```

## 🎯 Funcionalidades

- ✅ **Autenticação**: Login/logout com tokens
- ✅ **CRUD Usuários**: Criar, ler, atualizar, deletar
- ✅ **Transações**: Transferências entre usuários
- ✅ **Depósitos**: Adicionar saldo à conta
- ✅ **Histórico**: Consulta de transações por período
- ✅ **Banco SQLite**: Persistência de dados
- ✅ **Interface Gráfica**: Cliente Java Swing
- ✅ **Protocolo Validado**: Conformidade garantida

## 👥 Usuários de Teste

| CPF | Nome | Senha |
|-----|------|-------|
| 123.456.789-01 | João Silva Santos | 123456 |
| 987.654.321-02 | Maria Santos Oliveira | 654321 |
| 111.222.333-44 | Pedro Oliveira Costa | password |
| 555.666.777-88 | Ana Costa Ferreira | 123abc |
| 444.555.666-77 | Carlos Eduardo Lima | carlos123 |
| 333.444.555-66 | Fernanda Alves Souza | fernanda456 |
| 222.333.444-55 | Roberto Silva Junior | roberto789 |
| 666.777.888-99 | Juliana Pereira Rocha | juliana321 |
| 777.888.999-00 | Marcos Antonio Dias | marcos654 |
| 888.999.000-11 | Luciana Martins Cruz | luciana987 |

## 🔒 Segurança

- Senhas hasheadas com BCrypt
- Tokens de sessão únicos
- Validação de protocolo em todas as mensagens
- Timeouts de conexão configurados

## 📊 Tecnologias

- **Java 11+**: Linguagem principal
- **Maven**: Gerenciamento de dependências
- **SQLite**: Banco de dados
- **Jackson**: Processamento JSON
- **Logback**: Sistema de logs
- **JUnit**: Testes unitários
- **Java Swing**: Interface gráfica

## 🐛 Solução de Problemas

### Erro de Compilação
```bash
.\scripts\clean.bat  # Limpa e recompila
```

### Timeout de Conexão
- Verifique se o servidor está rodando
- Confirme IP e porta (padrão: localhost:8080)
- Timeout configurado para 5 segundos

### Arquivo JAR em Uso
```bash
.\scripts\stop.bat   # Para todos os processos Java
```

## 👥 Contribuição

1. Clone o repositório
2. Crie sua branch: `git checkout -b feature/nova-funcionalidade`
3. Commit: `git commit -m 'Adiciona nova funcionalidade'`
4. Push: `git push origin feature/nova-funcionalidade`
5. Abra um Pull Request

## 📄 Licença

Este projeto é licenciado sob a MIT License.

---

**Criado para a disciplina de Sistemas Distribuídos**