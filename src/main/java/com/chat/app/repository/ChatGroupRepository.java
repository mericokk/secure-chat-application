package com.chat.app.repository;

import com.chat.app.model.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
	Optional<ChatGroup> findByName(String name);

	List<ChatGroup> findByIdIn(List<Long> ids);
}