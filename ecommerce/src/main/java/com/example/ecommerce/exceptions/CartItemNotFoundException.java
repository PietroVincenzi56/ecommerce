package com.example.ecommerce.exceptions;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException() {
        super("Elemento carrello non trovato per prodotto id");
    }
}
