package net.gnfe.bin.rest.request.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.enumeration.Bandeira;
import net.gnfe.bin.domain.enumeration.FormaPagamento;
import net.gnfe.bin.rest.annotations.NotNull;
import net.gnfe.bin.rest.response.vo.FiltroProdutoResponse;

import java.util.List;

@ApiModel(value = "RequestCadastrarOrcamento")
public class RequestCadastrarOrcamento {

    @ApiModelProperty(notes = "CPF ou CNPJ.")
    private Long clienteId;

    @ApiModelProperty(notes = "Forma de Pagamento.")
    private FormaPagamento formaPagamento;

    @ApiModelProperty(notes = "Bandeira.")
    private Bandeira bandeira;

    @ApiModelProperty(notes = "Lista de Produtos.")
    private List<FiltroProdutoResponse> listaProdutoResponse;

    public RequestCadastrarOrcamento() {
    }

    @NotNull
    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    @NotNull
    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public Bandeira getBandeira() {
        return bandeira;
    }

    public void setBandeira(Bandeira bandeira) {
        this.bandeira = bandeira;
    }

    @NotNull
    public List<FiltroProdutoResponse> getListaProdutoResponse() {
        return listaProdutoResponse;
    }

    public void setListaProdutoResponse(List<FiltroProdutoResponse> listaProdutoResponse) {
        this.listaProdutoResponse = listaProdutoResponse;
    }
}
