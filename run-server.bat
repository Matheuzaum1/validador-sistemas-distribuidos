@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "%~dp0"
mvn exec:java -Dexec.mainClass=com.distribuidos.server.ServerMain
pause