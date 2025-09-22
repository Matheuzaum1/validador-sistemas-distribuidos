package com.distribuidos.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class MessageBuilder {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    // Métodos para construir mensagens do cliente
    public static String buildLoginMessage(String cpf, String senha) {
        Map<String, Object> message = new HashMap<>();
        message.put("operacao", "usuario_login");
        message.put("cpf", cpf);
        message.put("senha", senha);
        return toJson(message);
    }
    
    public static String buildLogoutMessage(String token) {
        Map<String, Object> message = new HashMap<>();
        message.put("operacao", "usuario_logout");
        message.put("token", token);
        return toJson(message);
    }
    
    public static String buildCreateUserMessage(String nome, String cpf, String senha) {
        Map<String, Object> message = new HashMap<>();
        message.put("operacao", "usuario_criar");
        message.put("nome", nome);
        message.put("cpf", cpf);
        message.put("senha", senha);
        return toJson(message);
    }
    
    public static String buildReadUserMessage(String token) {
        Map<String, Object> message = new HashMap<>();
        message.put("operacao", "usuario_ler");
        message.put("token", token);
        return toJson(message);
    }
    
    public static String buildUpdateUserMessage(String token, String nome, String senha) {
        Map<String, Object> message = new HashMap<>();
        message.put("operacao", "usuario_atualizar");
        message.put("token", token);
        
        Map<String, Object> usuario = new HashMap<>();
        if (nome != null && !nome.trim().isEmpty()) {
            usuario.put("nome", nome);
        }
        if (senha != null && !senha.trim().isEmpty()) {
            usuario.put("senha", senha);
        }
        message.put("usuario", usuario);
        
        return toJson(message);
    }
    
    public static String buildDeleteUserMessage(String token) {
        Map<String, Object> message = new HashMap<>();
        message.put("operacao", "usuario_deletar");
        message.put("token", token);
        return toJson(message);
    }
    
    // Métodos para construir respostas do servidor
    public static String buildSuccessResponse(String operacao, String info) {
        Map<String, Object> response = new HashMap<>();
        response.put("operacao", operacao);
        response.put("status", true);
        response.put("info", info);
        return toJson(response);
    }
    
    public static String buildSuccessResponse(String operacao, String info, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("operacao", operacao);
        response.put("status", true);
        response.put("info", info);
        
        if (data != null) {
            if (operacao.equals("usuario_login") && data instanceof String) {
                response.put("token", data);
            } else if (operacao.equals("usuario_ler") && data instanceof Usuario) {
                Usuario user = (Usuario) data;
                Map<String, Object> userData = new HashMap<>();
                userData.put("cpf", user.getCpf());
                userData.put("nome", user.getNome());
                userData.put("saldo", user.getSaldo());
                response.put("usuario", userData);
            }
        }
        
        return toJson(response);
    }
    
    public static String buildErrorResponse(String operacao, String info) {
        Map<String, Object> response = new HashMap<>();
        response.put("operacao", operacao);
        response.put("status", false);
        response.put("info", info);
        return toJson(response);
    }
    
    private static String toJson(Map<String, Object> map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter para JSON", e);
        }
    }
    
    // Método para extrair operação de uma mensagem JSON
    public static String extractOperation(String jsonMessage) {
        try {
            JsonNode node = mapper.readTree(jsonMessage);
            return node.get("operacao").asText();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair operação do JSON", e);
        }
    }
    
    // Método para extrair status de uma resposta JSON
    public static boolean extractStatus(String jsonMessage) {
        try {
            JsonNode node = mapper.readTree(jsonMessage);
            return node.get("status").asBoolean();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair status do JSON", e);
        }
    }
    
    // Método para extrair info de uma resposta JSON
    public static String extractInfo(String jsonMessage) {
        try {
            JsonNode node = mapper.readTree(jsonMessage);
            return node.get("info").asText();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair info do JSON", e);
        }
    }
}