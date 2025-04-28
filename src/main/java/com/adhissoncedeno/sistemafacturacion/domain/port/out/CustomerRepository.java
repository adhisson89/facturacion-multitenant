package com.adhissoncedeno.sistemafacturacion.domain.port.out;

import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    List<Customer> findByIdentificationNumberOrName(String criteria);
    void deleteById(UUID id);
}
