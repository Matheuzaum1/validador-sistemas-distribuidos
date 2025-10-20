# Sistema Validador - Distribuído

Um sistema bancário distribuído em Java para comunicação cliente-servidor com protocolo JSON personalizado, implementando operações CRUD de usuários, autenticação, transações financeiras e validação de protocolos.

## � Requisitos

### Requisitos do Sistema
- **Java 21+** (configurado para Java 21)
- **Maven 3.6+** para gerenciamento de dependências
- **Sistema Operacional**: Windows, Linux ou macOS
- **Memória RAM**: Mínimo 512MB disponível
- **Rede**: Porta 20000 disponível para o servidor

### Requisitos Funcionais - Fase 1 (EP-1: 2 pts)
- ✅ **Cadastro de usuário comum (C)** - Criar novo usuário no sistema
- ✅ **Login do usuário comum** - Autenticação com CPF e senha
- ✅ **Ler dados do próprio cadastro (R)** - Consultar informações do usuário logado
- ✅ **Atualizar dados do próprio cadastro (U)** - Modificar nome e senha
- ✅ **Logout do usuário comum** - Encerrar sessão
- ✅ **Apagar dados do próprio cadastro (D)** - Excluir conta do usuário

### Dependências Maven
```xml
<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.17.2</version>
    </dependency>
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.46.1.0</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.14</version>
    </dependency>
    <dependency>
        <groupId>org.mindrot</groupId>
        <artifactId>jbcrypt</artifactId>
        <version>0.4</version>
    </dependency>
</dependencies>
```

## �🚀 Guia de Execução

### Método 1: Script Unificado (Recomendado)
```batch
# Windows
.\sistema.bat

# Abre menu interativo com opções:
# 1. Compilar projeto
# 2. Iniciar servidor
# 3. Iniciar cliente
# 4. Compilar e iniciar ambos
# 5. Parar todos os processos
```

### Método 2: Scripts Individuais
```batch
# Compilar o projeto
.\scripts\build.bat

# Iniciar servidor (Terminal 1)
.\scripts\server.bat

# Iniciar cliente (Terminal 2)
.\scripts\client.bat
```

### Método 3: Maven Manual
```bash
# Compilar
mvn clean compile package

# Executar servidor
java -cp target/classes com.distribuidos.server.ServerMain

# Executar cliente (novo terminal)
java -cp target/classes com.distribuidos.client.ClientMain
```

### Método 4: JARs Executáveis
```bash
# Gerar JARs
mvn clean package

# Executar servidor
java -jar target/validador-sistemas-distribuidos-1.0.0.jar

# Executar cliente (requer JAR específico do cliente)
```

## 📁 Estrutura do Projeto

```
validador-sistemas-distribuidos/
├── src/main/java/com/distribuidos/     # Código fonte principal
│   ├── client/                         # Cliente Swing
│   │   ├── ClientConnection.java       # Comunicação com servidor
│   │   ├── ClientGUI.java              # Interface gráfica
│   │   └── ClientMain.java             # Ponto de entrada do cliente
│   ├── server/                         # Servidor
│   │   ├── ServerHandler.java          # Manipulador de requisições
│   │   ├── ServerGUI.java              # Interface gráfica do servidor
│   │   └── ServerMain.java             # Ponto de entrada do servidor
│   ├── common/                         # Classes compartilhadas
│   │   ├── ClientInfo.java             # Informações do cliente conectado
│   │   ├── MessageBuilder.java         # Construtor de mensagens JSON
│   │   ├── TokenManager.java           # Gerenciamento de tokens
│   │   ├── Transacao.java              # Modelo de transação
│   │   └── Usuario.java                # Modelo de usuário
│   ├── database/                       # Persistência de dados
│   │   └── DatabaseManager.java        # Operações SQLite
│   └── tools/                          # Ferramentas de teste
├── src/main/java/validador/            # Validador de protocolo
│   ├── RulesEnum.java                  # Enumeração de regras
│   └── Validator.java                  # Validador de mensagens
├── src/main/resources/                 # Recursos
│   └── logback.xml                     # Configuração de logs
├── scripts/                            # Scripts de automação
│   ├── sistema.bat                     # Script principal com menu
│   ├── build.bat                       # Compilação
│   ├── server.bat                      # Execução do servidor
│   └── client.bat                      # Execução do cliente
├── Essentials/                         # Documentação do protocolo
│   ├── README.md                       # Especificação completa
│   ├── RulesEnum.java                  # Cópia das regras
│   └── Validator.java                  # Cópia do validador
├── docs/                               # Documentação adicional
├── logs/                               # Logs de execução
├── target/                             # Arquivos compilados
├── pom.xml                             # Configuração Maven
├── database_setup.sql                  # Script de criação do banco
├── usuarios.db                         # Banco SQLite (gerado automaticamente)
└── README.md                           # Este arquivo
```

## 💻 Execução via IDEs

### Visual Studio Code
1. **Pré-requisitos**: 
   - Extension Pack for Java
   - Maven for Java extension

2. **Passos**:
   ```bash
   # Abrir projeto
   code .
   
   # Terminal integrado (Ctrl+`)
   mvn clean compile
   ```

3. **Executar**:
   - **Servidor**: `Ctrl+Shift+P` → "Java: Run Java" → `ServerMain.java`
   - **Cliente**: `Ctrl+Shift+P` → "Java: Run Java" → `ClientMain.java`

4. **Debug**:
   - Configurar breakpoints
   - `F5` para iniciar debug
   - Configurar `launch.json` se necessário

### Eclipse IDE
1. **Importar Projeto**:
   - `File` → `Import` → `Existing Maven Projects`
   - Selecionar pasta do projeto
   - `Finish`

2. **Configurar**:
   - Click direito no projeto → `Maven` → `Reload Projects`
   - `Project` → `Clean` → `Clean all projects`

3. **Executar**:
   - **Servidor**: Click direito em `ServerMain.java` → `Run As` → `Java Application`
   - **Cliente**: Click direito em `ClientMain.java` → `Run As` → `Java Application`

4. **Debug**:
   - Click direito → `Debug As` → `Java Application`
   - Configurar breakpoints clicando na margem esquerda

### IntelliJ IDEA
1. **Abrir Projeto**:
   - `File` → `Open` → Selecionar pasta do projeto
   - Aguardar indexação automática

2. **Configurar**:
   - Verificar `File` → `Project Structure` → `Project SDK` (Java 21)
   - `Maven` tab (lateral direita) → `Reload All Maven Projects`

3. **Executar**:
   - **Servidor**: Click direito em `ServerMain.java` → `Run 'ServerMain.main()'`
   - **Cliente**: Click direito em `ClientMain.java` → `Run 'ClientMain.main()'`

4. **Configurações de Run**:
   - `Run` → `Edit Configurations`
   - Adicionar configurações específicas se necessário

### NetBeans
1. **Abrir Projeto**:
   - `File` → `Open Project`
   - Selecionar pasta do projeto
   - NetBeans detecta automaticamente projeto Maven

2. **Executar**:
   - **Servidor**: Click direito em `ServerMain.java` → `Run File`
   - **Cliente**: Click direito em `ClientMain.java` → `Run File`

3. **Debug**:
   - Click direito → `Debug File`
   - Configurar breakpoints

## 🌐 Protocolo de Comunicação

### Formato Padrão
Todas as mensagens seguem o formato JSON:
```json
{
  "operacao": "nome_da_operacao",
  "token": "token_de_sessao",
  "campo1": "valor1",
  "campo2": "valor2"
}
```

### Operações Disponíveis

#### 1. Login do Usuário
```json
// Cliente → Servidor
{
  "operacao": "usuario_login",
  "cpf": "123.456.789-01",
  "senha": "123456"
}

// Servidor → Cliente (Sucesso)
{
  "operacao": "usuario_login",
  "status": true,
  "info": "Login realizado com sucesso",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### 2. Cadastro de Usuário
```json
// Cliente → Servidor
{
  "operacao": "usuario_criar",
  "nome": "João Silva",
  "cpf": "111.222.333-44",
  "senha": "minhasenha123"
}

// Servidor → Cliente (Sucesso)
{
  "operacao": "usuario_criar",
  "status": true,
  "info": "Usuário criado com sucesso"
}
```

#### 3. Leitura de Dados do Usuário
```json
// Cliente → Servidor
{
  "operacao": "usuario_ler",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}

// Servidor → Cliente (Sucesso)
{
  "operacao": "usuario_ler",
  "status": true,
  "info": "Dados do usuário",
  "usuario": {
    "cpf": "123.456.789-01",
    "nome": "João Silva Santos",
    "saldo": 1500.00,
    "criadoEm": "2025-10-17T10:30:00Z",
    "atualizadoEm": "2025-10-17T10:30:00Z"
  }
}
```

#### 4. Atualização de Dados
```json
// Cliente → Servidor
{
  "operacao": "usuario_atualizar",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "usuario": {
    "nome": "João Silva Santos Junior",
    "senha": "novasenha456"
  }
}
```

#### 5. Logout
```json
// Cliente → Servidor
{
  "operacao": "usuario_logout",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### 6. Exclusão de Conta
```json
// Cliente → Servidor
{
  "operacao": "usuario_deletar",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Para especificação completa do protocolo, consulte: [`Essentials/README.md`](Essentials/README.md)

## 🎯 Funcionalidades Implementadas

### Autenticação e Segurança
- ✅ **Login/Logout**: Sistema de autenticação com tokens JWT
- ✅ **Hash de Senhas**: BCrypt para armazenamento seguro
- ✅ **Validação de Sessão**: Tokens com expiração automática
- ✅ **Validação de CPF**: Formato brasileiro padrão

### CRUD de Usuários (Fase 1 - EP-1)
- ✅ **Create**: Cadastro de novos usuários
- ✅ **Read**: Consulta de dados do próprio usuário
- ✅ **Update**: Atualização de nome e senha
- ✅ **Delete**: Exclusão da própria conta

### Operações Financeiras
- ✅ **Consulta de Saldo**: Visualização do saldo atual
- ✅ **Transferências**: Entre usuários do sistema
- ✅ **Depósitos**: Adição de valores à conta
- ✅ **Histórico**: Consulta de transações por período

### Interface e Usabilidade
- ✅ **GUI Cliente**: Interface Swing intuitiva
- ✅ **GUI Servidor**: Monitoramento de conexões
- ✅ **Logs Detalhados**: Sistema de logging completo
- ✅ **Validação de Entrada**: Formatação automática de CPF

### Persistência
- ✅ **SQLite**: Banco de dados local
- ✅ **Transações ACID**: Integridade garantida
- ✅ **Dados de Teste**: Usuários pré-cadastrados
- ✅ **Auto-criação**: Banco criado automaticamente

## 👥 Usuários de Teste Pré-cadastrados

| CPF | Nome | Senha | Saldo Inicial |
|-----|------|-------|---------------|
| 123.456.789-01 | João Silva Santos | 123456 | R$ 1.500,00 |
| 987.654.321-02 | Maria Santos Oliveira | 654321 | R$ 2.300,00 |
| 111.222.333-44 | Pedro Oliveira Costa | password | R$ 800,00 |
| 555.666.777-88 | Ana Costa Ferreira | 123abc | R$ 4.200,00 |
| 444.555.666-77 | Carlos Eduardo Lima | carlos123 | R$ 950,00 |
| 333.444.555-66 | Fernanda Alves Souza | fernanda456 | R$ 1.750,00 |
| 222.333.444-55 | Roberto Silva Junior | roberto789 | R$ 3.100,00 |
| 666.777.888-99 | Juliana Pereira Rocha | juliana321 | R$ 650,00 |
| 777.888.999-00 | Marcos Antonio Dias | marcos654 | R$ 2.850,00 |
| 888.999.000-11 | Luciana Martins Cruz | luciana987 | R$ 1.200,00 |

### Conexão de Teste Recomendada
- **Servidor**: `localhost:8080`
- **CPF**: `123.456.789-01`
- **Senha**: `123456`

## 🛠️ Desenvolvimento e Build

### Comandos Maven Essenciais
```bash
# Limpar e compilar
mvn clean compile

# Executar testes
mvn test

# Gerar JARs
mvn package

# Limpar, testar e empacotar
mvn clean test package

# Pular testes durante build
mvn clean package -DskipTests
```

### Estrutura de Build
```xml
<!-- Configuração Java 21 -->
<properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>

<!-- Plugin de Compilação -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
</plugin>

<!-- Plugin Shade para JAR com dependências -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.5.1</version>
</plugin>
```

### Logs e Debug
```bash
# Logs são salvos em: logs/application.log
# Nível de log configurável em: src/main/resources/logback.xml

# Para debug detalhado, editar logback.xml:
<root level="DEBUG">
    <appender-ref ref="STDOUT"/>
    <appender-ref ref="FILE"/>
</root>
```

## 🔒 Segurança e Validação

### Validação de Protocolo
- Todas as mensagens são validadas pelo `Validator.java`
- Regras definidas em `RulesEnum.java`
- Conformidade garantida com protocolo bancário

### Segurança de Dados
- **Senhas**: Hash BCrypt com salt automático
- **Tokens**: Geração segura com timestamp
- **CPF**: Validação de formato e dígitos verificadores
- **SQL Injection**: Prepared statements em todas as queries

### Timeouts e Limites
- **Conexão**: 30 segundos para estabelecer
- **Leitura**: 5 segundos para resposta
- **Token**: Sem expiração automática (sessão manual)
- **Concurrent Connections**: Ilimitadas

## 🧪 Testes e Validação

### Executar Testes
```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=DatabaseManagerTest

# Testes com relatório
mvn test jacoco:report
```

### Validação Manual
```bash
# 1. Iniciar servidor
.\scripts\server.bat

# 2. Iniciar cliente
.\scripts\client.bat

# 3. Testar operações básicas:
#    - Login com usuário de teste
#    - Consultar dados
#    - Atualizar informações
#    - Logout
```

### Ferramentas de Teste Incluídas
- `ClientTestRunner.java`: Testes automatizados do cliente
- `InProcessE2ETestRunner.java`: Testes end-to-end
- `DbInspect.java`: Inspeção do banco de dados

## 🐛 Solução de Problemas

### Problemas Comuns

#### "Erro de conexão com o servidor"
```bash
# Verificar se servidor está rodando
netstat -an | findstr :8080

# Iniciar servidor
.\scripts\server.bat
```

#### "ClassNotFoundException"
```bash
# Recompilar projeto
mvn clean compile

# Verificar classpath
mvn dependency:tree
```

#### "Porta 8080 já está em uso"
```bash
# Windows: Encontrar processo usando a porta
netstat -ano | findstr :8080

# Terminar processo (substituir PID)
taskkill /F /PID [PID]
```

#### "Banco de dados bloqueado"
```bash
# Fechar todas as conexões
.\scripts\stop.bat

# Remover arquivo de lock (se existir)
del usuarios.db-wal
del usuarios.db-shm
```

#### "Maven não reconhecido"
```bash
# Verificar instalação Maven
mvn --version

# Adicionar ao PATH se necessário
set PATH=%PATH%;C:\apache-maven-3.x.x\bin
```

### Logs de Debug
```bash
# Verificar logs em tempo real
Get-Content logs\application.log -Wait

# Filtrar por nível de erro
Get-Content logs\application.log | Select-String "ERROR"
```

## 📚 Documentação Adicional

### Arquivos de Referência
- [`Essentials/README.md`](Essentials/README.md) - Especificação completa do protocolo
- [`database_setup.sql`](database_setup.sql) - Schema do banco de dados
- [`pom.xml`](pom.xml) - Configuração Maven completa

### Padrões de Código
- **Naming**: CamelCase para classes, camelCase para métodos
- **Encoding**: UTF-8 em todos os arquivos
- **Logs**: SLF4J com Logback
- **JSON**: Jackson para serialização/deserialização

### Arquitetura
```
Cliente (Swing GUI) ←→ Servidor (Socket) ←→ Banco (SQLite)
     ↑                        ↑                    ↑
ClientConnection.java    ServerHandler.java   DatabaseManager.java
     ↑                        ↑                    ↑
MessageBuilder.java      Validator.java       Usuario.java/Transacao.java
```

## 🤝 Contribuição

### Configuração para Desenvolvimento
```bash
# 1. Fork do repositório
git clone https://github.com/[seu-usuario]/validador-sistemas-distribuidos.git

# 2. Configurar ambiente
cd validador-sistemas-distribuidos
mvn clean compile

# 3. Executar testes
mvn test

# 4. Criar branch para feature
git checkout -b feature/nova-funcionalidade

# 5. Desenvolver e testar
# ... código ...

# 6. Commit e push
git add .
git commit -m "feat: adiciona nova funcionalidade"
git push origin feature/nova-funcionalidade

# 7. Abrir Pull Request
```

### Padrões para Commits
- `feat:` - Nova funcionalidade
- `fix:` - Correção de bug
- `docs:` - Documentação
- `style:` - Formatação
- `refactor:` - Refatoração
- `test:` - Testes
- `chore:` - Manutenção

## 📄 Licença

Este projeto é desenvolvido para fins educacionais na disciplina de **Sistemas Distribuídos**.

### Informações do Projeto
- **Instituição**: UTFPR-PG
- **Disciplina**: Sistemas Distribuídos
- **Semestre**: 2025.1
- **Fase Atual**: EP-1 (Operações CRUD de usuários)

---

## 📞 Suporte

Para dúvidas ou problemas:

1. **Verificar logs**: `logs/application.log`
2. **Consultar troubleshooting**: Seção "Solução de Problemas" acima
3. **Testar com usuários padrão**: Usar credenciais da tabela de usuários de teste
4. **Recompilar**: `mvn clean compile package`

**Sistema validado e funcional para os requisitos da Fase 1 (EP-1)**

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