package com.adhissoncedeno.sistemafacturacion.domain.port.in;

import java.util.UUID;

public interface DeleteCustomerUseCase {
    void delete(String identificationNumber);
}
