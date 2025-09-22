@echo off
echo Iniciando Cliente do Sistema Distribuido...
echo.

REM Compila o projeto se necessario
if not exist "target\classes" (
    echo Compilando projeto...
    mvn clean compile
    if errorlevel 1 (
        echo Erro na compilacao!
        pause
        exit /b 1
    )
)

REM Baixa dependencias se necessario
if not exist "target\dependency" (
    echo Baixando dependencias...
    mvn dependency:copy-dependencies
    if errorlevel 1 (
        echo Erro ao baixar dependencias!
        pause
        exit /b 1
    )
)

echo Iniciando cliente...
echo Para parar o cliente, feche a janela da aplicacao.
echo.

java -cp "target/classes;target/dependency/*" com.distribuidos.client.ClientMain

pause