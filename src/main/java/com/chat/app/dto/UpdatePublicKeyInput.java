package com.chat.app.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdatePublicKeyInput {

	@NotBlank(message = "Public key cannot be blank")
	private String publicKey;

	public UpdatePublicKeyInput() {
	}

	public UpdatePublicKeyInput(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
}
