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
import java.util.UUID;

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

    @PutMapping("/{id}")
    public Customer update(
            @PathVariable UUID id,
            @RequestBody UpdateCustomerCommand cmd
    ) {
        return updateUC.update(id, cmd);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        deleteUC.delete(id);
    }

    @PostMapping("/{id}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAddress(
            @PathVariable UUID id,
            @RequestBody Address address
    ) {
        addAddrUC.addAddress(id, address);
    }

    @GetMapping("/{id}/addresses")
    public List<Address> listAddresses(@PathVariable UUID id) {
        return listAddrUC.listAddresses(id);
    }
}
