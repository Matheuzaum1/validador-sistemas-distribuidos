@echo off
chcp 65001 >nul
cls

echo ===================================
echo  Sistema Validador - Distribuído
echo  Inicialização Automática
echo ===================================
echo.

REM Configurar Java
set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Verificar se Java está disponível
echo [1/4] Verificando Java...
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ ERRO: Java não encontrado. Instale Java 21+ ou configure JAVA_HOME
    pause
    exit /b 1
)
echo ✅ Java encontrado

REM Compilar projeto se necessário
echo [2/4] Compilando projeto...
mvn clean compile package -q >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ ERRO: Falha na compilação
    pause
    exit /b 1
)
echo ✅ Projeto compilado

REM Matar processos Java existentes
echo [3/4] Parando processos existentes...
taskkill /f /fi "IMAGENAME eq java.exe" >nul 2>&1
timeout /t 2 >nul

REM Iniciar servidor em background
echo [4/4] Iniciando sistema...
echo.
echo 🖥️  Iniciando SERVIDOR...
start "Validador - Servidor" java -Dfile.encoding=UTF-8 -jar target\validador-sistemas-distribuidos-1.0.0-server.jar

REM Aguardar servidor inicializar
timeout /t 5 >nul

REM Iniciar cliente
echo 💻 Iniciando CLIENTE...
start "Validador - Cliente" java -Dfile.encoding=UTF-8 -jar target\validador-sistemas-distribuidos-1.0.0-client.jar

echo.
echo ===================================
echo ✅ Sistema iniciado com sucesso!
echo ===================================
echo.
echo 📌 INSTRUÇÕES DE USO:
echo   1. No CLIENTE: Clique em "Conectar" 
echo   2. Use CPF: 123.456.789-01 Senha: 123456
echo   3. Teste todas as operações disponíveis
echo.
echo 🔧 RECURSOS DISPONÍVEIS:
echo   ✅ Formatação automática de CPF
echo   ✅ Validação de protocolo bancário  
echo   ✅ Interface gráfica completa
echo   ✅ Banco SQLite com usuários de teste
echo.
echo 🛑 Para parar: Feche as janelas do servidor e cliente
echo.
pause