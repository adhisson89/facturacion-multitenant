package com.adhissoncedeno.sistemafacturacion.application.service;

import com.adhissoncedeno.sistemafacturacion.domain.exception.DuplicateCustomerException;
import com.adhissoncedeno.sistemafacturacion.domain.model.Address;
import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.*;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.command.CreateCustomerCommand;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.command.UpdateCustomerCommand;
import com.adhissoncedeno.sistemafacturacion.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService implements
        ListCustomersUseCase,
        CreateCustomerUseCase,
        UpdateCustomerUseCase,
        DeleteCustomerUseCase,
        AddAddressUseCase,
        ListAddressesUseCase {

    private final CustomerRepository customerRepo;

    @Override
    public List<Customer> list(String criteria) {
        return customerRepo.findByIdentificationNumberOrName(criteria);
    }

    @Override
    public Customer create(CreateCustomerCommand cmd) {

        boolean exists = customerRepo.findByIdentificationNumberOrName(cmd.identificationNumber()).stream()
                .anyMatch(c -> c.identificationNumber().equals(cmd.identificationNumber()));
        if (exists) {
            throw new DuplicateCustomerException(cmd.identificationNumber());
        }

        var cmdAddr = cmd.primaryAddress();
        var address = new Address(
                null,
                cmdAddr.province(),
                cmdAddr.city(),
                cmdAddr.street(),
                cmdAddr.primary()
        );

        var customer = new Customer(
                null,
                cmd.identificationType(),
                cmd.identificationNumber(),
                cmd.name(),
                cmd.email(),
                cmd.phoneNumber(),
                List.of(address)
        );
        return customerRepo.save(customer);
    }

    @Override
    public Customer update(UUID id, UpdateCustomerCommand cmd) {
        var existing = customerRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        // ensure no duplicate identificationNumber
        customerRepo.findByIdentificationNumberOrName(cmd.taxNumber()).stream()
                .filter(c -> !c.id().equals(id) && c.identificationNumber().equals(cmd.taxNumber()))
                .findAny()
                .ifPresent(c -> { throw new IllegalStateException("Identification number in use"); });
        var updated = new Customer(
                id,
                cmd.idType(),
                cmd.taxNumber(),
                cmd.name(),
                cmd.email(),
                cmd.phone(),
                existing.addresses()
        );
        return customerRepo.save(updated);
    }

    @Override
    public void delete(UUID id) {
        customerRepo.deleteById(id);
    }

    @Override
    public void addAddress(UUID customerId, Address address) {
        var existing = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        var newAddr = new Address(
                null,
                address.province(),
                address.city(),
                address.street(),
                address.primary()
        );

        var newList = new java.util.ArrayList<>(existing.addresses());
        newList.add(newAddr);
        var updated = new Customer(
                customerId,
                existing.idType(),
                existing.identificationNumber(),
                existing.name(),
                existing.email(),
                existing.phone(),
                List.copyOf(newList)
        );
        customerRepo.save(updated);
    }

    @Override
    public List<Address> listAddresses(UUID customerId) {
        return customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"))
                .addresses();
    }
}