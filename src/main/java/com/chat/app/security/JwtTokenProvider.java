package com.chat.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	private final Key jwtSecret;
	private final long jwtExpirationInMs = 86400000;

	public JwtTokenProvider(@Value("${JWT_SECRET:}") String jwtSecretEnv) {
		Key secret;

		if (jwtSecretEnv != null && !jwtSecretEnv.isBlank()) {
			try {
				byte[] keyBytes = Decoders.BASE64.decode(jwtSecretEnv);
				secret = Keys.hmacShaKeyFor(keyBytes);
			} catch (Exception e) {
				logger.error("Failed to decode JWT_SECRET from environment; falling back to generated key", e);
				secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
			}
		} else {
			logger.warn(
					"JWT_SECRET not provided in environment; using ephemeral in-memory key (not safe for production)");
			secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
		}

		this.jwtSecret = secret;
	}

	public String generateToken(String username) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder().setSubject(username).setIssuedAt(new Date()).setExpiration(expiryDate).signWith(jwtSecret)
				.compact();
	}

	public String generateToken(Long userId, String username, String roles) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder().setSubject(username).claim("uid", userId).claim("roles", roles).setIssuedAt(now)
				.setExpiration(expiryDate).signWith(jwtSecret).compact();
	}

	public String getUsernameFromJWT(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();

		return claims.getSubject();
	}

	public Claims getClaimsFromJWT(String token) {
		return Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
			return true;
		} catch (JwtException | IllegalArgumentException ex) {

		}
		return false;
	}
}