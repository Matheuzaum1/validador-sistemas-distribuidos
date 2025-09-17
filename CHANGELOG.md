# CHANGELOG - NewPix Banking System

Histórico de desenvolvimento e correções implementadas no projeto.

## [2.0.0] - 2025-09-17 - Otimização Completa

### 🚀 Melhorias Principais
- Remoção de arquivos redundantes e código obsoleto
- Otimização de dependências Maven e configurações
- Criação de scripts cross-platform (Windows + Linux/macOS)
- Modernização da arquitetura de interface

### 🗑️ Limpeza de Código
- Removido LoginGUI.java obsoleto (substituído por LoginWindow.java)
- Removido LoginGUI_New.java redundante
- Eliminados arquivos de teste obsoletos
- Removidos scripts de backup desnecessários
- Otimizado pom.xml com dependências limpas

### 🛠️ Scripts de Automação
- newpix.ps1: Script PowerShell para Windows
- newpix.sh: Script Bash equivalente para Linux/macOS
- Funcionalidades: build, run, status, test, changelog

### 🔧 Otimizações Técnicas
- Todas as referências atualizadas para LoginWindow
- Classpath e configurações Maven otimizadas
- Remoção de imports desnecessários
- Código morto eliminado

## [1.5.0] - 2025-09-16 - Correções Finais de UX

### ✅ Correções Implementadas
- **Operação 'depositar'** implementada no servidor
- **Cliente corrigido** para usar protocolo JSON correto
- **Parâmetro 'quantidade'** conforme documentação
- **Loop infinito** no carregamento de dados corrigido
- **Sistema de tentativas** com limite (max 3)
- **Feedback visual** melhorado

### 🎨 Melhorias de Interface
- Página de configurações estabilizada
- Controle robusto de erros de conexão
- Interface mais responsiva
- Cores e contrastes corrigidos

### 📱 Funcionalidades UX
- Toggle de visibilidade da senha
- Auto-complete de CPF com formatação
- Sistema "Remember Me"
- Escaneamento automático de servidores
- Validação em tempo real

## [1.4.0] - 2025-09-15 - Modernização da Interface

### 🎨 Interface Modernizada
- LoginWindow: Nova interface principal com design moderno
- CadastroWindow: Sistema de cadastro integrado
- Componentes customizados: CpfField, PasswordFieldWithToggle
- Tema unificado: NewPixTheme para consistência visual
- Animações: AnimationUtils para transições suaves

### 🔐 Sistema de Autenticação Aprimorado
- Auto-complete de CPF com formatação automática
- Toggle de visibilidade da senha
- Sistema "Remember Me" com persistência
- Validação em tempo real com feedback visual
- Histórico de CPFs utilizados

### 🌐 Conectividade Inteligente
- Escaneamento automático de servidores na rede
- Configuração dinâmica de host e porta
- Teste de conexão em tempo real
- Feedback imediato de status de conexão

## [1.3.0] - 2025-09-14 - Correções de Protocolo

### 🔧 Protocolo JSON Corrigido
- Implementada operação "depositar" no MessageProcessor
- Parâmetro "quantidade" conforme especificação
- Todas operações seguem docs/Requisitos.md
- Nomenclatura minúscula com underscores

### 🐛 Bugs Corrigidos
- Driver SQLite: Classpath corrigido
- Cores invisíveis: Contraste adequado aplicado
- Ícones quadrados: Substituídos por ASCII compatível
- Loop infinito: Sistema de tentativas implementado

### 🛡️ Robustez e Confiabilidade
- Tratamento robusto de erros de conexão
- Logs detalhados para debugging
- Validação de entrada aprimorada
- Recuperação automática de falhas

## [1.2.0] - 2025-09-13 - Melhorias de Servidor

### 🖥️ Interface do Servidor Aprimorada
- ServerGUI modernizada com NewPixTheme
- Tabelas de usuários e transações em tempo real
- Informações de rede e status do servidor
- Controles de iniciar/parar servidor integrados

### 📊 Monitoramento e Logs
- Sistema de logs em tempo real
- Contador de clientes conectados
- Exibição de estatísticas do sistema
- Histórico de transações detalhado

## [1.1.0] - 2025-09-12 - Base Funcional

### 🏗️ Arquitetura Base
- Servidor: NewPixServer com ClientHandler multi-thread
- Cliente: NewPixClient com interface GUI
- Banco de dados: SQLite com DAOs implementados
- Comunicação: Protocolo JSON via TCP Sockets

### 🔒 Segurança
- Autenticação com BCrypt
- Sessões com tokens únicos
- Validação de CPF completa
- Timeout de conexão configurável

### 📋 Funcionalidades Core
- CRUD completo de usuários
- Sistema de transações PIX
- Operações de consulta, depósito, transferência
- Interface gráfica responsiva

## [1.0.0] - 2025-09-11 - Versão Inicial

### 🎉 Primeiro Release
- Implementação inicial do sistema bancário
- Interface básica funcional
- Comunicação cliente-servidor estabelecida
- Banco de dados SQLite configurado

---

## 🛠️ Tecnologias Utilizadas

- **Java 17+** - Linguagem principal
- **Maven** - Gerenciamento de dependências
- **SQLite** - Banco de dados local
- **Swing** - Interface gráfica
- **Jackson** - Processamento JSON
- **BCrypt** - Hash de senhas
- **JUnit** - Testes unitários

## 📁 Estrutura do Projeto

```
validador-sistemas-distribuidos/
├── src/main/java/com/newpix/
│   ├── client/          # Cliente e interfaces
│   ├── server/          # Servidor e handlers
│   ├── model/           # Modelos de dados
│   ├── dao/             # Acesso a dados
│   ├── service/         # Lógica de negócio
│   └── util/            # Utilitários
├── docs/                # Documentação
├── scripts/             # Scripts de automação
├── newpix.ps1          # Script Windows
├── newpix.sh           # Script Linux/macOS
└── README.md           # Documentação principal
```

## 🚀 Como Executar

### Usando Scripts de Automação

**Windows:**
```powershell
.\newpix.ps1 both-gui
```

**Linux/macOS:**
```bash
./newpix.sh both-gui
```

### Manualmente

```bash
# Compilar
mvn clean compile dependency:copy-dependencies

# Executar servidor
java -cp "target/classes:target/dependency/*" com.newpix.server.gui.ServerGUI

# Executar cliente
java -cp "target/classes:target/dependency/*" com.newpix.client.gui.LoginWindow
```

## 📞 Suporte

Para problemas ou dúvidas:
1. Consulte a documentação em `docs/Requisitos.md`
2. Execute `./newpix.sh test` para diagnóstico
3. Verifique os logs do sistema

---

**Desenvolvido com ❤️ por Matheuzaum1**