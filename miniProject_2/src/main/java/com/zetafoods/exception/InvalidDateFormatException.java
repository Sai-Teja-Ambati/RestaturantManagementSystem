package com.zetafoods.exception;


import com.zetafoods.error.InsuranceServiceErrorType;

public class InvalidDateFormatException extends InsuranceServiceException {
    public InvalidDateFormatException(String message, Throwable cause) {
        super(message, cause, InsuranceServiceErrorType.BAD_REQUEST);
    }
}
