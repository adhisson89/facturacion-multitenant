package com.adhissoncedeno.sistemafacturacion.domain.port.in.command;

import com.adhissoncedeno.sistemafacturacion.domain.model.Address;

public record CreateCustomerCommand(
        String identificationType,
        String identificationNumber,
        String name,
        String email,
        String phoneNumber,
        Address primaryAddress
) {
}
