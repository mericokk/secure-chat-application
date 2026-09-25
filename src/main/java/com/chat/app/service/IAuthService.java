package com.chat.app.service;

import com.chat.app.dto.LoginResponse;

public interface IAuthService {
	LoginResponse login(String username, String password);
}
