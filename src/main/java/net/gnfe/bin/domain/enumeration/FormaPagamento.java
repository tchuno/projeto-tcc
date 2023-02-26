package net.gnfe.bin.domain.enumeration;

public enum FormaPagamento {

	DINHEIRO("01"),
	CHEQUE("02"),
	CARTAO_CREDITO("03"),
	CARTAO_DEBITO("04"),
	CREDITO_LOJA("05"),
	VALE_ALIMENTACAO("10"),
	VALE_REFEICAO("11"),
	VALE_PRESENTE("12"),
	VALE_COMBUSTIVEL("13"),
	OUTROS("99")
	;

	private String tipo;

	FormaPagamento(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}
}
