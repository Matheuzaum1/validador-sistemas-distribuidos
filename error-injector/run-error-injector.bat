@echo off
cd /d "%~dp0"
echo.
echo ===============================================
echo   ERROR INJECTOR - Sistema de Injecao de Erros  
echo ===============================================
echo.
echo Iniciando Error Injector v1.0.0...
echo Sistema de Testes para Protocolo de Sistemas Distribuidos
echo.

REM Verificar se Java esta instalado
java -version >nul 2>&1
if errorlevel 1 (
    echo ERRO: Java nao esta instalado ou nao esta no PATH
    echo Instale Java 11+ e tente novamente
    pause
    exit /b 1
)

REM Verificar se o JAR existe
if not exist "target\error-injector-1.0.0.jar" (
    echo ERRO: JAR nao encontrado!
    echo Execute 'mvn package' primeiro para compilar o projeto
    pause
    exit /b 1
)

REM Executar o JAR
echo Executando: java -jar target\error-injector-1.0.0.jar
echo.
java -jar target\error-injector-1.0.0.jar

REM Se chegou aqui, a aplicacao foi fechada
echo.
echo Error Injector foi fechado.
pause