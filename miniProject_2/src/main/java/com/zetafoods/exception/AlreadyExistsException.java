package com.zetafoods.exception;

import com.zetafoods.error.InsuranceServiceErrorType;

public class AlreadyExistsException extends InsuranceServiceException {
    public AlreadyExistsException(String message) {
        super(message, InsuranceServiceErrorType.ALREADY_EXISTS.ALREADY_EXISTS);
    }
}
