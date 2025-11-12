package com.distribuidos.database;

/**
 * EXEMPLOS DE USO DOS NOVOS MÉTODOS PIX
 * 
 * Este arquivo demonstra como usar a nova API de gerenciamento de chaves Pix
 * e transações Pix no sistema atualizado.
 * 
 * Data: 12 de novembro de 2025
 * Versão: 1.0.0 - NewPix
 */

public class ExemplosUsoPix {
    
    public static void main(String[] args) {
        // Obter instância do DatabaseManager
        DatabaseManager db = DatabaseManager.getInstance();
        
        // ============================================================================
        // EXEMPLO 1: REGISTRAR CHAVES PIX
        // ============================================================================
        
        // Registrar múltiplas chaves para um usuário
        boolean sucesso1 = db.registrarChavePix(
            "123.456.789-01",      // CPF do proprietário
            "email",               // Tipo de chave
            "joao@email.com"       // Valor da chave
        );
        System.out.println("Chave Email registrada: " + sucesso1);
        
        // Registrar chave de telefone
        boolean sucesso2 = db.registrarChavePix(
            "123.456.789-01",
            "telefone",
            "+5511999999999"
        );
        System.out.println("Chave Telefone registrada: " + sucesso2);
        
        // Registrar chave aleatória
        boolean sucesso3 = db.registrarChavePix(
            "123.456.789-01",
            "aleatoria",
            "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
        );
        System.out.println("Chave Aleatória registrada: " + sucesso3);
        
        // ============================================================================
        // EXEMPLO 2: BUSCAR CPF POR CHAVE PIX
        // ============================================================================
        
        String cpf = db.buscarCpfPorChavePix("joao@email.com");
        if (cpf != null) {
            System.out.println("CPF encontrado para joao@email.com: " + cpf);
        } else {
            System.out.println("Nenhuma chave Pix encontrada");
        }
        
        // ============================================================================
        // EXEMPLO 3: LISTAR TODAS AS CHAVES PIX DE UM USUÁRIO
        // ============================================================================
        
        java.util.List<String> chavesDoJoao = db.listarChavesPix("123.456.789-01");
        System.out.println("\nChaves Pix de João:");
        for (String chave : chavesDoJoao) {
            System.out.println("  • " + chave);
        }
        // Saída esperada:
        // Chaves Pix de João:
        //   • aleatoria: a1b2c3d4-e5f6-7890-abcd-ef1234567890
        //   • telefone: +5511999999999
        //   • email: joao@email.com
        
        // ============================================================================
        // EXEMPLO 4: REGISTRAR TRANSAÇÃO PIX
        // ============================================================================
        
        String identificadorUnico = java.util.UUID.randomUUID().toString();
        
        boolean transacaoCriada = db.registrarTransacaoPix(
            "joao@email.com",           // Chave Pix de origem
            "maria@email.com",          // Chave Pix de destino
            "123.456.789-01",           // CPF do remetente
            "987.654.321-02",           // CPF do destinatário
            150.00,                     // Valor
            identificadorUnico          // Identificador único da transação
        );
        System.out.println("\nTransação Pix registrada: " + transacaoCriada);
        System.out.println("ID da Transação: " + identificadorUnico);
        
        // ============================================================================
        // EXEMPLO 5: LISTAR TRANSAÇÕES PIX DE UM USUÁRIO
        // ============================================================================
        
        java.util.List<String> transacoes = db.listarTransacoesPix("123.456.789-01");
        System.out.println("\nÚltimas transações Pix de João:");
        for (String transacao : transacoes) {
            System.out.println("  " + transacao);
        }
        // Saída esperada (em JSON):
        // {"de": "joao@email.com (123.456.789-01)", 
        //  "para": "maria@email.com (987.654.321-02)", 
        //  "valor": 150.00, 
        //  "data": "2025-11-12T10:55:00"}
        
        // ============================================================================
        // EXEMPLO 6: DESATIVAR UMA CHAVE PIX
        // ============================================================================
        
        boolean desativada = db.desativarChavePix("joao@email.com");
        System.out.println("\nChave Pix desativada: " + desativada);
        
        // Após desativação, a chave não aparecerá mais nas buscas
        chavesDoJoao = db.listarChavesPix("123.456.789-01");
        System.out.println("Chaves Pix restantes após desativação:");
        for (String chave : chavesDoJoao) {
            System.out.println("  • " + chave);
        }
        
        // ============================================================================
        // EXEMPLO 7: CONTAR ESTATÍSTICAS
        // ============================================================================
        
        int totalChaves = db.countChavesPix();
        int totalTransacoes = db.countTransacoesPix();
        
        System.out.println("\nESTATÍSTICAS DO SISTEMA PIX:");
        System.out.println("  Total de chaves Pix ativas: " + totalChaves);
        System.out.println("  Total de transações Pix: " + totalTransacoes);
        
        // ============================================================================
        // FLUXO COMPLETO: TRANSFERÊNCIA VIA PIX
        // ============================================================================
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("FLUXO COMPLETO: TRANSFERÊNCIA VIA PIX");
        System.out.println("=".repeat(70));
        
        // 1. Verificar se a chave Pix de destino existe
        String cpfDestino = db.buscarCpfPorChavePix("maria@email.com");
        if (cpfDestino != null) {
            System.out.println("✓ Chave destino encontrada: maria@email.com -> " + cpfDestino);
            
            // 2. Processar a transferência (lógica de negócio)
            double valor = 100.00;
            System.out.println("✓ Processando transferência de R$ " + valor);
            
            // 3. Registrar a transação Pix
            String idTransacao = java.util.UUID.randomUUID().toString();
            db.registrarTransacaoPix(
                "joao@email.com",
                "maria@email.com",
                "123.456.789-01",
                cpfDestino,
                valor,
                idTransacao
            );
            System.out.println("✓ Transação registrada: " + idTransacao);
            System.out.println("✓ Transferência concluída com sucesso!");
            
        } else {
            System.out.println("✗ Erro: Chave Pix destino não encontrada");
        }
        
        System.out.println("=".repeat(70));
    }
    
    // ============================================================================
    // CASOS DE USO PRÁTICOS
    // ============================================================================
    
    /**
     * Transferência simples via Pix
     */
    static class CasoDeUsoTransferenciaPix {
        void executarTransferencia(String chavePixOrigem, String chavePixDestino, double valor) {
            DatabaseManager db = DatabaseManager.getInstance();
            
            // 1. Resolver chaves Pix para CPFs
            String cpfOrigem = db.buscarCpfPorChavePix(chavePixOrigem);
            String cpfDestino = db.buscarCpfPorChavePix(chavePixDestino);
            
            if (cpfOrigem == null || cpfDestino == null) {
                System.out.println("Erro: Uma ou ambas as chaves Pix não foram encontradas");
                return;
            }
            
            // 2. Validar saldo (lógica de negócio)
            // ... validar saldo do cpfOrigem ...
            
            // 3. Executar transferência (lógica de negócio)
            // ... transferir valor ...
            
            // 4. Registrar no banco de dados
            String idTransacao = java.util.UUID.randomUUID().toString();
            db.registrarTransacaoPix(
                chavePixOrigem,
                chavePixDestino,
                cpfOrigem,
                cpfDestino,
                valor,
                idTransacao
            );
            
            System.out.println("Transferência Pix concluída: " + idTransacao);
        }
    }
    
    /**
     * Registrar múltiplas chaves Pix para um usuário
     */
    static class CasoDeUsoRegistroChavesPix {
        void registrarChavesDeUmUsuario(String cpf, String nome, String email, String telefone) {
            DatabaseManager db = DatabaseManager.getInstance();
            
            // Registrar email
            db.registrarChavePix(cpf, "email", email);
            
            // Registrar telefone
            db.registrarChavePix(cpf, "telefone", telefone);
            
            // Registrar chave aleatória
            String chaveAleatoria = java.util.UUID.randomUUID().toString();
            db.registrarChavePix(cpf, "aleatoria", chaveAleatoria);
            
            System.out.println("Chaves Pix registradas para " + nome);
        }
    }
}
