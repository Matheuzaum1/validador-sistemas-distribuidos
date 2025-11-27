package com.distribuidos.errorinjector.core;

/**
 * Enumeração de todos os tipos de erro que podem ser injetados no sistema
 */
public enum ErrorType {
    // ========== ERROS DE ESTRUTURA JSON ==========
    MISSING_OPERACAO("Operação ausente", "JSON sem campo 'operacao'", true, true),
    NULL_OPERACAO("Operação nula", "Campo 'operacao' é null", true, true),
    INVALID_OPERACAO("Operação inválida", "Operação não existe no protocolo", true, false),
    MISSING_STATUS("Status ausente", "JSON sem campo 'status' (servidor)", false, true),
    NULL_STATUS("Status nulo", "Campo 'status' é null (servidor)", false, true),
    MISSING_INFO("Info ausente", "JSON sem campo 'info' (servidor)", false, true),
    NULL_INFO("Info nulo", "Campo 'info' é null (servidor)", false, true),
    
    // ========== ERROS ESPECÍFICOS DE LOGIN ==========
    LOGIN_MISSING_TOKEN("Login sem token", "Login bem-sucedido mas sem token", false, true),
    LOGIN_NULL_TOKEN("Login token nulo", "Login bem-sucedido mas token é null", false, true),
    LOGIN_EMPTY_TOKEN("Login token vazio", "Login bem-sucedido mas token é string vazia", false, true),
    LOGIN_MISSING_CPF("Login sem CPF", "Mensagem de login sem campo 'cpf'", true, false),
    LOGIN_NULL_CPF("Login CPF nulo", "Campo 'cpf' é null", true, false),
    LOGIN_INVALID_CPF("Login CPF inválido", "CPF não está no formato correto", true, false),
    LOGIN_MISSING_SENHA("Login sem senha", "Mensagem de login sem campo 'senha'", true, false),
    LOGIN_NULL_SENHA("Login senha nula", "Campo 'senha' é null", true, false),
    
    // ========== ERROS ESPECÍFICOS DE USUÁRIO ==========
    USER_READ_MISSING_USUARIO("Usuario_ler sem usuario", "Leitura bem-sucedida mas sem campo 'usuario'", false, true),
    USER_READ_NULL_USUARIO("Usuario_ler usuario nulo", "Campo 'usuario' é null", false, true),
    USER_MISSING_TOKEN("Operação sem token", "Mensagem sem campo 'token'", true, false),
    USER_NULL_TOKEN("Token nulo", "Campo 'token' é null", true, false),
    USER_INVALID_TOKEN("Token inválido", "Token não tem formato válido", true, false),
    USER_CREATE_MISSING_NOME("Criar usuário sem nome", "Criação sem campo 'nome'", true, false),
    USER_CREATE_MISSING_CPF("Criar usuário sem CPF", "Criação sem campo 'cpf'", true, false),
    USER_CREATE_MISSING_SENHA("Criar usuário sem senha", "Criação sem campo 'senha'", true, false),
    
    // ========== ERROS ESPECÍFICOS DE TRANSAÇÃO ==========
    TRANSACTION_READ_MISSING_TRANSACOES("Transacao_ler sem transacoes", "Leitura bem-sucedida mas sem 'transacoes'", false, true),
    TRANSACTION_READ_NULL_TRANSACOES("Transacao_ler transacoes nulo", "Campo 'transacoes' é null", false, true),
    TRANSACTION_MISSING_CPF_DESTINO("Transação sem CPF destino", "Transferência sem 'cpf_destino'", true, false),
    TRANSACTION_NULL_CPF_DESTINO("Transação CPF destino nulo", "Campo 'cpf_destino' é null", true, false),
    TRANSACTION_MISSING_VALOR("Transação sem valor", "Transferência sem campo 'valor'", true, false),
    TRANSACTION_NULL_VALOR("Transação valor nulo", "Campo 'valor' é null", true, false),
    TRANSACTION_NEGATIVE_VALOR("Transação valor negativo", "Campo 'valor' é negativo", true, false),
    TRANSACTION_MISSING_DATA_INICIAL("Leitura sem data inicial", "Sem campo 'data_inicial'", true, false),
    TRANSACTION_MISSING_DATA_FINAL("Leitura sem data final", "Sem campo 'data_final'", true, false),
    TRANSACTION_INVALID_DATE_FORMAT("Data inválida", "Formato de data incorreto", true, false),
    
    // ========== ERROS DE PROTOCOLO ==========
    STATUS_FALSE_LOGIN("Status false em login", "Servidor retorna login com status:false", false, true),
    STATUS_FALSE_USER_CREATE("Status false em criação", "Servidor retorna criação com status:false", false, true),
    STATUS_FALSE_USER_READ("Status false em leitura", "Servidor retorna leitura com status:false", false, true),
    STATUS_FALSE_TRANSACTION("Status false em transação", "Servidor retorna transação com status:false", false, true),
    
    // ========== ERROS DE CONEXÃO ==========
    WRONG_FIRST_OPERATION("Primeira operação inválida", "Primeira operação não é 'conectar'", true, false),
    MALFORMED_JSON("JSON malformado", "Estrutura JSON inválida", true, true),
    EMPTY_MESSAGE("Mensagem vazia", "Mensagem está vazia ou só espaços", true, true),
    INVALID_ENCODING("Encoding inválido", "Mensagem com encoding incorreto", true, true),
    
    // ========== ERROS EXTRAS/ADICIONAIS ==========
    EXTRA_FIELDS("Campos extras", "JSON contém campos não esperados", true, true),
    MISSING_REQUIRED_FIELDS("Campos obrigatórios ausentes", "Faltam campos obrigatórios", true, true),
    WRONG_FIELD_TYPES("Tipos de campo incorretos", "Campos têm tipos errados", true, true),
    OVERSIZED_VALUES("Valores muito grandes", "Valores excedem limites do protocolo", true, true);
    
    private final String displayName;
    private final String description;
    private final boolean canAffectClient;  // Se pode ser injetado em mensagens do cliente
    private final boolean canAffectServer;  // Se pode ser injetado em respostas do servidor
    
    ErrorType(String displayName, String description, boolean canAffectClient, boolean canAffectServer) {
        this.displayName = displayName;
        this.description = description;
        this.canAffectClient = canAffectClient;
        this.canAffectServer = canAffectServer;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean canAffectClient() {
        return canAffectClient;
    }
    
    public boolean canAffectServer() {
        return canAffectServer;
    }
    
    /**
     * Retorna categoria do erro para agrupamento na UI
     */
    public String getCategory() {
        String name = this.name();
        if (name.startsWith("MISSING_") || name.startsWith("NULL_") || name.contains("_MISSING_") || name.contains("_NULL_")) {
            return "Campos Ausentes/Nulos";
        } else if (name.contains("LOGIN")) {
            return "Erros de Login";
        } else if (name.contains("USER")) {
            return "Erros de Usuário";
        } else if (name.contains("TRANSACTION")) {
            return "Erros de Transação";
        } else if (name.contains("STATUS_FALSE")) {
            return "Status False";
        } else if (name.contains("JSON") || name.contains("ENCODING") || name.contains("EMPTY")) {
            return "Erros de Formato";
        } else {
            return "Outros";
        }
    }
    
    /**
     * Retorna todos os tipos de erro que afetam o cliente
     */
    public static ErrorType[] getClientErrors() {
        return java.util.Arrays.stream(values())
                .filter(ErrorType::canAffectClient)
                .toArray(ErrorType[]::new);
    }
    
    /**
     * Retorna todos os tipos de erro que afetam o servidor
     */
    public static ErrorType[] getServerErrors() {
        return java.util.Arrays.stream(values())
                .filter(ErrorType::canAffectServer)
                .toArray(ErrorType[]::new);
    }
}