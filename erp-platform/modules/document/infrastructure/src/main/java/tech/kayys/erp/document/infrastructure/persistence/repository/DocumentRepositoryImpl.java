package tech.kayys.erp.document.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.FolderId;
import tech.kayys.erp.document.domain.model.Document;
import tech.kayys.erp.document.domain.repository.DocumentRepository;
import tech.kayys.erp.document.domain.valueobject.DocumentStatus;
import tech.kayys.erp.document.domain.valueobject.DocumentType;
import tech.kayys.erp.document.infrastructure.persistence.entity.DocumentEntity;
import tech.kayys.erp.document.infrastructure.persistence.mapper.DocumentMapper;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of DocumentRepository using Hibernate Reactive Panache.
 */
@ApplicationScoped
public class DocumentRepositoryImpl implements DocumentRepository {

    private final DocumentMapper mapper;

    public DocumentRepositoryImpl(DocumentMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<Document> save(Document document) {
        DocumentEntity entity = mapper.toEntity(document);
        
        if (entity.id != null) {
            return Panache.withTransaction(() -> entity.<DocumentEntity>persist()
                .onItem()
                .transform(v -> {
                    document.clearEvents();
                    return document;
                })
                .subscribe()
                .asCompletionStage());
        } else {
            entity.id = UUID.randomUUID();
            return Panache.withTransaction(() -> entity.<DocumentEntity>persist()
                .onItem()
                .transform(v -> {
                    document.clearEvents();
                    return document;
                })
                .subscribe()
                .asCompletionStage());
        }
    }

    @Override
    @WithSession
    public CompletionStage<Optional<Document>> findById(DocumentId id) {
        return DocumentEntity.<DocumentEntity>findById(id.getValue())
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
    public CompletionStage<Boolean> existsById(DocumentId id) {
        return DocumentEntity.<DocumentEntity>findById(id.getValue())
            .onItem()
            .transform(entity -> entity != null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(Document document) {
        return DocumentEntity.deleteById(document.getId().getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(DocumentId id) {
        return DocumentEntity.deleteById(id.getValue())
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByFolder(FolderId folderId) {
        return DocumentEntity.list("folderId = ?1", folderId.getValue())
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByStatus(DocumentStatus status) {
        return DocumentEntity.list("status = ?1", status)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByType(DocumentType type) {
        return DocumentEntity.list("documentType = ?1", type)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByOwner(String ownerId) {
        return DocumentEntity.list("ownerId = ?1", UUID.fromString(ownerId))
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByTag(String tag) {
        return DocumentEntity.find("?1 member of tags", tag)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByTitleContaining(String title) {
        return DocumentEntity.list("title like ?1", "%" + title + "%")
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findByDepartment(String department) {
        return DocumentEntity.list("department = ?1", department)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findPublishedBetween(Instant start, Instant end) {
        return DocumentEntity.list("publishedAt between ?1 and ?2", start, end)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findDocumentsExpiringSoon(int days) {
        Instant threshold = Instant.now().plusSeconds(days * 24L * 60L * 60L);
        return DocumentEntity.list("expiryDate is not null and expiryDate <= ?1", threshold)
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByStatus(DocumentStatus status) {
        return DocumentEntity.count("status = ?1", status)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Long> countByType(DocumentType type) {
        return DocumentEntity.count("documentType = ?1", type)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> searchDocuments(String searchTerm) {
        // Full-text search across title, description, tags, and content
        // This uses a native query for PostgreSQL full-text search
        return DocumentEntity.find("""
                title ilike ?1 or description ilike ?1 or 
                exists (select 1 from document_tags dt where dt.document_id = id and dt.tag ilike ?1)
                """, "%" + searchTerm + "%")
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<List<Document>> findSharedWithUser(String userId) {
        UUID userUUID = UUID.fromString(userId);
        return DocumentEntity.find("?1 member of sharedWith", userUUID)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()))
            .subscribe()
            .asCompletionStage();
    }
}