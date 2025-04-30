package com.adhissoncedeno.sistemafacturacion.infrastructure.config.tenant;

import com.adhissoncedeno.sistemafacturacion.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService {

    @Qualifier("defaultDataSource")
    private final DataSource dataSource;

    @Transactional
    public Tenant createTenant(String name, String schema) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM public.tenants WHERE schema = ?",
            Integer.class,
            schema
        );

        if (count != null && count > 0) {
            throw new TenantSchemaExistsException("Tenant with schema '" + schema + "' already exists");
        }

        UUID id = UUID.randomUUID();
        boolean active = true; // Default to active
        jdbcTemplate.update(
            "INSERT INTO public.tenants (id, name, schema, active) VALUES (?, ?, ?, ?)",
            id, name, schema, active
        );

        return new Tenant(id, name, schema, active);
    }

    public static class TenantSchemaExistsException extends RuntimeException {
        public TenantSchemaExistsException(String message) {
            super(message);
        }
    }
}