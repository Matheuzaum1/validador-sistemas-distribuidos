# 📁 Scripts do Sistema NewPix

Scripts organizados por plataforma para facilitar a execução e manutenção.

## 🗂️ Estrutura

```
scripts/
├── windows/          # Scripts para Windows
│   ├── menu.bat      # Menu interativo
│   ├── *.bat         # Scripts Batch
│   ├── *.ps1         # Scripts PowerShell
│   └── README.md     # Documentação Windows
├── linux/            # Scripts para Linux
│   ├── menu.sh       # Menu interativo
│   ├── *.sh          # Scripts Bash
│   └── README.md     # Documentação Linux
└── README.md         # Este arquivo
```

## 🚀 Uso Rápido

### Windows
```batch
# Menu completo
scripts\windows\menu.bat

# Início rápido
scripts\windows\install-dependencies.bat
scripts\windows\start-server.bat
scripts\windows\start-client.bat
```

### Linux
```bash
# Menu completo
./scripts/linux/menu.sh

# Início rápido  
./scripts/linux/install-dependencies.sh
./scripts/linux/start-server.sh
./scripts/linux/start-client.sh
```

## 📋 Scripts Disponíveis

| Funcionalidade | Windows | Linux |
|---|---|---|
| **Menu Interativo** | `menu.bat` | `menu.sh` |
| **Instalar Dependências** | `install-dependencies.bat` | `install-dependencies.sh` |
| **Iniciar Servidor** | `start-server.bat` | `start-server.sh` |
| **Iniciar Cliente** | `start-client.bat` | `start-client.sh` |
| **Executar Testes** | `run-tests.bat` | `run-tests.sh` |
| **Parar Java** | `kill-all-java.bat` | `kill-all-java.sh` |
| **Verificar UTF-8** | `check-utf8-bom.ps1` | `check-utf8-bom.sh` |

## 🎯 Funcionalidades

### ✅ Instalação Automática
- **Java 17** (OpenJDK)
- **Apache Maven**  
- **Git**
- **Dependências GUI** (Linux)

### 🖥️ Execução Robusta
- **Interface gráfica obrigatória**
- **Detecção de erros**
- **Limpeza automática**
- **Logs detalhados**

### 🛠️ Utilitários
- **Parar todos os processos Java**
- **Verificar codificação de arquivos**
- **Executar testes unitários**
- **Menu interativo**

## 🔧 Detecção Automática

Os scripts detectam automaticamente:
- **SO e distribuição** (Linux)
- **Gerenciador de pacotes** 
- **Arquitetura do sistema**
- **Dependências instaladas**
- **Ambiente gráfico** (X11/Wayland)

## 📚 Documentação Detalhada

- **[Windows Scripts](windows/README.md)** - Documentação completa para Windows
- **[Linux Scripts](linux/README.md)** - Documentação completa para Linux
- **[Como Usar](../COMO_USAR.md)** - Guia geral de uso do sistema

## 💡 Dicas

- 🎮 **Use os menus interativos** para facilidade de uso
- 🔄 **Sempre pare processos Java** antes de reiniciar  
- 📱 **GUIs aparecem automaticamente**
- 🐧 **No Linux, use `chmod +x`** para dar permissões
- 🪟 **No Windows, execute como administrador** se necessário
