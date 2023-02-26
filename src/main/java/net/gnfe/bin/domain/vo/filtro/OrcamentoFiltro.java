package net.gnfe.bin.domain.vo.filtro;

import net.gnfe.bin.domain.enumeration.FormaPagamento;
import net.gnfe.util.DummyUtils;
import org.primefaces.model.SortOrder;

import java.util.List;

public class OrcamentoFiltro implements Cloneable {

	private Long id;
	private List<Long> ids;
	private Long autorId;
	private List<Long> autorIds;
	private Long clienteId;
	private List<Long> clientIds;
	private FormaPagamento formaPagamento;
	private List<FormaPagamento> formaPagamentos;
	private String cpfCnpj;
	private String campoOrdem;
	private SortOrder ordem;

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

	public Long getAutorId() {
		return autorId;
	}

	public void setAutorId(Long autorId) {
		this.autorId = autorId;
	}

	public List<Long> getAutorIds() {
		return autorIds;
	}

	public void setAutorIds(List<Long> autorIds) {
		this.autorIds = autorIds;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}

	public List<Long> getClientIds() {
		return clientIds;
	}

	public void setClientIds(List<Long> clientIds) {
		this.clientIds = clientIds;
	}

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public List<FormaPagamento> getFormaPagamentos() {
		return formaPagamentos;
	}

	public void setFormaPagamentos(List<FormaPagamento> formaPagamentos) {
		this.formaPagamentos = formaPagamentos;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = DummyUtils.getCpfCnpjDesformatado(cpfCnpj);
	}

	public void setOrdenar(String campoOrdem, SortOrder ordem) {
		this.campoOrdem = campoOrdem;
		this.ordem = ordem;
	}

	public String getCampoOrdem() {
		return campoOrdem;
	}

	public void setCampoOrdem(String campoOrdem) {
		this.campoOrdem = campoOrdem;
	}

	public SortOrder getOrdem() {
		return ordem;
	}

	public void setOrdem(SortOrder ordem) {
		this.ordem = ordem;
	}
}