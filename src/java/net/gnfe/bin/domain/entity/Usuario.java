package net.gnfe.bin.domain.entity;

import net.gnfe.bin.domain.enumeration.MotivoBloqueioUsuario;
import net.gnfe.bin.domain.enumeration.MotivoDesativacaoUsuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.enumeration.StatusUsuario;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.other.Criptografia;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.*;

@Entity(name = "USUARIO")
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"LOGIN"}))
public class Usuario extends net.gnfe.util.ddd.Entity implements HttpSessionBindingListener {

	public static final String server = "gnfe";

	private static Map<Long, HttpSession> registroSessao = new HashMap<Long, HttpSession>();

	private Long id;
	private String nome;
	private String login;
	private String senha;
	private String email;
	private String telefone;
	private String cpfCnpj;
	private String rg;
	private String endereco;
	private Integer numero;
	private String bairro;
	private String cep;
	private String cidade;
	private String codIbge;
	private String estado;
	private StatusUsuario status;
	private Date dataExpiracaoSenha;
	private String senhasAnteriores;
	private Date dataCadastro;
	private Date dataUltimoAcesso;
	private MotivoBloqueioUsuario motivoBloqueio;
	private Date dataBloqueio;
	private Date dataExpiracaoBloqueio;
	private MotivoDesativacaoUsuario motivoDesativacao;
	private Date dataAtualizacao;
	private Usuario usuarioUltimaAtualizacao;

	private Set<Role> roles = new HashSet<Role>(0);

	private Runnable logoffListener;

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

	@Column(name="NOME", nullable=false, length=100)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name="SENHA", nullable=false, length=20)
	public String getSenha() {
		return this.senha;
	}

	public void setSenha(String senha) {
		this.senha =  Criptografia.encryptIfNot(Criptografia.GNFE, senha);
	}

	@Column(name="LOGIN", length=100)
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = StringUtils.lowerCase(login);
	}

	@Column(name="EMAIL", length=100)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name="TELEFONE", length=15)
	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	@Column(name="CPF_CNPJ", length=14)
	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = DummyUtils.getCpfCnpjDesformatado(cpfCnpj);
	}

	@Column(name="RG", length=15)
	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	@Column(name="ENDERECO", length=250)
	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	@Column(name="NUMERO")
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column(name="BAIRRO", length=150)
	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	@Column(name="CEP", length=10)
	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	@Column(name="CIDADE", length=150)
	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	@Column(name="COD_IBGE", length=10)
	public String getCodIbge() {
		return codIbge;
	}

	public void setCodIbge(String codIbge) {
		this.codIbge = codIbge;
	}

	@Column(name="ESTADO", length=150)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="STATUS", nullable=false)
	public StatusUsuario getStatus() {
		return status;
	}

	public void setStatus(StatusUsuario status) {
		this.status = status;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="MOTIVO_BLOQUEIO")
	public MotivoBloqueioUsuario getMotivoBloqueio() {
		return motivoBloqueio;
	}

	public void setMotivoBloqueio(MotivoBloqueioUsuario motivoBloqueio) {
		this.motivoBloqueio = motivoBloqueio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_EXPIRACAO_SENHA")
	public Date getDataExpiracaoSenha() {
		return dataExpiracaoSenha;
	}

	public void setDataExpiracaoSenha(Date dataExpiracaoSenha) {
		this.dataExpiracaoSenha = dataExpiracaoSenha;
	}

	@Column(name="SENHAS_ANTERIORES")
	public String getSenhasAnteriores() {
		return senhasAnteriores;
	}

	public void setSenhasAnteriores(String senhasAnteriores) {
		this.senhasAnteriores = senhasAnteriores;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO")
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_BLOQUEIO")
	public Date getDataBloqueio() {
		return dataBloqueio;
	}

	public void setDataBloqueio(Date dataBloqueio) {
		this.dataBloqueio = dataBloqueio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ULTIMO_ACESSO")
	public Date getDataUltimoAcesso() {
		return dataUltimoAcesso;
	}

	public void setDataUltimoAcesso(Date dataUltimoAcesso) {
		this.dataUltimoAcesso = dataUltimoAcesso;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_EXPIRACAO_BLOQUEIO")
	public Date getDataExpiracaoBloqueio() {
		return dataExpiracaoBloqueio;
	}

	public void setDataExpiracaoBloqueio(Date dataExpiracaoBloqueio) {
		this.dataExpiracaoBloqueio = dataExpiracaoBloqueio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ATUALIZACAO", nullable=false)
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="USUARIO_ULTIMA_ATUALIZACAO_ID")
	public Usuario getUsuarioUltimaAtualizacao() {
		return usuarioUltimaAtualizacao;
	}

	public void setUsuarioUltimaAtualizacao(Usuario usuarioUltimaAtualizacao) {
		this.usuarioUltimaAtualizacao = usuarioUltimaAtualizacao;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="MOTIVO_DESATIVACAO")
	public MotivoDesativacaoUsuario getMotivoDesativacao() {
		return motivoDesativacao;
	}

	public void setMotivoDesativacao(MotivoDesativacaoUsuario motivoDesativacao) {
		this.motivoDesativacao = motivoDesativacao;
	}

	@OneToMany(fetch=FetchType.EAGER, mappedBy="usuario", cascade=CascadeType.ALL, orphanRemoval=true)
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Transient
	public RoleGNFE getRoleGNFE() {
		return getRoleEnum(RoleGNFE.class);
	}

	@Transient
	public void setRoleGNFE(RoleGNFE roleGNFE) {
		setRole(RoleGNFE.class, roleGNFE);
	}

	@Transient
	private <T> T getRoleEnum(Class<T> clazz) {

		Set<Role> roles = getRoles();
		for (Role role : roles) {

			String nome = role.getNome();
			T enumValue = DummyUtils.getEnumValue(clazz.getSimpleName(), nome);
			if(enumValue != null) {
				return enumValue;
			}
		}

		return null;
	}

	@Transient
	private <T> void setRole(Class<T> class1, T roleEnum) {

		Role role = getRole(class1);
		if(roleEnum == null) {
			Set<Role> roles = getRoles();
			roles.remove(role);
		}
		else {
			if(role == null) {
				role = new Role();
				role.setUsuario(this);
				Set<Role> roles = getRoles();
				roles.add(role);
			}
			role.setNome(String.valueOf(roleEnum));
		}
	}

	@Transient
	private Role getRole(Class<?> clazz) {

		Set<Role> roles = getRoles();
		for (Role role : roles) {

			String nome = role.getNome();
			Object enumValue = DummyUtils.getEnumValue(clazz.getSimpleName(), nome);
			if(enumValue != null) {
				return role;
			}
		}

		return null;
	}

	@Override
	@Transient
	public void valueBound(HttpSessionBindingEvent event) {

		HttpSession session = event.getSession();

		HttpSession session2 = registroSessao.get(id);
		if(session2 != null && !session2.equals(session)) {
			session2.invalidate();
		}

		registroSessao.put(id, session);

		ServletContext servletContext = session.getServletContext();
		Map<String, Integer> count = getCountMap(servletContext);

		RoleGNFE roleAG = getRoleGNFE();
		String nomePerfil = roleAG != null ? roleAG.name() : null;

		if(nomePerfil != null) {
			synchronized (count) {

				Integer countAtual = count.get(nomePerfil);
				countAtual = countAtual != null ? countAtual : 0;
				countAtual = countAtual + 1;
				count.put(nomePerfil, countAtual);
			}
		}
	}

	@Override
	@Transient
	public void valueUnbound(HttpSessionBindingEvent event) {

		Long id = getId();
		registroSessao.remove(id);

		if(logoffListener != null) {
			try {
				logoffListener.run();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}

		ServletContext servletContext = event.getSession().getServletContext();
		Map<String, Integer> count = getCountMap(servletContext);

		RoleGNFE roleAG = getRoleGNFE();
		String nomePerfil = roleAG != null ? roleAG.name() : null;

		if(nomePerfil != null) {

			synchronized (count) {

				Integer countAtual = count.get(nomePerfil);
				countAtual = countAtual != null ? countAtual : 0;
				countAtual = countAtual - 1;
				countAtual = countAtual < 0 ? 0 : countAtual;
				count.put(nomePerfil, countAtual);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Integer> getCountMap(ServletContext servletContext) {

		Map<String, Integer> count = (Map<String, Integer>) servletContext.getAttribute("usuariosLogadosCount");

		if(count == null) {
			count = createCountMap(servletContext);
		}

		return count;
	}

	@SuppressWarnings("unchecked")
	private static synchronized Map<String, Integer> createCountMap(ServletContext servletContext) {

		Map<String, Integer> count = (Map<String, Integer>) servletContext.getAttribute("usuariosLogadosCount");

		if(count == null) {

			count = new HashMap<String, Integer>();
			servletContext.setAttribute("usuariosLogadosCount", count);
		}

		return count;
	}

	public void setLogoffListener(Runnable logoffListener) {
		this.logoffListener = logoffListener;
	}

	@Transient
	public boolean isAdminRole() {
		RoleGNFE roleGNFE = getRoleGNFE();
		return RoleGNFE.ADMIN.equals(roleGNFE);
	}

	@Transient
	public boolean isFuncionarioRole() {
		RoleGNFE roleGNFE = getRoleGNFE();
		return RoleGNFE.FUNCIONARIO.equals(roleGNFE);
	}

	@Transient
	public boolean isCondutorRole() {
		RoleGNFE roleGNFE = getRoleGNFE();
		return RoleGNFE.CLIENTE.equals(roleGNFE);
	}
}