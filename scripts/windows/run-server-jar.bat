@echo off
chcp 65001 > nul
echo ==========================================
echo   🖥️ Executando Servidor via JAR
echo ==========================================
echo.

REM Verificar se o JAR existe
if not exist "dist\newpix-server.jar" (
    echo ❌ JAR do servidor não encontrado!
    echo 📝 Execute primeiro: scripts\windows\build-jars.bat
    pause
    exit /b 1
)

echo 🚀 Iniciando servidor NewPix via JAR...
echo 📱 Interface gráfica aparecerá automaticamente!
echo 🛑 Para parar: Ctrl+C ou feche a janela do servidor
echo.

REM Executar o JAR do servidor
java -Djava.awt.headless=false -jar "dist\newpix-server.jar"

echo.
echo 🛑 Servidor encerrado.
