package com.chat.app.service;

import com.chat.app.exception.InvalidCredentialsException;
import com.chat.app.model.RefreshToken;
import com.chat.app.model.User;
import com.chat.app.repository.RefreshTokenRepository;
import com.chat.app.security.JwtTokenProvider;
import com.chat.app.dto.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService implements IAuthService {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
			RefreshTokenRepository refreshTokenRepository) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	public LoginResponse login(String username, String password) {

		User user;

		try {
			user = userService.getUserByUsername(username.trim());
		} catch (RuntimeException ex) {
			throw new InvalidCredentialsException();
		}

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new InvalidCredentialsException();
		}

		String accessToken = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), "USER");

		String refreshTokenValue = UUID.randomUUID().toString();
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUserId(user.getId());
		refreshToken.setToken(refreshTokenValue);
		refreshToken.setExpiresAt(LocalDateTime.now().plus(7, ChronoUnit.DAYS));

		refreshTokenRepository.save(refreshToken);

		return new LoginResponse(accessToken, refreshTokenValue);
	}
}