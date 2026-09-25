package com.chat.app.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateGroupInput {

	@NotBlank(message = "Group name cannot be blank")
	private String name;

	public CreateGroupInput() {
	}

	public CreateGroupInput(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
