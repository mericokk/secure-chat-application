package com.chat.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class RemoveMemberInput {

	@NotNull(message = "Group ID cannot be null")
	private Long groupId;

	@NotBlank(message = "Username cannot be blank")
	private String username;

	public RemoveMemberInput() {
	}

	public RemoveMemberInput(Long groupId, String username) {
		this.groupId = groupId;
		this.username = username;
	}

	public Long getGroupId() {
		return groupId;
	}

	public String getUsername() {
		return username;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
