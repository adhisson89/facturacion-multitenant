package com.adhissoncedeno.sistemafacturacion.domain.port.in;

import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.command.CreateCustomerCommand;

public interface CreateCustomerUseCase {
    Customer create(CreateCustomerCommand command);
}
