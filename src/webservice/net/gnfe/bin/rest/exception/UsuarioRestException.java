package net.gnfe.bin.rest.exception;

import net.gnfe.util.ddd.MessageKeyException;

public class UsuarioRestException extends MessageKeyException {

	public UsuarioRestException(String key, Object... args) {
		super(key, args);
	}
}