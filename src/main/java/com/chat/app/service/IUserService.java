package com.chat.app.service;

import com.chat.app.model.User;
import java.util.List;

public interface IUserService {
	User createUser(String username, String password, String publicKey);

	User getUserById(Long id);

	List<User> getUsersByIds(List<Long> userIds);

	User getUserByUsername(String username);

	User updatePublicKey(Long userId, String publicKey);
}
