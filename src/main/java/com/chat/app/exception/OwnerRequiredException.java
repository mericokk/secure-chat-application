package com.chat.app.exception;

public class OwnerRequiredException extends RuntimeException {

	private static final long serialVersionUID = 1298272619696780110L;

	public OwnerRequiredException() {
		super("OWNER_REQUIRED");
	}
}
