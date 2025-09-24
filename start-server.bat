@echo off
echo Iniciando Servidor do Sistema Distribuido...
echo.

REM Compila o projeto se necessario
if not exist "target\classes" (
    echo Compilando projeto...
    mvn -DskipTests package
    if errorlevel 1 (
        echo Erro na compilacao!
        pause
        exit /b 1
    )
)

REM Baixa dependencias se necessario
if not exist "target\lib" (
    echo Baixando dependencias para target\lib...
    mvn dependency:copy-dependencies -DoutputDirectory=target/lib
    if errorlevel 1 (
        echo Erro ao baixar dependencias!
        pause
        exit /b 1
    )
)

echo Iniciando servidor...
echo Para parar o servidor, feche a janela da aplicacao.
echo.

java -cp "target/classes;target/lib/*" com.distribuidos.server.ServerMain

pause