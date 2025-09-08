@echo off
echo ======================================
echo        NewPix Cliente - Iniciando
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
echo Iniciando cliente...
java -cp "target/classes;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.19.2\jackson-databind-2.19.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.19.2\jackson-core-2.19.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.19.2\jackson-annotations-2.19.2.jar;%USERPROFILE%\.m2\repository\org\xerial\sqlite-jdbc\3.43.0.0\sqlite-jdbc-3.43.0.0.jar;%USERPROFILE%\.m2\repository\at\favre\lib\bcrypt\0.4\bcrypt-0.4.jar" com.newpix.client.gui.LoginGUI

pause
