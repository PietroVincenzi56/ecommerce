package com.example.ecommerce.exceptions;

public class ProductNotFoundException extends Exception {
    public ProductNotFoundException() {
        super("Prodotto non trovato");
    }
}
