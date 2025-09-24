package com.distribuidos.tools;

import com.distribuidos.database.DatabaseManager;
import com.distribuidos.common.Transacao;

import java.util.List;

public class DbInspect {
    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();
        int count = db.countTransacoes();
        System.out.println("Transacoes count = " + count);

        List<Transacao> list = db.getAllTransacoes();
        int show = Math.min(list.size(), 30);
        for (int i = 0; i < show; i++) {
            Transacao t = list.get(i);
            System.out.printf("%d | %s -> %s | R$ %.2f | %s%n",
                t.getId(), t.getCpfOrigem(), t.getCpfDestino(), t.getValor(), t.getTimestamp());
        }
    }
}
