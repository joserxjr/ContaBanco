package DTO;

/*
* Essa clase dto é responsável por trafegar os dados do extrato
 */
public record ExtratoDTO(String tipoTransacao, String valor, String saldo, String data) {
}
