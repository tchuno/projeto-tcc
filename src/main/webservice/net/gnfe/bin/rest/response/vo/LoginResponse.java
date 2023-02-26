package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.SessaoHttpRequest;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;

import java.util.Date;

@ApiModel(value="LoginResponse")
public class LoginResponse{

    @ApiModelProperty(notes = "Nome do usuário logado")
    private String nome;

    @ApiModelProperty(notes = "Email do usuário logado")
    private String email;

    @ApiModelProperty(notes = "RoleGD")
    private RoleGNFE roleGNFE;

    @ApiModelProperty(value = "Data de expiração da Senha")
    private Date dataExpiracaoSenha;

    @ApiModelProperty(value = "Token de acesso")
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(SessaoHttpRequest sessaoHttpRequest) {
        Usuario usuario = sessaoHttpRequest.getUsuario();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.roleGNFE = usuario.getRoleGNFE();

        Date dataExpiracaoSenha = usuario.getDataExpiracaoSenha();
        if(dataExpiracaoSenha != null){
            this.dataExpiracaoSenha = dataExpiracaoSenha;
        }

        this.token = sessaoHttpRequest.getJsessionId();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RoleGNFE getRoleGNFE() {
        return roleGNFE;
    }

    public void setRoleGNFE(RoleGNFE roleGNFE) {
        this.roleGNFE = roleGNFE;
    }

    public Date getDataExpiracaoSenha() { return dataExpiracaoSenha; }

    public void setDataExpiracaoSenha(Date dataExpiracaoSenha) { this.dataExpiracaoSenha = dataExpiracaoSenha; }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}