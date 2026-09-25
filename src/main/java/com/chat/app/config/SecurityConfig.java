package com.chat.app.config;

import com.chat.app.security.JwtAuthenticationFilter;
import com.chat.app.security.JwtTokenProvider;
import com.chat.app.security.JwtWebSocketAuthenticationManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.webmvc.AuthenticationWebSocketInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtTokenProvider tokenProvider;

	public SecurityConfig(JwtTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Bean
	JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(tokenProvider);
	}

	@Bean
	AuthenticationWebSocketInterceptor authenticationWebSocketInterceptor(
			JwtWebSocketAuthenticationManager authenticationManager) {

		return new AuthenticationWebSocketInterceptor(payload -> {
			Object value = payload.get("Authorization");

			if (value == null) {
				return reactor.core.publisher.Mono.empty();
			}

			String authorization = value.toString();

			if (!authorization.startsWith("Bearer ")) {
				return reactor.core.publisher.Mono.empty();
			}

			String token = authorization.substring(7).trim();

			if (token.isEmpty()) {
				return reactor.core.publisher.Mono.empty();
			}

			return reactor.core.publisher.Mono.just(new UsernamePasswordAuthenticationToken(null, token));
		}, authenticationManager);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**", "/graphql").permitAll().anyRequest()
						.authenticated())
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}