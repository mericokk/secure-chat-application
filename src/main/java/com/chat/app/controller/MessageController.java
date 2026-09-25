package com.chat.app.controller;

import com.chat.app.dto.MessageDto;
import com.chat.app.dto.SendMessageInput;
import com.chat.app.model.Message;
import com.chat.app.model.User;
import com.chat.app.service.CurrentUserService;
import com.chat.app.service.GroupKeyService;
import com.chat.app.service.GroupMemberService;
import com.chat.app.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;

import java.security.Principal;
import java.util.List;

@Controller
@Validated
public class MessageController {

	private final MessageService messageService;
	private final GroupMemberService groupMemberService;
	private final CurrentUserService currentUserService;
	private final GroupKeyService groupKeyService;

	public MessageController(MessageService messageService, GroupMemberService groupMemberService,
			CurrentUserService currentUserService, GroupKeyService groupKeyService) {
		this.messageService = messageService;
		this.groupMemberService = groupMemberService;
		this.currentUserService = currentUserService;
		this.groupKeyService = groupKeyService;
	}

	@MutationMapping
	public MessageDto sendMessage(@Argument @Valid SendMessageInput input) {
		User currentUser = currentUserService.getCurrentUser();

		groupMemberService.requireMembership(input.getGroupId(), currentUser.getId());

		Integer currentVersion = groupKeyService.getCurrentVersion(input.getGroupId());

		if (!currentVersion.equals(input.getKeyVersion())) {
			throw new IllegalStateException("GROUP_KEY_VERSION_OUTDATED");
		}

		Message message = messageService.saveMessage(currentUser.getId(), currentUser.getUsername(), input.getGroupId(),
				input.getEncryptedContent(), input.getKeyVersion(), input.getIv());

		return MessageDto.from(message);
	}

	@QueryMapping
	public List<MessageDto> getGroupChatHistory(@Argument Long groupId) {
		User currentUser = currentUserService.getCurrentUser();

		groupMemberService.requireMembership(groupId, currentUser.getId());

		return messageService.getGroupChatHistory(groupId).stream().map(MessageDto::from).toList();
	}

	@SubscriptionMapping
	public Flux<MessageDto> messageAdded(@Argument Long groupId, Principal principal) {
		if (principal == null || principal.getName() == null) {
			throw new AccessDeniedException("UNAUTHENTICATED_SUBSCRIPTION");
		}

		User currentUser = currentUserService.getUserByUsername(principal.getName());

		groupMemberService.requireMembership(groupId, currentUser.getId());

		return messageService.listenToMessages(groupId).map(MessageDto::from);
	}
}