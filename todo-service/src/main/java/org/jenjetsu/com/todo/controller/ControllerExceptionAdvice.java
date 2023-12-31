package org.jenjetsu.com.todo.controller;

import org.jenjetsu.com.todo.exception.EntityAccessDeniedException;
import org.jenjetsu.com.todo.exception.EntityCreateException;
import org.jenjetsu.com.todo.exception.EntityDeleteException;
import org.jenjetsu.com.todo.exception.EntityModifyException;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.exception.EntityValidateException;
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

    @ExceptionHandler(value = EntityAccessDeniedException.class)
    @SneakyThrows
    public ResponseEntity<String> handleEntityAccessDenidedException(EntityAccessDeniedException e) {
        JSONObject responseBody = new JSONObject();
        responseBody.put(this.RESPONSE_KEY, e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody.toString());
    }
}
