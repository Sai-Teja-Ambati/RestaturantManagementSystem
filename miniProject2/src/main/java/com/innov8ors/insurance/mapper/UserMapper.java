package com.innov8ors.insurance.mapper;

import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.request.UserCreateRequest;
import com.innov8ors.insurance.response.UserCreateResponse;

import static com.innov8ors.insurance.util.InsuranceUtil.MAPPER;
import static com.innov8ors.insurance.util.InsuranceUtil.encodePassword;

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
