package Main;

import Controller.Control;
import View.VwGeral;

import java.time.LocalDateTime;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {

        System.out.println(LocalDateTime.now());
        System.out.println("Bem vindo ao Banco Grupo 3 SA");

        Control control = new Control(); // cria o controler
        VwGeral vwGeral = new VwGeral();
        System.out.println(LocalDateTime.now());

        boolean flag = true;
        String opcao = vwGeral.mostraMenu();
        do{
            switch (opcao){
                case "SA": // para sair do sistema
                    flag = false;
                    break;
                case "CC":
                    control.abreConta();
                    opcao = vwGeral.mostraMenu();
                    break;
                case "DC": // para depositar
                    control.deposita();
                    vwGeral.pulaVariasLinhas();
                    opcao = vwGeral.mostraMenu();
                    break;
                case "SC": // para sacar
                    control.saca();
                    vwGeral.pulaVariasLinhas();
                    opcao = vwGeral.mostraMenu();
                    break;
                case "EX": // para emitir um extrato
                    control.verExtrato();
                    opcao = vwGeral.mostraMenu();
                    vwGeral.pulaVariasLinhas();
                    break;
                default:
                    System.out.println("Opção errada! Digite uma opção válida");
                    vwGeral.pulaVariasLinhas();
                    opcao = vwGeral.mostraMenu();
            }

        }while (flag);
        System.out.println("Sitema encerrado!");
    }
}