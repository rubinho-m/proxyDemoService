package com.rubinho.vkproxy.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
    HttpStatus status;
    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
