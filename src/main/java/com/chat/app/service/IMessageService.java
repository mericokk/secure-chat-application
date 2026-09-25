package com.chat.app.service;

import com.chat.app.model.Message;
import reactor.core.publisher.Flux;
import java.util.List;

public interface IMessageService {
	Message saveMessage(Long senderId, String senderName, Long groupId, String encryptedContent, Integer keyVersion,
			String iv);

	List<Message> getGroupChatHistory(Long groupId);

	Flux<Message> listenToMessages(Long groupId);
}