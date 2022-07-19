package net.gnfe.bin.domain.entity;

import net.gnfe.bin.domain.enumeration.FormaPagamento;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "ORCAMENTO")
public class Orcamento extends net.gnfe.util.ddd.Entity {

	private Long id;
	private Usuario autor;
	private Usuario cliente;
	private FormaPagamento formaPagamento;

	private Set<OrcamentoProduto> produtos = new HashSet<OrcamentoProduto>(0);

	@Id
	@Column(name="ID", unique=true, nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AUTOR_ID")
	public Usuario getAutor() {
		return autor;
	}

	public void setAutor(Usuario autor) {
		this.autor = autor;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CLIENTE_ID")
	public Usuario getCliente() {
		return cliente;
	}

	public void setCliente(Usuario cliente) {
		this.cliente = cliente;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="FORMA_PAGAMENTO")
	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="orcamento", cascade=CascadeType.ALL, orphanRemoval=true)
	public Set<OrcamentoProduto> getProdutos() {
		return produtos;
	}

	public void setProdutos(Set<OrcamentoProduto> produtos) {
		this.produtos = produtos;
	}
}