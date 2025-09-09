@echo off
echo ======================================
echo      TESTE SIMPLES - Sistema NewPix
echo ======================================
echo.

cd /d "%~dp0"

echo 1. Compilando projeto...
mvn clean compile -q

if %ERRORLEVEL% NEQ 0 (
    echo Erro na compilacao!
    pause
    exit /b 1
)

echo.
echo 2. Projeto compilado com sucesso!
echo.
echo INSTRUCOES DE TESTE:
echo.
echo PASSO 1 - Abrir servidor:
echo   Execute: java -cp "target/classes;%USERPROFILE%\.m2\repository\*" com.newpix.server.NewPixServer 8080
echo.  
echo PASSO 2 - Abrir cliente (em outro terminal):
echo   Execute: java -cp "target/classes;%USERPROFILE%\.m2\repository\*" com.newpix.client.gui.LoginGUI
echo.
echo PASSO 3 - Testar sistema:
echo   1. Cadastrar usuario: CPF=12345678901, Nome=Teste, Senha=123456
echo   2. Fazer login com os dados cadastrados  
echo   3. Enviar PIX para outro usuario
echo   4. Verificar extrato
echo.
echo Pressione qualquer tecla para continuar...
pause > nul
