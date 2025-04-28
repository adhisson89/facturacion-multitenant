package com.adhissoncedeno.sistemafacturacion.domain.exception;

public class DuplicateCustomerException extends RuntimeException {
    public DuplicateCustomerException(String idNumber) {
        super("Customer with identification '" + idNumber + "' already exists.");
    }
}
