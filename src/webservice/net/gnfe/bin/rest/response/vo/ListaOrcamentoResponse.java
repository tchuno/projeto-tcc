package net.gnfe.bin.rest.response.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;


@ApiModel(value = "ListaOrcamentoResponse")
public class ListaOrcamentoResponse {

    @ApiModelProperty(notes = "Lista de Orçamentos.")
    private List<OrcamentoResponse> orcamentos;

    public List<OrcamentoResponse> getOrcamentos() {
        return orcamentos;
    }

    public void setOrcamentos(List<OrcamentoResponse> orcamentos) {
        this.orcamentos = orcamentos;
    }

    public ListaOrcamentoResponse(List<OrcamentoResponse> orcamentos) {
        this.orcamentos = orcamentos;
    }
}