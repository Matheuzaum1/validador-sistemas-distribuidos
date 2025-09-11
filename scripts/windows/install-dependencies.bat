@echo off
chcp 65001 > nul
echo ==========================================
echo   🚀 Instalação de Dependências - Windows
echo ==========================================
echo.

REM Verificar se Java está instalado
echo 📋 Verificando Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java não encontrado! Instalando...
    echo.
    echo 🔽 Baixando e instalando OpenJDK 17...
    
    REM Baixar OpenJDK 17 via winget (Windows 10/11)
    winget install Microsoft.OpenJDK.17 --silent --accept-package-agreements --accept-source-agreements
    
    if %errorlevel% neq 0 (
        echo ⚠️  Winget falhou. Tentando chocolatey...
        choco install openjdk17 -y
        
        if %errorlevel% neq 0 (
            echo ❌ Falha na instalação automática do Java!
            echo 📝 Por favor, instale manualmente:
            echo    - OpenJDK 17: https://adoptium.net/
            pause
            exit /b 1
        )
    )
) else (
    echo ✅ Java encontrado!
    java -version
)
echo.

REM Verificar se Maven está instalado
echo 📋 Verificando Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven não encontrado! Instalando...
    echo.
    echo 🔽 Instalando Apache Maven...
    
    REM Instalar Maven via winget
    winget install Apache.Maven --silent --accept-package-agreements --accept-source-agreements
    
    if %errorlevel% neq 0 (
        echo ⚠️  Winget falhou. Tentando chocolatey...
        choco install maven -y
        
        if %errorlevel% neq 0 (
            echo ❌ Falha na instalação automática do Maven!
            echo 📝 Por favor, instale manualmente:
            echo    - Apache Maven: https://maven.apache.org/download.cgi
            pause
            exit /b 1
        )
    )
    
    REM Adicionar Maven ao PATH se necessário
    echo 🔧 Configurando PATH...
    refreshenv
    
) else (
    echo ✅ Maven encontrado!
    mvn -version
)
echo.

REM Verificar se Git está instalado
echo 📋 Verificando Git...
git --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Git não encontrado! Instalando...
    echo.
    echo 🔽 Instalando Git...
    
    winget install Git.Git --silent --accept-package-agreements --accept-source-agreements
    
    if %errorlevel% neq 0 (
        echo ❌ Falha na instalação do Git!
        echo 📝 Por favor, instale manualmente:
        echo    - Git: https://git-scm.com/download/win
        pause
        exit /b 1
    )
) else (
    echo ✅ Git encontrado!
    git --version
)
echo.

REM Compilar o projeto
echo 📦 Compilando projeto...
cd /d "%~dp0..\.."
mvn clean compile

if %errorlevel% neq 0 (
    echo ❌ Falha na compilação!
    pause
    exit /b 1
)

echo.
echo ✅ ==========================================
echo ✅   Instalação concluída com sucesso!
echo ✅ ==========================================
echo.
echo 📋 Próximos passos:
echo    1. Execute: scripts\windows\start-server.bat
echo    2. Execute: scripts\windows\start-client.bat
echo.
pause
