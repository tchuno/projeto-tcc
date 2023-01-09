package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.util.DummyUtils;


@ApiModel(value = "UsuarioResponse")
public class UsuarioResponse {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(notes = "CPF ou CNPJ.")
    private String cpfCnpj;

    @ApiModelProperty(notes = "Nome.")
    private String nome;

    @ApiModelProperty(notes = "Email.")
    private String email;

    @ApiModelProperty(notes = "Login.")
    private String login;

    @ApiModelProperty(notes = "RoleGD (ADMIN, FUNCIONARIO, FORNECEDOR ou CLIENTE).")
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

    public UsuarioResponse(){}

    public UsuarioResponse(Usuario usuario){
        if(usuario != null) {
            this.id = usuario.getId();
            this.cpfCnpj = DummyUtils.getCpfCnpj(usuario.getCpfCnpj());
            this.nome = usuario.getNome();
            this.email = usuario.getEmail();
            this.login = usuario.getLogin();
            this.roleGD = usuario.getRoleGNFE();
            this.telefone = usuario.getTelefone();
            this.cep = usuario.getCep();
            this.endereco = usuario.getEndereco();
            this.numero = usuario.getNumero();
            this.bairro = usuario.getBairro();
            this.cidade = usuario.getCidade();
            this.codigoIBGE = usuario.getCodIbge();
            this.estado = usuario.getEstado();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCodigoIBGE() {
        return codigoIBGE;
    }

    public void setCodigoIBGE(String codigoIBGE) {
        this.codigoIBGE = codigoIBGE;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}