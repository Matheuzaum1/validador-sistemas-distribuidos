package com.distribuidos.tools;

import com.distribuidos.client.ClientConnection;
import com.distribuidos.database.DatabaseManager;
import com.distribuidos.common.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTestRunner {
    private static final Logger logger = LoggerFactory.getLogger(ClientTestRunner.class);

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8080;
        if (args != null && args.length >= 1) host = args[0];
        if (args != null && args.length >= 2) port = Integer.parseInt(args[1]);

    // Create a (hidden) client GUI to satisfy ClientConnection logging
    com.distribuidos.client.ClientGUI gui = new com.distribuidos.client.ClientGUI();
    gui.setVisible(false);
    ClientConnection cc = new ClientConnection(gui);

        System.out.println("Connecting to server " + host + ":" + port);
        if (!cc.connect(host, port)) {
            System.err.println("Failed to connect to server");
            System.exit(2);
        }

        // Use seeded user
        String cpf = "123.456.789-01";
        String senha = "123456";

        String loginResp = cc.login(cpf, senha);
        System.out.println("Login response: " + loginResp);

        // extract token
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(loginResp);
        String token = node.get("token").asText();

        // deposit
        String depResp = cc.deposit(token, 100.0);
        System.out.println("Deposit response: " + depResp);

        // transfer to another user
        String transferResp = cc.transfer(token, "987.654.321-02", 50.0);
        System.out.println("Transfer response: " + transferResp);

        // Print balances from DB
        DatabaseManager db = DatabaseManager.getInstance();
        System.out.println("Saldo origem: " + db.getUser(cpf).getSaldo());
        System.out.println("Saldo destino: " + db.getUser("987.654.321-02").getSaldo());

        // Print last transactions
        db.getAllTransacoes().stream().limit(5).forEach(t -> System.out.println(t));

        cc.disconnect();
    }
}
