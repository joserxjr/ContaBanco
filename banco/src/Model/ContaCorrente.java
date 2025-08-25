package Model;

import java.text.NumberFormat;
import java.util.Locale;

public class ContaCorrente extends Conta{

    private double chequeEspecial;

    public ContaCorrente(String numero, Usuario usuario, TipoConta tipoConta) {
        super(numero, usuario, tipoConta);
    }

    public double getSaldoDisponivel(){
        return this.saldo + this.chequeEspecial;
    }

    @Override
    public double getLimite() {
        return this.chequeEspecial;
    }

    @Override
    public void setLimite(double limite) {
        this.chequeEspecial = limite;
    }

    public void setChequeEspecial(double chequeEspecial) {
        this.chequeEspecial = chequeEspecial;
    }

    @Override
    public synchronized boolean saca(double valor){
        if(!this.temSaldoPraSaque(valor)){
            return  false;
        }
        this.saldo -= valor;

        return true;
    }

    // verifica se o cliente tem saldo suficiente par o saque
    public boolean temSaldoPraSaque(double valor){
        return !(valor > (this.chequeEspecial + this.saldo));
    }

    @Override
    public String particularidadeConta() {
        Locale brasil = new Locale("pt", "BR");

        NumberFormat nf = NumberFormat.getCurrencyInstance(brasil); // Formata o valor como moeda
        return "Valor do cheque especial: " + nf.format(chequeEspecial);
    }
}
