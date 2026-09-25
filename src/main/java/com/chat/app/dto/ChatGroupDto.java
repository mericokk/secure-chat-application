package com.chat.app.dto;

import com.chat.app.model.ChatGroup;
import java.time.LocalDateTime;

public class ChatGroupDto {

	private Long id;
	private String name;
	private Integer keyVersion;
	private LocalDateTime createdAt;

	public ChatGroupDto() {
	}

	public ChatGroupDto(Long id, String name, Integer keyVersion, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.keyVersion = keyVersion;
		this.createdAt = createdAt;
	}

	public static ChatGroupDto from(ChatGroup chatGroup) {
		return new ChatGroupDto(chatGroup.getId(), chatGroup.getName(), chatGroup.getKeyVersion(),
				chatGroup.getCreatedAt());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getKeyVersion() {
		return keyVersion;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setKeyVersion(Integer keyVersion) {
		this.keyVersion = keyVersion;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}