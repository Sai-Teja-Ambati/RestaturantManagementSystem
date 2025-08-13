package com.zetafoods.repository;

import com.zetafoods.entity.User;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
