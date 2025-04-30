package com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.adapter;

import com.adhissoncedeno.sistemafacturacion.domain.model.Tenant;
import com.adhissoncedeno.sistemafacturacion.domain.port.out.TenantRepository;
import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.entity.TenantEntity;
import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.repository.SpringDataTenantEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TenantRepositoryAdapter implements TenantRepository {

    private final SpringDataTenantEntityRepository repository;

    @Override
    public Tenant save(Tenant tenant) {
        TenantEntity entity = toEntity(tenant);
        TenantEntity saved = repository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return repository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<Tenant> findBySchema(String schema) {
        return repository.findBySchema(schema).map(this::toModel);
    }

    @Override
    public List<Tenant> findAll() {
        return repository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private TenantEntity toEntity(Tenant model) {
        return new TenantEntity(
                model.id(),
                model.name(),
                model.schema(),
                model.active()
        );
    }

    private Tenant toModel(TenantEntity entity) {
        return new Tenant(
                entity.getId(),
                entity.getName(),
                entity.getSchema(),
                entity.isActive()
        );
    }
}
