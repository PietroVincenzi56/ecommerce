package com.example.ecommerce.exceptions;

public class UserNotExistsException extends Exception {
    public UserNotExistsException() {
        super("Errore: User not exists.");
    }
}
