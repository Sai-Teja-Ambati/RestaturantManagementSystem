package com.zetafoods.service.impl;

import com.zetafoods.entity.User;
import com.zetafoods.entity.UserPrincipal;
import com.zetafoods.repository.dao.UserDao;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    public MyUserDetailsServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDao.getByEmail(email).orElseThrow();
        return new UserPrincipal(user);
    }
}
