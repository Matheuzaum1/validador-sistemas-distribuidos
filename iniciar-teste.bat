@echo off
echo ===================================
echo  Sistema Validador - TESTE
echo  Servidor + Cliente
echo ===================================
echo.

REM Configurar JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-25

echo Iniciando servidor...
start "Servidor" cmd /k "java -Dfile.encoding=UTF-8 -jar target\validador-sistemas-distribuidos-1.0.0-server.jar"

echo Aguardando servidor inicializar...
timeout /t 3 /nobreak >nul

echo Iniciando cliente...
start "Cliente" cmd /k "java -Dfile.encoding=UTF-8 -jar target\validador-sistemas-distribuidos-1.0.0-client.jar"

echo.
echo ===================================
echo  Sistema iniciado com sucesso!
echo ===================================
echo.
echo Servidor: Janela separada
echo Cliente: Janela separada
echo.
echo TESTE RECOMENDADO:
echo 1. No cliente, clique em "Conectar" primeiro
echo 2. Crie um usuario ou faca login
echo 3. Teste todas as operacoes
echo.
echo Para parar: Feche as janelas ou pressione Ctrl+C
pause