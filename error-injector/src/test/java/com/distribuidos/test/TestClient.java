package com.distribuidos.test;

import java.io.*;
import java.net.*;

public class TestClient {
    public static void main(String[] args) {
        try {
            System.out.println("🔌 Conectando ao proxy na porta 9999...");
            Socket socket = new Socket("localhost", 9999);
            System.out.println("✅ Conectado com sucesso!");
            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Enviar mensagem de teste
            String testMessage = "{\"operacao\":\"teste\",\"valor\":100.0}";
            System.out.println("📤 Enviando: " + testMessage);
            out.println(testMessage);
            
            // Aguardar resposta
            String response = in.readLine();
            System.out.println("📥 Recebido: " + response);
            
            socket.close();
            System.out.println("🔌 Conexão encerrada");
            
        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }
}