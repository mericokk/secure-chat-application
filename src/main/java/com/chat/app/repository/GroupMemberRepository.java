package com.chat.app.repository;

import com.chat.app.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

	boolean existsByGroupIdAndUserId(Long groupId, Long userId);

	Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

	List<GroupMember> findByGroupId(Long groupId);

	List<GroupMember> findByUserId(Long userId);

	void deleteByGroupIdAndUserId(Long groupId, Long userId);
}
