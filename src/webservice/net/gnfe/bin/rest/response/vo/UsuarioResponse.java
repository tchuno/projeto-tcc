package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.rest.request.vo.SuperVo;


@ApiModel(value = "UsuarioResponse")
public class UsuarioResponse extends SuperVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "nome")
    private String nome;

    @ApiModelProperty(value = "email")
    private String email;

    @ApiModelProperty(value = "RoleGD")
    private RoleGNFE roleGD;

    public UsuarioResponse(){}

    public UsuarioResponse(Usuario usuario){
        if(usuario != null) {
            this.id = usuario.getId();
            this.nome = usuario.getNome();
            this.email = usuario.getEmail();
            this.roleGD = usuario.getRoleGNFE();
        }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}