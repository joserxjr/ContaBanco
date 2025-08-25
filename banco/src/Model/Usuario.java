package Model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String cpf;
    private String nome;
    private List<Conta> contaList;

    public Usuario(String cpf, String nome) {
        this.cpf = cpf;
        this.nome = nome;
        this.contaList = new ArrayList<>();
    }

    public void addConta(Conta conta){
        this.contaList.add(conta);
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public List<Conta> getContaList() {
        return contaList;
    }
}
