package com.zetafoods.service.impl;

import com.zetafoods.entity.User;
import com.zetafoods.error.InsuranceServiceErrorType;
import com.zetafoods.exception.NotFoundException;
import com.zetafoods.exception.UnauthorizedException;
import com.zetafoods.repository.dao.UserDao;
import com.zetafoods.request.UserCreateRequest;
import com.zetafoods.request.UserLoginRequest;
import com.zetafoods.service.TokenService;
import com.zetafoods.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.zetafoods.mapper.UserMapper.getUserFromRequest;
import static com.zetafoods.util.Constant.ErrorMessage.INCORRECT_PASSWORD;
import static com.zetafoods.util.Constant.ErrorMessage.USER_NOT_FOUND;
import static com.zetafoods.util.InsuranceUtil.matchPasswordAndHash;

@Service
public class UserServiceImpl implements UserService {

    private final TokenService tokenService;

    private final UserDao userDao;

    public UserServiceImpl(TokenService tokenService, UserDao userDao) {
        this.tokenService = tokenService;
        this.userDao = userDao;
    }

    @Override
    public User register(UserCreateRequest userCreateRequest) {
        User user = getUserFromRequest(userCreateRequest);
        return userDao.persist(user);
    }

    @Override
    public String login(UserLoginRequest userLoginRequest) {
        Optional<User> userOptional = userDao.getByEmail(userLoginRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND);
        }
        User user = userOptional.get();
        if (!matchPasswordAndHash(userLoginRequest.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException(INCORRECT_PASSWORD, InsuranceServiceErrorType.UNAUTHORIZED);
        }
        return tokenService.generateToken(user.getEmail(), user.getRole().name());
    }
}