package com.chat.app.controller;

import com.chat.app.dto.CreateGroupInput;
import com.chat.app.dto.ChatGroupDto;
import com.chat.app.dto.UserDto;
import com.chat.app.dto.AddMemberInput;
import com.chat.app.dto.RemoveMemberInput;
import com.chat.app.dto.GroupMemberDto;
import com.chat.app.model.ChatGroup;
import com.chat.app.model.GroupMember;
import com.chat.app.model.User;
import com.chat.app.service.IGroupService;
import com.chat.app.service.IGroupMemberService;
import com.chat.app.service.IUserService;
import com.chat.app.service.CurrentUserService;

import jakarta.validation.Valid;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Controller
@Validated
public class GroupController {

	private final IGroupService groupService;
	private final IGroupMemberService groupMemberService;
	private final IUserService userService;
	private final CurrentUserService currentUserService;

	public GroupController(IGroupService groupService, IGroupMemberService groupMemberService, IUserService userService,
			CurrentUserService currentUserService) {

		this.groupService = groupService;
		this.groupMemberService = groupMemberService;
		this.userService = userService;
		this.currentUserService = currentUserService;
	}

	@MutationMapping
	public ChatGroupDto createGroup(@Argument @Valid CreateGroupInput input) {
		User currentUser = currentUserService.getCurrentUser();

		ChatGroup chatGroup = groupService.createGroup(input.getName(), currentUser.getId());

		return ChatGroupDto.from(chatGroup);
	}

	@QueryMapping
	public List<ChatGroupDto> getAllGroups() {
		User currentUser = currentUserService.getCurrentUser();

		return groupService.getGroupsForUser(currentUser.getId()).stream().map(ChatGroupDto::from).toList();
	}

	@QueryMapping
	public List<UserDto> getGroupMembers(@Argument Long groupId) {
		User currentUser = currentUserService.getCurrentUser();

		groupMemberService.requireMembership(groupId, currentUser.getId());

		List<Long> userIds = groupMemberService.getMembershipsForGroup(groupId).stream().map(GroupMember::getUserId)
				.toList();

		return userService.getUsersByIds(userIds).stream().map(UserDto::from).toList();
	}

	@QueryMapping
	public GroupMemberDto getMyGroupMembership(@Argument Long groupId) {
		User currentUser = currentUserService.getCurrentUser();

		GroupMember membership = groupMemberService.getMembership(groupId, currentUser.getId());

		if (membership == null) {
			throw new com.chat.app.exception.NotGroupMemberException();
		}

		return GroupMemberDto.from(membership);
	}

	@MutationMapping
	public GroupMemberDto addGroupMember(@Argument @Valid AddMemberInput input) {

		User currentUser = currentUserService.getCurrentUser();

		User targetUser = userService.getUserByUsername(input.getUsername().trim());

		GroupMember member = groupMemberService.addMember(input.getGroupId(), currentUser.getId(), targetUser.getId());

		return GroupMemberDto.from(member);
	}

	@MutationMapping
	public Boolean removeGroupMember(@Argument @Valid RemoveMemberInput input) {

		User currentUser = currentUserService.getCurrentUser();

		User targetUser = userService.getUserByUsername(input.getUsername().trim());

		groupMemberService.removeMember(input.getGroupId(), currentUser.getId(), targetUser.getId());

		return true;
	}
}