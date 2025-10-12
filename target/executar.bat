@echo off
echo ===================================
echo  Sistema Validador Distribuido
echo  Versao 1.0.0 - Java 21
echo ===================================
echo.

REM Verifica se o Java esta instalado
java -version >nul 2>&1
if errorlevel 1 (
    echo ERRO: Java nao encontrado!
    echo.
    echo Por favor, instale o Java 21 ou superior:
    echo https://adoptium.net/temurin/releases/
    echo.
    pause
    exit /b 1
)

echo Java encontrado. Verificando versao...
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i version') do (
    set JAVA_VERSION=%%i
)

echo Versao do Java: %JAVA_VERSION%
echo.

:menu
echo Escolha uma opcao:
echo.
echo [1] Iniciar Servidor (com interface grafica)
echo [2] Iniciar Servidor (modo headless)
echo [3] Iniciar Cliente
echo [4] Sair
echo.
set /p choice="Digite sua opcao (1-4): "

if "%choice%"=="1" goto start_server_gui
if "%choice%"=="2" goto start_server_headless
if "%choice%"=="3" goto start_client
if "%choice%"=="4" goto exit

echo Opcao invalida! Tente novamente.
echo.
goto menu

:start_server_gui
echo.
echo Iniciando servidor com interface grafica...
echo Pressione Ctrl+C para parar o servidor.
echo.
java -jar validador-sistemas-distribuidos-1.0.0-server.jar
goto menu

:start_server_headless
echo.
echo Iniciando servidor em modo headless (sem interface)...
echo Pressione Ctrl+C para parar o servidor.
echo.
java -jar validador-sistemas-distribuidos-1.0.0-server.jar -headless
goto menu

:start_client
echo.
echo Iniciando cliente...
echo.
java -jar validador-sistemas-distribuidos-1.0.0-client.jar
goto menu

:exit
echo.
echo Saindo...
exit /b 0