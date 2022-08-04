package net.gnfe.bin.rest.service;


import net.gnfe.bin.domain.entity.SessaoHttpRequest;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.repository.SessaoHttpRequestRepository;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.rest.exception.HTTP401Exception;
import net.gnfe.bin.rest.request.vo.RequestLogin;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.faces.AbstractBean;
import net.gnfe.util.other.Criptografia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Date;

@Service
public class SessaoHttpRequestService extends SuperServiceRest {

	@Autowired private SessaoHttpRequestRepository sessaoHttpRequestRepository;
	@Autowired private UsuarioService usuarioService;

	public SessaoHttpRequest get(Long id) {
		return sessaoHttpRequestRepository.get(id);
	}

	@Transactional(rollbackFor=Exception.class)
	public SessaoHttpRequest criaSessao(HttpServletRequest httpServletRequest, Usuario usuario) throws MessageKeyException {
		SessaoHttpRequest sessaoHttpRequest = new SessaoHttpRequest();
		sessaoHttpRequest.setAtiva(true);
		sessaoHttpRequest.setData(new Date());
		sessaoHttpRequest.setJsessionId(httpServletRequest.getSession().getId());
		sessaoHttpRequest.setUsuario(usuario);
		sessaoHttpRequestRepository.saveOrUpdate(sessaoHttpRequest);
		return sessaoHttpRequest;
	}

	@Transactional(rollbackFor=Exception.class)
	public void invalidaSessoesAnteriores(Usuario usuario) {
		sessaoHttpRequestRepository.mataSessaoUsuario(usuario);
	}

	/**
	 * Recupera uma sessão salva no banco através do JSESSIONID obtido o login.
	 * @param request
	 * @return
	 */
	public SessaoHttpRequest findByJSessionId(HttpServletRequest request) throws HTTP401Exception {

		String sessionId = request.getRequestedSessionId();

		if (!request.isRequestedSessionIdValid() || !request.isRequestedSessionIdFromCookie()) {
			throw new HTTP401Exception("http401.exception");
		}

		return sessaoHttpRequestRepository.findByJSessionId(sessionId);
	}

	/**
	 * Faz login no próprio container web através do Login e Senha do usuário.
	 * @param request
	 * @param requestLogin
	 * @return
	 * @throws ServletException
	 * @throws MessageKeyException
	 */
	public SessaoHttpRequest login(HttpServletRequest request, RequestLogin requestLogin) throws Exception {

		HttpSession session = request.getSession();
		try {

			Principal userPrincipal = request.getUserPrincipal();
			Usuario usuario = (Usuario) session.getAttribute(AbstractBean.USUARIO_SESSION_KEY);
			if(userPrincipal == null || usuario == null) {

				//Faz o login no container web.
				String login = requestLogin.getLogin();
				try {
					String senha = requestLogin.getSenha();
					senha = Criptografia.encrypt(Criptografia.GNFE, senha);
					request.login(login, senha);
				} catch (ServletException e) {
					e.printStackTrace();
					throw new HTTP401Exception("http401.loginSenha.exception");
				}

				usuario = usuarioService.getByLogin(login);

				session.setAttribute(AbstractBean.USUARIO_SESSION_KEY, usuario);
				session.setMaxInactiveInterval(15 * 60);
			}

			return criaSessao(request, usuario);
		}
		catch (Exception e) {
			session.invalidate();
			throw e;
		}
	}

	public int excluir(Date dataCorte) {
		return sessaoHttpRequestRepository.excluir(dataCorte);
	}
}
