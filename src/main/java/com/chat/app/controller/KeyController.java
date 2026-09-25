package com.chat.app.controller;

import com.chat.app.dto.GroupKeyDto;
import com.chat.app.dto.SaveGroupKeyInput;
import com.chat.app.model.GroupKey;
import com.chat.app.model.User;
import com.chat.app.service.CurrentUserService;
import com.chat.app.service.GroupKeyService;
import com.chat.app.service.GroupMemberService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class KeyController {

	private final GroupKeyService groupKeyService;
	private final GroupMemberService groupMemberService;
	private final CurrentUserService currentUserService;

	public KeyController(GroupKeyService groupKeyService, GroupMemberService groupMemberService,
			CurrentUserService currentUserService) {
		this.groupKeyService = groupKeyService;
		this.groupMemberService = groupMemberService;
		this.currentUserService = currentUserService;
	}

	@MutationMapping
	public GroupKeyDto saveGroupKey(@Argument @Valid SaveGroupKeyInput input) {
		User currentUser = currentUserService.getCurrentUser();

		GroupKey groupKey = groupKeyService.saveGroupKey(input.getGroupId(), currentUser.getId(), input.getUserId(),
				input.getKeyVersion(), input.getEncryptedGroupKey(), input.getIv(), input.getSenderPublicKey());

		return GroupKeyDto.from(groupKey);
	}

	@QueryMapping
	public GroupKeyDto getGroupKeyForUser(@Argument Long groupId, @Argument Integer keyVersion) {
		User currentUser = currentUserService.getCurrentUser();

		GroupKey groupKey = groupKeyService.getGroupKeyForUser(groupId, currentUser.getId(), keyVersion);

		return groupKey == null ? null : GroupKeyDto.from(groupKey);
	}

	@QueryMapping
	public Integer getGroupKeyVersion(@Argument Long groupId) {
		User currentUser = currentUserService.getCurrentUser();

		groupMemberService.requireMembership(groupId, currentUser.getId());

		return groupKeyService.getCurrentVersion(groupId);
	}

	@MutationMapping
	public Integer rotateGroupKey(@Argument Long groupId) {
		User currentUser = currentUserService.getCurrentUser();

		groupMemberService.requireOwner(groupId, currentUser.getId());

		return groupKeyService.rotateGroupKey(groupId);
	}
}