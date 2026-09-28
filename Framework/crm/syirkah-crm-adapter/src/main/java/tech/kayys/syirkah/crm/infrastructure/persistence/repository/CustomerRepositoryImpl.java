package tech.kayys.syirkah.crm.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.crm.domain.identifier.CustomerId;
import tech.kayys.syirkah.crm.domain.model.Customer;
import tech.kayys.syirkah.crm.domain.repository.CustomerRepository;
import tech.kayys.syirkah.crm.infrastructure.persistence.entity.CustomerEntity;
import tech.kayys.syirkah.crm.infrastructure.persistence.mapper.CustomerMapper;

import jakarta.enterprise.context.ApplicationScoped;
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
            .map(v -> {
                customer.clearEvents();
                return customer;
            })
        ).subscribe().asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Optional<Customer>> findById(CustomerId id) {
        Uni<CustomerEntity> uni = CustomerEntity.findById(id.getValue());
        return uni
            .map(entity -> entity == null ? Optional.<Customer>empty() : Optional.of(mapper.toDomain(entity)))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> existsById(CustomerId id) {
        Uni<CustomerEntity> uni = CustomerEntity.findById(id.getValue());
        return uni
            .map(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(Customer customer) {
        return CustomerEntity.deleteById(customer.getId().getValue())
            .map(v -> (Void) null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(CustomerId id) {
        return CustomerEntity.deleteById(id.getValue())
            .map(v -> (Void) null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Customer> findByEmail(String email) {
        Uni<CustomerEntity> uni = CustomerEntity.find("email = ?1", email).firstResult();
        return uni
            .map(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Customer> findByCustomerNumber(String customerNumber) {
        Uni<CustomerEntity> uni = CustomerEntity.find("customerNumber = ?1", customerNumber).firstResult();
        return uni
            .map(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Customer>> findByCompanyName(String companyName) {
        Uni<List<CustomerEntity>> uni = CustomerEntity.list("companyName like ?1", "%" + companyName + "%");
        return uni
            .map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Customer>> findByIndustry(String industry) {
        Uni<List<CustomerEntity>> uni = CustomerEntity.list("industry = ?1", industry);
        return uni
            .map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Customer>> findActiveCustomers() {
        Uni<List<CustomerEntity>> uni = CustomerEntity.list("active = true");
        return uni
            .map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> existsByEmail(String email) {
        return CustomerEntity.count("email = ?1", email)
            .map(count -> count > 0)
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