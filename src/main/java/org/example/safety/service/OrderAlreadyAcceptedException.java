package org.example.safety.service;

public class OrderAlreadyAcceptedException extends RuntimeException {
    public OrderAlreadyAcceptedException(String message) {
        super(message);
    }
}
