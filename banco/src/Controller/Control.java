package Controller;

import DTO.CabecalhoExtratoDTO;
import DTO.ContaDTO;
import DTO.ExtratoDTO;
import DTO.UsuarioDTO;
import Model.*;
import View.VwAbrirConta;
import View.VwTransacao;
import db.DataBase;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Control {

    private final DataBase dataBase;

    public Control(){
        this.dataBase = new DataBase();
        try {
            this.dataBase.carregaUsuarios();
            this.dataBase.carregaContas();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * esse método é responsável por gerenciar o processo de abertura de conta
    */
    public void abreConta(){
        VwAbrirConta abrirConta = new VwAbrirConta(); // cria a view para interagir com o usuário
        String cpf = abrirConta.getCpf();

        // procura se já existe um usuário cadastrado com o cpf informado
        Usuario usuarioProcurado = this.dataBase.procuraUsuarioPorCPF(cpf);

        ContaDTO contaDTO;
        String numeroConta = String.format("%05d", this.dataBase.getQtdContasAbertas() + 1); // cria o número da conta (que é a quantidade de contas cadastradas mais 1 - com cinco dígitos)

        if(usuarioProcurado != null){// significa que já existe um cliente cadastrado no sistema, então o sistema vai mostrar as contas que esse usuário já tem e perguntar se ele quer abrir uma nova conta
            UsuarioDTO usuarioDTO = new UsuarioDTO(usuarioProcurado.getNome(), usuarioProcurado.getCpf());
            List<ContaDTO> contasDTO = usuarioProcurado.getContaList().stream().
                    map(conta -> new ContaDTO(conta.getNumero(), "", conta.getTipoConta().toString(), "")).
                    toList();
            String opcao = abrirConta.querAbrirNovaConta(usuarioDTO, contasDTO);
            if(opcao.equalsIgnoreCase("s")){ // se for escolhido por abrir a conta
                contaDTO = abrirConta.cadastraConta(usuarioDTO);
                this.salvaContaClienteExistente(usuarioProcurado, contaDTO, numeroConta, abrirConta); // salva uma nova conta para o cliente existe
            }else { // senão, encerra-se o fluxo
                abrirConta.mostraString("Abertura de conta cancelada com sucesso");
                abrirConta.pulaVariasLinhas();
                return;
            }
        }else{ // significa que o cliente não tem conta aberta ainda
            contaDTO = abrirConta.cadastraConta(cpf); // após o usuário informar os dados para a abertura da conta, eles (os dados) são transferidos para o controller por meio de um objeto dto
            this.salvaNovaConta(contaDTO, numeroConta, abrirConta);
        }

        abrirConta.pulaVariasLinhas(); // pula algumas linhas na tela
    }

    // esse método salva um novo usuário e uma nova conta
    private void salvaNovaConta(ContaDTO contaDTO, String numeroConta, VwAbrirConta abrirConta){
        TipoConta tp;
        Usuario usuario = new Usuario(contaDTO.cpf(), contaDTO.titular());
        if(contaDTO.tipoConta().equals("corrente")){ // caso o usuário tenha escolhido a opção 1 (conta corrente)
            tp = TipoConta.CORRENTE;
            ContaCorrente contaCorrente = new ContaCorrente(numeroConta, usuario, tp); // cria a conta corrente
            usuario.addConta(contaCorrente);
            double valorChqueEspecial = abrirConta.getValorChequeEspecial(); // pega o valor do cheque especial
            contaCorrente.setChequeEspecial(valorChqueEspecial); // adiciona o cheque especial na conta
            this.dataBase.save(contaCorrente, usuario);// "salva" a conta no "banco de dados"
            ContaDTO contaDTO2 = new ContaDTO(numeroConta, contaDTO.titular(), tp.toString(), contaDTO.cpf()); // cria um objeto DTO para apresentar ao usuário
            abrirConta.msgSucesso(contaDTO2, toMoeda(valorChqueEspecial)); // mostra os dados da conta criada para o usuário
        }else if(contaDTO.tipoConta().equals("poupanca")){ // caso o usuário tenha escolhido a opção 2 (conta poupança)
            tp = TipoConta.POUPANCA;
            ContaPoupanca poupanca=  new ContaPoupanca(numeroConta, usuario, tp);
            usuario.addConta(poupanca);
            this.dataBase.save(poupanca, usuario);// cria o objeto conta poupança e "salva" no "banco de dados"
            ContaDTO contaDTO2 = new ContaDTO(numeroConta, contaDTO.titular(), tp.toString(), contaDTO.cpf()); // cria um objeto DTO para apresentar ao usuário
            abrirConta.msgSucesso(contaDTO2); // mostra os dados da conta criada para o usuário
        }else{
            abrirConta.erro(); // caso algo não funcione como esperado e um tipo de conta venha diferent de corrente ou poupança, mostra uma mensagem de erro para o usuário
        }
    }

    // esse método salva somente a nova conta
    private void salvaContaClienteExistente(Usuario usuario, ContaDTO contaDTO, String numeroConta, VwAbrirConta abrirConta){
        TipoConta tp;
        if(contaDTO.tipoConta().equals("corrente")){ // caso o usuário tenha escolhido a opção 1 (conta corrente)
            tp = TipoConta.CORRENTE;
            ContaCorrente contaCorrente = new ContaCorrente(numeroConta, usuario, tp); // cria a conta corrente
            usuario.addConta(contaCorrente);
            double valorChqueEspecial = abrirConta.getValorChequeEspecial(); // pega o valor do cheque especial
            contaCorrente.setChequeEspecial(valorChqueEspecial); // adiciona o cheque especial na conta
            this.dataBase.save(contaCorrente);// "salva" a conta no "banco de dados"
            ContaDTO contaDTO2 = new ContaDTO(numeroConta, contaDTO.titular(), tp.toString(), contaDTO.cpf()); // cria um objeto DTO para apresentar ao usuário
            abrirConta.msgSucesso(contaDTO2, toMoeda(valorChqueEspecial)); // mostra os dados da conta criada para o usuário
        }else if(contaDTO.tipoConta().equals("poupanca")){ // caso o usuário tenha escolhido a opção 2 (conta poupança)
            tp = TipoConta.POUPANCA;
            ContaPoupanca poupanca=  new ContaPoupanca(numeroConta, usuario, tp);
            usuario.addConta(poupanca);
            this.dataBase.save(poupanca);// cria o objeto conta poupança e "salva" no "banco de dados"
            ContaDTO contaDTO2 = new ContaDTO(numeroConta, contaDTO.titular(), tp.toString(), contaDTO.cpf()); // cria um objeto DTO para apresentar ao usuário
            abrirConta.msgSucesso(contaDTO2); // mostra os dados da conta criada para o usuário
        }else{
            abrirConta.erro(); // caso algo não funcione como esperado e um tipo de conta venha diferent de corrente ou poupança, mostra uma mensagem de erro para o usuário
        }
    }

    /*
    * esse método é respensável por gerenciar o processo de saque em uma conta
    */
    public void saca(){

        VwTransacao vwTransacao = new VwTransacao(); // cria a view para interagir com o usuário

        if(this.dataBase.semContasAbertas()){// se a lista de contas estiver vazia, informa ao usuário
            vwTransacao.mostraString("O sistema ainda não tem contas cadastradas. Antes de sacar é necessário cadastrar uma conta.");
        }else{ // havendo ao menos uma conta, o sistema solicita informações da conta
            // Cria o pool fixo com 1 thread
            ExecutorService service = Executors.newFixedThreadPool(1);
            try {
                String numeroContaBuscar = vwTransacao.getNumeroDaConta("sacar");// solicita ao usuário o número da conta
                Conta contaParaSaque = this.getConta(numeroContaBuscar); // pesquisa a conta a partir do número informado pelo usuário

                if (contaParaSaque == null){ // significa que a conta não existe
                    vwTransacao.mostraString("Conta não encontrada! Transação finalizada.");
                }else{ // conta encontrada
                    ContaDTO contaDTO = new ContaDTO(contaParaSaque.getNumero(), contaParaSaque.getUsuario().getNome(), contaParaSaque.getTipoConta().toString(), contaParaSaque.getUsuario().getCpf()); // cria um objeto dto para apresentar os dados da conta ao usuário
                    vwTransacao.mostraDadosConta(contaDTO); // mostra os dados da conta ao usuário
                    double valorDoSaque = vwTransacao.getValorTransacao("sacado"); // solicita ao usuário o valor a ser sacado
                    boolean temSaldo = contaParaSaque.temSaldoPraSaque(valorDoSaque); // verifica se o cliente tem saldo para saque

                    if(temSaldo){ // se tem saldo

                        Transacao transacao = new Transacao(valorDoSaque, LocalDateTime.now(), TipoTransacao.SAQUE, contaParaSaque.getSaldo());

                        Future<?> f = service.submit(() -> this.dataBase.salvaTransacao(transacao, contaParaSaque));
                        f.get(); // bloqueia até a thread terminar, evitando que o usuário faça outro saque antes que este seja efetuado
                        vwTransacao.mostraString("Saque realizado com sucesso"); // informa ao usuário que o saque foi bem sucedido

                }else{ // senão
                    vwTransacao.mostraString("Não foi possível realizar o saque! Conta com saldo insuficiente."); // informa ao usuário que não foi possível efetuar o saque
                    vwTransacao.mostraString("Saldo disponível: " + toMoeda(contaParaSaque.getSaldoDisponivel())); // informa ao usuário o valor do saldo disponível para saque
                }
            }} catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                service.shutdown();
            }
        }
        vwTransacao.pulaVariasLinhas(); // pula algumas linhas
    }

    /*
    *Esse método é responsável por gerenciar o processo de deposito em uma conta
    */
    public void deposita(){
        VwTransacao vwTransacao = new VwTransacao(); // cria a view para interagir com o usuário

        if(this.dataBase.semContasAbertas()){//verifica se já existem contas cadastradas no sistema
            vwTransacao.mostraString("O sistema ainda não tem contas cadastradas. Antes de depositar é necessário cadastrar uma conta.");
        }else{// havendo ao menos uma conta, o sistema solicita informações sobre a conta
            String numeroContaBuscar = vwTransacao.getNumeroDaConta("sacar");// solicita ao usuário o número da conta
            Conta contaParaDeposito = this.getConta(numeroContaBuscar); // pesquisa a conta a partir do número informado pelo usuário

            if (contaParaDeposito == null){ // significa que a conta não existe
                vwTransacao.mostraString("Conta não encontrada! Transação finalizada");
            }else { // conta encontrada
                ExecutorService service = Executors.newFixedThreadPool(1);
                try {
                    ContaDTO contaDTO = new ContaDTO(contaParaDeposito.getNumero(), contaParaDeposito.getUsuario().getNome(), contaParaDeposito.getTipoConta().toString(), contaParaDeposito.getUsuario().getCpf()); // cria um objeto dto para mostra informações da conta ao usuáiro
                    vwTransacao.mostraDadosConta(contaDTO); // mostra informações da conta ao usuário
                    double valorDoDeposito = vwTransacao.getValorTransacao("depoisitado"); // solicita o valor a ser depositado
                    Transacao transacao = new Transacao(valorDoDeposito, LocalDateTime.now(), TipoTransacao.DEPOSITO, contaParaDeposito.getSaldo());
                    service.submit(() -> this.dataBase.salvaTransacao(transacao, contaParaDeposito));
                } finally {
                    service.shutdown();
                }
                vwTransacao.mostraString("Depósito realizado com sucesso"); // informa o usuário que o depósito foi feito com sucesso
            }
        }
        vwTransacao.pulaVariasLinhas(); // pula algumas linhas
    }

    /*
    *Esse método é responsável por gerenciar o processo de apresentar um extrato para o usuário
    */
    public void verExtrato() {
        VwTransacao vwTransacao = new VwTransacao(); // cria a view para interagir com o usuário

        if (this.dataBase.semContasAbertas()) {// se não existir nenhuma conta cadastrada no sistema
            vwTransacao.mostraString("O sistema ainda não tem contas cadastradas. Antes de ver o extrato é necessário cadastrar uma conta.");
        } else {// havendo ao menos uma conta, o sistema solicita informações da conta

            String numeroContaBuscar = vwTransacao.getNumeroDaConta("ver o extrato");// solicita ao usuário o número da conta
            Conta contaParaExtrato = this.getConta(numeroContaBuscar); // pesquisa a conta a partir do número informado pelo usuário

            if (contaParaExtrato == null){ // significa que a conta não existe
                vwTransacao.mostraString("Conta não encontrada! Transação finalizada");
            }else{
                contaParaExtrato.getTransacoesOrdeandasDesc(); // ordena as transações em ordem decrescente

                ContaDTO contaDTO = new ContaDTO(contaParaExtrato.getNumero(), contaParaExtrato.getUsuario().getNome(), contaParaExtrato.getTipoConta().toString(), contaParaExtrato.getUsuario().getCpf()); // cria um objeto dto para mostrar os dados da conta ao usuário
                vwTransacao.mostraDadosConta(contaDTO); // mostra os dados da conta ao usuário

                List<ExtratoDTO> extrato = new ArrayList<>(); // cria uma lista de objetos dto para mostrar os dados das transações da conta
                CabecalhoExtratoDTO cabecalhoExtrato = new CabecalhoExtratoDTO(contaParaExtrato.getTipoConta().toString(), toMoeda(contaParaExtrato.getSaldo()) ,toMoeda(contaParaExtrato.getSaldoDisponivel()), contaParaExtrato.particularidadeConta()); // cria um objeto dto para mostrar o cabeçalho do extrato
                List<Transacao> transacoes = contaParaExtrato.getTransacoes(); // recupera a lista de transações da conta
                for(Transacao transacao : transacoes){
                    extrato.add(new ExtratoDTO(transacao.getTipoTransacao().toString(), toMoeda(transacao.getValor()), toMoeda(transacao.getSaldoPosTransacao()), getDataStr(transacao.getData()))); // adiciona as informações das transações nos objetos dtos para serem apresentados ao usuário
                }

                if (extrato.isEmpty()) { // se não houver dados para o extrato
                    vwTransacao.mostraString("Não há movimentação para conta informada");
                }else{ //se houver dados para o extrato
                    vwTransacao.mostraExtrato(extrato, cabecalhoExtrato); // mostra o extrato para o usuário
                }
            }
        }
        vwTransacao.pulaVariasLinhas(); // pula algumas linhas
    }

    /*
     *Esse método retorana uma conta a partir da lista de contas DATA_BASE_CONTA, tendo como chave de busca uma String com o número de uma conta
     */
    private Conta getConta(String numeroConta){
        Conta contaBusca = null;

        for(Conta conta: this.dataBase.getContas()){
            if(conta.getNumero().equals(numeroConta)){ // se encotrou a conta
                contaBusca = conta;
                break; // interrompe a iteração
            }
        }

        return contaBusca;
    }

    /*
    * Esse método recebe um valor double e retorna uma string no formato de moeda
     */
    private String toMoeda(double valor){
        Locale brasil = new Locale("pt", "BR");

        NumberFormat nf = NumberFormat.getCurrencyInstance(brasil);
        return nf.format(valor);
    }

    /*
    * esse método recebe uma data e retorna uma string no formato dd/MM/yyyy
     */
    private String getDataStr(LocalDateTime data){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return data.format(formatter);
    }
}