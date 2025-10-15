@echo off
setlocal enabledelayedexpansion

:: ===================================================================
:: Sistema Validador - Distribuido
:: Script Principal de Gerenciamento
:: ===================================================================

set "PROJECT_NAME=Sistema Validador - Distribuido"
set "VERSION=1.0.0"
set "MAIN_JAR=target\validador-sistemas-distribuidos-1.0.0.jar"

:MAIN_MENU
cls
echo.
echo =========================================
echo     %PROJECT_NAME%
echo             v%VERSION%
echo =========================================
echo.
echo Opcoes disponiveis:
echo.
echo 1. Compilar projeto
echo 2. Iniciar servidor
echo 3. Iniciar cliente  
echo 4. Executar testes
echo 5. Limpar e recompilar
echo 6. Parar todos os processos Java
echo 7. Verificar status
echo 8. Ajuda
echo 0. Sair
echo.
set /p "choice=Digite sua escolha (0-8): "

if "%choice%"=="1" call :BUILD
if "%choice%"=="2" call :SERVER
if "%choice%"=="3" call :CLIENT
if "%choice%"=="4" call :TEST
if "%choice%"=="5" call :CLEAN
if "%choice%"=="6" call :STOP
if "%choice%"=="7" call :STATUS
if "%choice%"=="8" call :HELP
if "%choice%"=="0" goto END

echo Opcao invalida! Tente novamente.
pause
goto MAIN_MENU

:: ===================================================================
:: FUNCOES
:: ===================================================================

:BUILD
echo.
echo Compilando projeto...
call mvn clean compile package
if !errorlevel! equ 0 (
    echo [OK] Compilacao concluida com sucesso!
) else (
    echo [ERRO] Erro na compilacao!
)
echo.
echo Pressione qualquer tecla para continuar...
pause > nul
goto MAIN_MENU

:SERVER
echo.
echo Verificando compilacao...
if not exist "%MAIN_JAR%" (
    echo Projeto nao compilado. Compilando agora...
    call mvn clean compile package
    if !errorlevel! neq 0 (
        echo [ERRO] Erro na compilacao!
        echo.
        echo Pressione qualquer tecla para continuar...
        pause > nul
        goto MAIN_MENU
    )
)

echo.
echo Iniciando servidor...
echo Pressione Ctrl+C para parar o servidor
echo.

java -cp "%MAIN_JAR%" com.distribuidos.server.ServerMain
echo.
echo Pressione qualquer tecla para continuar...
pause > nul
goto MAIN_MENU

:CLIENT
echo.
echo Verificando compilacao...
if not exist "%MAIN_JAR%" (
    echo Projeto nao compilado. Compilando agora...
    call mvn clean compile package
    if !errorlevel! neq 0 (
        echo [ERRO] Erro na compilacao!
        echo.
        echo Pressione qualquer tecla para continuar...
        pause > nul
        goto MAIN_MENU
    )
)

echo.
echo Iniciando cliente...
echo.

java -cp "%MAIN_JAR%" com.distribuidos.client.ClientMain
echo.
echo Pressione qualquer tecla para continuar...
pause > nul
goto MAIN_MENU

:TEST
echo.
echo Executando testes...
call mvn test
if !errorlevel! equ 0 (
    echo [OK] Todos os testes passaram!
) else (
    echo [ERRO] Alguns testes falharam!
)
echo.
echo Pressione qualquer tecla para continuar...
pause > nul
goto MAIN_MENU

:CLEAN
echo.
echo Limpando projeto...
call mvn clean
echo Recompilando...
call mvn compile package
if !errorlevel! equ 0 (
    echo [OK] Limpeza e recompilacao concluidas!
) else (
    echo [ERRO] Erro na recompilacao!
)
echo.
echo Pressione qualquer tecla para continuar...
pause > nul
goto MAIN_MENU

:STOP
echo.
echo Parando todos os processos Java...
taskkill /F /IM java.exe > nul 2>&1
taskkill /F /IM javaw.exe > nul 2>&1
echo [OK] Processos Java finalizados!
echo.
echo Pressione qualquer tecla para continuar...
pause > nul
goto MAIN_MENU

:STATUS
echo.
echo Status do sistema:
echo.

:: Verificar JARs
echo Arquivos compilados:
if exist "%MAIN_JAR%" (
    echo   [OK] JAR principal - %MAIN_JAR%
) else (
    echo   [X] JAR principal - %MAIN_JAR%
)

:: Verificar banco de dados
echo.
echo Banco de dados:
if exist "usuarios.db" (
    echo   [OK] usuarios.db - Banco principal
) else (
    echo   [X] usuarios.db - Banco principal
)

if exist "newpix.db" (
    echo   [OK] newpix.db - Banco alternativo
) else (
    echo   [X] newpix.db - Banco alternativo
)

:: Verificar logs
echo.
echo Logs:
if exist "logs\sistema-distribuido.log" (
    echo   [OK] logs\sistema-distribuido.log
) else (
    echo   [!] Nenhum log encontrado
)

:: Verificar processos Java em execução
echo.
echo Processos Java em execucao:
tasklist /FI "IMAGENAME eq java.exe" /FO CSV 2>nul | find /I "java.exe" > nul
if !errorlevel! equ 0 (
    echo   [OK] Processos Java detectados
    tasklist /FI "IMAGENAME eq java.exe" /FO TABLE 2>nul | findstr /V "INFO:"
) else (
    echo   [!] Nenhum processo Java em execucao
)

echo.
echo Pressione qualquer tecla para continuar...
pause > nul
goto MAIN_MENU

:HELP
cls
echo.
echo =========================================
echo              AJUDA
echo =========================================
echo.
echo Como usar o sistema:
echo.
echo 1. Primeira execucao:
echo    - Execute a opcao 1 (Compilar projeto)
echo    - Aguarde a compilacao terminar
echo.
echo 2. Iniciar o sistema:
echo    - Execute a opcao 2 (Iniciar servidor)
echo    - Em outro terminal, execute a opcao 3 (Iniciar cliente)
echo.
echo 3. Testar conexao:
echo    - No cliente, use:
echo      Servidor: localhost:8080
echo      CPF: 123.456.789-01
echo      Senha: 123456
echo.
echo 4. Solucao de problemas:
echo    - Se houver erro de compilacao: use opcao 5
echo    - Se o sistema travar: use opcao 6
echo    - Para verificar status: use opcao 7
echo.
echo Estrutura do projeto:
echo    - src/main/java/    : Codigo fonte
echo    - target/           : Arquivos compilados
echo    - logs/             : Logs do sistema
echo    - usuarios.db       : Banco de dados SQLite
echo    - docs/             : Documentacao
echo    - scripts/          : Scripts auxiliares
echo.
echo Documentacao completa:
echo    - README.md         : Visao geral
echo    - docs/protocol.md  : Protocolo de comunicacao
echo    - docs/development.md : Guia de desenvolvimento
echo.
echo Pressione qualquer tecla para continuar...
pause > nul
goto MAIN_MENU

:END
echo.
echo Obrigado por usar o %PROJECT_NAME%!
echo.
exit /b 0
