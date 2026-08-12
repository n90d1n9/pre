package tech.kayys.erp.document.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.DocumentVersionId;
import tech.kayys.erp.document.domain.model.DocumentVersion;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for DocumentVersion aggregates.
 */
public interface DocumentVersionRepository extends Repository<DocumentVersion, DocumentVersionId> {

    /**
     * Finds versions for a document.
     */
    CompletionStage<List<DocumentVersion>> findByDocument(DocumentId documentId);

    /**
     * Finds the current version of a document.
     */
    CompletionStage<DocumentVersion> findCurrentVersion(DocumentId documentId);

    /**
     * Finds versions created by a user.
     */
    CompletionStage<List<DocumentVersion>> findByCreator(String createdBy);

    /**
     * Counts versions for a document.
     */
    CompletionStage<Long> countByDocument(DocumentId documentId);

    /**
     * Finds versions older than a date.
     */
    CompletionStage<List<DocumentVersion>> findVersionsOlderThan(Instant date);
}