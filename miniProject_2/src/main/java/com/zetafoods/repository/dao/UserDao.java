package com.zetafoods.repository.dao;

import com.zetafoods.entity.User;
import com.zetafoods.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends UserRepository {
    default Optional<User> getByEmail(String email) {
        return findByEmail(email);
    }
}
