package net.gnfe.faces.event;

import net.gnfe.bin.GNFEConstants;
import net.gnfe.util.faces.FacesUtil;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.gnfe.util.DummyUtils.sysout;

public class LogRequestPhaseListener implements PhaseListener {

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

	@Override
	public void beforePhase(PhaseEvent event) {

		FacesContext facesContext = event.getFacesContext();
		ExternalContext externalContext = facesContext.getExternalContext();

		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

		String queryString = request.getQueryString();
		if("logoff".equals(queryString)) {

			HttpSession session = request.getSession();
			session.invalidate();

			String contextPath = request.getContextPath();
			try {
				externalContext.redirect(contextPath);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}

			return;
		}

		// Quem esta acessando?
		String name = "";
		Principal principal = externalContext.getUserPrincipal();
		if (principal != null) {
			name = principal.getName();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", GNFEConstants.LOCALE_PT_BR);
		sysout(sdf.format(new Date()) + " : " + FacesUtil.getViewId() + " : " + name + (FacesUtil.isAjaxRequest() ? " : ajax" : ""));
	}

	@Override
	public void afterPhase(PhaseEvent event) { }
}
