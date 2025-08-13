package com.zetafoods.exception;

import com.zetafoods.error.InsuranceServiceErrorType;

public class NotFoundException extends InsuranceServiceException {
    public NotFoundException(String message) {
        super(message, InsuranceServiceErrorType.NOT_FOUND);
    }
}
