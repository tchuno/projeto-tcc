package net.gnfe.bin.rest.request.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.enumeration.RoleGNFE;

@ApiModel(value = "RequestFiltroUsuario")
public class RequestFiltroUsuario extends SuperVo {

    @ApiModelProperty(notes = "Nome.")
    private String nome;

    @ApiModelProperty(notes = "Login.")
    private String login;

    @ApiModelProperty(notes = "RoleGD.")
    private RoleGNFE roleGD;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public RoleGNFE getRoleGD() {
        return roleGD;
    }

    public void setRoleGD(RoleGNFE roleGD) {
        this.roleGD = roleGD;
    }

}
