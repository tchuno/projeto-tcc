package net.gnfe.bin.domain.entity;

import net.gnfe.bin.domain.enumeration.StatusNotaFiscal;

import javax.persistence.*;
import java.util.Arrays;

@Entity(name = "ORCAMENTO_PRODUTO")
public class OrcamentoProduto extends net.gnfe.util.ddd.Entity {

	private Long id;

	private Orcamento orcamento;
	private Produto produto;
	private Integer quantidade = 1;

	@Id
	@Override
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORCAMENTO_ID", nullable = false)
	public Orcamento getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUTO_ID", nullable = false)
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	@Column(name="QUANTIDADE", nullable=false)
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		if(quantidade == null || quantidade == 0) {
			quantidade = 1;
		}

		if(produto != null && (!Arrays.asList(StatusNotaFiscal.CONCLUIDO, StatusNotaFiscal.CANCELADO).contains(orcamento.getNotaFiscal().getStatusNotaFiscal()))) {
			Integer estoqueAtual = produto.getEstoqueAtual();
			if (estoqueAtual != null && quantidade > estoqueAtual) {
				quantidade = estoqueAtual;
			}
		}

		this.quantidade = quantidade;
	}
}
