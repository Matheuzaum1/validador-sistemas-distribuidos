# 🐧 Scripts Linux - Sistema NewPix

Scripts otimizados para Linux (Bash).

## 🚀 Início Rápido

```bash
# Executar menu interativo
./scripts/linux/menu.sh

# OU executar diretamente:
./scripts/linux/install-dependencies.sh
./scripts/linux/start-server.sh
./scripts/linux/start-client.sh
```

## 📋 Scripts Disponíveis

### 🎯 Menu Principal

- **`menu.sh`** - Menu interativo com todas as opções

### 📦 Instalação

- **`install-dependencies.sh`** - Instala Java 17, Maven e Git automaticamente

### 🖥️ Execução

- **`start-server.sh`** - Inicia o servidor NewPix
- **`start-client.sh`** - Inicia o cliente NewPix
- **`run-tests.sh`** - Executa todos os testes

### 🛠️ Utilitários

- **`kill-all-java.sh`** - Para todos os processos Java
- **`check-utf8-bom.sh`** - Verifica codificação UTF-8 com BOM

## 🔧 Pré-requisitos

### Distribuições Suportadas

- **Ubuntu/Debian** (apt)
- **CentOS/RHEL/Fedora** (yum/dnf)
- **Arch Linux/Manjaro** (pacman)
- **openSUSE/SLES** (zypper)

### Instalação Automática

O script `install-dependencies.sh` detecta sua distribuição e instala:

- **OpenJDK 17**
- **Apache Maven**
- **Git**
- **Dependências GUI** (libXext, libXrender, etc.)

## 🎮 Como Usar

### 1️⃣ Primeira vez

```bash
# Tornar scripts executáveis
chmod +x scripts/linux/*.sh

# Instalar dependências
./scripts/linux/install-dependencies.sh
```

### 2️⃣ Uso normal

```bash
# Iniciar servidor (terminal 1)
./scripts/linux/start-server.sh

# Iniciar cliente (terminal 2)  
./scripts/linux/start-client.sh
```

### 3️⃣ Desenvolvimento

```bash
# Executar testes
./scripts/linux/run-tests.sh

# Limpar processos
./scripts/linux/kill-all-java.sh
```

## ⚠️ Solução de Problemas

### Permissão negada

```bash
# Dar permissão de execução
chmod +x scripts/linux/*.sh
```

### Java não encontrado

```bash
# Verificar instalação
java -version
mvn -version

# Recarregar PATH
source ~/.bashrc

# Reinstalar
./scripts/linux/install-dependencies.sh
```

### GUI não aparece

```bash
# Verificar display
echo $DISPLAY

# Parar processos anteriores
./scripts/linux/kill-all-java.sh

# Instalar dependências GUI
sudo apt install libxext6 libxrender1 libxtst6 libxi6  # Ubuntu/Debian
```

### Erro de compilação

```bash
# Limpar e recompilar
mvn clean compile
```

## 🖥️ Suporte a GUI

### X11 Forwarding (SSH)

```bash
# Conectar com X11 forwarding
ssh -X usuario@servidor

# Verificar display
echo $DISPLAY
```

### Wayland

```bash
# Configurar XWayland
export DISPLAY=:0
```

### Headless (sem GUI)

Para execução sem interface gráfica, modifique os scripts removendo:

```bash
export JAVA_OPTS="-Djava.awt.headless=false"
```

## 🔍 Logs e Debug

- **Servidor**: Logs aparecem no terminal e na GUI
- **Cliente**: Logs aparecem no terminal e na GUI
- **Maven**: Use `-X` para debug verbose
- **Processo Java**: Use `jps` ou `ps aux | grep java`

## 💡 Dicas Linux

- 🐧 **Use `./` antes dos scripts**
- 🔐 **Configure permissões com `chmod +x`**
- 🖥️ **Sempre inicie o servidor antes do cliente**
- 🧹 **Use kill-all-java.sh entre execuções**
- 📱 **Verifique DISPLAY para GUI**
- 🔄 **Use Ctrl+C para parar processos**

## 📦 Dependências por Distribuição

### Ubuntu/Debian

```bash
sudo apt update
sudo apt install openjdk-17-jdk maven git
sudo apt install libxext6 libxrender1 libxtst6 libxi6
```

### CentOS/RHEL/Fedora

```bash
sudo dnf install java-17-openjdk maven git
sudo dnf install libXext libXrender libXtst libXi
```

### Arch Linux

```bash
sudo pacman -S jdk17-openjdk maven git
```
