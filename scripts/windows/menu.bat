@echo off
chcp 65001 > nul
echo ==========================================
echo   🚀 Sistema NewPix - Windows
echo ==========================================
echo.
echo 📋 Escolha uma opção:
echo.
echo 1. 📦 Instalar dependências
echo 2. 🖥️  Iniciar servidor (Maven)
echo 3. 💻 Iniciar cliente (Maven)
echo 4. 🔨 Compilar JARs
echo 5. 🖥️  Executar servidor (JAR)
echo 6. 💻 Executar cliente (JAR)
echo 7. 🧪 Executar testes
echo 8. 🧹 Limpar processos Java
echo 9. 🔍 Verificar codificação UTF-8
echo 0. 🚪 Sair
echo.
set /p choice="Digite sua escolha (0-9): "

if "%choice%"=="1" goto install
if "%choice%"=="2" goto server
if "%choice%"=="3" goto client
if "%choice%"=="4" goto build_jars
if "%choice%"=="5" goto server_jar
if "%choice%"=="6" goto client_jar
if "%choice%"=="7" goto tests
if "%choice%"=="8" goto clean
if "%choice%"=="9" goto check
if "%choice%"=="0" goto exit
goto invalid

:install
echo.
call "%~dp0install-dependencies.bat"
goto menu

:server
echo.
call "%~dp0start-server.bat"
goto menu

:client
echo.
call "%~dp0start-client.bat"
goto menu

:build_jars
echo.
call "%~dp0build-jars.bat"
goto menu

:server_jar
echo.
call "%~dp0run-server-jar.bat"
goto menu

:client_jar
echo.
call "%~dp0run-client-jar.bat"
goto menu

:tests
echo.
call "%~dp0run-tests.bat"
goto menu

:clean
echo.
call "%~dp0kill-all-java.bat"
goto menu

:check
echo.
call "%~dp0check-utf8-bom.ps1"
goto menu

:invalid
echo ❌ Opção inválida! Tente novamente.
goto menu

:menu
echo.
echo Pressione qualquer tecla para voltar ao menu...
pause > nul
cls
goto start

:exit
echo 👋 Encerrando...
exit

:start
cls
goto menu
