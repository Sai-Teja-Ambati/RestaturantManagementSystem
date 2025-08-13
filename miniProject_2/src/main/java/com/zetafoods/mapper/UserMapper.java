package com.zetafoods.mapper;

import com.zetafoods.entity.User;
import com.zetafoods.request.UserCreateRequest;
import com.zetafoods.response.UserCreateResponse;

import static com.zetafoods.util.InsuranceUtil.MAPPER;
import static com.zetafoods.util.InsuranceUtil.encodePassword;

public class UserMapper {

    public static UserCreateResponse getResponseFromUser(User user) {
        return MAPPER.map(user, UserCreateResponse.class);
    }

    public static User getUserFromRequest(UserCreateRequest userCreateRequest) {
        User createdUser = MAPPER.map(userCreateRequest, User.class);
        createdUser.setPasswordHash(encodePassword(userCreateRequest.getPassword()));
        return createdUser;
    }
}
