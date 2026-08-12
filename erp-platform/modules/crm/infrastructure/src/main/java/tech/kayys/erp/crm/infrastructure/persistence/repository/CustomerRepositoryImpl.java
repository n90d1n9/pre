package tech.kayys.erp.crm.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import tech.kayys.erp.crm.domain.identifier.CustomerId;
import tech.kayys.erp.crm.domain.model.Customer;
import tech.kayys.erp.crm.domain.repository.CustomerRepository;
import tech.kayys.erp.crm.infrastructure.persistence.entity.CustomerEntity;
import tech.kayys.erp.crm.infrastructure.persistence.mapper.CustomerMapper;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of CustomerRepository.
 */
@ApplicationScoped
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerMapper mapper;

    public CustomerRepositoryImpl(CustomerMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<Customer> save(Customer customer) {
        CustomerEntity entity = mapper.toEntity(customer);
        
        return Panache.withTransaction(() -> entity.<CustomerEntity>persist()
            .onItem()
            .transform(v -> {
                customer.clearEvents();
                return customer;
            })
            .subscribe()
            .asCompletionStage());
    }

    @Override
    @WithSession
    public CompletionStage<Optional<Customer>> findById(CustomerId id) {
        return CustomerEntity.<CustomerEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> {
                if (entity == null) {
                    return Optional.empty();
                }
                return Optional.of(mapper.toDomain(entity));
            })
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> existsById(CustomerId id) {
        return CustomerEntity.<CustomerEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(Customer customer) {
        return CustomerEntity.deleteById(customer.getId().getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(CustomerId id) {
        return CustomerEntity.deleteById(id.getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Customer> findByEmail(String email) {
        return CustomerEntity.find("email = ?1", email)
            .firstResult()
            .onItem()
            .transform(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Customer> findByCustomerNumber(String customerNumber) {
        return CustomerEntity.find("customerNumber = ?1", customerNumber)
            .firstResult()
            .onItem()
            .transform(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Customer>> findByCompanyName(String companyName) {
        return CustomerEntity.list("companyName like ?1", "%" + companyName + "%")
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Customer>> findByIndustry(String industry) {
        return CustomerEntity.list("industry = ?1", industry)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Customer>> findActiveCustomers() {
        return CustomerEntity.list("active = true")
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> existsByEmail(String email) {
        return CustomerEntity.count("email = ?1", email)
            .onItem()
            .transform(count -> count > 0)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByIndustry(String industry) {
        return CustomerEntity.count("industry = ?1", industry)
            .subscribe()
            .asCompletionStage();
    }
}