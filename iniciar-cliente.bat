@echo off
title Sistema Distribuido - Cliente
echo.
echo ===============================================
echo   Sistema Distribuido - CLIENTE
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

echo Iniciando interface grafica do cliente...
echo.
echo ===============================================
echo.

java -cp "target\validador-sistemas-distribuidos-1.0.0.jar" com.distribuidos.client.ClientMain

pause
