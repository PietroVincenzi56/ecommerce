package com.example.ecommerce.exceptions;


public class MailUserAlreadyExistsException extends Exception {
  public MailUserAlreadyExistsException() {
    super("Un utente con questa email esiste già.");
  }

}
