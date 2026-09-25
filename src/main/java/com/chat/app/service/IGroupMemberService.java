package com.chat.app.service;

import com.chat.app.model.GroupMember;
import java.util.List;

public interface IGroupMemberService {
	boolean isMember(Long groupId, Long userId);

	void requireMembership(Long groupId, Long userId);

	void requireOwner(Long groupId, Long userId);

	GroupMember getMembership(Long groupId, Long userId);

	List<GroupMember> getMembershipsForUser(Long userId);

	List<GroupMember> getMembershipsForGroup(Long groupId);

	GroupMember addOwner(Long groupId, Long userId);

	GroupMember addMember(Long groupId, Long requesterId, Long targetUserId);

	void removeMember(Long groupId, Long requesterId, Long targetUserId);
}
