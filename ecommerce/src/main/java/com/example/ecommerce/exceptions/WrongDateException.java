package com.example.ecommerce.exceptions;

public class WrongDateException extends Exception {
    public WrongDateException() {
        super("Data range sbagliato");
    }
}
