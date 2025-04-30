package com.adhissoncedeno.sistemafacturacion.infrastructure.config.tenant;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class TenantDatabaseConfig {

    @Bean(name = "defaultDataSource")
    @Primary
    public DataSource defaultDataSource(Environment env) {

        HikariConfig config = new HikariConfig();
        String driverClassName = env.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        config.setDriverClassName(driverClassName);

        String url = env.getProperty("spring.datasource.url");
        if (url == null) {
            throw new IllegalStateException("Database URL is not configured");
        }
        config.setJdbcUrl(url);

        config.setUsername(env.getProperty("spring.datasource.username"));
        config.setPassword(env.getProperty("spring.datasource.password"));
        config.setPoolName("default-pool");

        return new HikariDataSource(config);
    }

    @Bean
    public TenantDataSourceInitializer tenantDataSourceInitializer() {
        return new TenantDataSourceInitializer();
    }

    @Bean(name = "dataSource")
    public DataSource routingDataSource(@Qualifier("defaultDataSource") DataSource defaultDataSource) {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        routingDataSource.setTargetDataSources(new ConcurrentHashMap<>());
        return routingDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource,
            Environment env) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto", "none"));
        jpaProperties.put("hibernate.show_sql", env.getProperty("spring.jpa.show-sql", "false"));

        em.setJpaProperties(jpaProperties);
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    public static class TenantRoutingDataSource extends AbstractRoutingDataSource {
        @Override
        protected Object determineCurrentLookupKey() {
            return TenantContextHolder.getTenantId();
        }

        public void addTenant(String tenant, DataSource dataSource) {
            Map<Object, Object> dataSources = new ConcurrentHashMap<>(getResolvedDataSources());
            dataSources.put(tenant, dataSource);
            setTargetDataSources(dataSources);
            afterPropertiesSet();
        }
    }

    public static class TenantDataSourceInitializer implements ApplicationListener<ApplicationReadyEvent> {
        @Autowired
        private Environment env;

        @Autowired
        @Qualifier("defaultDataSource")
        private DataSource defaultDataSource;

        @Autowired
        private TenantRoutingDataSource routingDataSource;

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(defaultDataSource);
            List<Map<String, Object>> tenants = jdbcTemplate.queryForList(
                    "SELECT id, name, schema, active FROM tenants WHERE active = true"
            );

            for (Map<String, Object> tenant : tenants) {
                String schema = (String) tenant.get("schema");
                if (schema != null) {
                    DataSource tenantDataSource = createDataSourceForTenant(schema);
                    routingDataSource.addTenant(schema, tenantDataSource);
                }
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
    }
}