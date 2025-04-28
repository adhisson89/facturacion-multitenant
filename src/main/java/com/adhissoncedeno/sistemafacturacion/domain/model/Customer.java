package com.adhissoncedeno.sistemafacturacion.domain.model;

import java.util.List;
import java.util.UUID;

public record Customer(
        UUID id,
        String idType,      // "RUC" or "ID"
        String identificationNumber,
        String name,
        String email,
        String phone,
        List<Address> addresses
) {}