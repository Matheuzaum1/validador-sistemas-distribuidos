#!/bin/bash

# Configurar codificação UTF-8
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

echo "=========================================="
echo "  🚀 Instalação de Dependências - Linux"
echo "=========================================="
echo

# Detectar distribuição Linux
if [ -f /etc/os-release ]; then
    . /etc/os-release
    DISTRO=$ID
else
    echo "❌ Não foi possível detectar a distribuição Linux!"
    exit 1
fi

echo "📋 Distribuição detectada: $PRETTY_NAME"
echo

# Função para instalar Java no Ubuntu/Debian
install_java_ubuntu() {
    echo "🔽 Instalando OpenJDK 17..."
    sudo apt update
    sudo apt install -y openjdk-17-jdk openjdk-17-jre
    
    # Configurar JAVA_HOME
    echo "🔧 Configurando JAVA_HOME..."
    JAVA_HOME_PATH=$(readlink -f /usr/bin/java | sed "s:bin/java::")
    echo "export JAVA_HOME=$JAVA_HOME_PATH" >> ~/.bashrc
    export JAVA_HOME=$JAVA_HOME_PATH
}

# Função para instalar Java no CentOS/RHEL/Fedora
install_java_redhat() {
    echo "🔽 Instalando OpenJDK 17..."
    if command -v dnf &> /dev/null; then
        sudo dnf install -y java-17-openjdk java-17-openjdk-devel
    else
        sudo yum install -y java-17-openjdk java-17-openjdk-devel
    fi
    
    # Configurar JAVA_HOME
    echo "🔧 Configurando JAVA_HOME..."
    JAVA_HOME_PATH=$(dirname $(dirname $(readlink -f $(which java))))
    echo "export JAVA_HOME=$JAVA_HOME_PATH" >> ~/.bashrc
    export JAVA_HOME=$JAVA_HOME_PATH
}

# Função para instalar Maven no Ubuntu/Debian
install_maven_ubuntu() {
    echo "🔽 Instalando Apache Maven..."
    sudo apt install -y maven
}

# Função para instalar Maven no CentOS/RHEL/Fedora
install_maven_redhat() {
    echo "🔽 Instalando Apache Maven..."
    if command -v dnf &> /dev/null; then
        sudo dnf install -y maven
    else
        sudo yum install -y maven
    fi
}

# Verificar e instalar Java
echo "📋 Verificando Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado! Instalando..."
    case $DISTRO in
        ubuntu|debian|pop)
            install_java_ubuntu
            ;;
        centos|rhel|fedora|rocky|almalinux)
            install_java_redhat
            ;;
        arch|manjaro)
            echo "🔽 Instalando OpenJDK 17..."
            sudo pacman -S --noconfirm jdk17-openjdk
            ;;
        opensuse*|sles)
            echo "🔽 Instalando OpenJDK 17..."
            sudo zypper install -y java-17-openjdk java-17-openjdk-devel
            ;;
        *)
            echo "❌ Distribuição não suportada: $DISTRO"
            echo "📝 Por favor, instale Java 17 manualmente"
            exit 1
            ;;
    esac
else
    echo "✅ Java encontrado!"
    java -version
fi
echo

# Verificar e instalar Maven
echo "📋 Verificando Maven..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado! Instalando..."
    case $DISTRO in
        ubuntu|debian|pop)
            install_maven_ubuntu
            ;;
        centos|rhel|fedora|rocky|almalinux)
            install_maven_redhat
            ;;
        arch|manjaro)
            echo "🔽 Instalando Apache Maven..."
            sudo pacman -S --noconfirm maven
            ;;
        opensuse*|sles)
            echo "🔽 Instalando Apache Maven..."
            sudo zypper install -y maven
            ;;
        *)
            echo "❌ Distribuição não suportada para Maven: $DISTRO"
            echo "📝 Instalando Maven manualmente..."
            
            # Download e instalação manual do Maven
            MAVEN_VERSION="3.9.5"
            cd /tmp
            wget https://downloads.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz
            sudo tar -xzf apache-maven-$MAVEN_VERSION-bin.tar.gz -C /opt/
            sudo ln -s /opt/apache-maven-$MAVEN_VERSION /opt/maven
            
            # Adicionar ao PATH
            echo "export PATH=/opt/maven/bin:\$PATH" >> ~/.bashrc
            export PATH=/opt/maven/bin:$PATH
            ;;
    esac
else
    echo "✅ Maven encontrado!"
    mvn -version
fi
echo

# Verificar e instalar Git
echo "📋 Verificando Git..."
if ! command -v git &> /dev/null; then
    echo "❌ Git não encontrado! Instalando..."
    case $DISTRO in
        ubuntu|debian|pop)
            sudo apt install -y git
            ;;
        centos|rhel|fedora|rocky|almalinux)
            if command -v dnf &> /dev/null; then
                sudo dnf install -y git
            else
                sudo yum install -y git
            fi
            ;;
        arch|manjaro)
            sudo pacman -S --noconfirm git
            ;;
        opensuse*|sles)
            sudo zypper install -y git
            ;;
        *)
            echo "❌ Distribuição não suportada para Git: $DISTRO"
            exit 1
            ;;
    esac
else
    echo "✅ Git encontrado!"
    git --version
fi
echo

# Verificar dependências adicionais para GUI
echo "📋 Verificando dependências para GUI..."
case $DISTRO in
    ubuntu|debian|pop)
        sudo apt install -y libxext6 libxrender1 libxtst6 libxi6
        ;;
    centos|rhel|fedora|rocky|almalinux)
        if command -v dnf &> /dev/null; then
            sudo dnf install -y libXext libXrender libXtst libXi
        else
            sudo yum install -y libXext libXrender libXtst libXi
        fi
        ;;
esac

# Compilar o projeto
echo "📦 Compilando projeto..."
cd "$(dirname "$0")/../.."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Falha na compilação!"
    exit 1
fi

echo
echo "✅ =========================================="
echo "✅   Instalação concluída com sucesso!"
echo "✅ =========================================="
echo
echo "📋 Próximos passos:"
echo "   1. Execute: ./scripts/linux/start-server.sh"
echo "   2. Execute: ./scripts/linux/start-client.sh"
echo
echo "🔄 Recarregue o terminal ou execute: source ~/.bashrc"
