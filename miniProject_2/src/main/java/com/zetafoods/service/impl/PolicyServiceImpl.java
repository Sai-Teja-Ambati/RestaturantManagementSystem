package com.zetafoods.service.impl;

import com.zetafoods.entity.Policy;
import com.zetafoods.repository.dao.PolicyDao;
import com.zetafoods.request.PolicyCreateRequest;
import com.zetafoods.service.PolicyService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.zetafoods.mapper.PolicyMapper.getPolicyFromRequest;
import static com.zetafoods.util.Constant.PolicyConstants.POLICY_NAME_PLACEHOLDER;
import static com.zetafoods.util.Constant.PolicyConstants.POLICY_TYPE_PLACEHOLDER;

@Service
public class PolicyServiceImpl implements PolicyService {
    private final PolicyDao policyDao;

    public PolicyServiceImpl(PolicyDao policyDao) {
        this.policyDao = policyDao;
    }

    @Override
    public Page<Policy> getPolicies(String type, Integer page, Integer size) {
        Specification<Policy> specification = getPoliciesQuery(type);
        return policyDao.getAll(specification, PageRequest.of(page, size, Sort.by(POLICY_NAME_PLACEHOLDER).ascending()));
    }

    @Override
    public Policy addPolicy(PolicyCreateRequest policyCreateRequest) {
        Policy policy = getPolicyFromRequest(policyCreateRequest);
        return policyDao.persist(policy);
    }

    private Specification<Policy> getPoliciesQuery(String type) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (StringUtils.isNotBlank(type)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(POLICY_TYPE_PLACEHOLDER), type));
            }
            return predicate;
        };
    }
}
