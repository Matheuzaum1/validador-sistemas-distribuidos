@echo off
cd /d "C:\Users\Matheus Henrique\Documents\validador-sistemas-distribuidos"
java -cp "target\classes;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.19.2\jackson-databind-2.19.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.19.2\jackson-core-2.19.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.19.2\jackson-annotations-2.19.2.jar;%USERPROFILE%\.m2\repository\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar;%USERPROFILE%\.m2\repository\org\xerial\sqlite-jdbc\3.43.0.0\sqlite-jdbc-3.43.0.0.jar" com.newpix.server.gui.ServerGUI
pause
