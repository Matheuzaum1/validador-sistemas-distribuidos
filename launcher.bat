@echo off
title Sistema Distribuido - Launcher
cls

echo ================================================
echo    Sistema Bancario Distribuido - Launcher
echo ================================================
echo.
echo Selecione uma opcao:
echo.
echo [1] Iniciar Servidor
echo [2] Iniciar Cliente  
echo [3] Compilar Projeto
echo [0] Sair
echo.
set /p choice="Digite sua opcao (0-3): "

if "%choice%"=="1" goto servidor
if "%choice%"=="2" goto cliente
if "%choice%"=="3" goto compilar
if "%choice%"=="0" goto sair
echo Opcao invalida! Tente novamente.
pause
goto inicio

:servidor
echo.
echo Iniciando Servidor...
call :setup_java
call :compile_if_needed
echo Executando servidor...
java -cp "target\classes;%CLASSPATH%" com.distribuidos.server.ServerMain
goto end

:cliente
echo.
echo Iniciando Cliente...
call :setup_java
call :compile_if_needed
echo Executando cliente...
java -cp "target\classes;%CLASSPATH%" com.distribuidos.client.ClientMain
goto end

:compilar
echo.
echo Compilando projeto...
call :setup_java
mvn clean compile
echo Compilacao concluida!
pause
goto inicio

:setup_java
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.17.2\jackson-databind-2.17.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.17.2\jackson-annotations-2.17.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.17.2\jackson-core-2.17.2.jar;%USERPROFILE%\.m2\repository\org\xerial\sqlite-jdbc\3.46.1.0\sqlite-jdbc-3.46.1.0.jar;%USERPROFILE%\.m2\repository\org\slf4j\slf4j-api\2.0.16\slf4j-api-2.0.16.jar;%USERPROFILE%\.m2\repository\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar;%USERPROFILE%\.m2\repository\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar;%USERPROFILE%\.m2\repository\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar;%USERPROFILE%\.m2\repository\com\formdev\flatlaf\3.2.1\flatlaf-3.2.1.jar
goto :eof

:compile_if_needed
if not exist "target\classes" (
    echo Projeto nao compilado. Compilando...
    mvn clean compile
    if errorlevel 1 (
        echo Erro na compilacao!
        pause
        exit /b 1
    )
)
goto :eof

:sair
echo Saindo...
exit /b 0

:end
echo.
echo Aplicacao encerrada.
pause
goto inicio