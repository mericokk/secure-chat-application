package com.chat.app.service;

import com.chat.app.model.ChatGroup;

import java.util.List;

public interface IGroupService {

	List<ChatGroup> getAllGroups();

	ChatGroup createGroup(String name, Long creatorId);

	List<ChatGroup> getGroupsForUser(Long userId);
}