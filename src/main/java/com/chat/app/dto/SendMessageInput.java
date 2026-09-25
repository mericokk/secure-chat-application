package com.chat.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SendMessageInput {

	@NotNull(message = "Group ID cannot be null")
	private Long groupId;

	@NotBlank(message = "Encrypted content cannot be blank")
	private String encryptedContent;

	@NotBlank(message = "IV cannot be blank")
	private String iv;

	@NotNull(message = "Key version cannot be null")
	private Integer keyVersion;

	public SendMessageInput() {
	}

	public SendMessageInput(Long groupId, String encryptedContent, String iv, Integer keyVersion) {
		this.groupId = groupId;
		this.encryptedContent = encryptedContent;
		this.iv = iv;
		this.keyVersion = keyVersion;
	}

	public Long getGroupId() {
		return groupId;
	}

	public String getEncryptedContent() {
		return encryptedContent;
	}

	public String getIv() {
		return iv;
	}

	public Integer getKeyVersion() {
		return keyVersion;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public void setEncryptedContent(String encryptedContent) {
		this.encryptedContent = encryptedContent;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public void setKeyVersion(Integer keyVersion) {
		this.keyVersion = keyVersion;
	}
}