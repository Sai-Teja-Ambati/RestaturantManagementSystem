package com.zetafoods.repository.dao;

import com.zetafoods.entity.Policy;
import com.zetafoods.repository.PolicyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyDao extends PolicyRepository {
    default Page<Policy> getAll(Specification<Policy> specification, Pageable pageable) {
        return findAll(specification, pageable);
    }
}
