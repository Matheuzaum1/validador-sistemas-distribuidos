# Scripts de Automação v2.0 - Validador Sistemas Distribuídos

Este documento descreve todos os scripts disponíveis para gerenciamento do projeto.

## 📋 Índice

1. [Menu Principal](#menu-principal)
2. [Compilação](#compilação)
3. [Servidor](#servidor)
4. [Cliente](#cliente)
5. [Sistema Completo](#sistema-completo)
6. [Error Injector](#error-injector)
7. [Sistema com Proxy](#sistema-com-proxy)
8. [Limpeza](#limpeza)
9. [Configuração de Ambiente](#configuração-de-ambiente)
10. [Fluxos de Trabalho](#fluxos-de-trabalho)

---

## Menu Principal

### `menu.ps1`
Menu interativo que centraliza todas as operações do projeto.

```powershell
.\menu.ps1
```

**Opções disponíveis:**
| Opção | Descrição |
|-------|-----------|
| 1 | Compilar projeto principal |
| 2 | Compilar Error Injector |
| 3 | Iniciar Servidor |
| 4 | Iniciar Cliente |
| 5 | Sistema Completo (Servidor + Cliente) |
| 6 | Sistema com Proxy (Servidor + Error Injector + Cliente) |
| 7 | Limpar artefatos de build |
| 8 | Configurar ambiente |
| 9 | Sair |

---

## Compilação

### `compilar.ps1`
Compila o projeto usando Maven.

**Uso básico:**
```powershell
# Compilar projeto principal
.\compilar.ps1

# Compilar error-injector
.\compilar.ps1 -errorInjector

# Compilar ambos
.\compilar.ps1 -all
```

**Parâmetros:**
| Parâmetro | Descrição | Padrão |
|-----------|-----------|--------|
| `-skipTests` | Pular testes durante compilação | `false` |
| `-clean` | Executar clean antes de compilar | `false` |
| `-errorInjector` | Compilar projeto error-injector | `false` |
| `-all` | Compilar todos os projetos | `false` |

**Exemplos:**
```powershell
# Compilação rápida sem testes
.\compilar.ps1 -skipTests

# Limpar e recompilar tudo
.\compilar.ps1 -clean -all
```

---

## Servidor

### `servidor.ps1`
Inicia o servidor do sistema.

**Uso:**
```powershell
# Porta padrão (8080)
.\servidor.ps1

# Porta customizada
.\servidor.ps1 -port 9000
```

**Parâmetros:**
| Parâmetro | Descrição | Padrão |
|-----------|-----------|--------|
| `-port` | Porta do servidor | `8080` |

---

## Cliente

### `cliente.ps1`
Inicia o cliente com interface gráfica.

**Uso:**
```powershell
# Conectar ao servidor local
.\cliente.ps1

# Conectar a servidor remoto
.\cliente.ps1 -host "192.168.1.100" -port 9000
```

**Parâmetros:**
| Parâmetro | Descrição | Padrão |
|-----------|-----------|--------|
| `-host` | Endereço do servidor | `localhost` |
| `-port` | Porta do servidor | `8080` |

---

## Sistema Completo

### `sistema.ps1`
Inicia servidor e cliente automaticamente.

**Uso:**
```powershell
# Configuração padrão
.\sistema.ps1

# Porta customizada
.\sistema.ps1 -port 9000
```

**Parâmetros:**
| Parâmetro | Descrição | Padrão |
|-----------|-----------|--------|
| `-port` | Porta para servidor e cliente | `8080` |
| `-clientCount` | Número de clientes a iniciar | `1` |

**Comportamento:**
1. Inicia o servidor na porta especificada
2. Aguarda 2 segundos para servidor estabilizar
3. Inicia o(s) cliente(s) conectando ao servidor

---

## Error Injector

### `error-injector.ps1`
Inicia o proxy de injeção de erros para testes.

**Uso:**
```powershell
# Configuração padrão (proxy:9999 -> servidor:8080)
.\error-injector.ps1

# Configuração customizada
.\error-injector.ps1 -proxyPort 7777 -serverPort 8080

# Servidor remoto
.\error-injector.ps1 -serverHost "192.168.1.100" -serverPort 8080
```

**Parâmetros:**
| Parâmetro | Descrição | Padrão |
|-----------|-----------|--------|
| `-proxyPort` | Porta do proxy | `9999` |
| `-serverHost` | Host do servidor real | `localhost` |
| `-serverPort` | Porta do servidor real | `8080` |

**Como funciona:**
```
Cliente ──► Proxy (9999) ──► Servidor (8080)
              │
              ▼
        Injeta erros
        aleatoriamente
```

**Tipos de erros injetados:**
- **JSON inválido**: `{operacao: sem_aspas}`
- **Campos faltando**: `{"operacao": "teste"}`
- **Tipos errados**: `{"operacao": 12345, "status": "nao_booleano"}`
- **Dados corrompidos**: `{"dados": "%%%CORRUPTED%%%"}`

---

## Sistema com Proxy

### `sistema-proxy.ps1`
Inicia o sistema completo com proxy de injeção de erros.

**Uso:**
```powershell
# Configuração padrão
.\sistema-proxy.ps1

# Configuração customizada
.\sistema-proxy.ps1 -serverPort 8080 -proxyPort 9999
```

**Parâmetros:**
| Parâmetro | Descrição | Padrão |
|-----------|-----------|--------|
| `-serverPort` | Porta do servidor | `8080` |
| `-proxyPort` | Porta do proxy | `9999` |
| `-clientCount` | Número de clientes | `1` |

**Arquitetura:**
```
┌─────────────────────────────────────────────────────────┐
│                    Sistema com Proxy                      │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────┐    ┌─────────────┐    ┌──────────────┐   │
│  │ Cliente  │───►│ Error Proxy │───►│   Servidor   │   │
│  │ (GUI)    │    │   (9999)    │    │    (8080)    │   │
│  └──────────┘    └─────────────┘    └──────────────┘   │
│                         │                               │
│                         ▼                               │
│                  Injeção de erros                       │
│                  para testes                            │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

**Sequência de inicialização:**
1. Inicia servidor na porta 8080
2. Aguarda 2 segundos
3. Inicia proxy na porta 9999 (aponta para 8080)
4. Aguarda 1 segundo
5. Inicia cliente conectando na porta 9999 (proxy)

---

## Limpeza

### `limpeza.ps1`
Remove artefatos de build e arquivos temporários.

**Uso:**
```powershell
# Limpeza básica
.\limpeza.ps1

# Limpeza completa (inclui .m2 local)
.\limpeza.ps1 -full
```

**O que é removido:**
- `target/` - Diretório de build principal
- `error-injector/target/` - Build do error-injector
- `*.class` - Classes compiladas soltas
- `*.log` - Arquivos de log
- `dependency-reduced-pom.xml` - Gerado pelo shade plugin

---

## Configuração de Ambiente

### `configurar-java.ps1`
Configura o ambiente Java para o projeto.

```powershell
.\configurar-java.ps1
```

**Funcionalidades:**
- Detecta instalações de Java no sistema
- Configura JAVA_HOME
- Verifica versão mínima (Java 11+)

### `setup-ambiente.ps1`
Configuração completa do ambiente de desenvolvimento.

```powershell
.\setup-ambiente.ps1
```

**Verificações:**
- Java instalado e configurado
- Maven instalado e configurado
- Conectividade com repositórios Maven

### `java-env.ps1`
Script auxiliar para variáveis de ambiente Java.

```powershell
. .\java-env.ps1
```

---

## Fluxos de Trabalho

### 🚀 Desenvolvimento Normal

```powershell
# 1. Compilar
.\compilar.ps1

# 2. Iniciar sistema
.\sistema.ps1
```

### 🧪 Testes com Injeção de Erros

```powershell
# 1. Compilar tudo
.\compilar.ps1 -all

# 2. Iniciar sistema com proxy
.\sistema-proxy.ps1

# Ou manualmente:
# Terminal 1: .\servidor.ps1
# Terminal 2: .\error-injector.ps1
# Terminal 3: .\cliente.ps1 -port 9999
```

### 🔄 Reconstrução Completa

```powershell
# 1. Limpar
.\limpeza.ps1

# 2. Recompilar tudo
.\compilar.ps1 -clean -all

# 3. Testar
.\sistema-proxy.ps1
```

### 🐛 Depuração de Erros

```powershell
# 1. Iniciar servidor com logs detalhados
.\servidor.ps1

# 2. Em outro terminal, iniciar proxy
.\error-injector.ps1

# 3. Em outro terminal, iniciar cliente no proxy
.\cliente.ps1 -port 9999

# 4. Observar logs em cada terminal
# - Servidor: erros de protocolo recebidos
# - Proxy: mensagens interceptadas e modificadas
# - Cliente: erro_servidor enviados e respostas recebidas
```

---

## 📝 Notas

### Portas Padrão
| Componente | Porta |
|------------|-------|
| Servidor | 8080 |
| Proxy Error Injector | 9999 |

### Requisitos
- Java 11 ou superior
- Maven 3.6 ou superior
- PowerShell 5.1 ou superior (Windows)

### Logs
Todos os componentes usam SLF4J/Logback para logging. Os logs são exibidos:
- No console de cada componente
- No painel de logs da GUI do cliente

---

## 🔧 Troubleshooting

### Erro: "Porta já em uso"
```powershell
# Encontrar processo na porta
netstat -ano | findstr :8080

# Matar processo pelo PID
taskkill /PID <pid> /F
```

### Erro: "Java não encontrado"
```powershell
# Configurar ambiente Java
.\configurar-java.ps1

# Ou definir JAVA_HOME manualmente
$env:JAVA_HOME = "C:\Program Files\Java\jdk-11"
```

### Erro: "Conexão recusada"
1. Verifique se o servidor está rodando
2. Verifique a porta correta
3. Se usando proxy, verifique se está conectando na porta do proxy (9999)

### Proxy não injeta erros
O proxy **não** injeta erros na primeira mensagem `conectar` para permitir que a conexão seja estabelecida. Erros são injetados apenas após a conexão inicial.
