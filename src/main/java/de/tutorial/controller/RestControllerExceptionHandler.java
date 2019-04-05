package de.tutorial.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.tutorial.exception.CustomerAlreadyExistsException;
import de.tutorial.exception.CustomerNotFoundException;
import de.tutorial.model.ErrorResponse;

@ControllerAdvice
public class RestControllerExceptionHandler {
    static final String NOT_FOUND_HINT = "Consider calling GET /customers to receive a list of all available customers";
    static final String ALREADY_EXISTS_HINT = NOT_FOUND_HINT;

    @ExceptionHandler(value = {CustomerNotFoundException.class})
    protected ResponseEntity<ErrorResponse> handleCustomerNotFound(final CustomerNotFoundException exception) {
        final ErrorResponse response = new ErrorResponse(exception.getMessage(), NOT_FOUND_HINT);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = {CustomerAlreadyExistsException.class})
    protected ResponseEntity<ErrorResponse> handleCustomerAlreadyExists(final CustomerAlreadyExistsException exception) {
        final ErrorResponse response = new ErrorResponse(exception.getMessage(), ALREADY_EXISTS_HINT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
