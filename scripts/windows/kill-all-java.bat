@echo off
echo ======================================
echo     Parando todos os processos Java
echo ======================================
echo.

echo Parando processos Java...
taskkill /F /IM java.exe /T >nul 2>&1

echo Aguardando limpeza...
timeout /t 3 /nobreak >nul

echo.
echo Verificando porta 8080...
netstat -an | find "8080" >nul
if %ERRORLEVEL% EQU 0 (
    echo Ainda ha conexoes na porta 8080
    netstat -an | find "8080"
) else (
    echo Porta 8080 esta livre!
)

echo.
echo Processos Java parados com sucesso!
echo Agora voce pode executar os scripts do projeto.
echo.

pause
