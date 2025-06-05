package com.example.ecommerce.exceptions;

public class PriceChangedException extends RuntimeException {
    public PriceChangedException() {
        super("Il prodotto che sta  cercando di acquistare è stato aggiornato, aggiorna il tuo carrello");
    }
}
