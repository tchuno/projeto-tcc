package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.NotaFiscal;
import net.gnfe.bin.domain.enumeration.StatusNotaFiscal;

import java.util.Date;


@ApiModel(value = "FiltroNotaFiscalResponse")
public class FiltroNotaFiscalResponse {

    @ApiModelProperty(notes = "ID.")
    private Long id;

    @ApiModelProperty(notes = "Data de Criação.")
    private Date dataCriacao;

    @ApiModelProperty(notes = "Data de Envio.")
    private Date dataEnvio;

    @ApiModelProperty(notes = "Status da Nota Fiscal.")
    private StatusNotaFiscal statusNotaFiscal;

    @ApiModelProperty(notes = "Chave de acesso para consultar..")
    private String chaveAcesso;

    @ApiModelProperty(notes = "Protocolo da emissão.")
    private String protocolo;

    @ApiModelProperty(notes = "XML Completo.")
    private String xml;

    @ApiModelProperty(notes = "Data de Cancelamento.")
    private Date dataCancelamento;

    @ApiModelProperty(notes = "Protocolo de Cancelamento.")
    private String protocoloCancelamento;

    @ApiModelProperty(notes = "XML Completo Cancelamento.")
    private String xmlCancelamento;

    public FiltroNotaFiscalResponse() {
    }

    public FiltroNotaFiscalResponse(NotaFiscal notaFiscal) {
        this.id = notaFiscal.getId();
        this.dataCriacao = notaFiscal.getDataCriacao();
        this.dataEnvio = notaFiscal.getDataEnvio();
        this.statusNotaFiscal = notaFiscal.getStatusNotaFiscal();
        this.chaveAcesso = notaFiscal.getChaveAcesso();
        this.protocolo = notaFiscal.getProtocolo();
        this.xml = notaFiscal.getXml();
        this.dataCancelamento = notaFiscal.getDataCancelamento();
        this.protocoloCancelamento = notaFiscal.getProtocoloCancelamento();
        this.xmlCancelamento = notaFiscal.getXmlCancelamento();
    }

    public Long getId() {
        return id;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public StatusNotaFiscal getStatusNotaFiscal() {
        return statusNotaFiscal;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public String getXml() {
        return xml;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public String getProtocoloCancelamento() {
        return protocoloCancelamento;
    }

    public String getXmlCancelamento() {
        return xmlCancelamento;
    }
}