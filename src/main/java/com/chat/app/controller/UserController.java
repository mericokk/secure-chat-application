package com.chat.app.controller;

import com.chat.app.dto.LoginInput;
import com.chat.app.dto.LoginResponse;
import com.chat.app.dto.RegisterUserInput;
import com.chat.app.dto.UpdatePublicKeyInput;
import com.chat.app.dto.UserDto;
import com.chat.app.model.User;
import com.chat.app.service.CurrentUserService;
import com.chat.app.service.IAuthService;
import com.chat.app.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class UserController {

	private final IUserService userService;
	private final IAuthService authService;
	private final CurrentUserService currentUserService;

	public UserController(IUserService userService, IAuthService authService, CurrentUserService currentUserService) {
		this.userService = userService;
		this.authService = authService;
		this.currentUserService = currentUserService;
	}

	@MutationMapping
	public LoginResponse login(@Argument @Valid LoginInput input) {
		return authService.login(input.getUsername(), input.getPassword());
	}

	@MutationMapping
	public UserDto registerUser(@Argument @Valid RegisterUserInput input) {
		User user = userService.createUser(input.getUsername(), input.getPassword(), input.getPublicKey());
		return UserDto.from(user);
	}

	@QueryMapping
	public UserDto getUser(@Argument String username) {
		return UserDto.from(userService.getUserByUsername(username));
	}

	@MutationMapping
	public UserDto updatePublicKey(@Argument @Valid UpdatePublicKeyInput input) {
		User currentUser = currentUserService.getCurrentUser();
		User updatedUser = userService.updatePublicKey(currentUser.getId(), input.getPublicKey());
		return UserDto.from(updatedUser);
	}
}