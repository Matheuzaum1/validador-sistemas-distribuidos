@echo off
title Sistema Distribuido - Servidor
echo.
echo ===============================================
echo   Sistema Distribuido - SERVIDOR
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

echo Iniciando servidor na porta 8080...
echo.
echo Pressione Ctrl+C para parar o servidor
echo ===============================================
echo.

java -cp "target\validador-sistemas-distribuidos-1.0.0.jar" com.distribuidos.server.ServerMain

pause
