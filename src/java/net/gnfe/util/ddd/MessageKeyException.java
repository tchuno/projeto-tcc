package net.gnfe.util.ddd;

import org.springframework.http.HttpStatus;

import java.util.Arrays;

public class MessageKeyException extends RuntimeException {

	private String key;
	private Object[] args;
	private HttpStatus status = HttpStatus.BAD_REQUEST;

	public MessageKeyException(String key) {
		this(key, (Object[])null);
	}

	public MessageKeyException(String key, Object... args) {
		super(key);
		this.key = key;
		this.args = args;
	}

	public MessageKeyException(String key, HttpStatus status) {
		super(key);
		this.status = status;
	}

	public String getKey() {
		return key;
	}

	public Object[] getArgs() {
		return args;
	}

	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		String s = getClass().getName();
		String message = getLocalizedMessage();
		return (message != null) ? (s + ": " + message + " " + (args != null ? Arrays.asList(args) : "")) : s;
	}
}
