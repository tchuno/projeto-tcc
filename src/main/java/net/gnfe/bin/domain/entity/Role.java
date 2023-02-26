package net.gnfe.bin.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name = "ROLE")
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"USUARIO_ID", "NOME"}))
public class Role extends net.gnfe.util.ddd.Entity {

	private Long id;
	private String nome;

	private Usuario usuario;

	@Id
	@Override
	@Column(name="ID", unique=true, nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="USUARIO_ID", nullable=false)
	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Column(name="NOME", length=20, nullable=false)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	/** necessário para funcionar com a autenticação do tomcat*/
	@Deprecated
	@Column(name="LOGIN", length=20, nullable=false)
	public String getLogin() {
		return this.usuario.getLogin();
	}

	@Deprecated
	public void setLogin(String login) {}

	@Override
	public String toString() {
		return getNome() + "#" + getId();
	}
}
