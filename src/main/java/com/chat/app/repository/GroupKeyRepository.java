package com.chat.app.repository;

import com.chat.app.model.GroupKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupKeyRepository extends JpaRepository<GroupKey, Long> {

	Optional<GroupKey> findByGroupIdAndUserIdAndKeyVersion(Long groupId, Long userId, Integer keyVersion);

	void deleteByGroupIdAndUserId(Long groupId, Long userId);

	Optional<GroupKey> findTopByGroupIdAndUserIdOrderByKeyVersionDesc(Long groupId, Long userId);
}