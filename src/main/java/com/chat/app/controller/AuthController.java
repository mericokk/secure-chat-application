package com.chat.app.controller;

import com.chat.app.dto.LoginResponse;
import com.chat.app.model.RefreshToken;
import com.chat.app.model.User;
import com.chat.app.repository.RefreshTokenRepository;
import com.chat.app.security.JwtTokenProvider;
import com.chat.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RestController
public class AuthController {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;

	public AuthController(RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider,
			UserService userService) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userService = userService;
	}

	public static class RefreshRequest {
		public String refreshToken;
	}

	@PostMapping("/auth/refresh")
	public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest req) {
		if (req == null || req.refreshToken == null || req.refreshToken.isBlank()) {
			return ResponseEntity.badRequest().build();
		}

		RefreshToken stored = refreshTokenRepository.findByToken(req.refreshToken).orElse(null);

		if (stored == null || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
			return ResponseEntity.status(401).build();
		}

		User user = userService.getUserById(stored.getUserId());

		String accessToken = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), "USER");

		String newRefresh = UUID.randomUUID().toString();
		stored.setToken(newRefresh);
		stored.setExpiresAt(LocalDateTime.now().plus(7, ChronoUnit.DAYS));
		refreshTokenRepository.save(stored);

		return ResponseEntity.ok(new LoginResponse(accessToken, newRefresh));
	}
}
