@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

:: ===================================================================
:: Sistema Validador - Distribuído
:: Script Principal de Gerenciamento
:: ===================================================================

set "PROJECT_NAME=Sistema Validador - Distribuído"
set "VERSION=1.0.0"
set "SERVER_JAR=target\validador-sistemas-distribuidos-1.0.0-server.jar"
set "CLIENT_JAR=target\validador-sistemas-distribuidos-1.0.0-client.jar"
set "MAIN_JAR=target\validador-sistemas-distribuidos-1.0.0.jar"

:: Cores para output
set "GREEN=[32m"
set "RED=[31m"
set "YELLOW=[33m"
set "BLUE=[34m"
set "RESET=[0m"

:MAIN_MENU
cls
echo.
echo %BLUE%=========================================%RESET%
echo %BLUE%    %PROJECT_NAME%%RESET%
echo %BLUE%            v%VERSION%%RESET%
echo %BLUE%=========================================%RESET%
echo.
echo %GREEN%Opções disponíveis:%RESET%
echo.
echo %YELLOW%1.%RESET% Compilar projeto
echo %YELLOW%2.%RESET% Iniciar servidor
echo %YELLOW%3.%RESET% Iniciar cliente
echo %YELLOW%4.%RESET% Executar testes
echo %YELLOW%5.%RESET% Limpar e recompilar
echo %YELLOW%6.%RESET% Parar todos os processos Java
echo %YELLOW%7.%RESET% Verificar status
echo %YELLOW%8.%RESET% Ajuda
echo %YELLOW%0.%RESET% Sair
echo.
set /p "choice=Digite sua escolha: "

if "%choice%"=="1" goto BUILD
if "%choice%"=="2" goto SERVER
if "%choice%"=="3" goto CLIENT
if "%choice%"=="4" goto TEST
if "%choice%"=="5" goto CLEAN
if "%choice%"=="6" goto STOP
if "%choice%"=="7" goto STATUS
if "%choice%"=="8" goto HELP
if "%choice%"=="0" goto END

echo %RED%Opção inválida! Tente novamente.%RESET%
pause
goto MAIN_MENU

:BUILD
echo.
echo %BLUE%Compilando projeto...%RESET%
call mvn clean compile package
if !errorlevel! equ 0 (
    echo %GREEN%✓ Compilação concluída com sucesso!%RESET%
) else (
    echo %RED%✗ Erro na compilação!%RESET%
)
pause
goto MAIN_MENU

:SERVER
echo.
echo %BLUE%Verificando compilação...%RESET%
if not exist "%SERVER_JAR%" (
    if not exist "%MAIN_JAR%" (
        echo %YELLOW%Projeto não compilado. Compilando agora...%RESET%
        call mvn clean compile package
        if !errorlevel! neq 0 (
            echo %RED%✗ Erro na compilação!%RESET%
            pause
            goto MAIN_MENU
        )
    )
)

echo %BLUE%Iniciando servidor...%RESET%
echo %YELLOW%Pressione Ctrl+C para parar o servidor%RESET%
echo.

if exist "%SERVER_JAR%" (
    java -jar "%SERVER_JAR%"
) else if exist "%MAIN_JAR%" (
    java -cp "%MAIN_JAR%" com.distribuidos.server.ServerMain
) else (
    echo %RED%✗ JAR do servidor não encontrado!%RESET%
    echo %YELLOW%Execute a opção 1 para compilar o projeto primeiro.%RESET%
)
pause
goto MAIN_MENU

:CLIENT
echo.
echo %BLUE%Verificando compilação...%RESET%
if not exist "%CLIENT_JAR%" (
    if not exist "%MAIN_JAR%" (
        echo %YELLOW%Projeto não compilado. Compilando agora...%RESET%
        call mvn clean compile package
        if !errorlevel! neq 0 (
            echo %RED%✗ Erro na compilação!%RESET%
            pause
            goto MAIN_MENU
        )
    )
)

echo %BLUE%Iniciando cliente...%RESET%
echo.

if exist "%CLIENT_JAR%" (
    java -jar "%CLIENT_JAR%"
) else if exist "%MAIN_JAR%" (
    java -cp "%MAIN_JAR%" com.distribuidos.client.ClientMain
) else (
    echo %RED%✗ JAR do cliente não encontrado!%RESET%
    echo %YELLOW%Execute a opção 1 para compilar o projeto primeiro.%RESET%
)
pause
goto MAIN_MENU

:TEST
echo.
echo %BLUE%Executando testes...%RESET%
call mvn test
if !errorlevel! equ 0 (
    echo %GREEN%✓ Todos os testes passaram!%RESET%
) else (
    echo %RED%✗ Alguns testes falharam!%RESET%
)
pause
goto MAIN_MENU

:CLEAN
echo.
echo %BLUE%Limpando projeto...%RESET%
call mvn clean
echo %BLUE%Recompilando...%RESET%
call mvn compile package
if !errorlevel! equ 0 (
    echo %GREEN%✓ Limpeza e recompilação concluídas!%RESET%
) else (
    echo %RED%✗ Erro na recompilação!%RESET%
)
pause
goto MAIN_MENU

:STOP
echo.
echo %BLUE%Parando todos os processos Java...%RESET%
taskkill /F /IM java.exe > nul 2>&1
taskkill /F /IM javaw.exe > nul 2>&1
echo %GREEN%✓ Processos Java finalizados!%RESET%
pause
goto MAIN_MENU

:STATUS
echo.
echo %BLUE%Status do sistema:%RESET%
echo.

:: Verificar JARs
echo %YELLOW%Arquivos compilados:%RESET%
if exist "%SERVER_JAR%" (
    echo %GREEN%  ✓ Servidor JAR%RESET% - %SERVER_JAR%
) else (
    echo %RED%  ✗ Servidor JAR%RESET% - %SERVER_JAR%
)

if exist "%CLIENT_JAR%" (
    echo %GREEN%  ✓ Cliente JAR%RESET% - %CLIENT_JAR%
) else (
    echo %RED%  ✗ Cliente JAR%RESET% - %CLIENT_JAR%
)

if exist "%MAIN_JAR%" (
    echo %GREEN%  ✓ JAR principal%RESET% - %MAIN_JAR%
) else (
    echo %RED%  ✗ JAR principal%RESET% - %MAIN_JAR%
)

:: Verificar banco de dados
echo.
echo %YELLOW%Banco de dados:%RESET%
if exist "usuarios.db" (
    echo %GREEN%  ✓ usuarios.db%RESET% - Banco principal
) else (
    echo %RED%  ✗ usuarios.db%RESET% - Banco principal
)

if exist "newpix.db" (
    echo %GREEN%  ✓ newpix.db%RESET% - Banco alternativo
) else (
    echo %RED%  ✗ newpix.db%RESET% - Banco alternativo
)

:: Verificar logs
echo.
echo %YELLOW%Logs:%RESET%
if exist "logs\sistema-distribuido.log" (
    echo %GREEN%  ✓ logs\sistema-distribuido.log%RESET%
) else (
    echo %YELLOW%  ! Nenhum log encontrado%RESET%
)

:: Verificar processos Java em execução
echo.
echo %YELLOW%Processos Java em execução:%RESET%
tasklist /FI "IMAGENAME eq java.exe" /FO CSV 2>nul | find /I "java.exe" > nul
if !errorlevel! equ 0 (
    echo %GREEN%  ✓ Processos Java detectados%RESET%
    tasklist /FI "IMAGENAME eq java.exe" /FO TABLE 2>nul | findstr /V "INFO:"
) else (
    echo %YELLOW%  ! Nenhum processo Java em execução%RESET%
)

pause
goto MAIN_MENU

:HELP
cls
echo.
echo %BLUE%=========================================%RESET%
echo %BLUE%              AJUDA%RESET%
echo %BLUE%=========================================%RESET%
echo.
echo %GREEN%Como usar o sistema:%RESET%
echo.
echo %YELLOW%1. Primeira execução:%RESET%
echo    - Execute a opção 1 (Compilar projeto)
echo    - Aguarde a compilação terminar
echo.
echo %YELLOW%2. Iniciar o sistema:%RESET%
echo    - Execute a opção 2 (Iniciar servidor)
echo    - Em outro terminal, execute a opção 3 (Iniciar cliente)
echo.
echo %YELLOW%3. Testar conexão:%RESET%
echo    - No cliente, use:
echo      Servidor: localhost:8080
echo      CPF: 123.456.789-01
echo      Senha: 123456
echo.
echo %YELLOW%4. Solução de problemas:%RESET%
echo    - Se houver erro de compilação: use opção 5
echo    - Se o sistema travar: use opção 6
echo    - Para verificar status: use opção 7
echo.
echo %GREEN%Estrutura do projeto:%RESET%
echo    - src/main/java/    : Código fonte
echo    - target/           : Arquivos compilados
echo    - logs/             : Logs do sistema
echo    - usuarios.db       : Banco de dados SQLite
echo    - docs/             : Documentação
echo    - scripts/          : Scripts auxiliares
echo.
echo %GREEN%Documentação completa:%RESET%
echo    - README.md         : Visão geral
echo    - docs/protocol.md  : Protocolo de comunicação
echo    - docs/development.md : Guia de desenvolvimento
echo.
pause
goto MAIN_MENU

:END
echo.
echo %GREEN%Obrigado por usar o %PROJECT_NAME%!%RESET%
echo.
exit /b 0
