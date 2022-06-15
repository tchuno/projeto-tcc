package net.gnfe.util.rest.dto;

import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Set;

public class ErrorDTO {

	public HttpStatus status;
	public Set<String> messages;

	public ErrorDTO() {
		this.status = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public ErrorDTO(HttpStatus status) {
		this.status = status;
	}

	public void addMessage(String message) {
		if (messages == null) {
			messages = new HashSet<String>();
		}
		messages.add(message);
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public Set<String> getMessages() {
		return messages;
	}

	public void setMessages(Set<String> messages) {
		this.messages = messages;
	}
}