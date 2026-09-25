package com.chat.app.exception;

public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -6771115676461199256L;

	public UserNotFoundException() {
		super("USER_NOT_FOUND");
	}
}