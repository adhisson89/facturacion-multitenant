package com.adhissoncedeno.sistemafacturacion.domain.model;

import java.util.UUID;

public record Tenant(
        UUID id,
        String name,
        String schema,
        boolean active
) {}