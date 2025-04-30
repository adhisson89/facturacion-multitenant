package com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.repository;

import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCustomerEntityRepository extends JpaRepository<CustomerEntity, UUID> {
    @Query("SELECT c FROM CustomerEntity c WHERE c.identificationNumber LIKE %:criteria% OR c.name LIKE %:criteria%")
    List<CustomerEntity> searchByTaxOrName(@Param("criteria") String criteria);
}
