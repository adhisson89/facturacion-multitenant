package com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.repository;

import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCustomerEntityRepository extends JpaRepository<CustomerEntity, UUID> {

    @Query("SELECT c FROM CustomerEntity c WHERE c.identificationNumber LIKE %:criteria% OR LOWER(c.name) LIKE LOWER(CONCAT('%', :criteria, '%'))")
    List<CustomerEntity> searchByTaxOrName(@Param("criteria") String criteria);

    Optional<CustomerEntity> findByIdentificationNumber(String identificationNumber);

    boolean existsByIdentificationNumber(String identificationNumber);

    @Modifying
    @Query("DELETE FROM CustomerEntity c WHERE c.identificationNumber = :identificationNumber")
    void deleteByIdentificationNumber(@Param("identificationNumber") String identificationNumber);
}