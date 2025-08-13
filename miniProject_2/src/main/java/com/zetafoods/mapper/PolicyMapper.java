package com.zetafoods.mapper;

import com.zetafoods.entity.Policy;
import com.zetafoods.request.PolicyCreateRequest;
import com.zetafoods.response.PolicyPaginatedResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

import static com.zetafoods.util.InsuranceUtil.MAPPER;

public class PolicyMapper {
    public static PolicyPaginatedResponse getResponseFromPoliciesPage(Page<Policy> policiesPage) {
        return PolicyPaginatedResponse.builder()
                .policies(policiesPage.getContent())
                .totalElements(policiesPage.getTotalElements())
                .totalPages(policiesPage.getTotalPages())
                .page(policiesPage.getPageable().getPageNumber())
                .size(policiesPage.getSize())
                .build();
    }

    public static Policy getPolicyFromRequest(PolicyCreateRequest policyCreateRequest) {
        Policy policy = MAPPER.map(policyCreateRequest, Policy.class);
        policy.setCreatedAt(LocalDateTime.now());
        return policy;
    }
}
