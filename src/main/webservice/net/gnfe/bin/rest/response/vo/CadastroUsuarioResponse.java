package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.MotivoBloqueioUsuario;
import net.gnfe.bin.domain.enumeration.StatusUsuario;

import java.util.Date;


@ApiModel(value = "CadastroUsuarioResponse")
public class CadastroUsuarioResponse extends net.gnfe.bin.rest.response.vo.UsuarioResponse {

    @ApiModelProperty(value = "Status")
    private StatusUsuario status;

    @ApiModelProperty(value = "Data do cadastro")
    private Date dataCadastro;

    @ApiModelProperty(value = "Data do último acesso")
    private Date dataUltimoAcesso;

    @ApiModelProperty(value = "Data do último acesso")
    private Date dataBloqueio;

    @ApiModelProperty(value = "Data expiração bloqueio")
    private Date dataExpiracaoBloqueio;

    @ApiModelProperty(notes = "Login do usuario.")
    private String login;

    @ApiModelProperty(notes = "Telefone.")
    private String telefone;

    @ApiModelProperty(notes = "Motivo do bloqueio.")
    private MotivoBloqueioUsuario motivoBloqueio;

    @ApiModelProperty(notes = "Motivo Desativação.")
    private MotivoDesativarUsuarioResponse motivoDesativacao;


    public CadastroUsuarioResponse(){}

    public CadastroUsuarioResponse(Usuario usuario){
        super(usuario);
        if(usuario != null) {
            this.telefone = usuario.getTelefone();
            this.login = usuario.getLogin();
            this.status = usuario.getStatus();
            this.dataCadastro = usuario.getDataCadastro();
            this.dataUltimoAcesso = usuario.getDataUltimoAcesso();
            this.dataBloqueio = usuario.getDataBloqueio();
            this.dataExpiracaoBloqueio = usuario.getDataExpiracaoBloqueio();
            this.motivoBloqueio = usuario.getMotivoBloqueio();
            this.motivoDesativacao = new MotivoDesativarUsuarioResponse(usuario.getMotivoDesativacao(), null);
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getDataUltimoAcesso() {
        return dataUltimoAcesso;
    }

    public void setDataUltimoAcesso(Date dataUltimoAcesso) {
        this.dataUltimoAcesso = dataUltimoAcesso;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public Date getDataExpiracaoBloqueio() {
        return dataExpiracaoBloqueio;
    }

    public void setDataExpiracaoBloqueio(Date dataExpiracaoBloqueio) {
        this.dataExpiracaoBloqueio = dataExpiracaoBloqueio;
    }

    public MotivoBloqueioUsuario getMotivoBloqueio() {
        return motivoBloqueio;
    }

    public void setMotivoBloqueio(MotivoBloqueioUsuario motivoBloqueio) {
        this.motivoBloqueio = motivoBloqueio;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public MotivoDesativarUsuarioResponse getMotivoDesativacao() {
        return motivoDesativacao;
    }

    public void setMotivoDesativacao(MotivoDesativarUsuarioResponse motivoDesativacao) {
        this.motivoDesativacao = motivoDesativacao;
    }
}