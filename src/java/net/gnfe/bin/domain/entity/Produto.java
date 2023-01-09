package net.gnfe.bin.domain.entity;

import net.gnfe.bin.domain.enumeration.UnidadeMedida;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "PRODUTO")
@Table
public class Produto extends net.gnfe.util.ddd.Entity {

	private Long id;
	private String idProduto;
	private String cod;
	private String nome;
	private String descricao;
	private String gtin;
	private String cnm;
	private String cest;
	private String cfop;
	private Usuario fornecedor;
	private Integer estoqueAtual;
	private UnidadeMedida unidadeMedida;
	private BigDecimal valorUnidade;
	private Integer tempoReposicao;
	private Integer estoqueMinimo;
	private Integer origemMercadoria;
	private BigDecimal aliquotaICMS;
	private BigDecimal aliquotaPIS;
	private BigDecimal aliquotaCOFINS;
	private String nomeImagem;
	private String imagemBase64;

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

	@Column(name="ID_PRODUTO", length=100)
	public String getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(String idProduto) {
		this.idProduto = idProduto;
	}

	@Column(name="COD", length=100)
	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	@Column(name="NOME", nullable=false, length=300)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name="DESCRICAO", length=500)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name="GTIN", length=100)
	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	@Column(name="CNM", length=100)
	public String getCnm() {
		return cnm;
	}

	public void setCnm(String cnm) {
		this.cnm = cnm;
	}

	@Column(name="CEST", length=100)
	public String getCest() {
		return cest;
	}

	public void setCest(String cest) {
		this.cest = cest;
	}

	@Column(name="CFOP", length=100)
	public String getCfop() {
		return cfop;
	}

	public void setCfop(String cfop) {
		this.cfop = cfop;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FORNECEDOR_ID")
	public Usuario getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Usuario fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Column(name="ESTOQUE_ATUAL")
	public Integer getEstoqueAtual() {
		return estoqueAtual;
	}

	public void setEstoqueAtual(Integer estoqueAtual) {
		this.estoqueAtual = estoqueAtual;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="UNIDADE_MEDIDA")
	public UnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	@Column(name="VALOR_UNIDADE")
	public BigDecimal getValorUnidade() {
		return valorUnidade;
	}

	public void setValorUnidade(BigDecimal valorUnidade) {
		this.valorUnidade = valorUnidade;
	}

	@Column(name="TEMPO_REPOSICAO")
	public Integer getTempoReposicao() {
		return tempoReposicao;
	}

	public void setTempoReposicao(Integer tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}

	@Column(name="ESTOQUE_MINIMO")
	public Integer getEstoqueMinimo() {
		return estoqueMinimo;
	}

	public void setEstoqueMinimo(Integer estoqueMinimo) {
		this.estoqueMinimo = estoqueMinimo;
	}


	@Column(name="ORIGEM_MERCADORIA")
	public Integer getOrigemMercadoria() {
		return origemMercadoria;
	}

	public void setOrigemMercadoria(Integer origemMercadoria) {
		this.origemMercadoria = origemMercadoria;
	}

	@Column(name="ALIQUOTA_ICMS")
	public BigDecimal getAliquotaICMS() {
		return aliquotaICMS;
	}

	public void setAliquotaICMS(BigDecimal aliquotaICMS) {
		this.aliquotaICMS = aliquotaICMS;
	}

	@Column(name="ALIQUOTA_PIS")
	public BigDecimal getAliquotaPIS() {
		return aliquotaPIS;
	}

	public void setAliquotaPIS(BigDecimal aliquotaPIS) {
		this.aliquotaPIS = aliquotaPIS;
	}


	@Column(name="ALIQUOTA_COFINS")
	public BigDecimal getAliquotaCOFINS() {
		return aliquotaCOFINS;
	}

	public void setAliquotaCOFINS(BigDecimal getAliquotaCOFINS) {
		this.aliquotaCOFINS = getAliquotaCOFINS;
	}

	@Column(name="NOME_IMAGEM")
	public String getNomeImagem() {
		return nomeImagem;
	}

	public void setNomeImagem(String nomeImagem) {
		this.nomeImagem = nomeImagem;
	}

	@Column(name="IMAGEM_BASE64")
	public String getImagemBase64() {
		return imagemBase64;
	}

	public void setImagemBase64(String imagemBase64) {
		this.imagemBase64 = imagemBase64;
	}
}