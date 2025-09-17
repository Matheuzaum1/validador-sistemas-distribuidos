#!/bin/bash

# NewPix Banking System - Script de Automação Linux/macOS
# Equivalente ao newpix.ps1 para sistemas Unix

ACTION="${1:-menu}"
PORT="${2:-8080}"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
GRAY='\033[0;37m'
NC='\033[0m' # No Color

show_header() {
    clear
    echo -e "${CYAN}===============================================${NC}"
    echo -e "${CYAN}          NEWPIX BANKING SYSTEM               ${NC}"
    echo -e "${GREEN}         v2025.09.17 - Atualizado             ${NC}"
    echo -e "${CYAN}===============================================${NC}"
    echo ""
}

test_java() {
    if command -v java >/dev/null 2>&1; then
        echo -e "Java: ${GREEN}OK${NC}"
        return 0
    else
        echo -e "Java: ${RED}NAO ENCONTRADO${NC}"
        return 1
    fi
}

test_maven() {
    if command -v mvn >/dev/null 2>&1; then
        echo -e "Maven: ${GREEN}OK${NC}"
        return 0
    else
        echo -e "Maven: ${RED}NAO ENCONTRADO${NC}"
        return 1
    fi
}

test_classes() {
    local server_class="target/classes/com/newpix/server/gui/ServerGUI.class"
    local client_class="target/classes/com/newpix/client/gui/LoginWindow.class"
    
    local server_ok=false
    local client_ok=false
    
    if [ -f "$server_class" ]; then
        echo -e "Servidor GUI: ${GREEN}OK${NC}"
        server_ok=true
    else
        echo -e "Servidor GUI: ${RED}CLASSE NAO ENCONTRADA${NC}"
    fi
    
    if [ -f "$client_class" ]; then
        echo -e "Cliente GUI: ${GREEN}OK${NC}"
        client_ok=true
    else
        echo -e "Cliente GUI: ${RED}CLASSE NAO ENCONTRADA${NC}"
    fi
    
    if [ "$server_ok" = true ] && [ "$client_ok" = true ]; then
        return 0
    else
        return 1
    fi
}

get_status() {
    show_header
    echo -e "${YELLOW}STATUS DO SISTEMA:${NC}"
    echo -e "${YELLOW}==================${NC}"
    echo ""
    
    test_java
    test_maven
    
    if [ -d "target/classes" ]; then
        echo -e "Build: ${GREEN}OK${NC}"
        test_classes
    else
        echo -e "Build: ${YELLOW}NECESSARIO COMPILAR${NC}"
    fi
    
    local java_procs=$(pgrep -f "java.*newpix" | wc -l)
    if [ "$java_procs" -gt 0 ]; then
        echo -e "Processos Java: ${GREEN}$java_procs em execucao${NC}"
    else
        echo -e "Processos Java: ${GRAY}Nenhum em execucao${NC}"
    fi
    echo ""
}

get_classpath() {
    local current_dir=$(pwd)
    local classpath="$current_dir/target/classes"
    
    # Adicionar JARs de dependências se existirem
    if [ -d "target/dependency" ]; then
        for jar in target/dependency/*.jar; do
            if [ -f "$jar" ]; then
                classpath="$classpath:$jar"
            fi
        done
    fi
    
    echo "$classpath"
}

start_build() {
    echo -e "${YELLOW}Compilando projeto...${NC}"
    if mvn clean compile dependency:copy-dependencies; then
        echo -e "${GREEN}Compilacao concluida!${NC}"
    else
        echo -e "${RED}Erro na compilacao!${NC}"
    fi
}

start_clean() {
    echo -e "${YELLOW}Limpando projeto...${NC}"
    if mvn clean; then
        echo -e "${GREEN}Projeto limpo com sucesso!${NC}"
        echo -e "${CYAN}Execute 'build' para recompilar.${NC}"
    else
        echo -e "${RED}Erro na limpeza!${NC}"
    fi
}

start_server_gui() {
    echo -e "${YELLOW}Iniciando GUI do Servidor...${NC}"
    
    local server_class="target/classes/com/newpix/server/gui/ServerGUI.class"
    if [ ! -f "$server_class" ]; then
        echo -e "${RED}Erro: Classe do servidor nao encontrada!${NC}"
        echo -e "${YELLOW}Execute 'build' primeiro.${NC}"
        return
    fi
    
    local classpath=$(get_classpath)
    nohup java -cp "$classpath" com.newpix.server.gui.ServerGUI >/dev/null 2>&1 &
    echo -e "${GREEN}Servidor GUI iniciado!${NC}"
}

start_client_gui() {
    echo -e "${YELLOW}Iniciando GUI do Cliente...${NC}"
    
    local client_class="target/classes/com/newpix/client/gui/LoginWindow.class"
    if [ ! -f "$client_class" ]; then
        echo -e "${RED}Erro: Classe do cliente nao encontrada!${NC}"
        echo -e "${YELLOW}Execute 'build' primeiro.${NC}"
        return
    fi
    
    local classpath=$(get_classpath)
    nohup java -cp "$classpath" com.newpix.client.gui.LoginWindow >/dev/null 2>&1 &
    echo -e "${GREEN}Cliente GUI iniciado!${NC}"
}

start_both_gui() {
    show_header
    echo -e "${CYAN}Iniciando sistema completo...${NC}"
    echo ""
    
    if [ ! -d "target/classes" ]; then
        echo -e "${YELLOW}Projeto nao compilado. Compilando...${NC}"
        start_build
        echo ""
    fi
    
    # Verificar se as classes existem
    if ! test_classes >/dev/null 2>&1; then
        echo -e "${RED}Erro: Classes necessarias nao encontradas!${NC}"
        echo -e "${YELLOW}Execute 'build' para compilar o projeto.${NC}"
        return
    fi
    
    echo -e "${YELLOW}1/2 - Iniciando GUI do Servidor...${NC}"
    start_server_gui
    sleep 2
    
    echo -e "${YELLOW}2/2 - Iniciando GUI do Cliente...${NC}"
    start_client_gui
    
    echo ""
    echo -e "${GREEN}Sistema iniciado com sucesso!${NC}"
    echo -e "${CYAN}Para parar: ./newpix.sh stop${NC}"
}

stop_services() {
    echo -e "${YELLOW}Parando servicos Java...${NC}"
    local java_pids=$(pgrep -f "java.*newpix")
    if [ -n "$java_pids" ]; then
        echo "$java_pids" | xargs kill -TERM
        sleep 2
        # Force kill se ainda estiverem rodando
        local remaining_pids=$(pgrep -f "java.*newpix")
        if [ -n "$remaining_pids" ]; then
            echo "$remaining_pids" | xargs kill -9
        fi
        echo -e "${GREEN}Servicos parados!${NC}"
    else
        echo -e "${GRAY}Nenhum servico em execucao.${NC}"
    fi
}

show_menu() {
    while true; do
        show_header
        get_status
        
        echo -e "${YELLOW}MENU PRINCIPAL:${NC}"
        echo -e "${YELLOW}===============${NC}"
        echo ""
        echo "1. Sistema Completo (Recomendado)"
        echo "2. Servidor GUI"
        echo "3. Cliente GUI"
        echo "4. Status"
        echo "5. Compilar"
        echo "6. Limpar Projeto"
        echo "7. Parar Servicos"
        echo "8. Testar Sistema"
        echo "9. Ver Mudancas (Changelog)"
        echo "0. Sair"
        echo ""
        
        read -p "Digite sua escolha (0-9): " choice
        
        case $choice in
            1) start_both_gui; break ;;
            2) start_server_gui; break ;;
            3) start_client_gui; break ;;
            4) get_status; read -p "Pressione ENTER para continuar..." ;;
            5) start_build; read -p "Pressione ENTER para continuar..." ;;
            6) start_clean; read -p "Pressione ENTER para continuar..." ;;
            7) stop_services; read -p "Pressione ENTER para continuar..." ;;
            8) test_system; read -p "Pressione ENTER para continuar..." ;;
            9) show_changelog; read -p "Pressione ENTER para continuar..." ;;
            0) echo -e "${GREEN}Ate logo!${NC}"; exit 0 ;;
            *) echo -e "${RED}Opcao invalida! Digite um numero de 0-9.${NC}"; sleep 1 ;;
        esac
    done
}

test_system() {
    show_header
    echo -e "${YELLOW}TESTE DO SISTEMA:${NC}"
    echo -e "${YELLOW}=================${NC}"
    echo ""
    
    echo -e "${CYAN}Verificando pre-requisitos...${NC}"
    
    local java_ok=false
    local maven_ok=false
    
    if test_java; then
        java_ok=true
    fi
    
    if test_maven; then
        maven_ok=true
    fi
    
    if [ "$java_ok" = false ] || [ "$maven_ok" = false ]; then
        echo ""
        echo -e "${RED}ERRO: Pre-requisitos nao atendidos!${NC}"
        return
    fi
    
    echo ""
    echo -e "${CYAN}Verificando compilacao...${NC}"
    
    if [ ! -d "target/classes" ]; then
        echo -e "${YELLOW}Projeto nao compilado. Compilando agora...${NC}"
        start_build
    fi
    
    echo ""
    echo -e "${CYAN}Verificando classes principais...${NC}"
    
    local classes_ok=false
    if test_classes; then
        classes_ok=true
    fi
    
    echo ""
    if [ "$classes_ok" = true ]; then
        echo -e "${GREEN}RESULTADO: Sistema pronto para execucao!${NC}"
        echo ""
        echo -e "${YELLOW}Proximos passos:${NC}"
        echo "1. Execute 'Sistema Completo' para iniciar servidor e cliente"
        echo "2. Ou execute servidor e cliente separadamente"
    else
        echo -e "${RED}RESULTADO: Sistema com problemas!${NC}"
        echo ""
        echo -e "${YELLOW}Solucoes:${NC}"
        echo "1. Execute 'Limpar Projeto' e depois 'Compilar'"
        echo "2. Verifique se todos os arquivos fonte estao presentes"
    fi
}

show_help() {
    show_header
    echo -e "${CYAN}AJUDA - NewPix Banking System${NC}"
    echo ""
    echo -e "${YELLOW}COMANDOS DISPONIVEIS:${NC}"
    echo "  ./newpix.sh menu       - Menu interativo"
    echo "  ./newpix.sh both-gui   - Sistema completo"
    echo "  ./newpix.sh server-gui - Servidor GUI"
    echo "  ./newpix.sh client-gui - Cliente GUI"
    echo "  ./newpix.sh status     - Status do sistema"
    echo "  ./newpix.sh build      - Compilar projeto"
    echo "  ./newpix.sh clean      - Limpar projeto"
    echo "  ./newpix.sh stop       - Parar servicos"
    echo "  ./newpix.sh test       - Testar sistema"
    echo "  ./newpix.sh changelog  - Ver mudancas recentes"
    echo "  ./newpix.sh help       - Esta ajuda"
    echo ""
    echo -e "${YELLOW}CLASSES PRINCIPAIS:${NC}"
    echo "  Servidor: com.newpix.server.gui.ServerGUI"
    echo "  Cliente:  com.newpix.client.gui.LoginWindow"
    echo ""
}

show_changelog() {
    show_header
    echo -e "${CYAN}CHANGELOG - Correcoes Implementadas${NC}"
    echo -e "${CYAN}====================================${NC}"
    echo ""
    echo -e "${GREEN}VERSAO ATUAL - 17/09/2025:${NC}"
    echo ""
    echo -e "  ${YELLOW}CORRECOES PRINCIPAIS:${NC}"
    echo -e "  - ${GREEN}Operacao 'depositar' implementada no servidor${NC}"
    echo -e "  - ${GREEN}Cliente corrigido para usar protocolo JSON correto${NC}"
    echo -e "  - ${GREEN}Parametro 'quantidade' conforme documentacao${NC}"
    echo -e "  - ${GREEN}Loop infinito no carregamento de dados corrigido${NC}"
    echo -e "  - ${GREEN}Sistema de tentativas com limite (max 3)${NC}"
    echo -e "  - ${GREEN}Feedback visual melhorado${NC}"
    echo ""
    echo -e "  ${YELLOW}CONFORMIDADE:${NC}"
    echo -e "  - ${GREEN}Todas operacoes seguem docs/Requisitos.md${NC}"
    echo -e "  - ${GREEN}Protocolo JSON com nomenclatura minuscula + '_'${NC}"
    echo -e "  - ${GREEN}Operacoes: usuario_*, depositar, transacao_*${NC}"
    echo ""
    echo -e "  ${YELLOW}MELHORIAS UX:${NC}"
    echo -e "  - ${GREEN}Pagina de configuracoes estabilizada${NC}"
    echo -e "  - ${GREEN}Controle robusto de erros de conexao${NC}"
    echo -e "  - ${GREEN}Interface mais responsiva${NC}"
    echo ""
    echo -e "  ${YELLOW}TECNOLOGIAS:${NC}"
    echo -e "  - ${WHITE}Java 17+ com Maven${NC}"
    echo -e "  - ${WHITE}SQLite database${NC}"
    echo -e "  - ${WHITE}Comunicacao via Socket JSON${NC}"
    echo -e "  - ${WHITE}Interface Swing moderna${NC}"
    echo ""
}

# Tornar o script executável se não for
if [ ! -x "$0" ]; then
    chmod +x "$0"
fi

# Main script execution
case "${ACTION,,}" in  # ${ACTION,,} converte para lowercase
    menu)       show_menu ;;
    both-gui)   start_both_gui ;;
    server-gui) start_server_gui ;;
    client-gui) start_client_gui ;;
    status)     get_status ;;
    build)      start_build ;;
    clean)      start_clean ;;
    stop)       stop_services ;;
    test)       test_system ;;
    changelog)  show_changelog ;;
    help)       show_help ;;
    *)          show_menu ;;
esac