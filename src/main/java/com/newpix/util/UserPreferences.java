package com.newpix.util;

import java.util.prefs.Preferences;

/**
 * Gerenciador de configurações e preferências do usuário.
 * Salva configurações como remember me, último CPF, host/porta do servidor.
 */
public class UserPreferences {
    
    private static final String PREFS_NODE = "com.newpix.client";
    private static final String LAST_CPF_KEY = "lastCpf";
    private static final String REMEMBER_ME_KEY = "rememberMe";
    private static final String SERVER_HOST_KEY = "serverHost";
    private static final String SERVER_PORT_KEY = "serverPort";
    private static final String WINDOW_WIDTH_KEY = "windowWidth";
    private static final String WINDOW_HEIGHT_KEY = "windowHeight";
    private static final String WINDOW_X_KEY = "windowX";
    private static final String WINDOW_Y_KEY = "windowY";
    
    private static Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
    
    /**
     * Salva o último CPF usado
     */
    public static void saveLastCpf(String cpf) {
        if (cpf != null && !cpf.trim().isEmpty()) {
            prefs.put(LAST_CPF_KEY, cpf);
            CLILogger.debug("CPF salvo nas preferências");
        }
    }
    
    /**
     * Recupera o último CPF usado
     */
    public static String getLastCpf() {
        return prefs.get(LAST_CPF_KEY, "");
    }
    
    /**
     * Define se deve lembrar do usuário
     */
    public static void setRememberMe(boolean remember) {
        prefs.putBoolean(REMEMBER_ME_KEY, remember);
        CLILogger.debug("Remember me definido como: " + remember);
    }
    
    /**
     * Verifica se deve lembrar do usuário
     */
    public static boolean getRememberMe() {
        return prefs.getBoolean(REMEMBER_ME_KEY, false);
    }
    
    /**
     * Salva configurações do servidor
     */
    public static void saveServerConfig(String host, int port) {
        prefs.put(SERVER_HOST_KEY, host);
        prefs.putInt(SERVER_PORT_KEY, port);
        CLILogger.info("Configuração do servidor salva: " + host + ":" + port);
    }
    
    /**
     * Recupera host do servidor
     */
    public static String getServerHost() {
        return prefs.get(SERVER_HOST_KEY, "localhost");
    }
    
    /**
     * Recupera porta do servidor
     */
    public static int getServerPort() {
        return prefs.getInt(SERVER_PORT_KEY, 8080);
    }
    
    /**
     * Salva posição e tamanho da janela
     */
    public static void saveWindowBounds(int x, int y, int width, int height) {
        prefs.putInt(WINDOW_X_KEY, x);
        prefs.putInt(WINDOW_Y_KEY, y);
        prefs.putInt(WINDOW_WIDTH_KEY, width);
        prefs.putInt(WINDOW_HEIGHT_KEY, height);
    }
    
    /**
     * Recupera posição X da janela
     */
    public static int getWindowX() {
        return prefs.getInt(WINDOW_X_KEY, -1);
    }
    
    /**
     * Recupera posição Y da janela
     */
    public static int getWindowY() {
        return prefs.getInt(WINDOW_Y_KEY, -1);
    }
    
    /**
     * Recupera largura da janela
     */
    public static int getWindowWidth() {
        return prefs.getInt(WINDOW_WIDTH_KEY, 400);
    }
    
    /**
     * Recupera altura da janela
     */
    public static int getWindowHeight() {
        return prefs.getInt(WINDOW_HEIGHT_KEY, 450);
    }
    
    /**
     * Limpa todas as preferências
     */
    public static void clearAll() {
        try {
            prefs.clear();
            CLILogger.info("Preferências do usuário limpas");
        } catch (Exception e) {
            CLILogger.error("Erro ao limpar preferências: " + e.getMessage());
        }
    }
    
    /**
     * Lista histórico de CPFs usados (limitado a 5)
     */
    public static String[] getCpfHistory() {
        String lastCpf = getLastCpf();
        if (lastCpf.isEmpty()) {
            return new String[0];
        }
        return new String[]{lastCpf}; // Por simplicidade, apenas o último
    }
    
    /**
     * Salva perfil de conexão
     */
    public static void saveConnectionProfile(String name, String host, int port) {
        String key = "profile_" + name;
        String value = host + ":" + port;
        prefs.put(key, value);
        CLILogger.info("Perfil de conexão salvo: " + name + " = " + value);
    }
    
    /**
     * Recupera perfil de conexão
     */
    public static String getConnectionProfile(String name) {
        String key = "profile_" + name;
        return prefs.get(key, "");
    }
    
    /**
     * Lista todos os perfis de conexão
     */
    public static String[] getConnectionProfiles() {
        try {
            return prefs.keys();
        } catch (Exception e) {
            CLILogger.error("Erro ao listar perfis: " + e.getMessage());
            return new String[0];
        }
    }
}
