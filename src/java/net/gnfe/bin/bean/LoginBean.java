package net.gnfe.bin.bean;

import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.faces.AbstractBean;
import net.gnfe.util.other.Criptografia;
import net.gnfe.util.other.LoginJwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@ManagedBean
@ViewScoped
public class LoginBean extends AbstractBean {

	private static final String TENTATIVAS_INCORRETAS_LOGIN = "tentativas_incorretas_login";

	@Autowired private UsuarioService usuarioService;

	private String login;
	private String senha;

	public void securityCheck() { }

	protected void initBean() {

		HttpServletRequest request = getRequest();
		String login = LoginJwtUtils.checkLogadoCookie(request);
		if(StringUtils.isNotBlank(login)) {
			Usuario usuarioLogando = usuarioService.getByLogin(login);
			if(usuarioLogando != null) {
				login(usuarioLogando);
			}
		}
	}

	public void login() {
		login(null);
	}

	public void login(Usuario usuario) {

		String login, senha, senhaCrypt;
		if(usuario != null) {
			login = usuario.getLogin();
			senhaCrypt = usuario.getSenha();
		} else {
			login = this.login;
			senhaCrypt = Criptografia.encrypt(Criptografia.GNFE, this.senha);
		}

		HttpServletRequest request = getRequest();
		HttpSession session = getSession();

		try {
			request.login(login, senhaCrypt);
		}
		catch (ServletException e) {

			addMessageError("usuarioSenhaInvalido.error");

			Integer tentativas = getTentativas();

			tentativas++;

			if(tentativas >= 5) {

				try {
					usuarioService.bloquear(login);
					tentativas = 0;

					addMessageError("acessoBloqueadoTemporariamente.error");
				}
				catch (MessageKeyException e2) {
					addMessageError(e2);
				}
			}

			registrarTentativa(tentativas);

			return;
		}

		try {
			Usuario usuarioLogado = usuarioService.login(login);

			if(usuarioLogado == null) {
				throw new RuntimeException("Login feito, mas usuarioService.login() retornou null.");
			}

			registrarTentativa(0);

			session.setAttribute(USUARIO_SESSION_KEY, usuarioLogado);

			LoginJwtUtils.criarCookie(usuarioLogado, getResponse());

			ExternalContext externalContext = getExternalContext();
			externalContext.redirect(getContextPath() + "/cadastros/usuarios/");
		}
		catch (Exception e) {

			session.invalidate();
			addMessageError(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Integer getTentativas() {

		ServletContext servletContext = getServletContext();
		Map<String, Integer> map = (Map<String, Integer>) servletContext.getAttribute(TENTATIVAS_INCORRETAS_LOGIN);
		if(map == null) {
			map = new HashMap<String, Integer>();
			servletContext.setAttribute(TENTATIVAS_INCORRETAS_LOGIN, map);
		}
		Integer tentativas = map.get(login);
		tentativas = tentativas != null ? tentativas : 0;

		return tentativas;
	}

	@SuppressWarnings("unchecked")
	private void registrarTentativa(int num) {

		ServletContext servletContext = getServletContext();
		Map<String, Integer> map = (Map<String, Integer>) servletContext.getAttribute(TENTATIVAS_INCORRETAS_LOGIN);
		if(map != null) {
			map.put(login, num);
		}
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String email) {
		this.login = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void invalidarSessaoSeDeslogado() {

		Usuario usuario = getUsuarioLogado();
		if(usuario == null) {
			HttpSession session = getSession();
			session.invalidate();
		}
	}
}
