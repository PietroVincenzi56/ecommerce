package com.example.ecommerce.exceptions;

public class NotEnoughQuantityException extends Exception {
    public NotEnoughQuantityException() {
        super("Non abbastanza quantità");
    }
}
