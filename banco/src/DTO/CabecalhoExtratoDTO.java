package DTO;

/*
 * essa classe dto é responsável por trafegar os dados do cabeçalho do extrato
 * (o saldo é o saldo sem o cheque especial e o saldo disponível é o saldo + o cheque especial,
 * quando a conta for poupança, o saldo é o mesmo que o saldo disponível
 */
public record CabecalhoExtratoDTO(String tipoConta, String saldoConta, String saldoDisponivel, String particularidade) {
}
