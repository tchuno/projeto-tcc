package net.gnfe.bin.domain.service;


import net.gnfe.util.DummyUtils;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.ddd.MessageKeyListException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ExceptionService {

	@Autowired private MessageService messageService;

	public void getMessage(StringBuilder exceptionsMessages, Throwable exception) {

		if(exception instanceof MessageKeyListException) {
			List<MessageKeyException> exceptions = ((MessageKeyListException) exception).getMessageKeyExceptions();
			getMessage(exceptionsMessages, exceptions);
		}
		else if(exception instanceof MessageKeyException) {
			MessageKeyException mke = (MessageKeyException) exception;
			getMessage(exceptionsMessages, mke);
		}
		else {
			String message = DummyUtils.getExceptionMessage(exception);
			exceptionsMessages.append(message).append("\n");
		}
	}

	public void getMessage(StringBuilder exceptionsMessages, List<MessageKeyException> exceptions) {
		for (MessageKeyException mke : exceptions) {
			getMessage(exceptionsMessages, mke);
		}
	}

	private void getMessage(StringBuilder exceptionsMessages, MessageKeyException mke) {
		String key = mke.getKey();
		Object[] args = mke.getArgs();
		String message = messageService.getValue(key, args);
		if(StringUtils.isBlank(message)) {
			message = DummyUtils.getExceptionMessage(mke);
		}
		exceptionsMessages.append(message).append("\n");
	}

	public String getMessage(Throwable e) {
		StringBuilder sb = new StringBuilder();
		getMessage(sb, e);
		return sb.toString();
	}
}
