package com.adhissoncedeno.sistemafacturacion.domain.port.in;

import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.command.UpdateCustomerCommand;

import java.util.UUID;

public interface UpdateCustomerUseCase {
    Customer update(String customerIdentificationNumber, UpdateCustomerCommand command);
}
