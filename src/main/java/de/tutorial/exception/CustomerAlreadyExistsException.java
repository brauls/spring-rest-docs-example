package de.tutorial.exception;

public class CustomerAlreadyExistsException extends Exception {
    public CustomerAlreadyExistsException(final String message) {
        super(message);
    }
}
