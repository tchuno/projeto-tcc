package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "ArquivoPDFResponse")
public class ArquivoPDFResponse {

    @ApiModelProperty(notes = "Nome do arquivo PDF.")
    private String nomeArquivo;

    @ApiModelProperty(notes = "Base64 do Arquivo PDF.")
    private String base64;

    @ApiModelProperty(notes = "Base64 do Arquivo XML. Comprovante da nota fiscal.")
    private String base64Xml;

    @ApiModelProperty(notes = "Base64 do Arquivo XML. Comprovante de cancelamento da nota fiscal.")
    private String base64XmlCancelamento;

    public ArquivoPDFResponse() {
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getBase64Xml() {
        return base64Xml;
    }

    public void setBase64Xml(String base64Xml) {
        this.base64Xml = base64Xml;
    }

    public String getBase64XmlCancelamento() {
        return base64XmlCancelamento;
    }

    public void setBase64XmlCancelamento(String base64XmlCancelamento) {
        this.base64XmlCancelamento = base64XmlCancelamento;
    }
}