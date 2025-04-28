package com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.mapper;

import com.adhissoncedeno.sistemafacturacion.domain.model.Address;
import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;
import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.entity.AddressEntity;
import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    public Customer toDomain(CustomerEntity e) {
        var addrs = e.getAddresses().stream()
                .map(a -> new Address(
                        a.getId(),
                        a.getProvince(),
                        a.getCity(),
                        a.getStreet(),
                        a.isPrimary()
                ))
                .collect(Collectors.toList());
        return new Customer(
                e.getId(), e.getIdType(), e.getTaxNumber(),
                e.getName(), e.getEmail(), e.getPhone(), addrs
        );
    }

    public CustomerEntity toEntity(Customer c) {
        var entity = CustomerEntity.builder()
                .id(c.id())
                .idType(c.idType())
                .taxNumber(c.identificationNumber())
                .name(c.name())
                .email(c.email())
                .phone(c.phone())
                .build();
        var addrs = c.addresses().stream()
                .map(a -> AddressEntity.builder()
                        .id(a.id())
                        .province(a.province())
                        .city(a.city())
                        .street(a.street())
                        .primary(a.primary())
                        .customer(entity)
                        .build())
                .collect(Collectors.toList());
        entity.setAddresses(addrs);
        return entity;
    }
}
