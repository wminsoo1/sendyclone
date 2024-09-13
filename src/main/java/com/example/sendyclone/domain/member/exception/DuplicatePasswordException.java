package com.example.sendyclone.domain.member.exception;

import com.example.sendyclone.global.exception.CustomException;
import com.example.sendyclone.global.exception.ErrorCode;

public class DuplicatePasswordException extends CustomException {
    public DuplicatePasswordException() {
        super(ErrorCode.DUPLICATE_PASSWORD);
    }
}
