package com.adhissoncedeno.sistemafacturacion.infrastructure.web;

import com.adhissoncedeno.sistemafacturacion.domain.model.Tenant;
import com.adhissoncedeno.sistemafacturacion.domain.port.out.TenantRepository;
import com.adhissoncedeno.sistemafacturacion.infrastructure.config.tenant.SchemaCreationService;
import com.adhissoncedeno.sistemafacturacion.infrastructure.config.tenant.TenantDatabaseConfig.TenantRoutingDataSource;
import com.adhissoncedeno.sistemafacturacion.infrastructure.config.tenant.TenantService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Slf4j
public class TenantController {

    private final TenantRepository tenantRepository;
    private final TenantService tenantService;
    private final SchemaCreationService schemaCreationService;
    private final TenantRoutingDataSource tenantRoutingDataSource;
    private final Environment env;

    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody TenantRequest request) {
        Tenant newTenant = null;
        try {
            newTenant = tenantService.createTenant(request.name(), request.schema());
            log.info("Tenant record created: {}", newTenant);

            schemaCreationService.createSchema(newTenant);
            log.info("Schema and tables created successfully for tenant: {}", newTenant.schema());

            try {
                DataSource tenantDataSource = createDataSourceForTenant(newTenant.schema());
                tenantRoutingDataSource.addTenant(newTenant.schema(), tenantDataSource);
                log.info("DataSource for tenant '{}' added to TenantRoutingDataSource.", newTenant.schema());
            } catch (Exception e) {
                log.error("Failed to create and add DataSource for new tenant {} ({}). Schema might be created but routing will fail.",
                        newTenant.schema(), newTenant.id(), e);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Tenant and schema created, but failed to configure routing: " + e.getMessage()));
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("id", newTenant.id(), "schema", newTenant.schema()));

        } catch (TenantService.TenantSchemaExistsException e) {
            log.warn("Attempted to create tenant with existing schema: {}", request.schema());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {

            String tenantSchema = (newTenant != null) ? newTenant.schema() : request.schema();
            log.error("Error during tenant creation process for schema: {}. Error: {}", tenantSchema, e.getMessage(), e);

            String errorMessage = "Failed to create tenant";
            if (e.getMessage().contains("Could not create/update schema")) {
                errorMessage = "Tenant record created, but failed to initialize schema: " + e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            } else if (newTenant != null) {
                errorMessage = "Tenant record created, but failed during setup: " + e.getMessage();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", errorMessage));
        }
    }

    private DataSource createDataSourceForTenant(String schema) {
        HikariConfig config = new HikariConfig();
        String driverClassName = env.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        config.setDriverClassName(driverClassName);

        String url = env.getProperty("spring.datasource.url");
        if (url == null) {
            throw new IllegalStateException("Database URL is not configured");
        }

        String tenantUrl = url;
        if (!tenantUrl.contains("currentSchema=")) {
            tenantUrl += (tenantUrl.contains("?") ? "&" : "?") + "currentSchema=" + schema;
        } else {
            tenantUrl = tenantUrl.replaceAll("currentSchema=[^&]*", "currentSchema=" + schema);
            if (!tenantUrl.contains("currentSchema=")) {
                tenantUrl += (tenantUrl.contains("?") ? "&" : "?") + "currentSchema=" + schema;
            }
        }
        config.setJdbcUrl(tenantUrl);

        config.setUsername(env.getProperty("spring.datasource.username"));
        config.setPassword(env.getProperty("spring.datasource.password"));
        config.setPoolName("tenant-" + schema + "-pool");

        return new HikariDataSource(config);
    }


    record TenantRequest(String name, String schema) {
    }

}