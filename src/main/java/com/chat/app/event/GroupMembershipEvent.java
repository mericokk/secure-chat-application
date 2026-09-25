package com.chat.app.event;

public record GroupMembershipEvent(Long groupId, Long userId, EventType type) {
	public enum EventType {
		ADDED, REMOVED
	}
}