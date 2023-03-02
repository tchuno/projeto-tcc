package net.gnfe.bin.domain.entity;

import net.gnfe.bin.domain.enumeration.MotivoMovimentacao;
import net.gnfe.bin.domain.vo.MovimentacaoProdutoVO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "MOVIMENTACAO_PRODUTO")
@Table
public class MovimentacaoProduto extends net.gnfe.util.ddd.Entity {

	private Long id;
	private Date data;
	private OrcamentoProduto orcamentoProduto;
	private Produto produto;
	private Usuario fornecedor;
	private Integer quantidade;
	private Integer qtdEstoque;
	private BigDecimal valorTotal;
	private Usuario autor;
	private boolean isEntrada;
	private MotivoMovimentacao motivoMovimentacao;
	private BigDecimal valorIcms;
	private BigDecimal valorPis;
	private BigDecimal valorCofins;

	public MovimentacaoProduto(MovimentacaoProdutoVO vo) {
		this.data = vo.getData();
		this.orcamentoProduto = vo.getOrcamentoProduto();
		this.produto = vo.getProduto();
		this.fornecedor = vo.getFornecedor();
		this.quantidade = vo.getQuantidade();
		this.qtdEstoque = vo.getQtdEstoque();
		this.autor = vo.getAutor();
		this.motivoMovimentacao = vo.getMotivoMovimentacao();
		this.isEntrada = vo.isEntrada();
		if(vo.getTotalNotaFiscalVO() != null) {
			this.valorTotal = vo.getTotalNotaFiscalVO().getValorTotal();
			this.valorIcms = vo.getTotalNotaFiscalVO().getvICMS();
			this.valorPis = vo.getTotalNotaFiscalVO().getvPIS();
			this.valorCofins = vo.getTotalNotaFiscalVO().getvCOFINS();
		}
	}

	public MovimentacaoProduto(MovimentacaoProduto movimentacaoProduto) {
		this.data = movimentacaoProduto.getData();
		this.orcamentoProduto = movimentacaoProduto.getOrcamentoProduto();
		this.produto = movimentacaoProduto.getProduto();
		this.fornecedor = movimentacaoProduto.getFornecedor();
		this.quantidade = movimentacaoProduto.getQuantidade();
		this.qtdEstoque = movimentacaoProduto.getQtdEstoque();
		this.valorTotal = movimentacaoProduto.getValorTotal();
		this.autor = movimentacaoProduto.getAutor();
		this.isEntrada = movimentacaoProduto.isEntrada();
		this.motivoMovimentacao = movimentacaoProduto.getMotivoMovimentacao();
		this.valorIcms = movimentacaoProduto.getValorIcms();
		this.valorPis = movimentacaoProduto.getValorPis();
		this.valorCofins = movimentacaoProduto.getValorCofins();
	}

	public MovimentacaoProduto() {
	}

	@Id
	@Override
	@Column(name="ID", unique=true, nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)

	@Column(name="DATA")
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ORCAMENTO_PRODUTO_ID")
	public OrcamentoProduto getOrcamentoProduto() {
		return orcamentoProduto;
	}

	public void setOrcamentoProduto(OrcamentoProduto orcamento) {
		this.orcamentoProduto = orcamento;
	}

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PRODUTO_ID")
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}


	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FORNECEDOR_ID")
	public Usuario getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Usuario fornecedor) {
		this.fornecedor = fornecedor;
	}

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AUTOR_ID")
	public Usuario getAutor() {
		return autor;
	}

	public void setAutor(Usuario autor) {
		this.autor = autor;
	}

	@Column(name="QUANTIDADE")
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="MOTIVO_MOVIMENTACAO")
	public MotivoMovimentacao getMotivoMovimentacao() {
		return motivoMovimentacao;
	}

	public void setMotivoMovimentacao(MotivoMovimentacao motivoMovimentacao) {
		this.motivoMovimentacao = motivoMovimentacao;
	}

	@Column(name="IS_ENTRADA")
	public boolean isEntrada() {
		return isEntrada;
	}

	public void setEntrada(boolean entrada) {
		this.isEntrada = entrada;
	}

	@Column(name="QTD_ESTOQUE")
	public Integer getQtdEstoque() {
		return qtdEstoque;
	}

	public void setQtdEstoque(Integer estoqueAtual) {
		this.qtdEstoque = estoqueAtual;
	}

	@Column(name="VALOR_TOTAL")
	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	@Column(name="VALOR_ICMS")
	public BigDecimal getValorIcms() {
		return valorIcms;
	}

	public void setValorIcms(BigDecimal valorIcms) {
		this.valorIcms = valorIcms;
	}

	@Column(name="VALOR_PIS")
	public BigDecimal getValorPis() {
		return valorPis;
	}

	public void setValorPis(BigDecimal valorPis) {
		this.valorPis = valorPis;
	}

	@Column(name="VALOR_COFINS")
	public BigDecimal getValorCofins() {
		return valorCofins;
	}

	public void setValorCofins(BigDecimal valorCofins) {
		this.valorCofins = valorCofins;
	}
}