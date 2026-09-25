package com.chat.app.service;

import com.chat.app.event.GroupMembershipEvent;
import com.chat.app.exception.NotGroupMemberException;
import com.chat.app.exception.OwnerRequiredException;
import com.chat.app.model.GroupMember;
import com.chat.app.repository.GroupKeyRepository;
import com.chat.app.repository.GroupMemberRepository;
import org.springframework.context.ApplicationEventPublisher; // <-- Olay yayıncı sınıfı
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupMemberService implements IGroupMemberService {

	private final GroupMemberRepository groupMemberRepository;
	private final GroupKeyRepository groupKeyRepository;
	private final ApplicationEventPublisher eventPublisher;

	public GroupMemberService(GroupMemberRepository groupMemberRepository, GroupKeyRepository groupKeyRepository,
			ApplicationEventPublisher eventPublisher) {
		this.groupMemberRepository = groupMemberRepository;
		this.groupKeyRepository = groupKeyRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public boolean isMember(Long groupId, Long userId) {
		return groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
	}

	@Override
	public void requireMembership(Long groupId, Long userId) {
		if (!isMember(groupId, userId)) {
			throw new NotGroupMemberException();
		}
	}

	@Override
	public void requireOwner(Long groupId, Long userId) {
		GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
				.orElseThrow(NotGroupMemberException::new);

		if (!"OWNER".equalsIgnoreCase(member.getRole())) {
			throw new OwnerRequiredException();
		}
	}

	@Override
	public GroupMember getMembership(Long groupId, Long userId) {
		return groupMemberRepository.findByGroupIdAndUserId(groupId, userId).orElseThrow(NotGroupMemberException::new);
	}

	@Override
	public List<GroupMember> getMembershipsForUser(Long userId) {
		return groupMemberRepository.findByUserId(userId);
	}

	@Override
	public List<GroupMember> getMembershipsForGroup(Long groupId) {
		return groupMemberRepository.findByGroupId(groupId);
	}

	@Override
	@Transactional
	public GroupMember addOwner(Long groupId, Long userId) {
		if (isMember(groupId, userId)) {
			return getMembership(groupId, userId);
		}

		GroupMember member = new GroupMember();
		member.setGroupId(groupId);
		member.setUserId(userId);
		member.setRole("OWNER");

		return groupMemberRepository.save(member);
	}

	@Transactional
	@Override
	public GroupMember addMember(Long groupId, Long requesterId, Long targetUserId) {
		requireOwner(groupId, requesterId);

		if (isMember(groupId, targetUserId)) {
			throw new RuntimeException("USER_ALREADY_MEMBER");
		}

		GroupMember member = new GroupMember();
		member.setGroupId(groupId);
		member.setUserId(targetUserId);
		member.setRole("MEMBER");

		GroupMember savedMember = groupMemberRepository.save(member);

		eventPublisher.publishEvent(new GroupMembershipEvent(groupId, targetUserId, GroupMembershipEvent.EventType.ADDED));

		return savedMember;
	}

	@Override
	@Transactional
	public void removeMember(Long groupId, Long requesterId, Long targetUserId) {
		boolean isSelfRemoval = requesterId.equals(targetUserId);

		if (isSelfRemoval) {
			requireMembership(groupId, requesterId);
			GroupMember member = getMembership(groupId, requesterId);
			if ("OWNER".equalsIgnoreCase(member.getRole())) {
				throw new RuntimeException("OWNER_CANNOT_REMOVE_SELF");
			}
		} else {
			requireOwner(groupId, requesterId);
		}

		if (!isMember(groupId, targetUserId)) {
			throw new NotGroupMemberException();
		}

		groupKeyRepository.deleteByGroupIdAndUserId(groupId, targetUserId);
		groupMemberRepository.deleteByGroupIdAndUserId(groupId, targetUserId);

		eventPublisher.publishEvent(new GroupMembershipEvent(groupId, targetUserId, GroupMembershipEvent.EventType.REMOVED));
	}
}