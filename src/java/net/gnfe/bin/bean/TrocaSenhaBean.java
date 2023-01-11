package net.gnfe.bin.bean;

import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.util.faces.AbstractBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.servlet.http.HttpSession;
import java.util.Date;

@ManagedBean
@ViewScoped
public class TrocaSenhaBean extends AbstractBean {

	@Autowired private UsuarioService usuarioService;

	private String key;
	private String login;
	private String novaSenha;
	private String senhaConfirm;

	public void securityCheck() { }

	protected void initBean() {

		if(StringUtils.isNotBlank(key)) {

			String[] decode = UsuarioService.decodeLogin(key);

			if(decode == null) {
				addMessageError("esqueciSenhaLinkInvalido.error");
				return;
			}

			String login = decode[0];
			String senha = decode[1];

			Usuario usuario = usuarioService.getByLogin(login);
			String senha2 = usuario.getSenha();

			if(!senha2.equals(senha)) {
				addMessageError("esqueciSenhaLinkInvalido.error");
				return;
			}

			this.login = login;
		}

		Usuario usuario = getUsuarioLogado();
		if(usuario != null) {

			this.login = usuario.getLogin();

			Date dataExpiracaoSenha = usuario.getDataExpiracaoSenha();
			Date agora = new Date();
			if(dataExpiracaoSenha == null || dataExpiracaoSenha.before(agora)) {

				addMessage("senhasExpiraca.error");
			}
		}
	}

	public void cancelar() {

		Usuario usuario = getUsuarioLogado();
		if(usuario != null) {
			Date agora = new Date();
			Date dataExpiracaoSenha = usuario.getDataExpiracaoSenha();
			if(dataExpiracaoSenha == null || dataExpiracaoSenha.before(agora)) {
				HttpSession session = getSession();
				session.invalidate();
			}

			if(usuario.isAdminRole()) {
				redirect("/cadastros/usuarios/");
				return;
			} else if (usuario.isFuncionarioRole()) {
				redirect("/cadastros/orcamentos/");
				return;
			} else {
				redirect( "/cadastros/orcamentos/");
				return;
			}
		}

		redirect("/index.xhtml");
	}

	public void salvarNovaSenha() {

		if(!StringUtils.equals(novaSenha, senhaConfirm)) {

			addMessageError("senhasInconsistentes.error");
			return;
		}

		try {
			usuarioService.atualizarSenhaLocal(login, novaSenha);

			Usuario usuario = usuarioService.getByLogin(login);
			HttpSession session = getSession();
			session.setAttribute(USUARIO_SESSION_KEY, usuario);

			addMessage("senhaAlterada.sucesso");

			Usuario usuarioLogado = getUsuarioLogado();
			if(usuarioLogado.isAdminRole()) {
				redirect( "/cadastros/usuarios/");
			} else if (usuarioLogado.isFuncionarioRole()) {
				redirect("/cadastros/orcamentos/");
			} else {
				redirect( "/cadastros/orcamentos/");
			}
		}
		catch (Exception e) {
			addMessageError(e);
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getSenhaConfirm() {
		return senhaConfirm;
	}

	public void setSenhaConfirm(String senhaConfirm) {
		this.senhaConfirm = senhaConfirm;
	}
}
