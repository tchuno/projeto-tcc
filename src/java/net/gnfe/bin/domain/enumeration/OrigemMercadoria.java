package net.gnfe.bin.domain.enumeration;

public enum OrigemMercadoria {

	ZERO("0",  "0 - Nacional, exceto as indicadas nos códigos 3 a 5"),
	UM("1", "1 - Estrangeira - Importação direta, exceto a indicada no código 6"),
	DOIS("2", "2 - Estrangeira - Adquirida no mercado interno, exceto a indicada no código 7"),
	TRES("3", "3 - Nacional, mercadoria ou bem com Conteúdo de Importação superior a 40%"),
	QUATRO("4", "4 - Nacional, cuja produção tenha sido feita em conformidade com os processos produtivos básicos"),
	CINCO("5", "5 - Nacional, mercadoria ou bem com Conteúdo de Importação inferior ou igual a 40%"),
	SEIS("6", "6 - Estrangeira - Importação direta, sem similar nacional, constante em lista de Resolução Camex e gás natural"),
	SETE("7", "7 -Estrangeira - Adquirida no mercado interno, sem similar nacional, constante em lista de Resolução Camex e gás natural")
	;

	private final String tipo;
	private final String nome;

	OrigemMercadoria(String tipo, String nome) {
		this.tipo = tipo; 
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public String getNome() {
		return nome;
	}
}
