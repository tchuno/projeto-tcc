package net.gnfe.bin.rest.request.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "RequestFiltroUsuario")
public class RequestFiltroProduto {

    @ApiModelProperty(notes = "Código do produto.")
    private String cod;

    @ApiModelProperty(notes = "Nome do Produto.")
    private String nome;

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
}
