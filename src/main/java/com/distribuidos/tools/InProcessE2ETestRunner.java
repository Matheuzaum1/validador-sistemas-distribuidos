package com.distribuidos.tools;

import com.distribuidos.client.ClientConnection;
import com.distribuidos.client.ClientGUI;
import com.distribuidos.database.DatabaseManager;

public class InProcessE2ETestRunner {
    public static void main(String[] args) throws Exception {
        int port = 20000;
        if (args != null && args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }

        // Start server GUI and start server on given port via reflection
        com.distribuidos.server.ServerGUI serverGUI = new com.distribuidos.server.ServerGUI();
        serverGUI.setVisible(false);

        java.lang.reflect.Field portField = com.distribuidos.server.ServerGUI.class.getDeclaredField("portField");
        portField.setAccessible(true);
        javax.swing.JTextField pf = (javax.swing.JTextField) portField.get(serverGUI);
        pf.setText(String.valueOf(port));

        java.lang.reflect.Method startMethod = com.distribuidos.server.ServerGUI.class.getDeclaredMethod("startServer");
        startMethod.setAccessible(true);
        startMethod.invoke(serverGUI);

        // Give server a moment
        Thread.sleep(300);

        // Create hidden client GUI and connection
        ClientGUI clientGUI = new ClientGUI();
        clientGUI.setVisible(false);
        ClientConnection cc = new ClientConnection(clientGUI);

        if (!cc.connect("localhost", port)) {
            System.err.println("Failed to connect to in-process server");
            System.exit(2);
        }

        String cpf = "123.456.789-01";
        String senha = "123456";

        String loginResp = cc.login(cpf, senha);
        System.out.println("Login response: " + loginResp);

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(loginResp);
        String token = node.get("token").asText();

        String depResp = cc.deposit(token, 42.0);
        System.out.println("Deposit response: " + depResp);

        String transferResp = cc.transfer(token, "987.654.321-02", 13.5);
        System.out.println("Transfer response: " + transferResp);

        DatabaseManager db = DatabaseManager.getInstance();
        System.out.println("Saldo origem: " + db.getUser(cpf).getSaldo());
        System.out.println("Saldo destino: " + db.getUser("987.654.321-02").getSaldo());

        System.out.println("Last transactions:");
        db.getAllTransacoes().stream().limit(5).forEach(t -> System.out.println(t));

        cc.disconnect();
        // stop server
        java.lang.reflect.Method stopMethod = com.distribuidos.server.ServerGUI.class.getDeclaredMethod("stopServer");
        stopMethod.setAccessible(true);
        stopMethod.invoke(serverGUI);
        System.exit(0);
    }
}
