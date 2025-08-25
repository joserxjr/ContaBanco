package View;

import DTO.ContaDTO;

import java.util.Scanner;

/*
* Essa classe é responsável pela interação do usuário com o sistema
 */
public class VwGeral {

    private final Scanner scanner = new Scanner(System.in);

    /*
    Esse método printa um texto na tela e recebe um inteiro
     */
    protected int getInteiro(String frase){
        System.out.println(frase);
        int valor = this.scanner.nextInt();
        this.scanner.nextLine();
        return valor;
    }

    /*
    Esse método printa um texto na tela e recebe um double
     */
    protected double getDouble(String frase){
        System.out.println(frase);
        double valor = this.scanner.nextDouble();
        this.scanner.nextLine();
        return valor;
    }

    /*
    Esse método printa um texto na tela e recebe uma String
     */
    protected String getString(String frase){
        System.out.println(frase); // printa na tela um texto idicando o que o usuário precisa inforar
        return this.scanner.nextLine();
    }

    /*
    Esse método printa uma string na tela
     */
    public void mostraString(String frase){
        System.out.println(frase);
    }

    public void mostraStringSemQuebraDeLinha(String frase){
        System.out.print(frase);
    }

    /*
    Esse método mostra o menu de opções do usuário na tela
     */
    public String mostraMenu(){
        System.out.println("Escolha a opção desejada:");
        System.out.println("CC - Para Cadastrar Conta");
        System.out.println("DC - Para Depositar");
        System.out.println("SC - Para Sacar");
        System.out.println("EX - Para ver Extrato");
        System.out.println("SA - Para Sair");
        String opcao = this.scanner.nextLine();

        return opcao.toUpperCase();
    }

    /*
    Esse método pula duas linhas
     */
    public void pulaVariasLinhas(){
        for (int i = 0; i < 1; i++) {
            System.out.println();
        }
    }

    /*
    Esse método limpa o scanner
     */
    public void limpaScanner(){
        this.scanner.nextLine();
    }

    public void mostraDadosConta(ContaDTO contaDTO){
        System.out.println("Número da conta: " + contaDTO.numero());
        System.out.println("Titular: " + contaDTO.titular());
        System.out.println("CPF: " + contaDTO.cpf());
        System.out.println("Tipo de Conta: " + contaDTO.tipoConta());
    }

}
