package com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tenants", schema = "public") // Store in public schema
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String schema;

    @Column(nullable = false)
    private boolean active;
}