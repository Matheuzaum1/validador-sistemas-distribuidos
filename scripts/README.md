# 📋 Scripts de Gerenciamento - Sistema Distribuído

Todos os scripts estão na pasta `scripts/` e foram otimizados para facilitar a execução do projeto.

## 🚀 Quick Start

Para iniciar o sistema completo:

```powershell
.\scripts\sistema.ps1
```

Ou use o menu interativo:

```powershell
.\scripts\menu.ps1
```

---

## 📜 Scripts Disponíveis

### 1️⃣ `menu.ps1` - Menu Interativo ⭐ RECOMENDADO

Menu interativo com todas as opções em um só lugar.

```powershell
.\scripts\menu.ps1
```

**Oferece:**
- 🚀 Executar sistema completo
- 🖥️ Iniciar servidor
- 💻 Iniciar cliente
- 🔨 Compilar projeto
- 🗑️ Limpar e recompilar
- 📖 Ver instruções

---

### 2️⃣ `sistema.ps1` - Sistema Completo

Inicia servidor e cliente automaticamente em janelas separadas.

```powershell
# Uso padrão
.\scripts\sistema.ps1

# Argumentos
.\scripts\sistema.ps1 -port 9000          # Usa porta 9000
.\scripts\sistema.ps1 -rebuild             # Recompila antes de iniciar
```

**Características:**
- ✅ Verifica disponibilidade de porta
- ✅ Compila automaticamente se necessário
- ✅ Inicializa servidor e cliente
- ✅ Aguarda 3 segundos para servidor inicializar
- ✅ Banner ASCII visual

---

### 3️⃣ `compilar.ps1` - Compilação

Compila o projeto Maven e gera o JAR.

```powershell
# Uso padrão
.\scripts\compilar.ps1

# Argumentos
.\scripts\compilar.ps1 -test               # Executa testes também
.\scripts\compilar.ps1 -clean:$false       # Não faz limpeza prévia
```

**Características:**
- ✅ Verifica se Maven está instalado
- ✅ Limpeza de builds anteriores
- ✅ Mostra tamanho do JAR gerado
- ✅ Detecção automática de erros

---

### 4️⃣ `servidor.ps1` - Servidor

Inicia apenas o servidor na porta 8080 (ou customizada).

```powershell
# Uso padrão
.\scripts\servidor.ps1

# Argumentos
.\scripts\servidor.ps1 -port 9000          # Usa porta 9000
```

**Características:**
- ✅ Verifica se porta está disponível
- ✅ Oferece opção de encerrar processo na porta
- ✅ Compila automaticamente se JAR não existir
- ✅ Teste de disponibilidade de porta antes de iniciar

---

### 5️⃣ `cliente.ps1` - Cliente

Inicia apenas o cliente com interface gráfica.

```powershell
# Uso padrão
.\scripts\cliente.ps1

# Argumentos
.\scripts\cliente.ps1 -host 192.168.1.100  # Conecta em outro host
.\scripts\cliente.ps1 -port 9000            # Conecta em porta diferente
```

**Características:**
- ✅ Testa conexão com servidor antes de iniciar
- ✅ Mostra aviso se servidor não estiver disponível
- ✅ Compila automaticamente se JAR não existir
- ✅ Suporte a host e porta customizáveis

---

### 6️⃣ `limpeza.ps1` - Limpeza de Build

Remove arquivos gerados e cache do projeto.

```powershell
# Limpeza padrão (remove apenas target/)
.\scripts\limpeza.ps1

# Argumentos
.\scripts\limpeza.ps1 -completa             # Remove também database e logs
.\scripts\limpeza.ps1 -completa -rebuild    # Limpeza total + recompila
```

**Características:**
- ✅ Remove pasta `target/`
- ✅ Opção de remover database (usuarios.db)
- ✅ Opção de remover logs/
- ✅ Opção de recompilar automaticamente

---

## 🎯 Fluxos de Trabalho Comuns

### 🔄 Desenvolvimento: Testar Mudanças Rápido
```powershell
# Terminal 1: Compilar e manter em watch
.\scripts\limpeza.ps1 -completa -rebuild

# Terminal 2: Iniciar servidor
.\scripts\servidor.ps1

# Terminal 3: Iniciar cliente
.\scripts\cliente.ps1
```

### 🚀 Produção: Primeiro Uso
```powershell
.\scripts\sistema.ps1
```

### 🧹 Quando Algo Quebrou
```powershell
# Limpeza completa + recompilação
.\scripts\limpeza.ps1 -completa -rebuild

# Depois
.\scripts\sistema.ps1
```

### 🔧 Testar em Porta Diferente
```powershell
# Terminal 1
.\scripts\servidor.ps1 -port 9000

# Terminal 2
.\scripts\cliente.ps1 -port 9000
```

### 🌐 Conectar em Outro Host
```powershell
# Supondo que o servidor está em 192.168.1.50
.\scripts\cliente.ps1 -host 192.168.1.50
```

---

## 📊 Features dos Scripts

| Feature | menu.ps1 | sistema.ps1 | compilar.ps1 | servidor.ps1 | cliente.ps1 | limpeza.ps1 |
|---------|----------|-------------|--------------|--------------|-------------|------------|
| Compilação automática | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| Verificação de Maven | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| Teste de porta | ✅ | ✅ | ❌ | ✅ | ❌ | ❌ |
| Teste de servidor | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ |
| Argumentos customizáveis | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Feedback visual melhorado | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Menu interativo | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |

---

## 🔧 Variáveis Padrão

Se desejar customizar os padrões, edite os scripts e modifique:

```powershell
# compilar.ps1
$JAR_PATH = "target\validador-sistemas-distribuidos-1.0.0.jar"

# servidor.ps1
$JAVA_MAIN = "com.distribuidos.server.ServerMain"
$port = 8080  # Padrão

# cliente.ps1
$JAVA_MAIN = "com.distribuidos.client.ClientMain"
$host = "localhost"  # Padrão
$port = 8080  # Padrão
```

---

## ⚠️ Troubleshooting

### "Maven não está instalado"
```powershell
# Instale Maven ou adicione ao PATH
# Verifique com:
mvn --version
```

### "Porta X já está em uso"
```powershell
# Use outra porta
.\scripts\servidor.ps1 -port 9000

# Ou deixe o script encerrar o processo (será perguntado)
```

### "JAR não foi gerado"
```powershell
# Tente recompilar
.\scripts\limpeza.ps1 -completa -rebuild
```

### "Cliente não consegue conectar"
```powershell
# 1. Verifique se servidor está rodando
# 2. Use outro host/porta:
.\scripts\cliente.ps1 -host 192.168.1.100 -port 9000

# 3. Teste manualmente:
java -cp target/validador-sistemas-distribuidos-1.0.0.jar com.distribuidos.client.ClientMain
```

---

## 📝 Notas Importantes

- ✅ Todos os scripts têm verificações de erro robustas
- ✅ Mensagens de erro são claras e indicam próximos passos
- ✅ Compilação é automática quando necessário
- ✅ Scripts são idempotentes (podem ser executados múltiplas vezes)
- ✅ Suportam argumentos via linha de comando
- ✅ Compatível com PowerShell 5.0+

---

## 🎓 Exemplo Completo

```powershell
# 1. Ver menu
.\scripts\menu.ps1

# 2. Escolher opção 1 (Sistema completo)
# Ou fazer manualmente:

# 3. Compilar
.\scripts\compilar.ps1

# 4. Iniciar servidor (Terminal 1)
.\scripts\servidor.ps1

# 5. Iniciar cliente (Terminal 2)
.\scripts\cliente.ps1

# 6. Quando quiser limpar tudo
.\scripts\limpeza.ps1 -completa -rebuild
```

---

**Desenvolvido para facilitar a execução do Sistema Distribuído** 🚀
