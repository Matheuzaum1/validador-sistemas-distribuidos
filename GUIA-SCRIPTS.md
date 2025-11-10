# 📋 Guia de Scripts de Execução

## 🚀 Scripts Disponíveis

### Na Raiz do Projeto

#### 1. **Compilação**
```cmd
compilar.bat           # Windows Batch
compilar.ps1           # PowerShell
```
Compila o projeto e gera o JAR executável.

#### 2. **Servidor**
```cmd
iniciar-servidor.bat   # Windows Batch
iniciar-servidor.ps1   # PowerShell
```
Inicia apenas o servidor na porta 8080.

#### 3. **Cliente**
```cmd
iniciar-cliente.bat    # Windows Batch
iniciar-cliente.ps1    # PowerShell
```
Inicia apenas o cliente com interface gráfica.

#### 4. **Sistema Completo** ⭐ RECOMENDADO
```cmd
iniciar-sistema.bat    # Windows Batch
iniciar-sistema.ps1    # PowerShell
```
Inicia servidor e cliente automaticamente em janelas separadas.

### Na Pasta `scripts/`

#### 5. **Menu Interativo**
```cmd
scripts\sistema.bat
```
Menu completo com opções para:
- Compilar projeto
- Iniciar servidor
- Iniciar cliente
- Executar testes
- Limpar e recompilar
- Parar processos Java
- Verificar status
- Ajuda

#### 6. **Scripts Individuais**
```cmd
scripts\build.bat      # Compilação
scripts\server.bat     # Servidor
scripts\client.bat     # Cliente
```

## 🎯 Uso Recomendado

### Para Desenvolvimento
Use os scripts individuais para controle fino:
1. `compilar.bat` - Compile o projeto
2. `iniciar-servidor.bat` - Inicie o servidor
3. `iniciar-cliente.bat` - Inicie o cliente

### Para Demonstração/Teste Rápido
Use o script completo:
```cmd
iniciar-sistema.bat
```
Este script:
- ✅ Compila automaticamente se necessário
- ✅ Inicia o servidor
- ✅ Aguarda 3 segundos
- ✅ Inicia o cliente
- ✅ Abre em janelas separadas

### Para Gerenciamento Completo
Use o menu interativo:
```cmd
scripts\sistema.bat
```

## 🔧 Diferenças entre .bat e .ps1

### Scripts .bat (Batch)
- ✅ Funciona em qualquer Windows
- ✅ Não requer permissões especiais
- ✅ Mais simples
- ❌ Menos recursos visuais

### Scripts .ps1 (PowerShell)
- ✅ Cores e formatação melhor
- ✅ Mais moderno
- ✅ Melhor tratamento de erros
- ❌ Pode requerer: `Set-ExecutionPolicy RemoteSigned`

## ⚙️ Configuração PowerShell

Se encontrar erro ao executar `.ps1`:

```powershell
# Execute como Administrador
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
```

## 📝 Exemplos de Uso

### Cenário 1: Primeira Execução
```cmd
# 1. Compile o projeto
compilar.bat

# 2. Inicie o servidor (terminal 1)
iniciar-servidor.bat

# 3. Inicie o cliente (terminal 2)
iniciar-cliente.bat
```

### Cenário 2: Execução Rápida
```cmd
# Tudo em um comando
iniciar-sistema.bat
```

### Cenário 3: Desenvolvimento com Testes
```cmd
# Use o menu interativo
scripts\sistema.bat

# Selecione:
# 1 - Compilar
# 4 - Executar testes
# 2 - Iniciar servidor
# 3 - Iniciar cliente
```

## 🛠️ Solução de Problemas

### JAR não encontrado
```cmd
compilar.bat
```

### Porta 8080 em uso
Pare processos Java:
```cmd
scripts\sistema.bat
# Opção 6 - Parar todos os processos Java
```

### Recompilar do zero
```cmd
scripts\sistema.bat
# Opção 5 - Limpar e recompilar
```

## 📂 Localização dos Arquivos

```
validador-sistemas-distribuidos/
│
├── compilar.bat              ⭐ Compilar
├── compilar.ps1
│
├── iniciar-servidor.bat      ⭐ Servidor
├── iniciar-servidor.ps1
│
├── iniciar-cliente.bat       ⭐ Cliente
├── iniciar-cliente.ps1
│
├── iniciar-sistema.bat       ⭐ Sistema Completo
├── iniciar-sistema.ps1
│
└── scripts/
    ├── sistema.bat           ⭐ Menu Interativo
    ├── build.bat
    ├── server.bat
    └── client.bat
```

## 🎓 Dicas

1. **Para apresentações**: Use `iniciar-sistema.bat` para setup rápido
2. **Para desenvolvimento**: Use scripts individuais para controle fino
3. **Para debugging**: Use o menu interativo (`scripts\sistema.bat`)
4. **PowerShell vs Batch**: PowerShell tem melhor visualização, Batch é mais compatível

## ✅ Checklist de Execução

- [ ] Java 17+ instalado
- [ ] Maven 3.6+ instalado
- [ ] Projeto compilado (`compilar.bat`)
- [ ] Servidor rodando (`iniciar-servidor.bat`)
- [ ] Cliente rodando (`iniciar-cliente.bat`)
- [ ] Banco de dados criado (automático)

## 🔗 Links Úteis

- [INICIO-RAPIDO.md](INICIO-RAPIDO.md) - Guia de início rápido
- [EXECUTAR.md](EXECUTAR.md) - Documentação completa
- [README.md](README.md) - Visão geral do projeto
