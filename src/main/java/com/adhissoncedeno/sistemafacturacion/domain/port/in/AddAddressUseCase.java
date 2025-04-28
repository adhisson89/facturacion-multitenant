package com.adhissoncedeno.sistemafacturacion.domain.port.in;

import com.adhissoncedeno.sistemafacturacion.domain.model.Address;

import java.util.UUID;

public interface AddAddressUseCase {
    void addAddress(UUID customerId, Address address);
}
