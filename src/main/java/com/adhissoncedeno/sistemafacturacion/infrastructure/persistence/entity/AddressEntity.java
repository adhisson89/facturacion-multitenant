package com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String province;
    private String city;
    private String street;
    @Column(name = "is_primary")
    private boolean primary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
}

