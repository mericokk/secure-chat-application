package com.chat.app.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtWebSocketAuthenticationManager implements AuthenticationManager {

	private final JwtTokenProvider tokenProvider;

	public JwtWebSocketAuthenticationManager(JwtTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String token = authentication.getCredentials().toString();

		if (!tokenProvider.validateToken(token)) {
			throw new IllegalArgumentException("INVALID_TOKEN");
		}

		var claims = tokenProvider.getClaimsFromJWT(token);
		String username = claims.getSubject();
		String roles = claims.get("roles", String.class);

		List<SimpleGrantedAuthority> authorities = roles == null || roles.isBlank() ? List.of()
				: Arrays.stream(roles.split(",")).map(String::trim).filter(role -> !role.isEmpty())
						.map(SimpleGrantedAuthority::new).toList();

		return new UsernamePasswordAuthenticationToken(username, token, authorities);
	}
}