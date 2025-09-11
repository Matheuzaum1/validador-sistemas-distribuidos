@echo off
chcp 65001 > nul
echo ==========================================
echo   📦 Compilando JARs - Sistema NewPix
echo ==========================================
echo.

REM Limpar compilação anterior
echo 🧹 Limpando compilação anterior...
if exist "dist" rmdir /s /q "dist"
mvn clean -q

echo.
echo 🔨 Compilando e empacotando JARs...
mvn package -q

if %errorlevel% neq 0 (
    echo ❌ Falha na compilação!
    pause
    exit /b 1
)

echo.
echo ✅ ==========================================
echo ✅   Compilação concluída com sucesso!
echo ✅ ==========================================
echo.
echo 📁 Arquivos criados em: dist/
echo    📄 newpix-server.jar (Servidor completo)
echo    📄 newpix-client.jar (Cliente)
echo    📂 lib/ (Dependências)
echo.
echo 🚀 Para executar:
echo    📖 Servidor: java -jar dist/newpix-server.jar
echo    📖 Cliente:  java -jar dist/newpix-client.jar
echo.

if exist "dist" (
    echo 📋 Conteúdo da pasta dist/:
    dir /b "dist"
    echo.
    if exist "dist\lib" (
        echo 📋 Dependências (lib/):
        dir /b "dist\lib" | findstr /c:".jar" | more
    )
)

pause
