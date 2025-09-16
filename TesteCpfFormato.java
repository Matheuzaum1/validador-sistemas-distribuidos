import java.io.*;
import java.net.Socket;

/**
 * Teste específico para validar o problema de formatação de CPF resolvido.
 * Testa tanto o formato com máscara quanto apenas números.
 */
public class TesteCpfFormato {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE DE VALIDAÇÃO DE CPF ===");
        System.out.println();
        
        // Teste 1: CPF com máscara (formato original que não funcionava)
        testeCadastroComMascara();
        
        // Teste 2: CPF apenas números
        testeCadastroApenasNumeros();
        
        // Teste 3: Login com diferentes formatos
        testeLoginDiferentesFormatos();
    }
    
    private static void testeCadastroComMascara() {
        System.out.println("📋 TESTE 1: Cadastro com CPF formatado (100.181.699-45)");
        
        String jsonCadastro = "{"
            + "\"operacao\":\"usuario_criar\","
            + "\"cpf\":\"100.181.699-45\","
            + "\"nome\":\"Matheus Henrique Rosendo Medeiros\","
            + "\"senha\":\"123456\""
            + "}";
            
        String resposta = enviarParaServidor(jsonCadastro);
        
        if (resposta != null && resposta.contains("\"success\":true")) {
            System.out.println("✅ SUCESSO: CPF com máscara aceito!");
        } else {
            System.out.println("❌ ERRO: CPF com máscara rejeitado!");
            System.out.println("Resposta: " + resposta);
        }
        System.out.println();
    }
    
    private static void testeCadastroApenasNumeros() {
        System.out.println("📋 TESTE 2: Cadastro com CPF apenas números (12345678901)");
        
        String jsonCadastro = "{"
            + "\"operacao\":\"usuario_criar\","
            + "\"cpf\":\"12345678901\","
            + "\"nome\":\"João Silva Teste\","
            + "\"senha\":\"123456\""
            + "}";
            
        String resposta = enviarParaServidor(jsonCadastro);
        
        if (resposta != null && resposta.contains("\"success\":true")) {
            System.out.println("✅ SUCESSO: CPF apenas números aceito!");
        } else {
            System.out.println("❌ ERRO: CPF apenas números rejeitado!");
            System.out.println("Resposta: " + resposta);
        }
        System.out.println();
    }
    
    private static void testeLoginDiferentesFormatos() {
        System.out.println("🔐 TESTE 3: Login com diferentes formatos de CPF");
        
        // Teste login com máscara
        String jsonLogin1 = "{"
            + "\"operacao\":\"usuario_login\","
            + "\"cpf\":\"100.181.699-45\","
            + "\"senha\":\"123456\""
            + "}";
            
        String resposta1 = enviarParaServidor(jsonLogin1);
        System.out.println("Login com máscara: " + 
            (resposta1 != null && resposta1.contains("\"success\":true") ? "✅ OK" : "❌ Falhou"));
        
        // Teste login apenas números  
        String jsonLogin2 = "{"
            + "\"operacao\":\"usuario_login\","
            + "\"cpf\":\"10018169945\","
            + "\"senha\":\"123456\""
            + "}";
            
        String resposta2 = enviarParaServidor(jsonLogin2);
        System.out.println("Login apenas números: " + 
            (resposta2 != null && resposta2.contains("\"success\":true") ? "✅ OK" : "❌ Falhou"));
        
        System.out.println();
    }
    
    private static String enviarParaServidor(String json) {
        try (Socket socket = new Socket("localhost", 20000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.println(json);
            return in.readLine();
            
        } catch (Exception e) {
            System.out.println("Erro de conexão: " + e.getMessage());
            return null;
        }
    }
}
