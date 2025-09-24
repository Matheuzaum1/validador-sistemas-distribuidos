package com.distribuidos.common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transacao implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String cpfOrigem;
    private String cpfDestino;
    private double valor;
    private LocalDateTime timestamp;
    // Optional cached names for origem/destino (populated by DAO)
    private String nomeEnviador;
    private String nomeRecebedor;

    public Transacao() {}

    public Transacao(long id, String cpfOrigem, String cpfDestino, double valor, LocalDateTime timestamp) {
        this.id = id;
        this.cpfOrigem = cpfOrigem;
        this.cpfDestino = cpfDestino;
        this.valor = valor;
        this.timestamp = timestamp;
    }

    public String getNomeEnviador() { return nomeEnviador; }
    public void setNomeEnviador(String nomeEnviador) { this.nomeEnviador = nomeEnviador; }

    public String getNomeRecebedor() { return nomeRecebedor; }
    public void setNomeRecebedor(String nomeRecebedor) { this.nomeRecebedor = nomeRecebedor; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCpfOrigem() { return cpfOrigem; }
    public void setCpfOrigem(String cpfOrigem) { this.cpfOrigem = cpfOrigem; }

    public String getCpfDestino() { return cpfDestino; }
    public void setCpfDestino(String cpfDestino) { this.cpfDestino = cpfDestino; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format("Transacao{id=%d, valor=%.2f, timestamp=%s, cpfOrigem=%s, nomeEnviador=%s, cpfDestino=%s, nomeRecebedor=%s}",
                id, valor, timestamp == null ? "null" : timestamp.toString(), cpfOrigem, nomeEnviador, cpfDestino, nomeRecebedor);
    }
}
