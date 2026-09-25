package com.chat.app.dto;

import com.chat.app.model.User;

public class UserDto {

	private Long id;
	private String username;
	private String publicKey;

	public UserDto() {
	}

	public UserDto(Long id, String username, String publicKey) {
		this.id = id;
		this.username = username;
		this.publicKey = publicKey;
	}

	public static UserDto from(User user) {
		return new UserDto(user.getId(), user.getUsername(), user.getPublicKey());
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPublicKey() {
		return publicKey;
	}
}
