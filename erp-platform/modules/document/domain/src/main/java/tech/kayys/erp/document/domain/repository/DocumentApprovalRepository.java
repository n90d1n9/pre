package tech.kayys.erp.document.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.document.domain.identifier.DocumentApprovalId;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.model.DocumentApproval;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for DocumentApproval aggregates.
 */
public interface DocumentApprovalRepository extends Repository<DocumentApproval, DocumentApprovalId> {

    /**
     * Finds approvals for a document.
     */
    CompletionStage<List<DocumentApproval>> findByDocument(DocumentId documentId);

    /**
     * Finds active approvals for a document.
     */
    CompletionStage<DocumentApproval> findActiveApproval(DocumentId documentId);

    /**
     * Finds approvals pending for a user.
     */
    CompletionStage<List<DocumentApproval>> findPendingApprovals(String userId);

    /**
     * Finds approvals initiated by a user.
     */
    CompletionStage<List<DocumentApproval>> findInitiatedBy(String userId);

    /**
     * Finds approvals by status.
     */
    CompletionStage<List<DocumentApproval>> findByStatus(String status);

    /**
     * Finds approvals escalated.
     */
    CompletionStage<List<DocumentApproval>> findEscalatedApprovals();
}