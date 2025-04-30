package com.adhissoncedeno.sistemafacturacion.infrastructure.web;

import com.adhissoncedeno.sistemafacturacion.domain.model.Address;
import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.*;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.command.CreateCustomerCommand;
import com.adhissoncedeno.sistemafacturacion.domain.port.in.command.UpdateCustomerCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final ListCustomersUseCase listUC;
    private final CreateCustomerUseCase createUC;
    private final UpdateCustomerUseCase updateUC;
    private final DeleteCustomerUseCase deleteUC;
    private final AddAddressUseCase addAddrUC;
    private final ListAddressesUseCase listAddrUC;

    @GetMapping
    public List<Customer> search(@RequestParam(defaultValue = "") String q) {
        return listUC.list(q);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@RequestBody CreateCustomerCommand cmd) {
        return createUC.create(cmd);
    }

    @PutMapping("/{identificationNumber}")
    public Customer update(
            @PathVariable String identificationNumber,
            @RequestBody UpdateCustomerCommand cmd
    ) {
        return updateUC.update(identificationNumber, cmd);
    }

    @DeleteMapping("/{identificationNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String identificationNumber) {
        deleteUC.delete(identificationNumber);
    }

    @PostMapping("/{identificationNumber}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAddress(
            @PathVariable String identificationNumber,
            @RequestBody Address address
    ) {
        addAddrUC.addAddress(identificationNumber, address);
    }

    @GetMapping("/{identificationNumber}/addresses")
    public List<Address> listAddresses(@PathVariable String identificationNumber) {
        return listAddrUC.listAddresses(identificationNumber);
    }
}