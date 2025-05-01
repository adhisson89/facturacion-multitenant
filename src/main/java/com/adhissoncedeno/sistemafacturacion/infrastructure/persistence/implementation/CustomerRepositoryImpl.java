package com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.implementation;

import com.adhissoncedeno.sistemafacturacion.domain.model.Customer;
import com.adhissoncedeno.sistemafacturacion.domain.port.out.CustomerRepository;
import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.mapper.CustomerMapper;
import com.adhissoncedeno.sistemafacturacion.infrastructure.persistence.repository.SpringDataCustomerEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {
    private final SpringDataCustomerEntityRepository repo;
    private final CustomerMapper mapper;

    @Override
    @Transactional
    public Customer save(Customer customer) {
        var entity = mapper.toEntity(customer);
        entity.getAddresses().forEach(addr -> addr.setCustomer(entity));
        var saved = repo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findByIdentificationNumberOrName(String criteria) {
        return repo.searchByTaxOrName(criteria).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findByIdentificationNumber(String identificationNumber) {
        return repo.findByIdentificationNumber(identificationNumber)
                   .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteByIdentificationNumber(String identificationNumber) {
        repo.findByIdentificationNumber(identificationNumber).ifPresent(customerEntity -> {
            repo.delete(customerEntity);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdentificationNumber(String identificationNumber) {
        return repo.existsByIdentificationNumber(identificationNumber);
    }
}