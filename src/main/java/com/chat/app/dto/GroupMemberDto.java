package com.chat.app.dto;

import com.chat.app.model.GroupMember;
import java.time.LocalDateTime;

public class GroupMemberDto {

	private Long id;
	private Long groupId;
	private Long userId;
	private String role;
	private LocalDateTime joinedAt;

	public GroupMemberDto() {
	}

	public GroupMemberDto(Long id, Long groupId, Long userId, String role, LocalDateTime joinedAt) {
		this.id = id;
		this.groupId = groupId;
		this.userId = userId;
		this.role = role;
		this.joinedAt = joinedAt;
	}

	public static GroupMemberDto from(GroupMember groupMember) {
		return new GroupMemberDto(groupMember.getId(), groupMember.getGroupId(), groupMember.getUserId(),
				groupMember.getRole(), groupMember.getJoinedAt());
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

	public String getRole() {
		return role;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}
}
