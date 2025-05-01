package com.adhissoncedeno.sistemafacturacion.domain.port.in;

import com.adhissoncedeno.sistemafacturacion.domain.model.Address;


public interface AddAddressUseCase {
    void addAddress(String customerIdentificationNumber, Address address);
}
