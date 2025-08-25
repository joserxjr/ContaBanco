package db;

import Model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;

public class DataBase {
    private static List<Conta> DATA_BASE_CONTAS = new ArrayList<>();
    private static List<Usuario> DATA_BASE_USUARIOS = new ArrayList<>();
    private static final String SEPARADOR = ";";
    private static final String PASTA = "src/db/texts/";

    public void carregaUsuarios() throws IOException {
        Path caminho = Paths.get(PASTA,"usuarios.txt");
        // Peta as linhas do arquivo usuários.txt
        List<String> linhas = Files.readAllLines(caminho);

        // Converter cada linha em um objeto Usuario
        DATA_BASE_USUARIOS = linhas.stream()
                .map(linha -> {
                    // Ex: "Id: 1, Nome: Ana"
                    String[] partes = linha.split(";"); // ["cpf", " nome"]
                    String cpf = partes[0].trim();   // pega o cpf
                    String nome = partes[1].trim(); // pega o nome
                    return new Usuario(cpf, nome);
                })
                .collect(Collectors.toList());
    }

    public void carregaContas() throws IOException, InterruptedException {
        Path caminho = Paths.get(PASTA,"contas.txt");
        // Peta as linhas do arquivo usuários.txt
        List<String> linhas = Files.readAllLines(caminho);

        // Converter cada linha em um objeto Usuario
        DATA_BASE_CONTAS = linhas.stream()
                .map(linha -> {
                    String[] partes = linha.split(";"); // ["cpf", " nome"]
                    String cpf = partes[0].trim();   // pega o cpf
                    String numero = partes[1].trim(); // pega o número da conta
                    Usuario usuario = this.procuraUsuarioPorCPF(cpf); // procura o usuário
                    Conta conta = getConta(partes, numero, usuario);
                    usuario.addConta(conta);

                    return conta;
                })
                .collect(Collectors.toList());

        this.carregaTransacoes();
    }

    private static Conta getConta(String[] partes, String numero, Usuario usuario) {
        String tp = partes[2].trim(); // pega o tipo de conta
        double saldo = Double.parseDouble(partes[3].trim());
        Conta conta;

        if(tp.equalsIgnoreCase("CORRENTE")){
            conta = new ContaCorrente(numero, usuario, TipoConta.CORRENTE);
            double limite = Double.parseDouble(partes[4].trim());
            conta.setLimite(limite);
        }else{
            conta = new ContaPoupanca(numero, usuario, TipoConta.POUPANCA);
        }

        conta.setSaldo(saldo);
        return conta;
    }

    public void carregaTransacoes() throws IOException {

        Path caminho = Paths.get(PASTA, "transacoes.txt");
        List<String> linhas = Files.readAllLines(caminho);

        for (String linha : linhas){
            String[] partes = linha.split(SEPARADOR);

            Transacao transacao = getTransacao(partes);

            String numeroConta = partes[0].trim();   // número da conta
            Conta conta = this.getConta(numeroConta);
            if(conta != null){
                conta.addTransacao(transacao);
            }
        }

    }

    private static Transacao getTransacao(String[] partes) {
        String tipoTransacao = partes[1].trim(); // pega tipo de transação
        double valorTransacao = Double.parseDouble(partes[2].trim()); // pega o valor da transacao
        LocalDateTime dataTransacao = LocalDateTime.parse(partes[3].trim());
        double saldoPosTransacao = Double.parseDouble(partes[4].trim()); // pega o saldo após a transação
        TipoTransacao tpTrn = (tipoTransacao.equalsIgnoreCase("saque")) ? TipoTransacao.SAQUE : TipoTransacao.DEPOSITO;
        Transacao transacao = new Transacao(valorTransacao, dataTransacao, tpTrn, saldoPosTransacao);
        return transacao;
    }

    public List<Conta> getContas(){
        return DATA_BASE_CONTAS;
    }

    // salva uma conta e um usuário
    public void save(Conta conta, Usuario usuario){
        if(!this.salvaUsuario(usuario)){
            throw new RuntimeException("Não foi possível salvar o usuário");
        }

        if(!this.salvaConta(conta)){
            throw new RuntimeException("Não foi possível salvar a conta");
        }

    }

    // salva uma conta
    public void save(Conta conta){
        if(!this.salvaConta(conta)){
            throw new RuntimeException("Não foi possível salvar a conta");
        }
    }

    private boolean salvaUsuario(Usuario usuario){
        String linha = usuario.getCpf() + SEPARADOR + usuario.getNome() + "\n";
        try {
            Path caminho = Paths.get(PASTA, "usuarios.txt");
            //salva o usuário no arquivo .txt
            Files.write(caminho, linha.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            DATA_BASE_USUARIOS.add(usuario); // adicona o usuário na lista
            return true;
        }catch (IOException e){
            return false;
        }
    }

    private boolean salvaConta(Conta conta){
        // se o limite for igual a zero, significa que a conta não tem limite
        String valorLimite = (conta.getLimite() == 0) ? "" : Double.toString(conta.getLimite());

        String linha = conta.getUsuario().getCpf() + SEPARADOR + conta.getNumero() + SEPARADOR
               + conta.getTipoConta() + SEPARADOR + conta.getSaldo() + SEPARADOR + valorLimite + "\n";
        try {
            Path caminho = Paths.get(PASTA,"contas.txt");
            Files.write(caminho, linha.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            DATA_BASE_CONTAS.add(conta);
            return true;
        }catch (IOException e){
            return false;
        }
    }

    public synchronized void salvaTransacao(Transacao transacao, Conta conta){
        String linha = "";

        this.espera(15000);// somente para simular a demora na execução
        try {
            Path caminho = Paths.get(PASTA,"transacoes.txt");
            if(transacao.getTipoTransacao().toString().equals("SAQUE")){ // se for saque, precisa verificar se tem saldo
                boolean sacou = conta.saca(transacao.getValor()); // se conseguir sacar
                if(sacou){ // salva a transação
                    linha = conta.getNumero() + SEPARADOR + transacao.getTipoTransacao() + SEPARADOR +
                            transacao.getValor() + SEPARADOR + transacao.getData() + SEPARADOR +
                            conta.getSaldo() + "\n";


                }
            }else{ // é depósito
                conta.deposita(transacao.getValor());
                linha = conta.getNumero() + SEPARADOR + transacao.getTipoTransacao() + SEPARADOR +
                        transacao.getValor() + SEPARADOR + transacao.getData() + SEPARADOR +
                        conta.getSaldo() + "\n";
            }

            Files.write(caminho, linha.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            transacao.setSaldoPosTransacao(conta.getSaldo());
            conta.addTransacao(transacao);
            this.updateContas(conta);
        }catch (IOException e){
            System.out.println("Não foi possível efetuar a transação");
        }
    }

    // após salvar uma transação é necessário atualizar o saldo da conta
    private synchronized void updateContas(Conta contaPraAlterar) throws IOException {

        this.espera(15000); // apenas simula uma espera para ver se a thread está funcionando

        Path caminho = Paths.get(PASTA, "contas.txt");

        // busca a linha pelo cpf do usuário e pelo número da conta
        String dadosBusca = contaPraAlterar.getUsuario().getCpf() + SEPARADOR + contaPraAlterar.getNumero();

        // são os novos dados para salvar
        String novosDadosDaConta = contaPraAlterar.getUsuario().getCpf() + SEPARADOR + contaPraAlterar.getNumero()
                + SEPARADOR + contaPraAlterar.getTipoConta() + SEPARADOR + contaPraAlterar.getSaldo()
                + SEPARADOR + contaPraAlterar.getLimite();

        // lê todas as linhas das contas
        List<String> linhas = Files.readAllLines(caminho);

        // Alterar a linha com os novos dados da conta
        for (int i = 0; i < linhas.size(); i++) {
            if (linhas.get(i).startsWith(dadosBusca)) {
                linhas.set(i, novosDadosDaConta); // substitui a linha
            }
        }

        // Reescrever o arquivo com os novos dados
        Files.write(caminho, linhas, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    }

    public int getQtdContasAbertas(){
        return DATA_BASE_CONTAS.size();
    }

    // verifica se já existem contas salvas
    public boolean semContasAbertas(){
        return DATA_BASE_CONTAS.isEmpty();
    }

    /*
    Como decidimos que um usuário pode ter várias contas, esse método procura na variável DATA_BASE_USUÁRIOS
    se já existe um usuário com este cpf, retornando-o
    */
    public Usuario procuraUsuarioPorCPF(String cpf){
        return DATA_BASE_USUARIOS.stream().
                filter(user -> user.getCpf().equalsIgnoreCase(cpf)).
                findFirst().orElse(null);
    }

    private Conta getConta(String numero){
        return DATA_BASE_CONTAS.stream().
                filter(conta -> conta.getNumero().equalsIgnoreCase(numero)).
                findFirst().orElse(null);
    }

    // esse método é pra simular um atraso na execução do programa
    public void espera(int millis){
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
