package net.gnfe.bin.domain.entity;

import net.gnfe.bin.domain.enumeration.Bandeira;
import net.gnfe.bin.domain.enumeration.FormaPagamento;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "ORCAMENTO")
public class Orcamento extends net.gnfe.util.ddd.Entity {

	private Long id;
	private Usuario autor;
	private Usuario cliente;
	private FormaPagamento formaPagamento;
	private Bandeira bandeira;
	private NotaFiscal notaFiscal;

	private Set<OrcamentoProduto> orcamentoProdutos = new HashSet<OrcamentoProduto>(0);

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
		if(!Arrays.asList(FormaPagamento.CARTAO_CREDITO, FormaPagamento.CARTAO_DEBITO).contains(formaPagamento)) {
			this.bandeira = null;
		}
		this.formaPagamento = formaPagamento;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="BANDEIRA")
	public Bandeira getBandeira() {
		return bandeira;
	}

	public void setBandeira(Bandeira banddeira) {
		this.bandeira = banddeira;
	}

	@OneToOne(fetch=FetchType.LAZY, mappedBy="orcamento", cascade=CascadeType.ALL, orphanRemoval=true)
	public NotaFiscal getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(NotaFiscal notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="orcamento", cascade=CascadeType.ALL, orphanRemoval=true)
	public Set<OrcamentoProduto> getOrcamentoProdutos() {
		return orcamentoProdutos;
	}

	public void setOrcamentoProdutos(Set<OrcamentoProduto> orcamentoProdutos) {
		this.orcamentoProdutos = orcamentoProdutos;
	}

	@Transient
	public List<Produto> getProdutos() {
		return this.getOrcamentoProdutos().stream().map(OrcamentoProduto::getProduto).collect(Collectors.toList());
	}
}