package com.example.sendyclone.domain.delivery.exception;

import com.example.sendyclone.global.exception.CustomException;
import com.example.sendyclone.global.exception.ErrorCode;

public class DeliveryNotFoundException extends CustomException {
    public DeliveryNotFoundException() {
        super(ErrorCode.DELIVERY_NOT_FOUND);
    }
}
