package net.gnfe.bin.rest.exception;

import net.gnfe.util.ddd.MessageKeyException;

public class DadosObrigatorioRequestException extends MessageKeyException {

	public DadosObrigatorioRequestException(String key, Object... args) {
		super(key, args);
	}
}