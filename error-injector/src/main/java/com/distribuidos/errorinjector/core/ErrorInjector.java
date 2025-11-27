package com.distribuidos.errorinjector.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsável por gerar mensagens com erros injetados
 */
public class ErrorInjector {
    private static final Logger logger = LoggerFactory.getLogger(ErrorInjector.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Injeta um erro específico em uma mensagem JSON
     * 
     * @param originalMessage Mensagem original
     * @param errorType Tipo de erro a injetar
     * @return Mensagem com erro injetado
     */
    public static String injectError(String originalMessage, ErrorType errorType) {
        try {
            logger.info("Injetando erro {} na mensagem: {}", errorType.getDisplayName(), originalMessage);
            
            switch (errorType) {
                // ========== ERROS DE ESTRUTURA JSON ==========
                case MISSING_OPERACAO:
                    return removeField(originalMessage, "operacao");
                    
                case NULL_OPERACAO:
                    return setFieldToNull(originalMessage, "operacao");
                    
                case INVALID_OPERACAO:
                    return setFieldValue(originalMessage, "operacao", "operacao_inexistente");
                    
                case MISSING_STATUS:
                    return removeField(originalMessage, "status");
                    
                case NULL_STATUS:
                    return setFieldToNull(originalMessage, "status");
                    
                case MISSING_INFO:
                    return removeField(originalMessage, "info");
                    
                case NULL_INFO:
                    return setFieldToNull(originalMessage, "info");
                
                // ========== ERROS ESPECÍFICOS DE LOGIN ==========
                case LOGIN_MISSING_TOKEN:
                    return removeField(originalMessage, "token");
                    
                case LOGIN_NULL_TOKEN:
                    return setFieldToNull(originalMessage, "token");
                    
                case LOGIN_EMPTY_TOKEN:
                    return setFieldValue(originalMessage, "token", "");
                    
                case LOGIN_MISSING_CPF:
                    return removeField(originalMessage, "cpf");
                    
                case LOGIN_NULL_CPF:
                    return setFieldToNull(originalMessage, "cpf");
                    
                case LOGIN_INVALID_CPF:
                    return setFieldValue(originalMessage, "cpf", "cpf-invalido");
                    
                case LOGIN_MISSING_SENHA:
                    return removeField(originalMessage, "senha");
                    
                case LOGIN_NULL_SENHA:
                    return setFieldToNull(originalMessage, "senha");
                
                // ========== ERROS ESPECÍFICOS DE USUÁRIO ==========
                case USER_READ_MISSING_USUARIO:
                    return removeField(originalMessage, "usuario");
                    
                case USER_READ_NULL_USUARIO:
                    return setFieldToNull(originalMessage, "usuario");
                    
                case USER_MISSING_TOKEN:
                    return removeField(originalMessage, "token");
                    
                case USER_NULL_TOKEN:
                    return setFieldToNull(originalMessage, "token");
                    
                case USER_INVALID_TOKEN:
                    return setFieldValue(originalMessage, "token", "token-invalido-formato");
                    
                case USER_CREATE_MISSING_NOME:
                    return removeField(originalMessage, "nome");
                    
                case USER_CREATE_MISSING_CPF:
                    return removeField(originalMessage, "cpf");
                    
                case USER_CREATE_MISSING_SENHA:
                    return removeField(originalMessage, "senha");
                
                // ========== ERROS ESPECÍFICOS DE TRANSAÇÃO ==========
                case TRANSACTION_READ_MISSING_TRANSACOES:
                    return removeField(originalMessage, "transacoes");
                    
                case TRANSACTION_READ_NULL_TRANSACOES:
                    return setFieldToNull(originalMessage, "transacoes");
                    
                case TRANSACTION_MISSING_CPF_DESTINO:
                    return removeField(originalMessage, "cpf_destino");
                    
                case TRANSACTION_NULL_CPF_DESTINO:
                    return setFieldToNull(originalMessage, "cpf_destino");
                    
                case TRANSACTION_MISSING_VALOR:
                    return removeField(originalMessage, "valor");
                    
                case TRANSACTION_NULL_VALOR:
                    return setFieldToNull(originalMessage, "valor");
                    
                case TRANSACTION_NEGATIVE_VALOR:
                    return setFieldValue(originalMessage, "valor", -100.50);
                    
                case TRANSACTION_MISSING_DATA_INICIAL:
                    return removeField(originalMessage, "data_inicial");
                    
                case TRANSACTION_MISSING_DATA_FINAL:
                    return removeField(originalMessage, "data_final");
                    
                case TRANSACTION_INVALID_DATE_FORMAT:
                    return setFieldValue(originalMessage, "data_inicial", "data-invalida");
                
                // ========== ERROS DE PROTOCOLO ==========
                case STATUS_FALSE_LOGIN:
                case STATUS_FALSE_USER_CREATE:
                case STATUS_FALSE_USER_READ:
                case STATUS_FALSE_TRANSACTION:
                    return setFieldValue(originalMessage, "status", false);
                
                // ========== ERROS DE CONEXÃO ==========
                case WRONG_FIRST_OPERATION:
                    return setFieldValue(originalMessage, "operacao", "usuario_login");
                    
                case MALFORMED_JSON:
                    return "{ \"operacao\": \"conectar\", \"malformed\": }";
                    
                case EMPTY_MESSAGE:
                    return "";
                    
                case INVALID_ENCODING:
                    return originalMessage.replace("\"", "\\\"");
                
                // ========== ERROS EXTRAS/ADICIONAIS ==========
                case EXTRA_FIELDS:
                    return addExtraField(originalMessage, "campo_extra", "valor_nao_esperado");
                    
                case MISSING_REQUIRED_FIELDS:
                    // Remove múltiplos campos importantes
                    String temp = removeField(originalMessage, "operacao");
                    return removeField(temp, "status");
                    
                case WRONG_FIELD_TYPES:
                    return setFieldValue(originalMessage, "status", "true"); // string instead of boolean
                    
                case OVERSIZED_VALUES:
                    return setFieldValue(originalMessage, "info", "x".repeat(10000)); // string muito grande
                
                default:
                    logger.warn("Tipo de erro não implementado: {}", errorType);
                    return originalMessage;
            }
            
        } catch (Exception e) {
            logger.error("Erro ao injetar erro {} na mensagem {}", errorType, originalMessage, e);
            return originalMessage;
        }
    }
    
    /**
     * Remove um campo do JSON
     */
    private static String removeField(String json, String fieldName) {
        try {
            ObjectNode node = (ObjectNode) mapper.readTree(json);
            node.remove(fieldName);
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            logger.error("Erro ao remover campo {}", fieldName, e);
            return json;
        }
    }
    
    /**
     * Define um campo como null
     */
    private static String setFieldToNull(String json, String fieldName) {
        try {
            ObjectNode node = (ObjectNode) mapper.readTree(json);
            node.putNull(fieldName);
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            logger.error("Erro ao definir campo {} como null", fieldName, e);
            return json;
        }
    }
    
    /**
     * Define o valor de um campo
     */
    private static String setFieldValue(String json, String fieldName, Object value) {
        try {
            ObjectNode node = (ObjectNode) mapper.readTree(json);
            if (value instanceof String) {
                node.put(fieldName, (String) value);
            } else if (value instanceof Boolean) {
                node.put(fieldName, (Boolean) value);
            } else if (value instanceof Double) {
                node.put(fieldName, (Double) value);
            } else if (value instanceof Integer) {
                node.put(fieldName, (Integer) value);
            } else {
                node.put(fieldName, value.toString());
            }
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            logger.error("Erro ao definir campo {} para {}", fieldName, value, e);
            return json;
        }
    }
    
    /**
     * Adiciona um campo extra ao JSON
     */
    private static String addExtraField(String json, String fieldName, Object value) {
        try {
            ObjectNode node = (ObjectNode) mapper.readTree(json);
            if (value instanceof String) {
                node.put(fieldName, (String) value);
            } else {
                node.put(fieldName, value.toString());
            }
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            logger.error("Erro ao adicionar campo extra {}", fieldName, e);
            return json;
        }
    }
    
    /**
     * Gera exemplos de mensagens válidas para cada operação
     */
    public static Map<String, String> getValidMessageExamples() {
        Map<String, String> examples = new HashMap<>();
        
        examples.put("conectar", "{\"operacao\": \"conectar\"}");
        
        examples.put("usuario_login", 
            "{\"operacao\": \"usuario_login\", \"cpf\": \"123.456.789-01\", \"senha\": \"123456\"}");
        
        examples.put("usuario_logout", 
            "{\"operacao\": \"usuario_logout\", \"token\": \"abc123\"}");
        
        examples.put("usuario_criar", 
            "{\"operacao\": \"usuario_criar\", \"nome\": \"João Silva\", \"cpf\": \"123.456.789-01\", \"senha\": \"123456\"}");
        
        examples.put("usuario_ler", 
            "{\"operacao\": \"usuario_ler\", \"token\": \"abc123\"}");
        
        examples.put("usuario_atualizar", 
            "{\"operacao\": \"usuario_atualizar\", \"token\": \"abc123\", \"usuario\": {\"nome\": \"João Silva Atualizado\"}}");
        
        examples.put("usuario_deletar", 
            "{\"operacao\": \"usuario_deletar\", \"token\": \"abc123\"}");
        
        examples.put("transacao_criar", 
            "{\"operacao\": \"transacao_criar\", \"token\": \"abc123\", \"cpf_destino\": \"098.765.432-10\", \"valor\": 150.75}");
        
        examples.put("depositar", 
            "{\"operacao\": \"depositar\", \"token\": \"abc123\", \"valor_enviado\": 100.00}");
        
        examples.put("transacao_ler", 
            "{\"operacao\": \"transacao_ler\", \"token\": \"abc123\", \"data_inicial\": \"2025-01-01T00:00:00Z\", \"data_final\": \"2025-01-31T23:59:59Z\"}");
        
        // Respostas do servidor
        examples.put("resposta_login_sucesso", 
            "{\"operacao\": \"usuario_login\", \"status\": true, \"info\": \"Login realizado com sucesso\", \"token\": \"abc123\"}");
        
        examples.put("resposta_login_erro", 
            "{\"operacao\": \"usuario_login\", \"status\": false, \"info\": \"CPF ou senha inválidos\"}");
        
        examples.put("resposta_usuario_ler", 
            "{\"operacao\": \"usuario_ler\", \"status\": true, \"info\": \"Dados obtidos\", \"usuario\": {\"cpf\": \"123.456.789-01\", \"nome\": \"João Silva\", \"saldo\": 1000.00}}");
        
        examples.put("resposta_transacao_ler", 
            "{\"operacao\": \"transacao_ler\", \"status\": true, \"info\": \"Transações obtidas\", \"transacoes\": []}");
        
        return examples;
    }
}