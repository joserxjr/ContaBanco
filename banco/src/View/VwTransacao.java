package View;

import DTO.CabecalhoExtratoDTO;
import DTO.ExtratoDTO;

import java.util.List;

public class VwTransacao extends VwGeral{

    /*
    Esse método solicita que o usuário informe o tipo de extrato (se em ordem crescente ou decrescente)
     */
    public String getTipoDeExtrato(){
        String tipoExtrato;
        do{ // enquanto o usuário não digitar 1 ou 2 vai continuar no laço
            tipoExtrato = this.getString("Digite 1 se você quiser o extrato com a data na ordem crescente ou 2 se você quiser a data na ordem decrescente");

            if (!tipoExtrato.equals("1") && !tipoExtrato.equals("2")){ // se o usuário digitar algo que não seja 1 ou 2, informa ao usuário que a opção digita está inválda
                System.out.println("Opção inválida");
            }

        }while (!tipoExtrato.equals("1") && !tipoExtrato.equals("2"));

        if(tipoExtrato.equals("1")){
            tipoExtrato = "crescente";
        }

        if (tipoExtrato.equals("2")){
            tipoExtrato = "decrescente";
        }

        return tipoExtrato;
    }

    /*
    Esse método é solicita o número da conta para o usuário
     */
    public String getNumeroDaConta(String tipoTracao){
        String numeroConta;
        do{ // enquanto o usuário não digitar uma string com cinco caracteres o laço continua
            numeroConta = this.getString("Informe o número da conta (com 5 dígitos) para " + tipoTracao);

            if (numeroConta.length() != 5){ // se o número da conta não tiver cinco dígitos, informa ao usuário que o número da conta está inválido
                System.out.println("O número da conta deve conter 5 dígitos");
            }

        }while (numeroConta.length() != 5);

        return numeroConta;
    }

    /*
    Esse método solicita o valor da transação para o usuário
     */
    public double getValorTransacao(String tipoTransacao){
        double valor;

        do{// enquanto o valor for menor ou igual a 0, continua no laço
            try{
                valor = this.getDouble("Informe o valor a ser " + tipoTransacao + " (maior que 0)");
            } catch (Exception e) { // se o valor não for numérico
                valor = -1; // atribui -1 para o valor
                this.limpaScanner();
            }

            if(valor <= 0){ // se o valor for menor ou igual a zero, informa ao usuário que o valor precisa ser maior que zero
                System.out.println("Você deve informar um valor numérico maior que 0. Se você estiver usando as casas decimais (centavos), use vírgula em vez de ponto.");
            }

        }while (valor <= 0);

        return valor;
    }

    /*
    Esse método é responsável por mostrar o extrato ao usuário
     */
    public void mostraExtrato(List<ExtratoDTO> extrato, CabecalhoExtratoDTO cabecalhoExtratoDTO){

        if(cabecalhoExtratoDTO.tipoConta().equals("CORRENTE")){ // se a conta for conta corrente
            System.out.println(cabecalhoExtratoDTO.particularidade());
            System.out.println("Saldo com o cheque especial: " + cabecalhoExtratoDTO.saldoDisponivel());
            System.out.println("Saldo sem o cheque especial: " + cabecalhoExtratoDTO.saldoConta());
            System.out.println("O saldo do extrato abaixo não conta o valor do cheque especial");
        } else if (cabecalhoExtratoDTO.tipoConta().equals("POUPANCA")) { // se a conta for poupança
            System.out.println("Saldo da conta: " + cabecalhoExtratoDTO.saldoConta());
        }

        // cabeçalho da "tabela" do extrato
        System.out.print("Data");
        printaEspaco(24, 4);
        System.out.print("Tipo");
        printaEspaco(12, 4);
        System.out.print("Valor");
        printaEspaco(20, 5);
        System.out.println("Saldo");

        for(ExtratoDTO transacao : extrato){
            System.out.print(transacao.data()); // mostra a data e hora da transação
            printaEspaco(5, 0);
            System.out.print(transacao.tipoTransacao()); // mostra o tipo de transação (de depósito ou saque)
            printaEspaco(12, transacao.tipoTransacao().length());
            System.out.print(transacao.valor()); // mostra o valor da transação
            printaEspaco(20, transacao.valor().length());
            System.out.println(transacao.saldo());// mostra o saldo da conta depois da transação
        }
    }

    /*
    esse método é responsável por printar na tela espaçõs em branco, onde o a quantidade de espaços é a
    subtração da espaço esperado que a informação ocupe (tamanhoTotal) e o tamanho real da inforamção (tamanhoString)
     */
    private void printaEspaco(int tamanhoTotal, int tamanhoString){
        int tamanhoPercorrer = tamanhoTotal - tamanhoString;
        for(int i = 0; i < tamanhoPercorrer; i++)
            System.out.print(" ");
    }

}
