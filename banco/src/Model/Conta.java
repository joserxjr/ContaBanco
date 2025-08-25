package Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Conta {
    private String numero;
    protected double saldo;
    private TipoConta tipoConta;
    private List<Transacao> transacoes;
    private Usuario usuario;

    public Conta(String numero, Usuario usuario, TipoConta tipoConta) {
        this.numero = numero;
        this.usuario = usuario;
        this.tipoConta = tipoConta;
        this.saldo = 0;
        this.transacoes = new ArrayList<>();
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void addTransacao(Transacao transacao){
        this.transacoes.add(transacao);
    }

    public String getNumero() {
        return numero;
    }

    public Usuario getUsuario(){
        return this.usuario;
    }

    public TipoConta getTipoConta() {
        return tipoConta;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void getTransacoesOrdeandas(){
        this.transacoes.sort(Comparator.comparing(Transacao::getData));
    }

    public void getTransacoesOrdeandasDesc(){
        this.transacoes.sort(Comparator.comparing(Transacao::getData).reversed());
    }

    public double getSaldo() {
        return saldo;
    }

    public synchronized boolean saca(double valor) {
        if(!this.temSaldoPraSaque(valor)){
            return  false;
        }

        this.saldo -= valor;

        return true;
    }

    public boolean temSaldoPraSaque(double valor){
        return !(valor > this.saldo);
    }

    public void deposita(double valor) {
        this.saldo += valor;
    }

    public abstract String particularidadeConta();

    public abstract double getSaldoDisponivel();

    public abstract double getLimite();

    public abstract void setLimite(double limite);
}
