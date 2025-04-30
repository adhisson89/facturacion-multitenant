package com.adhissoncedeno.sistemafacturacion.infrastructure.config.tenant;

import com.adhissoncedeno.sistemafacturacion.domain.model.Tenant;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.integration.spring.SpringResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchemaCreationService {

    @Qualifier("defaultDataSource")
    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;
    private static final String TENANT_CHANGELOG_PATH = "classpath:db/changelog/db.changelog-tenant.yaml";

    public void createSchema(Tenant tenant) {
        try (Connection connection = dataSource.getConnection()) {
            log.info("Attempting to create schema '{}' for tenant: {}", tenant.schema(), tenant.name());
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA IF NOT EXISTS " + tenant.schema());
                log.info("Schema '{}' created or already exists.", tenant.schema());
            }

            log.info("Running Liquibase tenant changelog ({}) on schema '{}'", TENANT_CHANGELOG_PATH, tenant.schema());
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setDefaultSchemaName(tenant.schema());

            SpringResourceAccessor resourceAccessor = new SpringResourceAccessor(resourceLoader);

            Liquibase liquibase = new Liquibase(TENANT_CHANGELOG_PATH,
                    resourceAccessor, database);

            liquibase.update(new Contexts(), new LabelExpression());

            log.info("Liquibase tenant changelog applied successfully to schema '{}' for tenant: {}",
                    tenant.schema(), tenant.name());

        } catch (Exception e) {
            log.error("Error during schema creation or Liquibase update for tenant '{}' (schema '{}'): {}",
                    tenant.name(), tenant.schema(), e.getMessage(), e);
            throw new RuntimeException("Could not create/update schema '" + tenant.schema() + "' for tenant '" + tenant.name() + "'", e);
        }

    }


}