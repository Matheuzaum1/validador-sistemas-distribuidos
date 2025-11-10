package com.distribuidos.exception;

/**
 * Enum que centraliza todos os códigos de erro da aplicação.
 * Facilita tratamento consistente e rastreamento de problemas.
 */
public enum ErrorCode {
    // Erros de Validação (ERR_1xx)
    INVALID_CPF("ERR_101", "CPF inválido"),
    INVALID_PASSWORD("ERR_102", "Senha inválida"),
    INVALID_FORMAT("ERR_103", "Formato inválido"),
    VALIDATION_FAILED("ERR_104", "Validação falhou"),
    
    // Erros de Autenticação (ERR_2xx)
    USER_NOT_FOUND("ERR_201", "Usuário não encontrado"),
    INVALID_CREDENTIALS("ERR_202", "Credenciais inválidas"),
    USER_ALREADY_EXISTS("ERR_203", "Usuário já existe"),
    USER_BLOCKED("ERR_204", "Usuário bloqueado"),
    
    // Erros de Autorização (ERR_3xx)
    ACCESS_DENIED("ERR_301", "Acesso negado"),
    INSUFFICIENT_PERMISSIONS("ERR_302", "Permissões insuficientes"),
    
    // Erros de Transação (ERR_4xx)
    INSUFFICIENT_BALANCE("ERR_401", "Saldo insuficiente"),
    INVALID_TRANSACTION("ERR_402", "Transação inválida"),
    TRANSACTION_FAILED("ERR_403", "Falha na transação"),
    DUPLICATE_TRANSACTION("ERR_404", "Transação duplicada"),
    
    // Erros de Banco de Dados (ERR_5xx)
    DATABASE_ERROR("ERR_501", "Erro de banco de dados"),
    CONNECTION_FAILED("ERR_502", "Falha ao conectar"),
    QUERY_ERROR("ERR_503", "Erro na consulta"),
    TRANSACTION_ERROR("ERR_504", "Erro na transação do BD"),
    
    // Erros de Conexão/Rede (ERR_6xx)
    CONNECTION_ERROR("ERR_601", "Erro de conexão"),
    TIMEOUT("ERR_602", "Timeout na conexão"),
    SERVER_UNAVAILABLE("ERR_603", "Servidor indisponível"),
    
    // Erros Internos (ERR_7xx)
    INTERNAL_ERROR("ERR_701", "Erro interno do sistema"),
    CONFIGURATION_ERROR("ERR_702", "Erro de configuração"),
    UNEXPECTED_ERROR("ERR_703", "Erro inesperado");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
