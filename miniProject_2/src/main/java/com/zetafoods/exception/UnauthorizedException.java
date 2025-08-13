package com.zetafoods.exception;

import com.zetafoods.error.InsuranceServiceErrorType;

public class UnauthorizedException extends InsuranceServiceException {
    public UnauthorizedException(String message, InsuranceServiceErrorType errorType) {
        super(message, errorType);
    }
}
