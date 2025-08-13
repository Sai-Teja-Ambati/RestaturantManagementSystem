package com.zetafoods.repository;

import com.zetafoods.entity.Policy;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PolicyRepository extends BaseRepository<Policy, Long> {
}
