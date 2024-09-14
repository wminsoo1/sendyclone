package com.example.sendyclone.domain.delivery.exception;

import com.example.sendyclone.global.exception.CustomException;
import com.example.sendyclone.global.exception.ErrorCode;

public class DeliveryException extends CustomException {
    public DeliveryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static DeliveryException fromErrorCode(final ErrorCode errorCode) {
        return new DeliveryException(errorCode);
    }
}
