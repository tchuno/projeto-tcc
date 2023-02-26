package net.gnfe.util.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.gnfe.bin.domain.service.MessageService;
import net.gnfe.util.rest.dto.ErrorDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestWsHandlerInterceptorAdapter extends HandlerInterceptorAdapter {

	@Autowired private MessageService messageService;

	public RestWsHandlerInterceptorAdapter() {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		String uri = request.getRequestURI();
		if (StringUtils.contains(uri, "/rest/") && handler instanceof HandlerMethod) {

			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Object bean = handlerMethod.getBean();

			if (bean instanceof AbstractController) {

				AbstractController ac = (AbstractController) bean;
				if(!ac.ifSecurityOk()) {

					ErrorDTO ErrorDTO = new ErrorDTO();
					HttpStatus status = HttpStatus.UNAUTHORIZED;
					ErrorDTO.status = status;
					String msg = messageService.getValue("falhaAutenticacao.error");
					ErrorDTO.addMessage(msg);

					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					response.setStatus(status.value());

					ServletOutputStream outputStream = response.getOutputStream();
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.writeValue(outputStream, ErrorDTO);

					return false;
				}
			}
		}

		return super.preHandle(request, response, handler);
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