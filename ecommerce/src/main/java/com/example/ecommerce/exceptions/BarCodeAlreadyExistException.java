package com.example.ecommerce.exceptions;

public class BarCodeAlreadyExistException extends Exception {
    public BarCodeAlreadyExistException() {
        super("Bar code already exist: " );
    }
}
