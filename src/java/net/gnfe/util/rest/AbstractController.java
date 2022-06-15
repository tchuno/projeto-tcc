package net.gnfe.util.rest;

import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.service.MessageService;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.faces.AbstractBean;
import net.gnfe.util.other.Criptografia;
import net.gnfe.util.rest.dto.ErrorDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

public abstract class AbstractController {

	@Autowired protected HttpServletRequest request;
	@Autowired private MessageService messageService;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDTO> handleException(Exception e) {

		String message = null;
		if(e instanceof MessageKeyException) {
			MessageKeyException mke = (MessageKeyException) e;
			String key = mke.getKey();
			Object[] args = mke.getArgs();
			message = messageService.getValue(key, args);
		}
		else {
			e.printStackTrace();
			message = ExceptionUtils.getRootCauseMessage(e);
		}

		ErrorDTO errorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST);
		errorDTO.addMessage(message);
		return new ResponseEntity<ErrorDTO>(errorDTO, errorDTO.status);
	}

	public boolean ifSecurityOk() {

		String xAccessTokenEncrypt = request.getHeader("x-access-token-encrypt");
		
		boolean securityOk = false;
		if(StringUtils.isNotBlank(xAccessTokenEncrypt)) {
			String decrypt = Criptografia.decrypt(Criptografia.GNFE, xAccessTokenEncrypt);
			Date dataRecebida = DummyUtils.parseDateTime2(decrypt);
			
			Date agora = new Date();
			securityOk = DateUtils.addHours(agora, -2).before(dataRecebida) && DateUtils.addHours(agora, 2).after(dataRecebida);
		}

		if(!securityOk) {
			HttpSession session = request.getSession();
			Usuario usuarioLogado = (Usuario) session.getAttribute(AbstractBean.USUARIO_SESSION_KEY);
			securityOk = usuarioLogado != null && usuarioLogado.isAdminRole();
		}

		return securityOk;
	}
}