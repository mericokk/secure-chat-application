package com.chat.app.dto;

import com.chat.app.model.GroupKey;

public class GroupKeyDto {

	private Long id;
	private Long groupId;
	private Long userId;
	private Integer keyVersion;
	private String encryptedGroupKey;
	private String iv;
	private String senderPublicKey;

	public GroupKeyDto() {
	}

	public GroupKeyDto(Long id, Long groupId, Long userId, Integer keyVersion, String encryptedGroupKey, String iv,
			String senderPublicKey) {
		this.id = id;
		this.groupId = groupId;
		this.userId = userId;
		this.keyVersion = keyVersion;
		this.encryptedGroupKey = encryptedGroupKey;
		this.iv = iv;
		this.senderPublicKey = senderPublicKey;
	}

	public static GroupKeyDto from(GroupKey groupKey) {
		return new GroupKeyDto(groupKey.getId(), groupKey.getGroupId(), groupKey.getUserId(), groupKey.getKeyVersion(),
				groupKey.getEncryptedGroupKey(), groupKey.getIv(), groupKey.getSenderPublicKey());
	}

	public Long getId() {
		return id;
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

	public void setId(Long id) {
		this.id = id;
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

	public String getIv() {
		return iv;
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