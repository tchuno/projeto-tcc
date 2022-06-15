package net.gnfe.bin.domain.service;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

	@Resource(name="resourceMessage") private MessageSource resource;

	public String getValue(String key) {
		return getValue(key, (Object[]) null);
	}

	public String getValue(String key, Object... args) {

		String valor = System.getProperty(key);
		if(StringUtils.isNotBlank(valor)) {
			return valor;
		}

		valor = getValueFromResource(key, args);
		return valor;
	}

	private String getValueFromResource(String key, Object[] args) {

		try {
			return resource.getMessage(key, args, null);
		}
		catch (NoSuchMessageException e) {
			//apenas não encontrou a mensagem
			return null;
		}
	}
}
