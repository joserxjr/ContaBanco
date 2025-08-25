package Model;

public class ContaPoupanca extends Conta{

    public ContaPoupanca(String numero, Usuario usuario, TipoConta tipoConta) {
        super(numero, usuario, tipoConta);
    }

    @Override
    public String particularidadeConta() {
        return "Conta poupança";
    }

    @Override
    public double getSaldoDisponivel() {
        return this.saldo;
    }

    @Override
    public double getLimite() {
        return 0;
    }

    @Override
    public void setLimite(double limite) {
        // não acontece nada porque é conta corrente
    }
}
