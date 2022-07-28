package net.gnfe.util.ddd;

public class MessageKeyException extends RuntimeException {

	private final String key;
	private final Object[] args;

	public MessageKeyException(String message) {
		this(message, (Object[])null);
	}

	public MessageKeyException(String key, Object... args) {
		super(key);
		this.key = key;
		this.args = args;
	}

	public String getKey() {
		return key;
	}

	public Object[] getArgs() {
		return args;
	}
}
