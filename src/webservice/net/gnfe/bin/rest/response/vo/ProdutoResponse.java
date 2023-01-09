package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.enumeration.UnidadeMedida;
import net.gnfe.util.DummyUtils;


@ApiModel(value = "ProdutoResponse")
public class ProdutoResponse {

    @ApiModelProperty(notes = "ID.")
    private Long id;

    @ApiModelProperty(notes = "Código.")
    private String cod;

    @ApiModelProperty(notes = "Nome.")
    private String nome;

    @ApiModelProperty(notes = "Descrição.")
    private String descricao;

    @ApiModelProperty(notes = "Total em estoque.")
    private Integer estoqueAtual;

    @ApiModelProperty(notes = "Unidade de Medida.")
    private UnidadeMedida unidadeMedida;

    @ApiModelProperty(notes = "Valor da Unidade.")
    private String valorUnidade;

    @ApiModelProperty(notes = "Nome da imagem do produto.")
    private String nomeImagem;

    @ApiModelProperty(notes = "Base 64 da Imagem do produto.")
    private String imagemBase64;

    public ProdutoResponse(Produto produto) {
        this.id = produto.getId();
        this.cod = produto.getCod();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.estoqueAtual = produto.getEstoqueAtual();
        this.unidadeMedida = produto.getUnidadeMedida();
        this.valorUnidade = DummyUtils.formatCurrency(produto.getValorUnidade());
        this.nomeImagem = produto.getNomeImagem();
        this.imagemBase64 = produto.getImagemBase64();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getEstoqueAtual() {
        return estoqueAtual;
    }

    public void setEstoqueAtual(Integer estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getValorUnidade() {
        return valorUnidade;
    }

    public void setValorUnidade(String valorUnidade) {
        this.valorUnidade = valorUnidade;
    }

    public String getNomeImagem() {
        return nomeImagem;
    }

    public void setNomeImagem(String nomeImagem) {
        this.nomeImagem = nomeImagem;
    }

    public String getImagemBase64() {
        return imagemBase64;
    }

    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
    }
}