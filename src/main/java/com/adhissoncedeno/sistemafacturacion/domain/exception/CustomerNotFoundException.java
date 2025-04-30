package com.adhissoncedeno.sistemafacturacion.domain.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String identificationNumber) {
        super("Customer with identification number '" + identificationNumber + "' not found.");
    }

    public CustomerNotFoundException(java.util.UUID id) {
        super("Customer with id '" + id + "' not found.");
    }
}
