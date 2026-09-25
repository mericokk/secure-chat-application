package com.chat.app.service;

import com.chat.app.event.GroupMembershipEvent;
import com.chat.app.model.ChatGroup;
import com.chat.app.model.GroupKey;
import com.chat.app.repository.ChatGroupRepository;
import com.chat.app.repository.GroupKeyRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupKeyService {

	private final ChatGroupRepository chatGroupRepository;
	private final GroupKeyRepository groupKeyRepository;
	private final GroupMemberService groupMemberService;

	public GroupKeyService(ChatGroupRepository chatGroupRepository, GroupKeyRepository groupKeyRepository,
			GroupMemberService groupMemberService) {
		this.chatGroupRepository = chatGroupRepository;
		this.groupKeyRepository = groupKeyRepository;
		this.groupMemberService = groupMemberService;
	}

	public Integer getCurrentVersion(Long groupId) {
		ChatGroup group = chatGroupRepository.findById(groupId)
				.orElseThrow(() -> new IllegalStateException("GROUP_NOT_FOUND"));

		Integer version = group.getKeyVersion();

		if (version == null || version < 1) {
			version = 1;
			group.setKeyVersion(version);
			chatGroupRepository.save(group);
		}

		return version;
	}

	@Transactional
	public Integer rotateGroupKey(Long groupId) {
		ChatGroup group = chatGroupRepository.findById(groupId)
				.orElseThrow(() -> new IllegalStateException("GROUP_NOT_FOUND"));

		Integer currentVersion = group.getKeyVersion();

		if (currentVersion == null || currentVersion < 1) {
			currentVersion = 1;
		}

		Integer nextVersion = currentVersion + 1;
		group.setKeyVersion(nextVersion);
		chatGroupRepository.save(group);

		return nextVersion;
	}

	@Transactional
	@EventListener
	public void handleMembershipChange(GroupMembershipEvent event) {
		rotateGroupKey(event.groupId());
	}

	@Transactional
	public GroupKey saveGroupKey(Long groupId, Long requesterId, Long targetUserId, Integer keyVersion,
			String encryptedGroupKey, String iv, String senderPublicKey) {

		if (targetUserId == null) {
			targetUserId = requesterId;
		}

		groupMemberService.requireMembership(groupId, requesterId);
		groupMemberService.requireMembership(groupId, targetUserId);

		if (!requesterId.equals(targetUserId)) {
			groupMemberService.requireOwner(groupId, requesterId);
		}

		Integer currentVersion = getCurrentVersion(groupId);

		if (!currentVersion.equals(keyVersion)) {
			throw new IllegalStateException("GROUP_KEY_VERSION_OUTDATED");
		}

		GroupKey groupKey = groupKeyRepository.findByGroupIdAndUserIdAndKeyVersion(groupId, targetUserId, keyVersion)
				.orElseGet(GroupKey::new);

		groupKey.setGroupId(groupId);
		groupKey.setUserId(targetUserId);
		groupKey.setKeyVersion(keyVersion);
		groupKey.setEncryptedGroupKey(encryptedGroupKey);
		groupKey.setIv(iv);
		groupKey.setSenderPublicKey(senderPublicKey);

		return groupKeyRepository.save(groupKey);
	}

	public GroupKey getGroupKeyForUser(Long groupId, Long userId, Integer keyVersion) {
		groupMemberService.requireMembership(groupId, userId);

		Integer version = keyVersion != null ? keyVersion : getCurrentVersion(groupId);

		if (version < 1) {
			throw new IllegalArgumentException("INVALID_KEY_VERSION");
		}

		return groupKeyRepository.findByGroupIdAndUserIdAndKeyVersion(groupId, userId, version).orElse(null);
	}
}