package org.jenjetsu.com.restapi.controller;

import org.jenjetsu.com.restapi.exception.EntityCreateException;
import org.jenjetsu.com.restapi.exception.EntityDeleteException;
import org.jenjetsu.com.restapi.exception.EntityModifyException;
import org.jenjetsu.com.restapi.exception.EntityNotFoundException;
import org.jenjetsu.com.restapi.exception.EntityValidateException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.SneakyThrows;

@ControllerAdvice
public class ControllerExceptionAdvice {

    private final String RESPONSE_KEY = "error_message";

    @ExceptionHandler(value = EntityNotFoundException.class)
    @SneakyThrows
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
        JSONObject responseBody = new JSONObject();
        responseBody.put(this.RESPONSE_KEY, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody.toString());
    }

    @ExceptionHandler(value = EntityValidateException.class)
    @SneakyThrows
    public ResponseEntity<String> handleEntityValidateException(EntityValidateException e) {
        JSONObject responseBody = new JSONObject();
        responseBody.put(this.RESPONSE_KEY, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody.toString());
    }

    @ExceptionHandler(value = {
                                EntityCreateException.class,
                                EntityModifyException.class,
                                EntityDeleteException.class
                              })
    @SneakyThrows
    public ResponseEntity<String> handleInternalException(Exception e) {
        JSONObject responseBody = new JSONObject();
        responseBody.put(this.RESPONSE_KEY, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody.toString());
    }
}
