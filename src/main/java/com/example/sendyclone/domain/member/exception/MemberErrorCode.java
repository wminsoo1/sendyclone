package com.example.sendyclone.domain.member.exception;

import com.example.sendyclone.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND("멤버가 존재하지 않습니다", HttpStatus.NOT_FOUND),
    DUPLICATE_PASSWORD("이메일이 이미 존재합니다", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus httpStatus;

    MemberErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
