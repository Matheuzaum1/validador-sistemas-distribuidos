@echo off
echo Iniciando servidor...
if exist "target\validador-sistemas-distribuidos-1.0.0-server.jar" (
    java -jar "target\validador-sistemas-distribuidos-1.0.0-server.jar"
) else if exist "target\validador-sistemas-distribuidos-1.0.0.jar" (
    java -cp "target\validador-sistemas-distribuidos-1.0.0.jar" com.distribuidos.server.ServerMain
) else (
    echo [ERRO] JAR nao encontrado! Execute build.bat primeiro.
    pause
)
