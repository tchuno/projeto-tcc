package net.gnfe.bin.domain.vo.filtro;

import java.util.List;

public class OrcamentoProdutoFiltro implements Cloneable {

	private Long id;
	private List<Long> ids;
	private Long orcamentoId;
	private List<Long> orcamentoIds;
	private Long produtoId;
	private List<Long> produtoIds;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public Long getOrcamentoId() {
		return orcamentoId;
	}

	public void setOrcamentoId(Long orcamentoId) {
		this.orcamentoId = orcamentoId;
	}

	public List<Long> getOrcamentoIds() {
		return orcamentoIds;
	}

	public void setOrcamentoIds(List<Long> orcamentoIds) {
		this.orcamentoIds = orcamentoIds;
	}

	public Long getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(Long produtoId) {
		this.produtoId = produtoId;
	}

	public List<Long> getProdutoIds() {
		return produtoIds;
	}

	public void setProdutoIds(List<Long> produtoIds) {
		this.produtoIds = produtoIds;
	}
}