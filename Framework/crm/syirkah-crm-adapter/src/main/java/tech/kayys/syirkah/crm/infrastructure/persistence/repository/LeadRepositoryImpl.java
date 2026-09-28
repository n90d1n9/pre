package tech.kayys.syirkah.crm.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import tech.kayys.syirkah.crm.domain.identifier.LeadId;
import tech.kayys.syirkah.crm.domain.model.Lead;
import tech.kayys.syirkah.crm.domain.repository.LeadRepository;
import tech.kayys.syirkah.crm.domain.valueobject.LeadStatus;
import tech.kayys.syirkah.crm.infrastructure.persistence.entity.LeadEntity;
import tech.kayys.syirkah.crm.infrastructure.persistence.mapper.LeadMapper;
import tech.kayys.syirkah.identity.domain.user.UserId;

import jakarta.enterprise.context.ApplicationScoped;
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
        
        if (entity.id == null) {
            entity.id = UUID.randomUUID();
        }
        
        return Panache.withTransaction(() -> entity.<LeadEntity>persist()
            .map(v -> {
                lead.clearEvents();
                return lead;
            })
        ).subscribe().asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Optional<Lead>> findById(LeadId id) {
        io.smallrye.mutiny.Uni<LeadEntity> uni = LeadEntity.findById(id.getValue());
        return uni
            .map(entity -> entity == null ? Optional.<Lead>empty() : Optional.of(mapper.toDomain(entity)))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> existsById(LeadId id) {
        io.smallrye.mutiny.Uni<LeadEntity> uni = LeadEntity.findById(id.getValue());
        return uni
            .map(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(Lead lead) {
        return LeadEntity.deleteById(lead.getId().getValue())
            .map(v -> (Void) null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(LeadId id) {
        return LeadEntity.deleteById(id.getValue())
            .map(v -> (Void) null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findByStatus(LeadStatus status) {
        return LeadEntity.<LeadEntity>list("status = ?1", status)
            .map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findByEmail(String email) {
        return LeadEntity.<LeadEntity>list("email = ?1", email)
            .map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findByAssignedTo(UserId assignedTo) {
        return LeadEntity.<LeadEntity>list("assignedTo = ?1", assignedTo.value())
            .map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findActiveLeads() {
        return LeadEntity.<LeadEntity>list("active = true")
            .map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findQualifiedLeads() {
        return LeadEntity.<LeadEntity>list("status in ?1", 
                List.of(LeadStatus.QUALIFIED, LeadStatus.NURTURING))
            .map(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Lead>> findByScoreGreaterThan(double score) {
        return LeadEntity.<LeadEntity>list("score >= ?1", score)
            .map(entities -> entities.stream()
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