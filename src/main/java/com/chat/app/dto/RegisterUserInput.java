package com.chat.app.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterUserInput {

	@NotBlank(message = "Username cannot be blank")
	private String username;

	@NotBlank(message = "Password cannot be blank")
	private String password;

	@NotBlank(message = "Public key cannot be blank")
	private String publicKey;

	public RegisterUserInput() {
	}

	public RegisterUserInput(String username, String password, String publicKey) {
		this.username = username;
		this.password = password;
		this.publicKey = publicKey;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
}
