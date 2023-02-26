package net.gnfe.bin.rest.request.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.rest.annotations.NotNull;

/**
 *
 */
@ApiModel(value = "RequestCadastrarUsuario")
public class RequestCadastrarUsuario {

    @ApiModelProperty(notes = "CPF ou CNPJ.")
    private String cpfCnpj;

    @ApiModelProperty(notes = "Nome.")
    private String nome;

    @ApiModelProperty(notes = "Email.")
    private String email;

    @ApiModelProperty(notes = "Login.")
    private String login;

    @ApiModelProperty(notes = "RoleGD.")
    private RoleGNFE roleGD;

    @ApiModelProperty(notes = "Telefone.")
    private String telefone;

    @ApiModelProperty(notes = "CEP.")
    private String cep;

    @ApiModelProperty(notes = "Endereço.")
    private String endereco;

    @ApiModelProperty(notes = "Número endereço.")
    private Integer numero;

    @ApiModelProperty(notes = "Bairro.")
    private String bairro;

    @ApiModelProperty(notes = "Cidade.")
    private String cidade;

    @ApiModelProperty(notes = "Codigo IBGE da Cidade.")
    private String codigoIBGE;

    @ApiModelProperty(notes = "Estado (Max 2. Caracteres).")
    private String estado;

    public RequestCadastrarUsuario() {
    }

    @NotNull
    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @NotNull
    public RoleGNFE getRoleGD() {
        return roleGD;
    }

    public void setRoleGD(RoleGNFE roleGD) {
        this.roleGD = roleGD;
    }

    @NotNull
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @NotNull
    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    @NotNull
    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @NotNull
    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    @NotNull
    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    @NotNull
    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    @NotNull
    public String getCodigoIBGE() {
        return codigoIBGE;
    }

    public void setCodigoIBGE(String codigoIBGE) {
        this.codigoIBGE = codigoIBGE;
    }

    @NotNull
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
