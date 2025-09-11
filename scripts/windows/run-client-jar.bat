@echo off
chcp 65001 > nul
echo ==========================================
echo   💻 Executando Cliente via JAR
echo ==========================================
echo.

REM Verificar se o JAR existe
if not exist "dist\newpix-client.jar" (
    echo ❌ JAR do cliente não encontrado!
    echo 📝 Execute primeiro: scripts\windows\build-jars.bat
    pause
    exit /b 1
)

REM Verificar se as dependências existem
if not exist "dist\lib\" (
    echo ❌ Dependências não encontradas!
    echo 📝 Execute primeiro: scripts\windows\build-jars.bat
    pause
    exit /b 1
)

echo 🚀 Iniciando cliente NewPix via JAR...
echo 📱 Interface gráfica aparecerá automaticamente!
echo 🛑 Para parar: Feche a janela do cliente
echo.

REM Executar o JAR do cliente
java -Djava.awt.headless=false -jar "dist\newpix-client.jar"

echo.
echo 🛑 Cliente encerrado.
