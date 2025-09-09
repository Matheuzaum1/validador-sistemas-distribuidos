@echo off
echo ======================================
echo     NewPix - Setup de Dependencias
echo ======================================
echo.

cd /d "%~dp0\.."

echo Verificando Java...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Java nao encontrado! Instale Java 17 ou superior.
    pause
    exit /b 1
)
echo OK Java encontrado!

echo.
echo Verificando Maven...
mvn -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Maven nao encontrado! Instale Apache Maven.
    pause
    exit /b 1
)
echo OK Maven encontrado!

echo.
echo Limpando cache do Maven...
mvn clean

echo.
echo Forcando download de dependencias...
mvn dependency:purge-local-repository -q

echo.
echo Baixando dependencias do projeto...
mvn dependency:resolve -q

echo.
echo Compilando projeto...
mvn compile -q

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Setup concluido com sucesso!
    echo Todas as dependencias foram baixadas!
    echo Projeto compilado e pronto para uso!
    echo.
    echo Agora voce pode usar:
    echo   scripts\start-server.bat  (para servidor^)
    echo   scripts\start-client.bat  (para cliente^)
) else (
    echo.
    echo ERRO durante o setup!
    echo Verifique sua conexao com internet.
)

echo.
pause
