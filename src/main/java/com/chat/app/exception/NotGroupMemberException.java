package com.chat.app.exception;

public class NotGroupMemberException extends RuntimeException {

	private static final long serialVersionUID = 2783736489241054000L;

	public NotGroupMemberException() {
		super("NOT_GROUP_MEMBER");
	}
}
