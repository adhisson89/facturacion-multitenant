package com.adhissoncedeno.sistemafacturacion.domain.port.out;

import com.adhissoncedeno.sistemafacturacion.domain.model.Tenant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {
    Tenant save(Tenant tenant);
    Optional<Tenant> findById(UUID id);
    Optional<Tenant> findBySchema(String schema);
    List<Tenant> findAll();
}