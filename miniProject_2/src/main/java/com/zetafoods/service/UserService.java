package com.zetafoods.service;

import com.zetafoods.entity.User;
import com.zetafoods.request.UserCreateRequest;
import com.zetafoods.request.UserLoginRequest;

public interface UserService {
    User register(UserCreateRequest userCreateRequest);

    String login(UserLoginRequest userLoginRequest);
}
