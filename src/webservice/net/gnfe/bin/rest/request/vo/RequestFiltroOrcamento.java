package net.gnfe.bin.rest.request.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.enumeration.StatusNotaFiscal;

@ApiModel(value = "RequestFiltroOrcamento")
public class RequestFiltroOrcamento {

    @ApiModelProperty(notes = "ID.")
    private Long id;

    @ApiModelProperty(notes = "CPF do cliente.")
    private String cpfCliente;

    @ApiModelProperty(notes = "CPF do autor.")
    private String cpfAutor;

    @ApiModelProperty(notes = "Status da Nota Fiscal.")
    private StatusNotaFiscal statusNotaFiscal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    public String getCpfAutor() {
        return cpfAutor;
    }

    public void setCpfAutor(String cpfAutor) {
        this.cpfAutor = cpfAutor;
    }

    public StatusNotaFiscal getStatusNotaFiscal() {
        return statusNotaFiscal;
    }

    public void setStatusNotaFiscal(StatusNotaFiscal statusNotaFiscal) {
        this.statusNotaFiscal = statusNotaFiscal;
    }
}
