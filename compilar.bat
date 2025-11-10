@echo off
title Sistema Distribuido - Compilacao
echo.
echo ===============================================
echo   Sistema Distribuido - COMPILACAO
echo ===============================================
echo.

echo Limpando builds anteriores...
call mvn clean

echo.
echo Compilando projeto...
call mvn package -DskipTests

if errorlevel 1 (
    echo.
    echo [ERRO] Falha na compilacao!
    echo.
    pause
    exit /b 1
) else (
    echo.
    echo ===============================================
    echo [OK] Compilacao concluida com sucesso!
    echo ===============================================
    echo.
    echo Arquivo gerado:
    echo   target\validador-sistemas-distribuidos-1.0.0.jar
    echo.
    echo Para executar:
    echo   - Servidor: iniciar-servidor.bat
    echo   - Cliente:  iniciar-cliente.bat
    echo.
)

pause
