# 🚀 Como Usar o Sistema NewPix

## ⚠️ IMPORTANTE: Interfaces Gráficas Obrigatórias

**TODOS os scripts foram configurados para FORÇAR a aparição das interfaces gráficas!**
- ✅ O GUI do servidor SEMPRE aparecerá
- ✅ O GUI do cliente SEMPRE aparecerá  
- ✅ Nenhuma execução em segundo plano sem interface

## 🛑 Parar Processos em Execução

**ANTES de iniciar o projeto, sempre pare processos anteriores:**

### Windows PowerShell:
```powershell
scripts\kill-all-java.ps1
```

### Windows Batch:
```batch
scripts\kill-all-java.bat
```

## 🖥️ Iniciando o Sistema

### 1. Iniciar o Servidor (OBRIGATÓRIO primeiro!)

**PowerShell:**
```powershell
scripts\start-server.ps1
```

**Batch:**
```batch
scripts\start-server.bat
```

**Maven:**
```bash
mvn compile exec:java -Pserver
```

### 2. Iniciar o Cliente (após servidor estar rodando)

**PowerShell:**
```powershell
scripts\start-client.ps1
```

**Batch:**
```batch
scripts\start-client.bat
```

**Maven:**
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
1. Execute primeiro: `scripts\kill-all-java.ps1` ou `scripts\kill-all-java.bat`
2. Verifique se não há outro Java rodando: `Get-Process java`
3. Tente executar novamente os scripts

### Erro de conexão?
1. Certifique-se que o SERVIDOR está rodando primeiro
2. Aguarde alguns segundos antes de iniciar o cliente
3. Verifique se a porta 8080 está livre

### Projeto não compila?
```bash
mvn clean compile
```

## 📋 Ordem de Execução para Testes

1. **Parar tudo:** `scripts\kill-all-java.ps1`
2. **Iniciar servidor:** `scripts\start-server.ps1`  
3. **Aguardar GUI do servidor aparecer**
4. **Iniciar cliente:** `scripts\start-client.ps1`
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
```powershell
.\scripts\check-utf8-bom.ps1
```