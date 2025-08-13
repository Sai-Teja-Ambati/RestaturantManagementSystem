package com.zetafoods.service;

import com.zetafoods.entity.Policy;
import com.zetafoods.request.PolicyCreateRequest;
import org.springframework.data.domain.Page;

public interface PolicyService {
    Page<Policy> getPolicies(String type, Integer page, Integer size);

    Policy addPolicy(PolicyCreateRequest policyCreateRequest);
}
