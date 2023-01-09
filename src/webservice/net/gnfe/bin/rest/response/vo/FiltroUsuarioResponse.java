package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;


@ApiModel(value = "FiltroUsuarioResponse")
public class FiltroUsuarioResponse {

    @ApiModelProperty(notes = "ID.")
    private Long id;

    @ApiModelProperty(notes = "Nome.")
    private String nome;

    @ApiModelProperty(notes = "Cpf ou CNPJ.")
    private String cpfCnpj;

    @ApiModelProperty(notes = "RoleGNFE.")
    private RoleGNFE roleGNFE;

    public FiltroUsuarioResponse() {
    }

    public FiltroUsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.cpfCnpj = usuario.getCpfCnpj();
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

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public RoleGNFE getRoleGNFE() {
        return roleGNFE;
    }

    public void setRoleGNFE(RoleGNFE roleGNFE) {
        this.roleGNFE = roleGNFE;
    }
}