@echo off
echo Iniciando Cliente do Sistema Distribuido...
echo.

REM Define JAVA_HOME para Java 21+
set JAVA_HOME=C:\Program Files\Java\jdk-25

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

echo Iniciando cliente...
echo Para parar o cliente, feche a janela da aplicacao.
echo.

java -cp "target/classes;target/lib/*" com.distribuidos.client.ClientMain

pause