# 📋 Índice de Navegação - Sistema NewPix

## 🚀 Para Começar Rapidamente
- **README Principal:** `README.md` - Visão geral e início rápido
- **Guia de Uso:** `COMO_USAR.md` - Instruções passo a passo

## 📁 Estrutura Organizada

### 📖 Documentação (`docs/`)
- `TESTE_COMPLETO.md` - Guia completo de execução e teste
- `ROBUSTEZ_AVALIACAO.md` - Documentação de robustez para avaliação
- `STATUS_FINAL.md` - Status atual e funcionalidades
- `README_PROJETO.md` - Documentação técnica detalhada  
- `Requisitos.md` - Requisitos originais do projeto

### 🎮 Scripts de Execução (`scripts/`)
- `start-server.ps1` - Iniciar servidor (PowerShell)
- `start-client.ps1` - Iniciar cliente (PowerShell)
- `start-server.bat` - Iniciar servidor (Batch)
- `start-client.bat` - Iniciar cliente (Batch)
- `run-tests.bat` - Executar testes
- `test-simple.bat` - Teste simplificado

### 💻 Código Fonte (`src/main/java/`)
```
com/newpix/
├── client/          # Cliente e interfaces gráficas
├── server/          # Servidor e processamento  
├── dao/             # Acesso a dados (SQLite)
├── model/           # Modelos de dados
├── service/         # Lógica de negócios
└── util/            # Utilitários e helpers

validador/           # Validadores JSON originais
```

### 🔧 Validador Original (`validador-original/`)
- `Validator.java` - Validador JSON do projeto base
- `RulesEnum.java` - Enums de regras originais

## ⚡ Execução Mais Rápida

### Método JAR (Recomendado)
```bash
# 1. Compilar:
mvn clean package -DskipTests

# 2. Servidor:
java -jar target/validador-sistemas-distribuidos-1.0.0-jar-with-dependencies.jar 8082

# 3. Cliente:
java -cp target/validador-sistemas-distribuidos-1.0.0-jar-with-dependencies.jar com.newpix.client.gui.LoginGUI
```

### Scripts PowerShell
```powershell
.\scripts\start-server.ps1
.\scripts\start-client.ps1
```

## 🎯 Para Avaliação Acadêmica

O projeto está **100% organizado** e pronto para avaliação:

- ✅ **Documentação clara** em `docs/`
- ✅ **Scripts funcionais** em `scripts/`
- ✅ **Código organizado** por responsabilidade
- ✅ **Execução simplificada** via JAR
- ✅ **Validadores originais** preservados

## 📞 Suporte

- **Problemas de execução:** Ver `COMO_USAR.md`
- **Documentação técnica:** Ver `docs/README_PROJETO.md`
- **Testes e robustez:** Ver `docs/ROBUSTEZ_AVALIACAO.md`
