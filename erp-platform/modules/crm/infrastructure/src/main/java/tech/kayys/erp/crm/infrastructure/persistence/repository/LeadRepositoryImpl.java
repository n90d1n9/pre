package tech.kayys.erp.crm.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import tech.kayys.erp.crm.domain.identifier.LeadId;
import tech.kayys.erp.crm.domain.model.Lead;
import tech.kayys.erp.crm.domain.repository.LeadRepository;
import tech.kayys.erp.crm.domain.valueobject.LeadStatus;
import tech.kayys.erp.crm.infrastructure.persistence.entity.LeadEntity;
import tech.kayys.erp.crm.infrastructure.persistence.mapper.LeadMapper;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of LeadRepository using Hibernate Reactive Panache.
 */
@ApplicationScoped
public class LeadRepositoryImpl implements LeadRepository {

    private final LeadMapper mapper;

    public LeadRepositoryImpl(LeadMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<Lead> save(Lead lead) {
        LeadEntity entity = mapper.toEntity(lead);
        
        if (entity.id != null) {
            return Panache.withTransaction(() -> entity.<LeadEntity>persist()
                .onItem()
                .transform(v -> {
                    lead.clearEvents();
                    return lead;
                })
                .subscribe()
                .asCompletionStage());
        } else {
            entity.id = UUID.randomUUID();
            return Panache.withTransaction(() -> entity.<LeadEntity>persist()
                .onItem()
                .transform(v -> {
                    lead.clearEvents();
                    return lead;
                })
                .subscribe()
                .asCompletionStage());
        }
    }

    @Override
    @WithSession
    public CompletionStage<Optional<Lead>> findById(LeadId id) {
        return LeadEntity.<LeadEntity>findById(id.getValue())
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
    public CompletionStage<Boolean> existsById(LeadId id) {
        return LeadEntity.<LeadEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(Lead lead) {
        return LeadEntity.deleteById(lead.getId().getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(LeadId id) {
        return LeadEntity.deleteById(id.getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findByStatus(LeadStatus status) {
        return LeadEntity.list("status = ?1", status)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findByEmail(String email) {
        return LeadEntity.list("email = ?1", email)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findByAssignedTo(String assignedTo) {
        return LeadEntity.list("assignedTo = ?1", UUID.fromString(assignedTo))
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findActiveLeads() {
        return LeadEntity.list("active = true")
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findQualifiedLeads() {
        return LeadEntity.list("status in ?1", 
                List.of(LeadStatus.QUALIFIED, LeadStatus.NURTURING))
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findByScoreGreaterThan(double score) {
        return LeadEntity.list("score >= ?1", score)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByStatus(LeadStatus status) {
        return LeadEntity.count("status = ?1", status)
            .subscribe()
            .asCompletionStage();
    }
}