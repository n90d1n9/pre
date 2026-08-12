package tech.kayys.erp.document.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.FolderId;
import tech.kayys.erp.document.domain.model.Document;
import tech.kayys.erp.document.domain.valueobject.DocumentStatus;
import tech.kayys.erp.document.domain.valueobject.DocumentType;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Document aggregates.
 */
public interface DocumentRepository extends Repository<Document, DocumentId> {

    /**
     * Finds documents by folder.
     */
    CompletionStage<List<Document>> findByFolder(FolderId folderId);

    /**
     * Finds documents by status.
     */
    CompletionStage<List<Document>> findByStatus(DocumentStatus status);

    /**
     * Finds documents by type.
     */
    CompletionStage<List<Document>> findByType(DocumentType type);

    /**
     * Finds documents by owner.
     */
    CompletionStage<List<Document>> findByOwner(String ownerId);

    /**
     * Finds documents by tag.
     */
    CompletionStage<List<Document>> findByTag(String tag);

    /**
     * Finds documents by title containing text.
     */
    CompletionStage<List<Document>> findByTitleContaining(String title);

    /**
     * Finds documents by department.
     */
    CompletionStage<List<Document>> findByDepartment(String department);

    /**
     * Finds documents published between dates.
     */
    CompletionStage<List<Document>> findPublishedBetween(Instant start, Instant end);

    /**
     * Finds documents expiring soon.
     */
    CompletionStage<List<Document>> findDocumentsExpiringSoon(int days);

    /**
     * Counts documents by status.
     */
    CompletionStage<Long> countByStatus(DocumentStatus status);

    /**
     * Counts documents by type.
     */
    CompletionStage<Long> countByType(DocumentType type);

    /**
     * Searches documents with full-text search.
     */
    CompletionStage<List<Document>> searchDocuments(String searchTerm);

    /**
     * Finds shared documents for a user.
     */
    CompletionStage<List<Document>> findSharedWithUser(String userId);
}