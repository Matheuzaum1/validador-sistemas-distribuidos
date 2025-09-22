package com.distribuidos.common;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class TokenManager {
    private static final Map<String, String> tokenToCpf = new ConcurrentHashMap<>();
    private static final Map<String, Long> tokenExpiry = new ConcurrentHashMap<>();
    private static final SecureRandom random = new SecureRandom();
    private static final long TOKEN_VALIDITY = 30 * 60 * 1000; // 30 minutos
    
    public static String generateToken(String cpf) {
        // Remove token antigo se existir
        removeTokenForCpf(cpf);
        
        // Gera novo token
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        
        // Armazena token
        tokenToCpf.put(token, cpf);
        tokenExpiry.put(token, System.currentTimeMillis() + TOKEN_VALIDITY);
        
        return token;
    }
    
    public static String getCpfFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        
        // Verifica se token existe e não expirou
        Long expiry = tokenExpiry.get(token);
        if (expiry == null || System.currentTimeMillis() > expiry) {
            removeToken(token);
            return null;
        }
        
        return tokenToCpf.get(token);
    }
    
    public static boolean isValidToken(String token) {
        return getCpfFromToken(token) != null;
    }
    
    public static void removeToken(String token) {
        if (token != null) {
            tokenToCpf.remove(token);
            tokenExpiry.remove(token);
        }
    }
    
    public static void removeTokenForCpf(String cpf) {
        String tokenToRemove = null;
        for (Map.Entry<String, String> entry : tokenToCpf.entrySet()) {
            if (cpf.equals(entry.getValue())) {
                tokenToRemove = entry.getKey();
                break;
            }
        }
        if (tokenToRemove != null) {
            removeToken(tokenToRemove);
        }
    }
    
    public static void cleanExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        tokenExpiry.entrySet().removeIf(entry -> {
            if (currentTime > entry.getValue()) {
                tokenToCpf.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }
    
    public static int getActiveTokenCount() {
        cleanExpiredTokens();
        return tokenToCpf.size();
    }
}