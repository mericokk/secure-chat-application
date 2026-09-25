package com.chat.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SaveGroupKeyInput {

	@NotNull(message = "Group ID cannot be null")
	private Long groupId;

	private Long userId;

	@NotNull(message = "Key version cannot be null")
	private Integer keyVersion;

	@NotBlank(message = "Encrypted group key cannot be blank")
	private String encryptedGroupKey;

	@NotBlank(message = "IV cannot be blank")
	private String iv;

	@NotBlank(message = "Sender public key cannot be blank")
	private String senderPublicKey;

	public SaveGroupKeyInput() {
	}

	public SaveGroupKeyInput(Long groupId, Long userId, Integer keyVersion, String encryptedGroupKey, String iv,
			String senderPublicKey) {
		this.groupId = groupId;
		this.userId = userId;
		this.keyVersion = keyVersion;
		this.encryptedGroupKey = encryptedGroupKey;
		this.iv = iv;
		this.senderPublicKey = senderPublicKey;
	}

	public Long getGroupId() {
		return groupId;
	}

	public Long getUserId() {
		return userId;
	}

	public Integer getKeyVersion() {
		return keyVersion;
	}

	public String getEncryptedGroupKey() {
		return encryptedGroupKey;
	}

	public String getIv() {
		return iv;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setKeyVersion(Integer keyVersion) {
		this.keyVersion = keyVersion;
	}

	public void setEncryptedGroupKey(String encryptedGroupKey) {
		this.encryptedGroupKey = encryptedGroupKey;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}
}