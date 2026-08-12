package tech.kayys.erp.document.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.document.domain.identifier.DocumentId;
import tech.kayys.erp.document.domain.identifier.DocumentApprovalId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Document approval aggregate root.
 * Manages document approval workflows.
 */
public final class DocumentApproval extends AggregateRoot<DocumentApprovalId> {
    
    private static final long serialVersionUID = 1L;
    
    private DocumentId documentId;
    private String documentTitle;
    private List<Approver> approvers;
    private String currentApprover;
    private int currentStep;
    private String status; // PENDING, APPROVED, REJECTED, ESCALATED
    private String initiatedBy;
    private Instant initiatedAt;
    private Instant completedAt;
    private String notes;
    private boolean active;

    private DocumentApproval(DocumentApprovalId id) {
        super(id);
        this.approvers = new ArrayList<>();
        this.status = "PENDING";
        this.active = true;
        this.currentStep = 0;
        this.initiatedAt = Instant.now();
    }

    private DocumentApproval() {
        super();
    }

    /**
     * Factory method to create a new document approval.
     */
    public static DocumentApproval create(
            DocumentApprovalId id,
            DocumentId documentId,
            String documentTitle,
            List<Approver> approvers,
            String initiatedBy) {
        DocumentApproval approval = new DocumentApproval(id);
        approval.documentId = documentId;
        approval.documentTitle = documentTitle;
        approval.approvers = new ArrayList<>(approvers);
        approval.initiatedBy = initiatedBy;
        if (!approvers.isEmpty()) {
            approval.currentApprover = approvers.get(0).getUserId();
        }
        return approval;
    }

    /**
     * Approves the document by current approver.
     */
    public void approve(String userId, String comments) {
        if (!isCurrentApprover(userId)) {
            throw new IllegalStateException("User is not the current approver");
        }
        
        Approver current = approvers.get(currentStep);
        if (current != null) {
            current.approve(comments);
        }
        
        currentStep++;
        
        if (currentStep >= approvers.size()) {
            this.status = "APPROVED";
            this.completedAt = Instant.now();
            this.active = false;
        } else {
            this.currentApprover = approvers.get(currentStep).getUserId();
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the document by current approver.
     */
    public void reject(String userId, String reason) {
        if (!isCurrentApprover(userId)) {
            throw new IllegalStateException("User is not the current approver");
        }
        
        this.status = "REJECTED";
        this.completedAt = Instant.now();
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Escalates the approval.
     */
    public void escalate(String reason) {
        this.status = "ESCALATED";
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if a user is the current approver.
     */
    public boolean isCurrentApprover(String userId) {
        if (currentStep >= approvers.size()) {
            return false;
        }
        return approvers.get(currentStep).getUserId().equals(userId);
    }

    /**
     * Gets the current approver.
     */
    public Approver getCurrentApprover() {
        if (currentStep >= approvers.size()) {
            return null;
        }
        return approvers.get(currentStep);
    }

    /**
     * Gets the approval progress.
     */
    public double getProgress() {
        if (approvers.isEmpty()) {
            return 0.0;
        }
        return (double) currentStep / approvers.size() * 100.0;
    }

    // Getters
    public DocumentId getDocumentId() { return documentId; }
    public String getDocumentTitle() { return documentTitle; }
    public List<Approver> getApprovers() { return Collections.unmodifiableList(approvers); }
    public String getCurrentApprover() { return currentApprover; }
    public int getCurrentStep() { return currentStep; }
    public String getStatus() { return status; }
    public String getInitiatedBy() { return initiatedBy; }
    public Instant getInitiatedAt() { return initiatedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "DocumentApproval{" +
                "id=" + getId() +
                ", documentId=" + documentId +
                ", status=" + status +
                ", progress=" + getProgress() + "%" +
                '}';
    }

    /**
     * Approver value object.
     */
    public static final class Approver implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String userId;
        private final String userName;
        private final String role;
        private final int order;
        private boolean approved;
        private String comments;
        private Instant approvedAt;

        public Approver(String userId, String userName, String role, int order) {
            this.userId = userId;
            this.userName = userName;
            this.role = role;
            this.order = order;
            this.approved = false;
            validate();
        }

        @Override
        public void validate() {
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("User ID cannot be empty");
            }
            if (order < 0) {
                throw new IllegalArgumentException("Order cannot be negative");
            }
        }

        public String getUserId() { return userId; }
        public String getUserName() { return userName; }
        public String getRole() { return role; }
        public int getOrder() { return order; }
        public boolean isApproved() { return approved; }
        public String getComments() { return comments; }
        public Instant getApprovedAt() { return approvedAt; }

        public void approve(String comments) {
            this.approved = true;
            this.comments = comments;
            this.approvedAt = Instant.now();
        }

        @Override
        public String toString() {
            return "Approver{" +
                    "userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", approved=" + approved +
                    '}';
        }
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <!-- ... existing modules ... -->

    <!-- Document Management -->
    <module>modules/document/domain</module>
    <module>modules/document/application</module>
    <module>modules/document/infrastructure</module>
    <module>modules/document/interfaces</module>

    <!-- Cross-Cutting Modules -->
    <module>modules/security/domain</module>
    <module>modules/security/application</module>
    <module>modules/security/infrastructure</module>
    <module>modules/security/interfaces</module>

    <module>modules/audit/domain</module>
    <module>modules/audit/application</module>
    <module>modules/audit/infrastructure</module>
    <module>modules/audit/interfaces</module>

    <module>modules/i18n/domain</module>
    <module>modules/i18n/application</module>
    <module>modules/i18n/infrastructure</module>
    <module>modules/i18n/interfaces</module>

    <module>modules/privacy/domain</module>
    <module>modules/privacy/application</module>
    <module>modules/privacy/infrastructure</module>
    <module>modules/privacy/interfaces</module>

    <module>modules/portal/domain</module>
    <module>modules/portal/application</module>
    <module>modules/portal/infrastructure</module>
    <module>modules/portal/interfaces</module>
</modules>