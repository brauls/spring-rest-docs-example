package de.tutorial.exception;

public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException(final String message) {
        super(message);
    }
}
