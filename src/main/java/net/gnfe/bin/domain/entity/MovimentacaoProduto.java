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
	private Orcamento orcamento;
	private Produto produto;
	private Integer quantidade;
	private boolean isEntrada;
	private MotivoMovimentacao motivoMovimentacao;
	private Integer estoqueAtual;
	private BigDecimal valorTotal;

	public MovimentacaoProduto(MovimentacaoProdutoVO vo) {
		this.data = vo.getData();
		this.orcamento = vo.getOrcamento();
		this.produto = vo.getProduto();
		this.quantidade = vo.getQuantidade();
		this.motivoMovimentacao = vo.getMotivoMovimentacao();
		this.isEntrada = vo.isEntrada();
		this.valorTotal = vo.getValorTotal();
	}

	public MovimentacaoProduto(MovimentacaoProduto movimentacaoProduto) {
		this.data = movimentacaoProduto.getData();
		this.orcamento = movimentacaoProduto.getOrcamento();
		this.produto = movimentacaoProduto.getProduto();
		this.quantidade = movimentacaoProduto.getQuantidade();
		this.motivoMovimentacao = movimentacaoProduto.getMotivoMovimentacao();
		this.isEntrada = movimentacaoProduto.isEntrada();
		this.valorTotal = movimentacaoProduto.getValorTotal();
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
	@JoinColumn(name="ORCAMENTO_ID")
	public Orcamento getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
	}

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PRODUTO_ID")
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
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

	@Column(name="ESTOQUE_ATUAL")
	public Integer getEstoqueAtual() {
		return estoqueAtual;
	}

	public void setEstoqueAtual(Integer estoqueAtual) {
		this.estoqueAtual = estoqueAtual;
	}

	@Column(name="VALOR_TOTAL")
	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
}