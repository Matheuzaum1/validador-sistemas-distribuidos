# Script NewPix.ps1 - Atualizado

## Resumo das Atualizações

O script `newpix.ps1` foi atualizado para funcionar com as versões reformuladas do cliente e servidor.

### Classes Atualizadas

- **Servidor**: `com.newpix.server.gui.ServerGUI`
- **Cliente**: `com.newpix.client.gui.LoginGUI`

### Novas Funcionalidades

1. **Verificação de Classes**: O script agora verifica se as classes principais existem antes de tentar executá-las
2. **Teste do Sistema**: Nova opção para testar se tudo está funcionando corretamente
3. **Melhor Feedback**: Mensagens mais claras sobre o status do sistema

### Comandos Disponíveis

```powershell
# Menu interativo
.\newpix.ps1 menu

# Sistema completo (servidor + cliente)
.\newpix.ps1 both-gui

# Apenas servidor
.\newpix.ps1 server-gui

# Apenas cliente
.\newpix.ps1 client-gui

# Verificar status
.\newpix.ps1 status

# Testar sistema
.\newpix.ps1 test

# Compilar projeto
.\newpix.ps1 build

# Limpar projeto
.\newpix.ps1 clean

# Parar serviços
.\newpix.ps1 stop

# Ajuda
.\newpix.ps1 help
```

### Pré-requisitos

- Java 8 ou superior
- Maven 3.x
- PowerShell (Windows)

### Como Usar

1. **Primeira execução**: `.\newpix.ps1 build` para compilar
2. **Testar sistema**: `.\newpix.ps1 test` para verificar se tudo está OK
3. **Executar sistema**: `.\newpix.ps1 both-gui` para iniciar servidor e cliente

### Principais Melhorias

- ✅ Verificação automática de dependências
- ✅ Validação de classes compiladas
- ✅ Feedback claro sobre erros
- ✅ Opção de teste integrada
- ✅ Compatibilidade com GUIs reformuladas