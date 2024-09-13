package com.example.sendyclone.domain.delivery.exception;

import com.example.sendyclone.global.exception.CustomException;
import com.example.sendyclone.global.exception.ErrorCode;

public class ReservationNumberNotFoundException extends CustomException {
    public ReservationNumberNotFoundException() {
        super(ErrorCode.RESERVATION_NUMBER_NOT_FOUND);
    }
}
