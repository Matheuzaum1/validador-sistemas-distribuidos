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

    public Transacao() {}

    public Transacao(long id, String cpfOrigem, String cpfDestino, double valor, LocalDateTime timestamp) {
        this.id = id;
        this.cpfOrigem = cpfOrigem;
        this.cpfDestino = cpfDestino;
        this.valor = valor;
        this.timestamp = timestamp;
    }

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
}
