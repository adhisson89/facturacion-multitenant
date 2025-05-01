package com.adhissoncedeno.sistemafacturacion.application.service;

import com.adhissoncedeno.sistemafacturacion.domain.exception.CustomerNotFoundException;
import com.adhissoncedeno.sistemafacturacion.domain.exception.DuplicateCustomerException;
import com.adhissoncedeno.sistemafacturacion.domain.model.Address;
import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;
import com.adhissoncedeno.sistemafacturacion.domain.model.IdentificationType;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.command.CreateCustomerCommand;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.command.UpdateCustomerCommand;
import com.adhissoncedeno.sistemafacturacion.domain.port.out.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepo;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private Address address;
    private String identificationNumber;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        identificationNumber = "1234567890";
        customerId = UUID.randomUUID();
        address = new Address(UUID.randomUUID(), "Province", "City", "Street 123", true);
        customer = new Customer(
                customerId,
                IdentificationType.CEDULA.toString(),
                identificationNumber,
                "Test Customer",
                "test@example.com",
                "123456789",
                new ArrayList<>(List.of(address))
        );
    }

    @Test
    void listCustomers_shouldReturnListOfCustomers() {
        String criteria = "Test";
        when(customerRepo.findByIdentificationNumberOrName(criteria)).thenReturn(List.of(customer));

        List<Customer> result = customerService.list(criteria);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customer, result.getFirst());
        verify(customerRepo).findByIdentificationNumberOrName(criteria);
    }

    @Test
    void createCustomer_shouldCreateAndReturnCustomer() {
        Address address = new Address(UUID.randomUUID(), "Province", "City", "Street 123", true);
        CreateCustomerCommand cmd = new CreateCustomerCommand(
                IdentificationType.CEDULA.toString(),
                identificationNumber,
                "New Customer",
                "new@example.com",
                "987654321",
                address
        );

        when(customerRepo.existsByIdentificationNumber(identificationNumber)).thenReturn(false);
        when(customerRepo.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            return new Customer(
                    UUID.randomUUID(),
                    c.idType(),
                    c.identificationNumber(),
                    c.name(),
                    c.email(),
                    c.phone(),
                    c.addresses()
            );
        });


        Customer createdCustomer = customerService.create(cmd);

        assertNotNull(createdCustomer);
        assertNotNull(createdCustomer.id());
        assertEquals(cmd.identificationNumber(), createdCustomer.identificationNumber());
        assertEquals(cmd.name(), createdCustomer.name());
        assertEquals(1, createdCustomer.addresses().size());
        assertEquals(cmd.primaryAddress().street(), createdCustomer.addresses().getFirst().street());
        verify(customerRepo).existsByIdentificationNumber(identificationNumber);
        verify(customerRepo).save(any(Customer.class));
    }

    @Test
    void createCustomer_shouldThrowDuplicateCustomerException_whenIdentificationExists() {

        Address address = new Address(UUID.randomUUID(), "Province", "City", "Street 123", true);
        CreateCustomerCommand cmd = new CreateCustomerCommand(
                IdentificationType.CEDULA.toString(),
                identificationNumber,
                "New Customer",
                "new@example.com",
                "987654321",
                address
        );

        when(customerRepo.existsByIdentificationNumber(identificationNumber)).thenReturn(true);

        assertThrows(DuplicateCustomerException.class, () -> customerService.create(cmd));

        verify(customerRepo).existsByIdentificationNumber(identificationNumber);
        verify(customerRepo, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_shouldUpdateAndReturnCustomer() {
        UpdateCustomerCommand cmd = new UpdateCustomerCommand(
                null, // idType not changing
                null, // identificationNumber not changing
                "Updated Name",
                "updated@example.com",
                null // phone not changing
        );

        when(customerRepo.findByIdentificationNumber(identificationNumber)).thenReturn(Optional.of(customer));
        when(customerRepo.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updatedCustomer = customerService.update(identificationNumber, cmd);

        assertNotNull(updatedCustomer);
        assertEquals(customer.id(), updatedCustomer.id());
        assertEquals(identificationNumber, updatedCustomer.identificationNumber());
        assertEquals("Updated Name", updatedCustomer.name());
        assertEquals("updated@example.com", updatedCustomer.email());
        assertEquals(customer.phone(), updatedCustomer.phone()); // Unchanged field
        verify(customerRepo).findByIdentificationNumber(identificationNumber);
        verify(customerRepo).save(any(Customer.class));
    }

    @Test
    void updateCustomer_shouldUpdateIdentificationNumber() {
        String newIdentificationNumber = "0987654321";
        UpdateCustomerCommand cmd = new UpdateCustomerCommand(
                null,
                newIdentificationNumber,
                "Updated Name",
                null,
                null
        );

        when(customerRepo.findByIdentificationNumber(identificationNumber)).thenReturn(Optional.of(customer));
        when(customerRepo.existsByIdentificationNumber(newIdentificationNumber)).thenReturn(false); // New ID doesn't exist
        when(customerRepo.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updatedCustomer = customerService.update(identificationNumber, cmd);

        assertNotNull(updatedCustomer);
        assertEquals(newIdentificationNumber, updatedCustomer.identificationNumber());
        assertEquals("Updated Name", updatedCustomer.name());
        verify(customerRepo).findByIdentificationNumber(identificationNumber);
        verify(customerRepo).existsByIdentificationNumber(newIdentificationNumber);
        verify(customerRepo).save(any(Customer.class));
    }


    @Test
    void updateCustomer_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        UpdateCustomerCommand cmd = new UpdateCustomerCommand(null, null, "Updated Name", null, null);
        when(customerRepo.findByIdentificationNumber(identificationNumber)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.update(identificationNumber, cmd));

        verify(customerRepo).findByIdentificationNumber(identificationNumber);
        verify(customerRepo, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_shouldThrowDuplicateCustomerException_whenNewIdentificationExists() {
        String newIdentificationNumber = "0987654321";
        UpdateCustomerCommand cmd = new UpdateCustomerCommand(
                null,
                newIdentificationNumber,
                "Updated Name",
                null,
                null
        );

        when(customerRepo.findByIdentificationNumber(identificationNumber)).thenReturn(Optional.of(customer));
        when(customerRepo.existsByIdentificationNumber(newIdentificationNumber)).thenReturn(true);

        assertThrows(DuplicateCustomerException.class, () -> customerService.update(identificationNumber, cmd));

        verify(customerRepo).findByIdentificationNumber(identificationNumber);
        verify(customerRepo).existsByIdentificationNumber(newIdentificationNumber);
        verify(customerRepo, never()).save(any(Customer.class));
    }


    @Test
    void deleteCustomer_shouldDeleteCustomer() {
        when(customerRepo.existsByIdentificationNumber(identificationNumber)).thenReturn(true);
        doNothing().when(customerRepo).deleteByIdentificationNumber(identificationNumber);

        customerService.delete(identificationNumber);

        verify(customerRepo).existsByIdentificationNumber(identificationNumber);
        verify(customerRepo).deleteByIdentificationNumber(identificationNumber);
    }

    @Test
    void deleteCustomer_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        when(customerRepo.existsByIdentificationNumber(identificationNumber)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> customerService.delete(identificationNumber));

        verify(customerRepo).existsByIdentificationNumber(identificationNumber);
        verify(customerRepo, never()).deleteByIdentificationNumber(anyString());
    }

    @Test
    void addAddress_shouldAddAddressToCustomer() {
        Address newAddress = new Address(null, "New Province", "New City", "New Street", false);

        when(customerRepo.findByIdentificationNumber(identificationNumber)).thenReturn(Optional.of(customer));
        when(customerRepo.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        customerService.addAddress(identificationNumber, newAddress);

        // Verify save was called with a customer having 2 addresses
        verify(customerRepo).save(argThat(savedCustomer ->
                savedCustomer.id().equals(customerId) &&
                        savedCustomer.addresses().size() == 2 &&
                        savedCustomer.addresses().stream().anyMatch(a -> "New Street".equals(a.street()))
        ));
    }

    @Test
    void addAddress_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        Address newAddress = new Address(null, "New Province", "New City", "New Street", false);
        when(customerRepo.findByIdentificationNumber(identificationNumber)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.addAddress(identificationNumber, newAddress));

        verify(customerRepo).findByIdentificationNumber(identificationNumber);
        verify(customerRepo, never()).save(any(Customer.class));
    }

    @Test
    void listAddresses_shouldReturnListOfAddresses() {
        when(customerRepo.findByIdentificationNumber(identificationNumber)).thenReturn(Optional.of(customer));

        List<Address> addresses = customerService.listAddresses(identificationNumber);

        assertNotNull(addresses);
        assertEquals(1, addresses.size());
        assertEquals(address, addresses.getFirst());
        verify(customerRepo).findByIdentificationNumber(identificationNumber);
    }

    @Test
    void listAddresses_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        when(customerRepo.findByIdentificationNumber(identificationNumber)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.listAddresses(identificationNumber));

        verify(customerRepo).findByIdentificationNumber(identificationNumber);
    }

    @Test
    void listAddresses_shouldReturnEmptyList_whenCustomerHasNoAddresses() {
        customer.addresses().clear();
        when(customerRepo.findByIdentificationNumber(identificationNumber)).thenReturn(Optional.of(customer));

        List<Address> addresses = customerService.listAddresses(identificationNumber);

        assertNotNull(addresses);
        assertTrue(addresses.isEmpty());
        verify(customerRepo).findByIdentificationNumber(identificationNumber);
    }
}