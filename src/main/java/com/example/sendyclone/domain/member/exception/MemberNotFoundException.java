package com.example.sendyclone.domain.member.exception;

import com.example.sendyclone.global.exception.CustomException;
import com.example.sendyclone.global.exception.ErrorCode;

public class MemberNotFoundException extends CustomException {
    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
