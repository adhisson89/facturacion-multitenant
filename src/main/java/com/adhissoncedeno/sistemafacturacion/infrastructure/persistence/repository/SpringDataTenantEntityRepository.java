package com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.repository;

import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTenantEntityRepository extends JpaRepository<TenantEntity, UUID> {
    Optional<TenantEntity> findBySchema(String schema);
}