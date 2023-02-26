package net.gnfe.bin.domain.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Serve para gerenciar a sessão das chamadas HTTP para os serviços rest (Controller).
 * A autenticação do usuário continua sendo feita via REALM, e essa tabela vai guardar a JSESSIONID para recuperar o usuário
 * nas próximas requisições após o login.
 *
 */
@Entity(name="SESSAO_HTTP_REQUEST")
public class SessaoHttpRequest extends net.gnfe.util.ddd.Entity {

	private Long id;
	private Date data;
	private String jsessionId;
	private boolean ativa;
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

	public void setData(Date data) {
		this.data = data;
	}

	@Column(name="DATA", nullable=false)
	public Date getData() {
		return data;
	}

	@Column(name="JSESSIONID")
	public String getJsessionId() {
		return jsessionId;
	}

	public void setJsessionId(String jsessionId) {
		this.jsessionId = jsessionId;
	}

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="USUARIO_ID")
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Column(name="ATIVA", nullable=false)
	public boolean getAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}
}
