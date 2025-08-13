package com.zetafoods.exception;


import com.zetafoods.error.InsuranceServiceErrorType;

public class BadRequestException extends InsuranceServiceException {
    public BadRequestException(String message, Throwable cause) {
        super(message, cause, InsuranceServiceErrorType.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(message, InsuranceServiceErrorType.BAD_REQUEST);
    }
}
