package net.gnfe.bin.rest.exception;

import net.gnfe.util.ddd.MessageKeyException;
import org.springframework.http.HttpStatus;

public class HTTP401Exception extends MessageKeyException {

	private String key;
	private HttpStatus status;

	public HTTP401Exception(String key) {
		super(key);
		this.status = HttpStatus.UNAUTHORIZED;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
}