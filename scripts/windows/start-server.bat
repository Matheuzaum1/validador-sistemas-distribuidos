@echo off
echo ======================================
echo        NewPix Server - Iniciando
echo ======================================
echo.

cd /d "%~dp0"

echo Compilando projeto...
mvn clean compile -q

if %ERRORLEVEL% NEQ 0 (
    echo Erro na compilacao!
    pause
    exit /b 1
)

echo.
echo FORCANDO ABERTURA DO GUI DO SERVIDOR...
echo A interface grafica DEVE aparecer!
echo.
java -Djava.awt.headless=false -cp "target/classes;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.19.2\jackson-databind-2.19.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.19.2\jackson-core-2.19.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.19.2\jackson-annotations-2.19.2.jar;%USERPROFILE%\.m2\repository\org\xerial\sqlite-jdbc\3.43.0.0\sqlite-jdbc-3.43.0.0.jar;%USERPROFILE%\.m2\repository\at\favre\lib\bcrypt\0.4\bcrypt-0.4.jar" com.newpix.server.gui.ServerGUI

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERRO: GUI do servidor nao pode ser aberto!
    echo Verifique se ha algum processo Java ainda rodando.
    echo.
)

pause
