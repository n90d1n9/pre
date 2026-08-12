package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderApprovalId;
import tech.kayys.erp.purchasing.domain.valueobject.ApprovalStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Purchase Order Approval aggregate root.
 * Tracks the approval process for a purchase order.
 */
public final class PurchaseOrderApproval extends AggregateRoot<PurchaseOrderApprovalId> {
    
    private static final long serialVersionUID = 1L;
    
    private String purchaseOrderId;
    private String workflowId;
    private ApprovalStatus status;
    private int currentStepIndex;
    private List<ApprovalRecord> approvals;
    private List<ApprovalHistory> history;
    private int rejectionCount;
    private String rejectedBy;
    private String rejectionReason;
    private Instant rejectedAt;
    private Instant completedAt;
    private String completedBy;
    private String notes;
    private boolean active;

    private PurchaseOrderApproval(PurchaseOrderApprovalId id) {
        super(id);
        this.approvals = new ArrayList<>();
        this.history = new ArrayList<>();
        this.status = ApprovalStatus.PENDING;
        this.currentStepIndex = 0;
        this.rejectionCount = 0;
        this.active = true;
    }

    private PurchaseOrderApproval() {
        super();
    }

    /**
     * Factory method to create a new purchase order approval.
     */
    public static PurchaseOrderApproval create(
            PurchaseOrderApprovalId id,
            String purchaseOrderId,
            String workflowId) {
        PurchaseOrderApproval approval = new PurchaseOrderApproval(id);
        approval.purchaseOrderId = purchaseOrderId;
        approval.workflowId = workflowId;
        return approval;
    }

    /**
     * Records an approval.
     */
    public void approve(String approverId, String approverName, String notes) {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot approve finalized approval");
        }

        ApprovalRecord record = new ApprovalRecord(
            java.util.UUID.randomUUID().toString(),
            approverId,
            approverName,
            currentStepIndex,
            "APPROVED",
            notes,
            Instant.now()
        );
        approvals.add(record);
        
        addHistory("APPROVED", "Approved by: " + approverName);

        // Move to next step
        currentStepIndex++;
        
        // Check if all steps are completed
        if (isAllStepsCompleted()) {
            this.status = ApprovalStatus.APPROVED;
            this.completedAt = Instant.now();
            this.completedBy = approverId;
        }

        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records a rejection.
     */
    public void reject(String approverId, String approverName, String reason) {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot reject finalized approval");
        }

        ApprovalRecord record = new ApprovalRecord(
            java.util.UUID.randomUUID().toString(),
            approverId,
            approverName,
            currentStepIndex,
            "REJECTED",
            reason,
            Instant.now()
        );
        approvals.add(record);
        
        this.rejectionCount++;
        this.rejectedBy = approverId;
        this.rejectionReason = reason;
        this.rejectedAt = Instant.now();
        this.status = ApprovalStatus.REJECTED;
        
        addHistory("REJECTED", "Rejected by: " + approverName + " - " + reason);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if all approval steps are completed.
     */
    private boolean isAllStepsCompleted() {
        // In production, get workflow and check steps count
        return currentStepIndex >= 3; // Placeholder
    }

    /**
     * Gets the current approver.
     */
    public String getCurrentApprover() {
        // In production, get workflow and return approver for current step
        return "Approver_" + currentStepIndex;
    }

    /**
     * Cancels the approval process.
     */
    public void cancel(String reason) {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot cancel finalized approval");
        }
        this.status = ApprovalStatus.CANCELLED;
        addHistory("CANCELLED", "Cancelled: " + reason);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void addHistory(String action, String details) {
        ApprovalHistory historyEntry = new ApprovalHistory(
            java.util.UUID.randomUUID().toString(),
            action,
            details,
            Instant.now()
        );
        history.add(historyEntry);
    }

    // Getters
    public String getPurchaseOrderId() { return purchaseOrderId; }
    public String getWorkflowId() { return workflowId; }
    public ApprovalStatus getStatus() { return status; }
    public int getCurrentStepIndex() { return currentStepIndex; }
    public List<ApprovalRecord> getApprovals() { return Collections.unmodifiableList(approvals); }
    public List<ApprovalHistory> getHistory() { return Collections.unmodifiableList(history); }
    public int getRejectionCount() { return rejectionCount; }
    public String getRejectedBy() { return rejectedBy; }
    public String getRejectionReason() { return rejectionReason; }
    public Instant getRejectedAt() { return rejectedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getCompletedBy() { return completedBy; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "PurchaseOrderApproval{" +
                "id=" + getId() +
                ", purchaseOrderId='" + purchaseOrderId + '\'' +
                ", status=" + status +
                ", currentStep=" + currentStepIndex +
                '}';
    }

    /**
     * Approval record value object.
     */
    public static final class ApprovalRecord implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String recordId;
        private final String approverId;
        private final String approverName;
        private final int stepIndex;
        private final String decision;
        private final String notes;
        private final Instant timestamp;

        public ApprovalRecord(
                String recordId,
                String approverId,
                String approverName,
                int stepIndex,
                String decision,
                String notes,
                Instant timestamp) {
            this.recordId = recordId;
            this.approverId = approverId;
            this.approverName = approverName;
            this.stepIndex = stepIndex;
            this.decision = decision;
            this.notes = notes;
            this.timestamp = timestamp;
        }

        public String getRecordId() { return recordId; }
        public String getApproverId() { return approverId; }
        public String getApproverName() { return approverName; }
        public int getStepIndex() { return stepIndex; }
        public String getDecision() { return decision; }
        public String getNotes() { return notes; }
        public Instant getTimestamp() { return timestamp; }
    }

    /**
     * Approval history value object.
     */
    public static final class ApprovalHistory {
        private final String historyId;
        private final String action;
        private final String details;
        private final Instant timestamp;

        public ApprovalHistory(String historyId, String action, String details, Instant timestamp) {
            this.historyId = historyId;
            this.action = action;
            this.details = details;
            this.timestamp = timestamp;
        }

        public String getHistoryId() { return historyId; }
        public String getAction() { return action; }
        public String getDetails() { return details; }
        public Instant getTimestamp() { return timestamp; }
    }
}