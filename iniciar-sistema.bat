@echo off
title Sistema Distribuido - Inicializacao Completa
echo.
echo ===============================================
echo   Sistema Distribuido - INICIALIZACAO
echo ===============================================
echo.

:: Verifica se o JAR existe
if not exist "target\validador-sistemas-distribuidos-1.0.0.jar" (
    echo [!] JAR nao encontrado. Compilando projeto...
    echo.
    call mvn clean package -DskipTests
    if errorlevel 1 (
        echo.
        echo [ERRO] Falha na compilacao!
        echo.
        pause
        exit /b 1
    )
    echo.
    echo [OK] Compilacao concluida!
    echo.
)

echo Iniciando servidor em segundo plano...
start "Servidor - Porta 8080" java -cp "target\validador-sistemas-distribuidos-1.0.0.jar" com.distribuidos.server.ServerMain

echo Aguardando servidor inicializar...
timeout /t 3 /nobreak > nul

echo Iniciando cliente...
echo.
echo ===============================================
echo.

start "Cliente - Interface Grafica" java -cp "target\validador-sistemas-distribuidos-1.0.0.jar" com.distribuidos.client.ClientMain

echo.
echo ===============================================
echo [OK] Sistema iniciado!
echo ===============================================
echo.
echo Servidor: Janela "Servidor - Porta 8080"
echo Cliente:  Janela "Cliente - Interface Grafica"
echo.
echo Para encerrar, feche as janelas ou pressione Ctrl+C
echo.

pause
