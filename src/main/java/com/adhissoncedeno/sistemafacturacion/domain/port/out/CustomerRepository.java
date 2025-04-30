package com.adhissoncedeno.sistemafacturacion.domain.port.out;

import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    List<Customer> findByIdentificationNumberOrName(String criteria);
    Optional<Customer> findByIdentificationNumber(String identificationNumber);
    void deleteByIdentificationNumber(String identificationNumber);
    boolean existsByIdentificationNumber(String identificationNumber);

}
