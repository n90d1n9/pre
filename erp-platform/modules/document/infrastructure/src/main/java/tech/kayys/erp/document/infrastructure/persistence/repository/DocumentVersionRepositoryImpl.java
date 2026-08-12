package tech.kayys.erp.document.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.DocumentVersionId;
import tech.kayys.erp.document.domain.model.DocumentVersion;
import tech.kayys.erp.document.domain.repository.DocumentVersionRepository;
import tech.kayys.erp.document.infrastructure.persistence.entity.DocumentVersionEntity;
import tech.kayys.erp.document.infrastructure.persistence.mapper.DocumentVersionMapper;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of DocumentVersionRepository.
 */
@ApplicationScoped
public class DocumentVersionRepositoryImpl implements DocumentVersionRepository {

    private final DocumentVersionMapper mapper;

    public DocumentVersionRepositoryImpl(DocumentVersionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<DocumentVersion> save(DocumentVersion version) {
        DocumentVersionEntity entity = mapper.toEntity(version);
        
        return Panache.withTransaction(() -> entity.<DocumentVersionEntity>persist()
            .onItem()
            .transform(v -> {
                version.clearEvents();
                return version;
            })
            .subscribe()
            .asCompletionStage());
    }

    @Override
    @WithSession
    public CompletionStage<Optional<DocumentVersion>> findById(DocumentVersionId id) {
        return DocumentVersionEntity.<DocumentVersionEntity>findById(id.getValue())
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
    public CompletionStage<Boolean> existsById(DocumentVersionId id) {
        return DocumentVersionEntity.<DocumentVersionEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(DocumentVersion version) {
        return DocumentVersionEntity.deleteById(version.getId().getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(DocumentVersionId id) {
        return DocumentVersionEntity.deleteById(id.getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<DocumentVersion>> findByDocument(DocumentId documentId) {
        return DocumentVersionEntity.list("documentId = ?1 order by createdAt desc", documentId.getValue())
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<DocumentVersion> findCurrentVersion(DocumentId documentId) {
        return DocumentVersionEntity.find("documentId = ?1 and current = true", documentId.getValue())
            .firstResult()
            .onItem()
            .transform(entity -> entity != null ? mapper.toDomain(entity) : null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<DocumentVersion>> findByCreator(String createdBy) {
        return DocumentVersionEntity.list("createdBy = ?1 order by createdAt desc", createdBy)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByDocument(DocumentId documentId) {
        return DocumentVersionEntity.count("documentId = ?1", documentId.getValue())
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<DocumentVersion>> findVersionsOlderThan(Instant date) {
        return DocumentVersionEntity.list("createdAt < ?1", date)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }
}