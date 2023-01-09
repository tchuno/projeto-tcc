package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.Produto;


@ApiModel(value = "FiltroProdutoResponse")
public class FiltroProdutoResponse {

    @ApiModelProperty(notes = "ID.")
    private Long id;

    @ApiModelProperty(notes = "Quantidade.")
    private Integer quantidade;

    public FiltroProdutoResponse() {
    }

    public FiltroProdutoResponse(Produto produto, Integer quantidade) {
        this.id = produto.getId();
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}