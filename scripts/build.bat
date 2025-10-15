@echo off
echo Compilando projeto...
call mvn clean compile package
if %errorlevel% equ 0 (
    echo [OK] Compilacao concluida com sucesso!
) else (
    echo [ERRO] Erro na compilacao!
)
pause
