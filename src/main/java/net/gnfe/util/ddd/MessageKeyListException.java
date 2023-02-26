package net.gnfe.util.ddd;

import java.util.Arrays;
import java.util.List;

public class MessageKeyListException extends RuntimeException {

	private final List<MessageKeyException> messageKeyExceptions;

	public MessageKeyListException(List<MessageKeyException> messageKeyExceptions) {
		this.messageKeyExceptions = messageKeyExceptions;
	}

	public List<MessageKeyException> getMessageKeyExceptions() {
		return messageKeyExceptions;
	}

	@Override
	public String toString() {
		String s = getClass().getName();
		StringBuffer keys = new StringBuffer();
		for (MessageKeyException e : messageKeyExceptions) {
			keys.append(e.getKey()).append("(").append(Arrays.asList(e.getArgs())).append(") ");
		}
		return s + ": " + keys;
	}
}
