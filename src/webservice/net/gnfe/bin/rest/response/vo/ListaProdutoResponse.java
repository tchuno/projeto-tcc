package net.gnfe.bin.rest.response.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "ListaProdutoResponse")
public class ListaProdutoResponse {

    @ApiModelProperty(notes = "Lista de Produtos.")
    private List<ProdutoResponse> produtos;

    public List<ProdutoResponse> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProdutoResponse> produtos) {
        this.produtos = produtos;
    }

    public ListaProdutoResponse(List<ProdutoResponse> produtos) {
        this.produtos = produtos;
    }
}