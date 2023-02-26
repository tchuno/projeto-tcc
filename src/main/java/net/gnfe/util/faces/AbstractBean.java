package net.gnfe.util.faces;

import net.gnfe.bin.bean.MenuBean;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.Funcionalidade;
import net.gnfe.util.ddd.Entity;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.ddd.MessageKeyListException;
import net.gnfe.util.menu.Item;
import net.gnfe.util.menu.Menu;
import net.gnfe.util.other.LoginJwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import javax.faces.validator.ValidatorException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static net.gnfe.util.DummyUtils.systrace;

public abstract class AbstractBean implements Serializable {

	public static final String USUARIO_SESSION_KEY = "usuario_session_key";
	private static final String SECURITY_CHECK_KEY = "security_check_key";

	public AbstractBean() {

		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	public final void init() {

		initBean();
	}

	protected abstract void initBean();

	public boolean isSecurityOk() {

		Map<String, Object> viewMap = getViewMap();
		Object ok = viewMap.get(SECURITY_CHECK_KEY);
		return ok != null && (Boolean) ok;
	}

	@PostConstruct
	public void securityCheck() {

		Map<String, Object> viewMap = getViewMap();
		Object ok = viewMap.get(SECURITY_CHECK_KEY);
		if(ok != null) {
			return;
		}

		String viewId = FacesUtil.getViewId();
		if(viewId != null && (
				viewId.contains("/login.xhtml")
				|| viewId.contains("/trocar-senha.xhtml")
				|| viewId.contains("/cadastrar.xhtml")
				|| viewId.contains("/erro/")
				|| viewId.contains("/usuario/autenticar")
				)) {
			viewMap.put(SECURITY_CHECK_KEY, true);
			return;
		}

		Usuario usuario = getUsuarioLogado();
		if(usuario != null) {

			HttpServletRequest request = getRequest();
			String login = usuario.getLogin();
			boolean checkLogadoJwt = LoginJwtUtils.checkLogadoCookie(request, login);
			if(!checkLogadoJwt) {
				logoff();
				return;
			}

			if(usuario.isAdminRole() && "/admin/customizacoes.xhtml".equals(viewId)) {
				viewMap.put(SECURITY_CHECK_KEY, true);
				return;
			}

			Date agora = new Date();
			Date dataExpiracaoSenha = usuario.getDataExpiracaoSenha();
			if(dataExpiracaoSenha == null || dataExpiracaoSenha.before(agora)) {
				redirect("/trocar-senha/");
				return;
			}

			Menu menu = MenuBean.getMenuEstatico();
			Item activeItem = menu.getActiveItem();
			if(activeItem == null) {
				throw new RuntimeException("activeItem está null; talvez vc tenha esquecido de configurar <subItens> no menu.xml");
			}

			String id = activeItem.getId();

			if(StringUtils.isBlank(id)) {
				Item parent = activeItem.getParent();
				id = parent != null ? parent.getId() : null;
			}

			if(StringUtils.isNotBlank(id)) {

				Funcionalidade funcionalidade = Funcionalidade.valueOf(id);

				if(funcionalidade != null && !funcionalidade.podeAcessar(activeItem, usuario)) {

					systrace("usuário " + usuario.getLogin() + " tentou acessar " + funcionalidade + " e foi impedido. (Erro 403)");
					sendError(403);
					return;
				}
			}

			viewMap.put(SECURITY_CHECK_KEY, true);
		}
		else {

			systrace("usuário logado está null (Erro 403)");
			sendError(403);
			return;
		}
	}

	private void sendError(int errorCode) {

		HttpServletResponse response = getResponse();
		try {
			response.sendError(errorCode);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Map<String, Object> viewMap = getViewMap();
		viewMap.put(SECURITY_CHECK_KEY, false);
	}

	protected void addMessageError(Throwable e) {

		if(e instanceof MessageKeyException) {
			addMessageError((MessageKeyException) e);
			return;
		}
		else if(e instanceof MessageKeyListException) {
			List<MessageKeyException> messageKeyExceptions = ((MessageKeyListException) e).getMessageKeyExceptions();
			for (MessageKeyException mke : messageKeyExceptions) {
				addMessageError(mke);
			}
		}
		else if(e instanceof ValidatorException) {
			addMessageError((ValidatorException) e);
			return;
		}

		addMessageErrorInesperado(e);
	}

	private void addMessageErrorInesperado(Throwable e) {

		e.printStackTrace();

		String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);

		String message = getMessage("erroInesperado.error", rootCauseMessage);

		addFaceMessage(FacesMessage.SEVERITY_ERROR, message, null);
	}

	protected void addMessageError(MessageKeyException e) {

		try {
			String key = e.getKey();
			Object[] args = e.getArgs();

			String message = getMessage(key, args);

			addFaceMessage(FacesMessage.SEVERITY_ERROR, message, null);
		}
		catch (Exception e2) {
			e2.printStackTrace();
			addMessageErrorInesperado(e);
		}
	}

	protected void addMessageError(ValidatorException e) {

		try {
			FacesMessage facesMessage = e.getFacesMessage();
			addFaceMessage(facesMessage, null);
		}
		catch (Exception e2) {
			e2.printStackTrace();
			addMessageErrorInesperado(e);
		}
	}

	protected void addMessageWarn(String key) {
		addMessageWarnToComponent(key, null, (Object[]) null);
	}

	protected void addMessageWarn(String key, Object... args) {
		addMessageWarnToComponent(key, null, args);
	}

	protected void addMessageWarnToComponent(String key, String componentId) {
		addMessageWarnToComponent(key, componentId, (Object[]) null);
	}

	protected void addMessageWarnToComponent(String key, String componentId, Object... args) {
		String message = getMessage(key, args);
		addFaceMessage(FacesMessage.SEVERITY_WARN, message, componentId);
	}

	protected void addMessageError(String key) {
		addMessageErrorToComponent(key, null, (Object[]) null);
	}

	protected void addMessageError(String key, Object... args) {
		addMessageErrorToComponent(key, null, args);
	}

	protected void addMessageErrorToComponent(String key, String componentId) {
		addMessageErrorToComponent(key, componentId, (Object[]) null);
	}

	protected void addMessageErrorToComponent(String key, String componentId, Object... args) {
		String message = getMessage(key, args);
		addFaceMessage(FacesMessage.SEVERITY_ERROR, message, componentId);
	}

	protected String getMessage(String key) {
		return getMessage(key, (Object[]) null);
	}

	protected String getMessage(String key, Object... args) {

		ResourceBundle bundle = FacesUtil.getMessages();

		String message = bundle.getString(key);

		if(args != null) {
			message = MessageFormat.format(message, args);
		}

		return message;
	}

	protected void addMessage(String key) {
		addMessageToComponent(key, null);
	}

	protected void addMessage(String key, Object... args) {
		addMessageToComponent(key, null, args);
	}

	protected void addMessageToComponent(String key, String componentId, Object... args) {
		String message = getMessage(key, args);
		addFaceMessage(FacesMessage.SEVERITY_INFO, message, componentId);
	}

	protected void addFaceMessage(Severity severity, String message) {
		addFaceMessage(severity, message, null);
	}

	protected void addFaceMessage(Severity severity, String message, String componentId) {

		addFaceMessage(new FacesMessage(severity, message, null), componentId);
	}

	protected void addFaceMessage(FacesMessage facesMessage, String componentId) {

		FacesContext facesContext = getFacesContext();
		facesContext.addMessage(componentId, facesMessage);

		ExternalContext externalContext = getExternalContext();
		Flash flash = externalContext.getFlash();
		flash.setKeepMessages(true);
	}

	protected boolean isInsert(Entity entity) {
		Long id = entity.getId();
		return id == null;
	}

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	protected HttpServletResponse getResponse() {
		ExternalContext externalContext = getExternalContext();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		return response;
	}

	protected HttpServletRequest getRequest() {
		ExternalContext externalContext = getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		return request;
	}

	protected ServletContext getServletContext() {
		HttpServletRequest request = getRequest();
		ServletContext servletContext = request.getServletContext();
		return servletContext;
	}

	protected HttpSession getSession() {
		ExternalContext externalContext = getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(true);
		return session;
	}

	protected ExternalContext getExternalContext() {
		return getFacesContext().getExternalContext();
	}

	protected String getContextPath() {
		return getExternalContext().getRequestContextPath();
	}

	protected void redirect(String path) {

		ExternalContext externalContext = getExternalContext();
		try {
			externalContext.redirect(getContextPath() + path);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		Map<String, Object> viewMap = getViewMap();
		viewMap.put(SECURITY_CHECK_KEY, false);
	}

	protected String getPath() {
		HttpServletRequest request = getRequest();
		StringBuffer requestURL = request.getRequestURL();
		return requestURL.toString();
	}

	public Usuario getUsuarioLogado() {

		HttpSession session = getSession();
		Usuario usuario = (Usuario) session.getAttribute(USUARIO_SESSION_KEY);
		return usuario;
	}

	protected Map<String, Object> getViewMap() {

		FacesContext facesContext = getFacesContext();
		UIViewRoot viewRoot = facesContext.getViewRoot();
		Map<String, Object> viewMap = viewRoot.getViewMap();

		return viewMap;
	}

	protected void logoff() {
		LoginJwtUtils.invalidarCookie(getResponse());
		HttpSession session = getSession();
		session.invalidate();
		redirect("/");
	}

	protected void setRequestAttribute(String name, Object value) {
		HttpServletRequest request = getRequest();
		request.setAttribute(name, value);
	}
}
