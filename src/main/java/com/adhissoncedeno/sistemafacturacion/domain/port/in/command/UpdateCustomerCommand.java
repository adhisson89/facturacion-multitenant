package com.adhissoncedeno.sistemafacturacion.domain.port.in.command;

public record UpdateCustomerCommand(
        String idType,
        String identificationNumber,
        String name,
        String email,
        String phone
) {}
