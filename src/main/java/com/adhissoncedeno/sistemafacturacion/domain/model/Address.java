package com.adhissoncedeno.sistemafacturacion.domain.model;

import java.util.UUID;

public record Address(
        UUID id,
        String province,
        String city,
        String street,
        boolean primary
) {}
