package com.example.sendyclone.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


public interface ErrorCode {
    String getMessage();
    HttpStatus getHttpStatus();

}
