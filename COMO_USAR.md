# 🚀 Como Usar o Sistema NewPix

## 🖥️ Scripts por Plataforma

### 🪟 Windows
```batch
# Menu interativo (recomendado)
scripts\windows\menu.bat

# Scripts individuais
scripts\windows\install-dependencies.bat
scripts\windows\start-server.bat
scripts\windows\start-client.bat
```

### 🐧 Linux  
```bash
# Menu interativo (recomendado)
./scripts/linux/menu.sh

# Scripts individuais
./scripts/linux/install-dependencies.sh
./scripts/linux/start-server.sh
./scripts/linux/start-client.sh
```

## ⚠️ IMPORTANTE: Interfaces Gráficas Obrigatórias

**TODOS os scripts foram configurados para FORÇAR a aparição das interfaces gráficas!**
- ✅ O GUI do servidor SEMPRE aparecerá
- ✅ O GUI do cliente SEMPRE aparecerá  
- ✅ Nenhuma execução em segundo plano sem interface

## 🛑 Parar Processos em Execução

**ANTES de iniciar o projeto, sempre pare processos anteriores:**

### Windows:
```batch
scripts\windows\kill-all-java.bat
# OU PowerShell:
scripts\windows\kill-all-java.ps1
```

### Linux:
```bash
./scripts/linux/kill-all-java.sh
```

## 🖥️ Iniciando o Sistema

### 1. Instalar Dependências (primeira vez)

**Windows:**
```batch
scripts\windows\install-dependencies.bat
```

**Linux:**
```bash
./scripts/linux/install-dependencies.sh
```

### 2. Iniciar o Servidor (OBRIGATÓRIO primeiro!)

**Windows:**
```batch
scripts\windows\start-server.bat
# OU PowerShell:
scripts\windows\start-server.ps1
```

**Linux:**
```bash
./scripts/linux/start-server.sh
```

**Maven (qualquer plataforma):**
```bash
mvn compile exec:java -Pserver
```

### 3. Iniciar o Cliente (após servidor estar rodando)

**Windows:**
```batch
scripts\windows\start-client.bat
# OU PowerShell:
scripts\windows\start-client.ps1
```

**Linux:**
```bash
./scripts/linux/start-client.sh
```

**Maven (qualquer plataforma):**
```bash
mvn compile exec:java -Pclient
```

## 📱 Usando o Sistema

### GUI do Servidor
- ✅ Interface aparece automaticamente
- ✅ Mostra logs de conexões
- ✅ Controle de parada do servidor
- ✅ Status das operações

### GUI do Cliente  
- ✅ Interface de login aparece automaticamente
- ✅ Cadastro de novos usuários
- ✅ Área principal com todas as operações
- ✅ Histórico de transações

## 🔧 Solução de Problemas

### GUI não aparece?

**Windows:**
```batch
# Parar processos anteriores
scripts\windows\kill-all-java.bat
# Verificar processos Java
Get-Process java
# Reiniciar
scripts\windows\start-server.bat
```

**Linux:**
```bash
# Parar processos anteriores  
./scripts/linux/kill-all-java.sh
# Verificar display
echo $DISPLAY
# Instalar dependências GUI se necessário
sudo apt install libxext6 libxrender1 libxtst6 libxi6
# Reiniciar
./scripts/linux/start-server.sh
```

### Erro de conexão?
1. Certifique-se que o SERVIDOR está rodando primeiro
2. Aguarde alguns segundos antes de iniciar o cliente
3. Verifique se a porta 8080 está livre

### Projeto não compila?
```bash
mvn clean compile
```

## 📋 Ordem de Execução para Testes

### Windows:
1. **Parar tudo:** `scripts\windows\kill-all-java.bat`
2. **Iniciar servidor:** `scripts\windows\start-server.bat`  
3. **Aguardar GUI do servidor aparecer**
4. **Iniciar cliente:** `scripts\windows\start-client.bat`
5. **GUI do cliente aparece automaticamente**

### Linux:
1. **Parar tudo:** `./scripts/linux/kill-all-java.sh`
2. **Iniciar servidor:** `./scripts/linux/start-server.sh`  
3. **Aguardar GUI do servidor aparecer**
4. **Iniciar cliente:** `./scripts/linux/start-client.sh`
5. **GUI do cliente aparece automaticamente**

## 🎯 Garantias do Sistema

- ✅ **GUI obrigatório:** Todas as execuções abrem interfaces visuais
- ✅ **Não roda escondido:** Nenhum processo fica em segundo plano sem GUI
- ✅ **Fácil parada:** Scripts para parar todos os processos
- ✅ **Execução limpa:** Sempre para processos anteriores

## 📝 Codificação de Arquivos

✅ **Todos os arquivos de texto foram convertidos para UTF-8 com BOM:**
- 📄 Documentação (.md)
- 🖥️ Scripts PowerShell (.ps1) 
- ⚙️ Scripts Batch (.bat)
- ☕ Código Java (.java)
- 📋 Configurações (pom.xml, .gitignore)

Para verificar a codificação dos arquivos:

**Windows:**
```powershell
.\scripts\windows\check-utf8-bom.ps1
```

**Linux:**
```bash
./scripts/linux/check-utf8-bom.sh
```