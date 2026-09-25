package com.chat.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "group_keys", uniqueConstraints = { @UniqueConstraint(name = "uk_group_user_version", columnNames = {
		"group_id", "user_id", "key_version" }) }, indexes = {
				@Index(name = "idx_group_keys_group_user", columnList = "group_id, user_id") })
public class GroupKey {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "group_id", nullable = false)
	private Long groupId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "key_version", nullable = false)
	private int keyVersion;

	@Column(name = "encrypted_group_key", columnDefinition = "TEXT", nullable = false)
	private String encryptedGroupKey;

	@Column(nullable = false, length = 255)
	private String iv;

	@Column(name = "sender_public_key", columnDefinition = "TEXT", nullable = false)
	private String senderPublicKey;

	public GroupKey() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getKeyVersion() {
		return keyVersion;
	}

	public void setKeyVersion(int keyVersion) {
		this.keyVersion = keyVersion;
	}

	public String getEncryptedGroupKey() {
		return encryptedGroupKey;
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