package com.chat.app.exception;

public class InvalidCredentialsException extends RuntimeException {
	private static final long serialVersionUID = 2610282687991509377L;

	public InvalidCredentialsException() {
		super("INVALID_CREDENTIALS");
	}
}