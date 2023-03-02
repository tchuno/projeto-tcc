package net.gnfe.bin.domain.enumeration;

public enum CamposProduto {

	ID("ID"),
	COD("CODIGO"),
	NOME("NOME"),
	DESC("DESCRICAO"),
	GTIN("GTIN"),
	NCM("NCM"),
	CEST("CEST"),
	CFOP("CFOP"),
	FORNECEDOR("FORNECEDOR ID"),
	ESTOQUE_ATUAL("ESTOQUE ATUAL"),
	UNIDADE_MEDIDA("UNIDADE MEDIDA"),
	VALOR_UNIDADE("VALOR UNIDADE"),
	VALOR_COMPRA("VALOR COMPRA"),
	REPOSICAO("TEMPO REPOSICAO (DIAS)"),
	ESTOQUE_MINIMO("ESTOQUE MINIMO"),
	ORIGEM_MERCADORIA("ORIGEM MERCADORIA"),
	ICMS("ALIQUOTA ICMS"),
	PIS("ALIQUOTA PIS"),
	COFINS("ALIQUOTA COFINS")
	;

	private String nome;

	CamposProduto(String s) {
		this.nome = s;
	}

	public String getNome() {
		return nome;
	}
}
