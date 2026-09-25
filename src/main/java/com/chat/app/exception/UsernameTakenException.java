package com.chat.app.exception;

public class UsernameTakenException extends RuntimeException {
	private static final long serialVersionUID = -7578093636500605238L;

	public UsernameTakenException() {
		super("USERNAME_TAKEN");
	}
}