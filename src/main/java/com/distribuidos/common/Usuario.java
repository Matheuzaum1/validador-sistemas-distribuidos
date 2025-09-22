package com.distribuidos.common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String cpf;
    private String nome;
    private String senha;
    private double saldo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    
    public Usuario() {}
    
    public Usuario(String cpf, String nome, String senha) {
        this.cpf = cpf;
        this.nome = nome;
        this.senha = senha;
        this.saldo = 0.0;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }
    
    // Getters e Setters
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { 
        this.nome = nome;
        this.atualizadoEm = LocalDateTime.now();
    }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { 
        this.senha = senha;
        this.atualizadoEm = LocalDateTime.now();
    }
    
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { 
        this.saldo = saldo;
        this.atualizadoEm = LocalDateTime.now();
    }
    
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", saldo=" + saldo +
                ", criadoEm=" + criadoEm +
                ", atualizadoEm=" + atualizadoEm +
                '}';
    }
}