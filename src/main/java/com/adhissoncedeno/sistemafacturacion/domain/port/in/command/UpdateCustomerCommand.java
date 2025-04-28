package com.adhissoncedeno.sistemafacturacion.domain.port.in.command;

public record UpdateCustomerCommand(
        String idType,
        String taxNumber,
        String name,
        String email,
        String phone
) {}
