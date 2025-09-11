# 🪟 Scripts Windows - Sistema NewPix

Scripts otimizados para Windows (PowerShell + Batch).

## 🚀 Início Rápido

```batch
# Executar menu interativo
scripts\windows\menu.bat

# OU executar diretamente:
scripts\windows\install-dependencies.bat
scripts\windows\start-server.bat
scripts\windows\start-client.bat
```

## 📋 Scripts Disponíveis

### 🎯 Menu Principal
- **`menu.bat`** - Menu interativo com todas as opções

### 📦 Instalação
- **`install-dependencies.bat`** - Instala Java 17, Maven e Git automaticamente

### 🖥️ Execução
- **`start-server.bat`** - Inicia o servidor NewPix
- **`start-client.bat`** - Inicia o cliente NewPix
- **`run-tests.bat`** - Executa todos os testes

### 🛠️ Utilitários
- **`kill-all-java.bat`** - Para todos os processos Java
- **`kill-all-java.ps1`** - Versão PowerShell (mais robusta)
- **`check-utf8-bom.ps1`** - Verifica codificação UTF-8 com BOM

## 🔧 Pré-requisitos

### Instalação Automática
O script `install-dependencies.bat` instala automaticamente:
- **OpenJDK 17** (via winget ou chocolatey)
- **Apache Maven** (via winget ou chocolatey)
- **Git** (via winget)

### Instalação Manual
Se a instalação automática falhar:

1. **Java 17**:
   - Download: https://adoptium.net/
   - Adicionar ao PATH

2. **Apache Maven**:
   - Download: https://maven.apache.org/download.cgi
   - Adicionar ao PATH

3. **Git** (opcional):
   - Download: https://git-scm.com/download/win

## 🎮 Como Usar

### 1️⃣ Primeira vez
```batch
# Instalar dependências
scripts\windows\install-dependencies.bat
```

### 2️⃣ Uso normal
```batch
# Iniciar servidor (terminal 1)
scripts\windows\start-server.bat

# Iniciar cliente (terminal 2)  
scripts\windows\start-client.bat
```

### 3️⃣ Desenvolvimento
```batch
# Executar testes
scripts\windows\run-tests.bat

# Limpar processos
scripts\windows\kill-all-java.bat
```

## ⚠️ Solução de Problemas

### Java não encontrado
```batch
# Verificar instalação
java -version
mvn -version

# Reinstalar
scripts\windows\install-dependencies.bat
```

### GUI não aparece
```batch
# Parar processos anteriores
scripts\windows\kill-all-java.bat

# Reiniciar
scripts\windows\start-server.bat
```

### Erro de compilação
```batch
# Limpar e recompilar
mvn clean compile
```

## 🔍 Logs e Debug

- **Servidor**: Logs aparecem no terminal e na GUI
- **Cliente**: Logs aparecem no terminal e na GUI  
- **Maven**: Use `-X` para debug verbose
- **Compilação**: Verifique `target/` para arquivos gerados

## 💡 Dicas

- 🖥️ **Sempre inicie o servidor antes do cliente**
- 🧹 **Use kill-all-java.bat entre execuções**
- 📱 **As GUIs aparecem automaticamente**
- 🔄 **Use Ctrl+C para parar processos no terminal**
