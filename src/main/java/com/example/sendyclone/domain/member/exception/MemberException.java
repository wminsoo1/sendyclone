package com.example.sendyclone.domain.member.exception;

import com.example.sendyclone.global.exception.CustomException;
import com.example.sendyclone.global.exception.ErrorCode;

public class MemberException extends CustomException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static MemberException fromErrorCode(final ErrorCode errorCode) {
        return new MemberException(errorCode);
    }
}
