package net.gnfe.bin.rest.config;

import net.gnfe.bin.domain.entity.SessaoHttpRequest;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.rest.exception.HTTP401Exception;
import net.gnfe.bin.rest.service.SessaoHttpRequestService;
import net.gnfe.util.faces.AbstractBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;

public class SuperInterceptorRestAdapter extends HandlerInterceptorAdapter {

    @Autowired private SessaoHttpRequestService sessaoHttpRequestService;

    /**
     * Lista de paths que não precisar ser validados pelo interceptor.
     */
    private final String[] PATH_ABERTO = new String[]{
            "swagger",
            "/login"
    };

    public SuperInterceptorRestAdapter() {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws HTTP401Exception {

        request.setAttribute("HORA_INICIO", System.currentTimeMillis());

        /** Só vai validar alguma coisa que seja diferente das excecões mapeadas. */

        if(request.getMethod().equals("OPTIONS")){
            return true;
        }

        for (String path : PATH_ABERTO) {
            if (StringUtils.contains(request.getPathInfo(), path)) {
                return true;
            }
        }

        HttpSession session = request.getSession();

        Usuario usuario = (Usuario) session.getAttribute(AbstractBean.USUARIO_SESSION_KEY);
        if(usuario != null) {
            String login = usuario.getLogin();
            Principal userPrincipal = request.getUserPrincipal();
            if(userPrincipal != null) {
                String userPrincipalName = userPrincipal.getName();
                if(login.equals(userPrincipalName)) {
                    return true;
                }
            }
        }

        SessaoHttpRequest sessaoHttpRequest = sessaoHttpRequestService.findByJSessionId(request);
        if (sessaoHttpRequest == null) {
            throw new HTTP401Exception("http401.exception");
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}