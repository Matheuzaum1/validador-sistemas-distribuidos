# Sistema Validador de Sistemas Distribuídos - Guia de Distribuição

## Arquivos para Distribuição

Este projeto gera 3 arquivos JAR no diretório `target/`:

### 1. Fat JARs (Recomendados para Distribuição)
- **`validador-sistemas-distribuidos-1.0.0-server.jar`** (~16.6 MB)
  - Servidor completo com todas as dependências
  - Pronto para executar sem instalações adicionais
  
- **`validador-sistemas-distribuidos-1.0.0-client.jar`** (~16.6 MB)
  - Cliente completo com todas as dependências
  - Pronto para executar sem instalações adicionais

### 2. JAR Original (Somente Classes do Projeto)
- **`validador-sistemas-distribuidos-1.0.0.jar`** (~0.08 MB)
  - Apenas as classes do projeto (requer dependências separadas)

## Requisitos do Sistema

### Para seus colegas testarem:
- **Java 21 ou superior** instalado no sistema
- Sistema operacional: Windows, Linux ou macOS
- Mínimo 512 MB de RAM disponível

### Verificar instalação do Java:
```bash
java -version
```

Se o Java não estiver instalado ou for uma versão anterior ao 21, baixe em:
https://adoptium.net/temurin/releases/

## Como Executar

### Opção 1: Fat JARs (Mais Fácil - Recomendado)

#### Servidor:
```bash
java -jar validador-sistemas-distribuidos-1.0.0-server.jar
```

#### Cliente:
```bash
java -jar validador-sistemas-distribuidos-1.0.0-client.jar
```

### Opção 2: Modo Headless (Servidor sem Interface Gráfica)
```bash
java -jar validador-sistemas-distribuidos-1.0.0-server.jar -headless
```

## Funcionalidades do Sistema

### Servidor
- Interface gráfica para monitoramento
- Gerenciamento de conexões de clientes
- Banco de dados SQLite integrado
- Logging de todas as operações
- Suporte a múltiplos clientes simultâneos

### Cliente
- Interface gráfica intuitiva
- Operações CRUD completas:
  - **Login/Logout** de usuários
  - **Criar** novos usuários
  - **Consultar** dados de usuários
  - **Atualizar** informações
  - **Deletar** usuários
  - **Transações** entre usuários
  - **Depósitos** em contas
  - **Conectar** ao servidor

## Protocolo de Comunicação

O sistema utiliza comunicação JSON sobre TCP/IP:
- **Porta padrão**: 12345
- **Formato**: Mensagens JSON estruturadas
- **Encoding**: UTF-8 para suporte a caracteres especiais
- **Validação**: Todas as mensagens são validadas antes do processamento

## Estrutura dos Arquivos de Dados

- **Banco de dados**: `sistema_distribuido.db` (criado automaticamente)
- **Logs**: Console e arquivo (formato UTF-8)
- **Configuração**: `logback.xml` (embutido no JAR)

## Testando o Sistema

### Cenário de Teste Básico:

1. **Iniciar o Servidor**:
   ```bash
   java -jar validador-sistemas-distribuidos-1.0.0-server.jar
   ```
   - Aguarde a mensagem "Servidor iniciado com sucesso"

2. **Iniciar o Cliente**:
   ```bash
   java -jar validador-sistemas-distribuidos-1.0.0-client.jar
   ```
   - A interface gráfica do cliente deve aparecer

3. **Conectar ao Servidor**:
   - No cliente, use o botão "Conectar"
   - Host: `localhost` (se estiver no mesmo computador)
   - Porta: `12345`

4. **Testar Operações**:
   - Criar um usuário novo
   - Fazer login com o usuário criado
   - Realizar operações CRUD
   - Testar transações e depósitos

### Teste em Rede Local:

Para testar entre computadores diferentes:
1. O computador com o servidor precisa permitir conexões na porta 12345
2. No cliente, use o IP do computador servidor no lugar de `localhost`

## Solução de Problemas

### Erro "Main class not found":
- Verifique se está usando os fat JARs (arquivos maiores)
- Certifique-se de que o Java 21+ está instalado

### Erro de conexão:
- Verifique se o servidor está rodando
- Confirme se a porta 12345 não está bloqueada pelo firewall
- No Windows: `netstat -an | findstr 12345`

### Caracteres especiais não aparecem:
- O sistema foi configurado para UTF-8
- Se ainda houver problemas, execute com: `java -Dfile.encoding=UTF-8 -jar arquivo.jar`

### Warnings sobre "restricted methods":
- São avisos normais do SQLite no Java 21+
- Não afetam o funcionamento do sistema
- Para suprimir: `java --enable-native-access=ALL-UNNAMED -jar arquivo.jar`

## Logs e Debugging

Os logs são exibidos no console e incluem:
- Conexões de clientes
- Operações realizadas
- Erros e exceções
- Mensagens do protocolo (em modo debug)

Para logging mais detalhado, edite o arquivo `logback.xml` no JAR ou use variáveis de ambiente.

## Suporte Técnico

- **Versão Java**: 21 LTS
- **Versão Maven**: 3.9.11+
- **Dependências principais**: Jackson 2.17.2, SQLite 3.46.1.0, Logback 1.4.14
- **Protocolos**: TCP/IP, JSON, UTF-8
- **Plataformas**: Multiplataforma (Windows, Linux, macOS)

---

**Data de compilação**: Outubro 2025  
**Versão do sistema**: 1.0.0  
**Compilado com**: Java 25 (target Java 21)