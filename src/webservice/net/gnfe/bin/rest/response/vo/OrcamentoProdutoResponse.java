package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.Produto;


@ApiModel(value = "ProdutoResponse")
public class OrcamentoProdutoResponse extends ProdutoResponse {

    @ApiModelProperty(notes = "Quantidade.")
    private Integer quantidade;

    public OrcamentoProdutoResponse(Produto produto, Integer quantidade) {
        super(produto);
        this.quantidade = quantidade;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}