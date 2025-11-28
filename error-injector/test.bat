@echo off
echo === Simple Error Injector Test ===
echo.

cd /d "c:\Users\matheusrm\Documents\validador-sistemas-distribuidos\simple-error-injector"

echo Verificando JAR...
if exist "target\simple-error-injector-1.0.0.jar" (
    echo ✅ JAR encontrado
) else (
    echo ❌ JAR não encontrado
    pause
    exit /b 1
)

echo.
echo Executando Simple Error Injector...
echo Pressione Ctrl+C para parar
echo.

java -jar "target\simple-error-injector-1.0.0.jar"

pause