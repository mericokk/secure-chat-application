package com.chat.app.service;

import com.chat.app.model.ChatGroup;
import com.chat.app.model.GroupMember;
import com.chat.app.repository.ChatGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService implements IGroupService {

	private final ChatGroupRepository chatGroupRepository;
	private final GroupMemberService groupMemberService;

	public GroupService(ChatGroupRepository chatGroupRepository, GroupMemberService groupMemberService) {
		this.chatGroupRepository = chatGroupRepository;
		this.groupMemberService = groupMemberService;
	}

	@Override
	public List<ChatGroup> getAllGroups() {
		return chatGroupRepository.findAll();
	}

	@Override
	@Transactional
	public ChatGroup createGroup(String name, Long creatorId) {
		ChatGroup group = new ChatGroup();
		group.setName(name.trim());
		group.setKeyVersion(1);

		ChatGroup savedGroup = chatGroupRepository.save(group);
		groupMemberService.addOwner(savedGroup.getId(), creatorId);

		return savedGroup;
	}

	@Override
	public List<ChatGroup> getGroupsForUser(Long userId) {
		List<Long> groupIds = groupMemberService.getMembershipsForUser(userId).stream().map(GroupMember::getGroupId)
				.toList();

		if (groupIds.isEmpty()) {
			return List.of();
		}

		return chatGroupRepository.findByIdIn(groupIds);
	}
}