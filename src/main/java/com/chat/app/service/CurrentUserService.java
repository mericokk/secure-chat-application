package com.chat.app.service;

import com.chat.app.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

	private final UserService userService;

	public CurrentUserService(UserService userService) {
		this.userService = userService;
	}

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null
				|| "anonymousUser".equals(authentication.getPrincipal())) {

			throw new IllegalStateException("AUTHENTICATION_REQUIRED");
		}

		String username = authentication.getName();

		return getUserByUsername(username);
	}

	public User getUserByUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalStateException("AUTHENTICATION_REQUIRED");
		}
		return userService.getUserByUsername(username);
	}
}