package com.adhissoncedeno.sistemafacturacion.domain.port.in;

import com.adhissoncedeno.sistemafacturacion.domain.model.Address;

import java.util.List;
import java.util.UUID;

public interface ListAddressesUseCase {
    List<Address> listAddresses(String customerIdentificationNumber);
}
