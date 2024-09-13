package com.example.sendyclone.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    DELIVERY_NOT_FOUND("배달이 존재하지 않습니다", HttpStatus.NOT_FOUND),
    RESERVATION_NUMBER_NOT_FOUND("배달 예약 번호가 존재하지 않습니다", HttpStatus.NOT_FOUND),


    MEMBER_NOT_FOUND("멤버가 존재하지 않습니다", HttpStatus.NOT_FOUND),
    DUPLICATE_PASSWORD("이메일이 이미 존재합니다", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
