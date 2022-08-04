package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.rest.request.vo.SuperVo;


@ApiModel(value = "FiltroUsuarioResponse")
public class FiltroUsuarioResponse extends SuperVo {

    @ApiModelProperty(notes = "ID.")
    private Long id;

    @ApiModelProperty(notes = "Nome.")
    private String nome;

    @ApiModelProperty(notes = "Login.")
    private String login;

    @ApiModelProperty(notes = "RoleGNFE.")
    private RoleGNFE roleGNFE;

    @ApiModelProperty(notes = "Nome do Subperfil.")
    private String subperfil;

    @ApiModelProperty(notes = "Área.")
    private String area;

    public FiltroUsuarioResponse() {
    }

    public FiltroUsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.login = usuario.getLogin();
        this.roleGNFE = usuario.getRoleGNFE();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public RoleGNFE getRoleGNFE() {
        return roleGNFE;
    }

    public void setRoleGNFE(RoleGNFE roleGNFE) {
        this.roleGNFE = roleGNFE;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}