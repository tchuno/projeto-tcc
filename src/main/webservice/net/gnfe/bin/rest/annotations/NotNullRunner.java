package net.gnfe.bin.rest.annotations;

import net.gnfe.bin.rest.exception.DadosObrigatorioRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

@Service
public class NotNullRunner {

	/**
	 * Faz a validação do atributo anotado como obrigatório.
	 * @param obj
	 * @throws Exception
	 */
	public void run(Object obj) throws DadosObrigatorioRequestException {
		Class<?> klass = obj.getClass();
		for (Method method : klass.getMethods()) {
			NotNull annotation = method.getAnnotation(NotNull.class);
			if (annotation != null) {
				validaSuperChecker(obj, method, annotation);
			}
		}
	}

	/**
	 * Faz a devidas validações.
	 * @param obj
	 * @param method
	 * @param annotation
	 * @throws DadosObrigatorioRequestException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private void validaSuperChecker(Object obj, Method method, NotNull annotation) throws DadosObrigatorioRequestException {
		String msgKey = "request.required.parameter";

		if (StringUtils.isNotEmpty(annotation.messageKey())) {
			msgKey = annotation.messageKey();
		}

		try {
			Object value  = method.invoke(obj);

			String nomeCampo = method.getName();
			if(StringUtils.isNotEmpty(annotation.nomeCampo())){
				nomeCampo = annotation.nomeCampo();
			}

			if (value == null) {
				throw new DadosObrigatorioRequestException(msgKey, nomeCampo);
			}
			if (value instanceof Collection) {
				if (((Collection) value).isEmpty()) {
					throw new DadosObrigatorioRequestException(msgKey, nomeCampo);
				}
			}
			if (value instanceof String) {
				if (StringUtils.isEmpty((String) value)) {
					throw new DadosObrigatorioRequestException(msgKey, nomeCampo);
				}
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}
}