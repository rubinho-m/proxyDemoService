package com.rubinho.vkproxy.controllers;

import com.rubinho.vkproxy.dto.ErrorDto;
import com.rubinho.vkproxy.exceptions.AppException;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {PropertyValueException.class})
    public ResponseEntity<ErrorDto> missedValue(PropertyValueException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.builder().message(ex.getMessage()).build());
    }


    @ExceptionHandler(value = {AppException.class})
    public ResponseEntity<ErrorDto> handleException(AppException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ErrorDto.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorDto> handleServerBug(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorDto.builder().message(ex.getMessage()).build());
    }

}
