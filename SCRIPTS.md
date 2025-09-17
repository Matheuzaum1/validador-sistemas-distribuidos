# Scripts de Automação NewPix Banking System

Este projeto inclui scripts de automação para facilitar o desenvolvimento e execução do sistema em diferentes plataformas.

## Scripts Disponíveis

### Windows (PowerShell)

- **newpix.ps1** - Script principal para Windows

### Linux/macOS (Bash)

- **newpix.sh** - Script equivalente para sistemas Unix

## Como Usar

### No Windows

```powershell
# Menu interativo
.\newpix.ps1

# Comandos diretos
.\newpix.ps1 both-gui      # Sistema completo
.\newpix.ps1 server-gui    # Apenas servidor
.\newpix.ps1 client-gui    # Apenas cliente
.\newpix.ps1 build         # Compilar projeto
.\newpix.ps1 status        # Ver status
.\newpix.ps1 help          # Ajuda
```

### No Linux/macOS

```bash
# Tornar executável (primeira vez)
chmod +x newpix.sh

# Menu interativo
./newpix.sh

# Comandos diretos
./newpix.sh both-gui      # Sistema completo
./newpix.sh server-gui    # Apenas servidor
./newpix.sh client-gui    # Apenas cliente
./newpix.sh build         # Compilar projeto
./newpix.sh status        # Ver status
./newpix.sh help          # Ajuda
```

## Funcionalidades dos Scripts

Ambos os scripts oferecem as mesmas funcionalidades:

1. **Sistema Completo** - Inicia servidor e cliente automaticamente
2. **Servidor GUI** - Inicia apenas a interface do servidor
3. **Cliente GUI** - Inicia apenas a interface do cliente
4. **Status** - Mostra status do sistema e pré-requisitos
5. **Compilar** - Compila o projeto Maven
6. **Limpar** - Limpa arquivos compilados
7. **Parar Serviços** - Para todos os processos Java do sistema
8. **Testar Sistema** - Verifica se tudo está funcionando
9. **Changelog** - Mostra mudanças recentes

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- Sistema operacional com interface gráfica (para as GUIs)

## Classes Principais

- **Servidor**: `com.newpix.server.gui.ServerGUI`
- **Cliente**: `com.newpix.client.gui.LoginWindow`

## Solução de Problemas

Se encontrar problemas:

1. Execute `test` para verificar pré-requisitos
2. Execute `clean` seguido de `build` para recompilar
3. Verifique se Java e Maven estão no PATH
4. No Linux/macOS, certifique-se de que o script tem permissão de execução

## Exemplo de Uso Completo

```bash
# Linux/macOS
./newpix.sh test        # Verificar sistema
./newpix.sh build       # Compilar se necessário
./newpix.sh both-gui    # Iniciar sistema completo
```

```powershell
# Windows
.\newpix.ps1 test        # Verificar sistema
.\newpix.ps1 build       # Compilar se necessário
.\newpix.ps1 both-gui    # Iniciar sistema completo
```