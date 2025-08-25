package Model;

import java.time.LocalDateTime;

public class Transacao {

    private double valor;
    private LocalDateTime data;
    private TipoTransacao tipoTransacao;
    private double saldoPosTransacao;

    public Transacao(double valor, LocalDateTime data, TipoTransacao tipoTransacao, double saldoPosTransacao) {
        this.valor = valor;
        this.data = data;
        this.tipoTransacao = tipoTransacao;
        this.saldoPosTransacao = saldoPosTransacao;
    }

    public TipoTransacao getTipoTransacao() {
        return tipoTransacao;
    }

    public double getValor(){
        return valor;
    }

    public double getSaldoPosTransacao(){
        return saldoPosTransacao;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setSaldoPosTransacao(double saldoPosTransacao) {
        this.saldoPosTransacao = saldoPosTransacao;
    }
}
