package net.gnfe.bin.domain.vo.filtro;

import net.gnfe.bin.domain.enumeration.MotivoDesativacaoUsuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.enumeration.StatusUsuario;
import net.gnfe.util.DummyUtils;

import java.util.Date;
import java.util.List;

public class UsuarioFiltro implements Cloneable {

	private RoleGNFE roleGNFE;
	private StatusUsuario status;
	private String login;
	private String nome;
	private List<Long> ids;
	private Date dataAtualizacao;
	private MotivoDesativacaoUsuario motivoDesativacaoUsuario;
	private List<String> logins;
	private String cpfCnpj;
	private String endereco;
	private Integer numero;
	private String bairro;
	private String cep;
	private String cidade;
	private String estado;
	private String email;

	public StatusUsuario getStatus() {
		return status;
	}

	public void setStatus(StatusUsuario status) {
		this.status = status;
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public UsuarioFiltro clone() {
		try {
			return (UsuarioFiltro) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public MotivoDesativacaoUsuario getMotivoDesativacaoUsuario() {
		return motivoDesativacaoUsuario;
	}

	public void setMotivoDesativacaoUsuario(MotivoDesativacaoUsuario motivoDesativacaoUsuario) {
		this.motivoDesativacaoUsuario = motivoDesativacaoUsuario;
	}

	public List<String> getLogins() {
		return logins;
	}

	public void setLogins(List<String> logins) {
		this.logins = logins;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = DummyUtils.getCpfCnpjDesformatado(cpfCnpj);
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

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}