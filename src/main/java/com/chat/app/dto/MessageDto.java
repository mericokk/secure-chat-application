package com.chat.app.dto;

import com.chat.app.model.Message;
import java.time.LocalDateTime;

public class MessageDto {

	private Long id;
	private Long senderId;
	private String senderName;
	private Long groupId;
	private String encryptedContent;
	private Integer keyVersion;
	private LocalDateTime createdAt;
	private String iv;

	public MessageDto() {
	}

	public MessageDto(Long id, Long senderId, String senderName, Long groupId, String encryptedContent,
			Integer keyVersion, LocalDateTime createdAt, String iv) {
		this.id = id;
		this.senderId = senderId;
		this.senderName = senderName;
		this.groupId = groupId;
		this.encryptedContent = encryptedContent;
		this.keyVersion = keyVersion;
		this.createdAt = createdAt;
		this.iv = iv;
	}

	public static MessageDto from(Message m) {
		return new MessageDto(m.getId(), m.getSenderId(), m.getSenderName(), m.getGroupId(), m.getEncryptedContent(),
				m.getKeyVersion(), m.getCreatedAt(), m.getIv());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getEncryptedContent() {
		return encryptedContent;
	}

	public void setEncryptedContent(String encryptedContent) {
		this.encryptedContent = encryptedContent;
	}

	public Integer getKeyVersion() {
		return keyVersion;
	}

	public void setKeyVersion(Integer keyVersion) {
		this.keyVersion = keyVersion;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}
}