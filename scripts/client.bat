@echo off
echo Iniciando cliente...
if exist "target\validador-sistemas-distribuidos-1.0.0.jar" (
    java -cp "target\validador-sistemas-distribuidos-1.0.0.jar" com.distribuidos.client.ClientMain
) else (
    echo [ERRO] JAR nao encontrado! Execute build.bat primeiro.
    pause
)
