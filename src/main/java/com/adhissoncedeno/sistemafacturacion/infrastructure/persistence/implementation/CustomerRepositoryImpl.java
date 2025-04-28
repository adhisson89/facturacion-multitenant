package com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.implementation;

import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;
import com.adhissoncedeno.sistemafacturacion.domain.port.out.CustomerRepository;
import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.mapper.CustomerMapper;
import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.repository.SpringDataCustomerEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {
    private final SpringDataCustomerEntityRepository repo;
    private final CustomerMapper mapper;

    @Override
    public Customer save(Customer customer) {
        var entity = mapper.toEntity(customer);
        var saved = repo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return repo.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Customer> findByIdentificationNumberOrName(String criteria) {
        return repo.searchByTaxOrName(criteria).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
