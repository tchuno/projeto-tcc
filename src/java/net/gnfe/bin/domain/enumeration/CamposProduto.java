package net.gnfe.bin.domain.enumeration;

public enum CamposProduto {

	ID("ID"),
	COD("CODIGO"),
	DESC("DESCRICAO"),
	DESC_CURTA("DESCRICAO CURTA"),
	GTIN("GTIN/EAN"),
	CNM("CLASSIFICACAO FISCAL"),
	CST("ORIGEM"),
	CEST("CEST"),
	ESTOQUE("ESTOQUE"),
	UNIDADE_MED("UNIDADE"),
	PRECO("PRECO"),
	ESTOQUE_MIN("ESTOQUE MINIMO")
	;

	private String nome;

	CamposProduto(String s) {
		this.nome = s;
	}

	public String getNome() {
		return nome;
	}
}
