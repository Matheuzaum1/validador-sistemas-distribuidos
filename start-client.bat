@echo off
echo Iniciando Cliente do Sistema Distribuido...
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

cd /d "%~dp0"

REM Compilar o projeto se necessário
if not exist "target\classes" (
    echo Compilando projeto...
    mvn compile
)

REM Executar o cliente
echo Iniciando cliente...
java -cp "target\classes;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.17.2\jackson-databind-2.17.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.17.2\jackson-annotations-2.17.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.17.2\jackson-core-2.17.2.jar;%USERPROFILE%\.m2\repository\org\xerial\sqlite-jdbc\3.46.1.0\sqlite-jdbc-3.46.1.0.jar;%USERPROFILE%\.m2\repository\org\slf4j\slf4j-api\2.0.16\slf4j-api-2.0.16.jar;%USERPROFILE%\.m2\repository\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar;%USERPROFILE%\.m2\repository\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar;%USERPROFILE%\.m2\repository\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar;%USERPROFILE%\.m2\repository\com\formdev\flatlaf\3.2.1\flatlaf-3.2.1.jar" com.distribuidos.client.ClientMain

pause
