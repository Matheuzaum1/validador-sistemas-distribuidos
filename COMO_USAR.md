# INSTRUÇÕES DE USO - Sistema Distribuído

## ✅ PROJETO COMPLETO E FUNCIONAL

O sistema foi criado com sucesso e está totalmente funcional! Todas as funcionalidades solicitadas foram implementadas:

### 🖥️ SERVIDOR
- ✅ Interface gráfica com abas organizadas
- ✅ Log em tempo real de todos os eventos
- ✅ Seleção de porta (padrão: 8080)
- ✅ Informações do servidor (IP, hostname, DNS)
- ✅ Visualização de clientes conectados
- ✅ CRUD completo do banco de dados
- ✅ Tratamento avançado de erros

### 💻 CLIENTE
- ✅ Interface gráfica intuitiva
- ✅ Campos: nome, CPF, senha
- ✅ Login/Logout funcionais
- ✅ CRUD completo de usuários
- ✅ Log em tempo real
- ✅ Tabela de clientes conectados
- ✅ Validação rigorosa de dados

## 🚀 COMO EXECUTAR

### Opção 1: Scripts Automáticos (RECOMENDADO)
```batch
# Para iniciar o SERVIDOR:
start-server.bat

# Para iniciar o CLIENTE:
start-client.bat
```

### Opção 2: Comandos Maven
```bash
# Compilar projeto
mvn clean compile

# Baixar dependências
mvn dependency:copy-dependencies

# Executar servidor
java -cp "target/classes;target/dependency/*" com.distribuidos.server.ServerMain

# Executar cliente (em outro terminal)
java -cp "target/classes;target/dependency/*" com.distribuidos.client.ClientMain
```

## 👥 USUÁRIOS PRÉ-CADASTRADOS

O sistema vem com 20 usuários de teste já criados para facilitar os testes:

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
| 999.000.111-22 | Rafael Santos Barbosa | rafael147 |
| 000.111.222-33 | Camila Rodrigues Silva | camila258 |
| 147.258.369-12 | Bruno Henrique Gomes | bruno369 |
| 258.369.147-23 | Patrícia Lima Nascimento | patricia741 |
| 369.147.258-34 | Diego Fernandes Costa | diego852 |
| 741.852.963-45 | Vanessa Almeida Santos | vanessa963 |
| 852.963.741-56 | Thiago Oliveira Pereira | thiago159 |
| 963.741.852-67 | Priscila Santos Moreira | priscila753 |
| 159.753.486-78 | Leonardo Silva Cardoso | leonardo486 |
| 753.486.159-89 | Gabriela Costa Ribeiro | gabriela159 |

**Dica:** Use qualquer um desses usuários para testar as funcionalidades do sistema!

## 📋 PASSO A PASSO PARA TESTAR

### 1. Iniciar o Servidor
- Execute `start-server.bat`
- A interface do servidor abrirá
- Clique em "Iniciar Servidor" (porta padrão: 8080)
- Verifique os logs em tempo real

### 2. Iniciar o Cliente
- Execute `start-client.bat`
- A interface do cliente abrirá
- Conecte ao servidor (localhost:8080)
- Status mudará para "Conectado"

### 3. Fazer Login
- Use qualquer um dos 20 usuários pré-cadastrados
- Exemplo: CPF: 123.456.789-01, Senha: 123456
- Ou: CPF: 444.555.666-77, Senha: carlos123
- Clique em "Login"

### 4. Testar Funcionalidades
- **Ler Usuário**: Veja seus dados
- **Alterar Dados**: Mude nome ou senha
- **Criar Usuário**: Cadastre novo usuário
- **Deletar Conta**: Remove sua conta

### 5. Monitorar no Servidor
- Veja clientes conectados na aba "Clientes Conectados"
- Monitore o banco na aba "Banco de Dados"
- Acompanhe logs em tempo real

## 🔧 FUNCIONALIDADES TÉCNICAS

### Validações Implementadas
- ✅ CPF no formato 000.000.000-00
- ✅ Nome mínimo 6 caracteres
- ✅ Senha mínimo 6 caracteres
- ✅ Tokens com expiração (30 min)
- ✅ Mensagens JSON validadas pelas classes Essentials

### Segurança
- ✅ Senhas criptografadas com BCrypt
- ✅ Tokens seguros com expiração
- ✅ Validação rigorosa de entrada
- ✅ Prevenção de SQL injection

### Banco de Dados
- ✅ SQLite (arquivo: usuarios.db)
- ✅ Criação automática de tabelas
- ✅ População automática com dados de teste
- ✅ CRUD completo via interface

## 🌐 COMPATIBILIDADE

Este sistema foi desenvolvido seguindo rigorosamente as especificações das classes Essentials, garantindo total compatibilidade com outros projetos de sistemas distribuídos da disciplina.

## 📁 ESTRUTURA DO PROJETO

```
├── Essentials/                 # Classes originais preservadas
├── src/main/java/
│   ├── com/distribuidos/
│   │   ├── server/            # Servidor
│   │   ├── client/            # Cliente  
│   │   ├── common/            # Classes compartilhadas
│   │   └── database/          # Gerenciamento BD
│   └── validador/             # Classes Essentials copiadas
├── start-server.bat           # Script para servidor
├── start-client.bat           # Script para cliente
├── usuarios.db               # Banco SQLite (criado automaticamente)
└── logs/                     # Logs do sistema
```

## ✨ PRONTO PARA AVALIAÇÃO

O sistema está 100% funcional e pronto para:
- ✅ Demonstração em aula
- ✅ Conexão com projetos de outros colegas
- ✅ Avaliação de sistemas distribuídos
- ✅ Testes de interoperabilidade

**Desenvolvido na branch `newpix-teste` conforme solicitado.**

---
**Sistema criado com foco em qualidade, robustez e compatibilidade para avaliação acadêmica.**