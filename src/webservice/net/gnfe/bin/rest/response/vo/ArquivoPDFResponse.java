package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "ArquivoPDFResponse")
public class ArquivoPDFResponse {

    @ApiModelProperty(notes = "Nome do arquivo PDF.")
    private String nomeArquivo;

    @ApiModelProperty(notes = "Base64 do Arquivo PDF.")
    private String base64;

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
}