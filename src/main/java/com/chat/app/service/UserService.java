package com.chat.app.service;

import com.chat.app.exception.UserNotFoundException;
import com.chat.app.exception.UsernameTakenException;
import com.chat.app.model.User;
import com.chat.app.repository.UserRepository;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements IUserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User createUser(String username, String password, String publicKey) {

		String normalizedUsername = username.trim();

		if (userRepository.findByUsername(normalizedUsername).isPresent()) {
			throw new UsernameTakenException();
		}

		User user = new User();
		user.setUsername(normalizedUsername);
		user.setPassword(passwordEncoder.encode(password));
		user.setPublicKey(publicKey);

		return userRepository.save(user);
	}

	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
	}

	public List<User> getUsersByIds(List<Long> userIds) {
		return userRepository.findAllById(userIds);
	}

	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
	}

	@Transactional
	public User updatePublicKey(Long userId, String publicKey) {
		User user = getUserById(userId);
		user.setPublicKey(publicKey);
		return userRepository.save(user);
	}
}
