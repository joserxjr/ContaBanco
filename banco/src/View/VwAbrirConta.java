package View;

import DTO.ContaDTO;
import DTO.UsuarioDTO;
import Model.Conta;

import java.util.List;

/*
Essa classe é responsável por capturar e mostrar os dados relativos à abertura de conta
 */
public class VwAbrirConta extends VwGeral{

    /*
    Esse método recebe e valida o nome do cliente
     */
    private String getNomeCliente(){
        this.pulaVariasLinhas();
        String nome = "";
        int qtdCaracteresNome;
        do { // enquanto o nome não tiver 3 ou mais caracteres, repete a solicitção do nome do cliente
            nome = this.getString("Informe o nome do cliente (no mínimo 3 caracteres)");
            qtdCaracteresNome = nome.length();

            if(qtdCaracteresNome < 3){ // se o nome tiver menos de 3 caracteres, informa ao usuário
                System.out.println("O nome do cliente deve conter no mínimo 3 caracteres");
            }

        }while (qtdCaracteresNome < 3);

        return nome;
    }

    /*
     * Esse método recebe e valida o cpf do cliente
     * Como esse trabalho é apenas para estudos, resolvermos considerar que o cpf tem apenas 4 dígitos numéricos
     */
    public String getCpf(){
        String cpf;
        int qtdCaracteresCpf = 0;
        do{ // enquanto o cpf não tiver 4 caracteres, repete a solicitação do cpf do cliente
            cpf = this.getString("Informe o cpf do cliente (4 caracteres numéricos)");
            qtdCaracteresCpf = cpf.length();

            if(qtdCaracteresCpf != 4){ // se o cpf for informado com mais ou menos caracteres
                System.out.println("O cpf deve conter 4 caracteres numéricos");
            }else{ // se o cpf tiver quatro caracteres
                try { // tenta converter o cpf digitado em um número
                    int cpfNumerico = Integer.parseInt(cpf); // converte a String para inteiro
                    cpf = String.format("%04d", cpfNumerico);
                }catch (Exception e){
                    System.out.println("O cpf deve ser numérico");
                    qtdCaracteresCpf = 0;
                }
            }
        }while (qtdCaracteresCpf != 4);

        return cpf;
    }

    /*
    Esse método retorna o tipo de conta que o usuário deseja cadastrar
     */
    private String getTipoConta(){
        int opcao;
        String tc = "";

        do{ // enquanto o usuáiro não digiar 1 ou 2 repete a solcitação do tipo de conta
            System.out.println("Informe o Tipo de Conta:");
            System.out.println("1 - Para Conta Corrente");
            System.out.println("2 - Para Conta Poupança");

            try{
                opcao = this.getInteiro("Digite aqui: ");
            } catch (Exception e) { // se o usuário digitar qualquer coisa que não seja inteiro
                opcao = -1;
                this.limpaScanner();
            }

            if(opcao == 1){ // se o usuário digitou 1
                tc = "corrente";
            }else if(opcao == 2){ // se usuário digitou 2
                tc = "poupanca";
            }else { // senão, inforam a necessidade de digitar 1 ou 2
                System.out.println("Você deve informar 1 ou 2");
            }

        }while (opcao < 1 || opcao > 2);


        return tc;
    }

    /*
    Esse método informa que a conta foi aberta com sucesso
     */
    public void msgSucesso(ContaDTO conta){
        System.out.println("Conta aberta com sucesso");
        this.mostraDadosConta(conta);
    }

    /*
    Esse método informa que a conta foi aberta com sucesso e informa o valor do cheque especial
     */
    public void msgSucesso(ContaDTO conta, String valorChequeEspecial){
        this.msgSucesso(conta);
        System.out.println("Valor do cheque especial: " + valorChequeEspecial);
    }

    /*
    Esse método capura os dados para abertura da conta e retorna-os em um objeto dto
     */
    public ContaDTO cadastraConta(String cpf){
        String nome = this.getNomeCliente();
        String tipoConta = this.getTipoConta();

        return new ContaDTO("", nome, tipoConta, cpf);
    }

    /*
    Esse método capura os dados para abertura da conta e retorna-os em um objeto dto
     */
    public ContaDTO cadastraConta(UsuarioDTO usuarioDTO){
        String tipoConta = this.getTipoConta();
        return new ContaDTO("", usuarioDTO.nome(), tipoConta, usuarioDTO.cpf());
    }

    /*
    Esse método pega o valor do cheque especial para a conta
     */
    public double getValorChequeEspecial(){
        double valorChequeEspecial = 0;
        do{ // enquanto o valor não for um double mairo que o repete a solicitação deo valor do cheque especial
            try{
                valorChequeEspecial = this.getDouble("Informe o valor do cheque especial (deve ser maior que 0)");
            }catch (Exception e) { // se o valor digitado pelo usuário for qualquer coisa não numérica
                valorChequeEspecial = 0;
                this.limpaScanner();
            }

            if(valorChequeEspecial <= 0){ // se o valor for menor ou igual a zero, informa ao usuáiro
                System.out.println("Você deve informar um valor numérico maior que 0. Se você estiver usando as casas decimais (centavos), use vírgula em vez de ponto.");
            }

        }while (valorChequeEspecial <= 0);


        return valorChequeEspecial;
    }

    // printa uma mensagem de erro caso a abertura da conta não seja bem sucedida
    public void erro(){
        System.out.println("Algo deu errado ao abrir a conta");
    }

    /*
    Como decidimos que um usuário pode ter mais de uma conta,
    esse método mostra as contas que o usuário já tem e pede que o usuário decida se quer abrir uma nova conta
    */
    public String querAbrirNovaConta(UsuarioDTO usuarioDTO, List<ContaDTO> contaDTOList){
        System.out.println("Esse cliente já possui conta conosco");
        System.out.println("Cliente: " + usuarioDTO.nome());
        System.out.println("CPF: " + usuarioDTO.cpf());
        System.out.println("Conta(s):");
        contaDTOList.forEach(c -> System.out.println("Conta " + c.tipoConta() + ", número " + c.numero()));

        System.out.println();
        System.out.println("Deseja abrir outra conta?");
        String opcao;

        do{
            opcao = this.getString("Digite S se você quer abrir outra conta ou N se você não quer abrir outra conta para o cliente");

            if(!opcao.equalsIgnoreCase("S") && !opcao.equalsIgnoreCase("N") ){
                System.out.println("Opção inválida");
            }

        }while (!opcao.equalsIgnoreCase("S") && !opcao.equalsIgnoreCase("N"));

        return opcao;
    }
}
