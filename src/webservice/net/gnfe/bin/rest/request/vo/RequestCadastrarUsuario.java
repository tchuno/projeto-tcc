package net.gnfe.bin.rest.request.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.rest.annotations.NotNull;

/**
 *
 */
@ApiModel(value = "RequestCadastrarUsuario")
public class RequestCadastrarUsuario extends SuperVo {

    @ApiModelProperty(notes = "Nome.")
    private String nome;

    @ApiModelProperty(notes = "Login.")
    private String login;

    @ApiModelProperty(notes = "E-mail.")
    private String email;

    @ApiModelProperty(notes = "RoleGNFE.")
    private RoleGNFE roleGD;

    @ApiModelProperty(notes = "Telefone.")
    private String telefone;

    @NotNull
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    public RoleGNFE getRoleGD() {
        return roleGD;
    }

    public void setRoleGD(RoleGNFE roleGD) {
        this.roleGD = roleGD;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
