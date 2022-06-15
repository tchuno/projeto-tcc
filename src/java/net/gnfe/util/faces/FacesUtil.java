package net.gnfe.util.faces;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.servlet.ServletContext;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class FacesUtil {

	public static void addMsgInfo(String mensagem){

		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem, mensagem);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, facesMessage);
	}

	public static void addMsgError(String mensagem){

		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, facesMessage);
	}

	public static String getParam(String nome){

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String , String> parametros = externalContext.getRequestParameterMap();
		String valor = parametros.get(nome);
		return valor;
	}

	public static boolean isAjaxRequest() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		PartialViewContext partialViewContext = facesContext.getPartialViewContext();
		return partialViewContext.isAjaxRequest();
	}

	public static String getViewId() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIViewRoot viewRoot = facesContext.getViewRoot();
		return viewRoot.getViewId();
	}

	public static ServletContext getServletContext() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		ServletContext servletContext = (ServletContext) externalContext.getContext();

		return servletContext;
	}

	public static ResourceBundle getMessages() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIViewRoot viewRoot = facesContext.getViewRoot();
		Locale locale = viewRoot.getLocale();
		Thread currentThread = Thread.currentThread();
		ClassLoader loader = currentThread.getContextClassLoader();
		ResourceBundle bundle = ResourceBundle.getBundle("net.bin.gnfe.messages.msg", locale, loader);

		return bundle;
	}
}
