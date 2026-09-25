package com.chat.app.service;

import com.chat.app.model.Message;
import com.chat.app.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class MessageService implements IMessageService {

	private final MessageRepository messageRepository;

	private final ConcurrentMap<Long, Sinks.Many<Message>> groupSinks = new ConcurrentHashMap<>();

	public MessageService(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	private Sinks.Many<Message> getSinkForGroup(Long groupId) {
		return groupSinks.computeIfAbsent(groupId, id -> Sinks.many().multicast().directBestEffort());
	}

	@Override
	@Transactional
	public Message saveMessage(Long senderId, String username, Long groupId, String encryptedContent,
			Integer keyVersion, String iv) {
		Message message = new Message();
		message.setSenderId(senderId);
		message.setSenderName(username);
		message.setGroupId(groupId);
		message.setEncryptedContent(encryptedContent);
		message.setKeyVersion(keyVersion);
		message.setIv(iv);
		message.setCreatedAt(LocalDateTime.now());

		Message savedMessage = messageRepository.save(message);

		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					getSinkForGroup(groupId).tryEmitNext(savedMessage);
				}
			});
		} else {
			getSinkForGroup(groupId).tryEmitNext(savedMessage);
		}

		return savedMessage;
	}

	@Override
	public List<Message> getGroupChatHistory(Long groupId) {
		return messageRepository.findByGroupIdOrderByCreatedAtAsc(groupId);
	}

	@Override
	public Flux<Message> listenToMessages(Long groupId) {
		return getSinkForGroup(groupId).asFlux().publish().autoConnect();
	}
}