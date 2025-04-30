package com.adhissoncedeno.sistemafacturacion.application.service;

import com.adhissoncedeno.sistemafacturacion.domain.exception.CustomerNotFoundException;
import com.adhissoncedeno.sistemafacturacion.domain.exception.DuplicateCustomerException;
import com.adhissoncedeno.sistemafacturacion.domain.model.Address;
import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.*;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.command.CreateCustomerCommand;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.command.UpdateCustomerCommand;
import com.adhissoncedeno.sistemafacturacion.domain.port.out.CustomerRepository;
import com.adhissoncedeno.sistemafacturacion.infrastructure.config.tenant.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Override
    @Transactional(readOnly = true)
    public List<Customer> list(String criteria) {
        return customerRepo.findByIdentificationNumberOrName(criteria);
    }

    @Override
    @Transactional
    public Customer create(CreateCustomerCommand cmd) {
        String currentTenant = TenantContextHolder.getTenantId();
        log.info("Creating customer in tenant schema: {}", currentTenant);

        if (customerRepo.existsByIdentificationNumber(cmd.identificationNumber())) {
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
    @Transactional
    public Customer update(String identificationNumber, UpdateCustomerCommand cmd) {

        Customer existing = customerRepo.findByIdentificationNumber(identificationNumber)
                .orElseThrow(() -> new CustomerNotFoundException(identificationNumber));


        String newIdentificationNumber = cmd.identificationNumber() != null && !cmd.identificationNumber().equals(identificationNumber)
                ? cmd.identificationNumber()
                : identificationNumber;

        if (!newIdentificationNumber.equals(identificationNumber)) {
            if (customerRepo.existsByIdentificationNumber(newIdentificationNumber)) {
                throw new DuplicateCustomerException(newIdentificationNumber);
            }
        }


        Customer updatedCustomer = new Customer(
                existing.id(),
                cmd.idType() != null ? cmd.idType() : existing.idType(),
                newIdentificationNumber,
                cmd.name() != null ? cmd.name() : existing.name(),
                cmd.email() != null ? cmd.email() : existing.email(),
                cmd.phone() != null ? cmd.phone() : existing.phone(),
                existing.addresses()
        );

        return customerRepo.save(updatedCustomer);
    }


    @Override
    @Transactional
    public void delete(String identificationNumber) {
        if (!customerRepo.existsByIdentificationNumber(identificationNumber)) {
            throw new CustomerNotFoundException(identificationNumber);
        }
        customerRepo.deleteByIdentificationNumber(identificationNumber);
    }

    @Override
    @Transactional
    public void addAddress(String customerIdentificationNumber, Address address) {
        Customer existing = customerRepo.findByIdentificationNumber(customerIdentificationNumber)
                .orElseThrow(() -> new CustomerNotFoundException(customerIdentificationNumber));

        var newAddr = new Address(
                null,
                address.province(),
                address.city(),
                address.street(),
                address.primary()
        );


        var newList = new ArrayList<>(existing.addresses());
        newList.add(newAddr);

        var updated = new Customer(
                existing.id(),
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
    @Transactional(readOnly = true)
    public List<Address> listAddresses(String customerIdentificationNumber) {
        return customerRepo.findByIdentificationNumber(customerIdentificationNumber)
                .map(Customer::addresses)
                .orElseThrow(() -> new CustomerNotFoundException(customerIdentificationNumber));
    }
}