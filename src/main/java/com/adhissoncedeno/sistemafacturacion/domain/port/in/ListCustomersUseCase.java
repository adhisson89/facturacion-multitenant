package com.adhissoncedeno.sistemafacturacion.domain.port.in;

import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;

import java.util.List;

public interface ListCustomersUseCase {
    List<Customer> list(String filter);
}
