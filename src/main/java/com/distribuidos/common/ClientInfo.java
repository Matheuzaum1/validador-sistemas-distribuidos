package com.distribuidos.common;

import java.io.Serializable;

public class ClientInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String ip;
    private int porta;
    private String hostname;
    private String cpfUsuario;
    private String nomeUsuario;
    private long conectadoEm;
    
    public ClientInfo() {}
    
    public ClientInfo(String ip, int porta, String hostname) {
        this.ip = ip;
        this.porta = porta;
        this.hostname = hostname;
        this.conectadoEm = System.currentTimeMillis();
    }
    
    // Getters e Setters
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    public int getPorta() { return porta; }
    public void setPorta(int porta) { this.porta = porta; }
    
    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }
    
    public String getCpfUsuario() { return cpfUsuario; }
    public void setCpfUsuario(String cpfUsuario) { this.cpfUsuario = cpfUsuario; }
    
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    
    public long getConectadoEm() { return conectadoEm; }
    public void setConectadoEm(long conectadoEm) { this.conectadoEm = conectadoEm; }
    
    @Override
    public String toString() {
        return "ClientInfo{" +
                "ip='" + ip + '\'' +
                ", porta=" + porta +
                ", hostname='" + hostname + '\'' +
                ", cpfUsuario='" + cpfUsuario + '\'' +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                ", conectadoEm=" + conectadoEm +
                '}';
    }
}