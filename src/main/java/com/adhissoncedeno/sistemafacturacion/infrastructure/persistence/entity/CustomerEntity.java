package com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "id_type")
    private String idType;
    @Column(name = "identification_Number")
    private String identificationNumber;
    private String name;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddressEntity> addresses;
}
