package tech.kayys.erp.document.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Document approval entity for persistence.
 */
@Entity
@Table(name = "document_approvals", indexes = {
    @Index(name = "idx_approval_document", columnList = "document_id"),
    @Index(name = "idx_approval_status", columnList = "status")
})
public class DocumentApprovalEntity extends BaseEntity {

    @Column(name = "document_id", nullable = false, columnDefinition = "UUID")
    public UUID documentId;

    @Column(name = "document_title", length = 255)
    public String documentTitle;

    @Column(name = "current_approver", columnDefinition = "UUID")
    public UUID currentApprover;

    @Column(name = "current_step")
    public int currentStep;

    @Column(name = "status", nullable = false, length = 20)
    public String status;

    @Column(name = "initiated_by", columnDefinition = "UUID")
    public UUID initiatedBy;

    @Column(name = "initiated_at", nullable = false)
    public Instant initiatedAt;

    @Column(name = "completed_at")
    public Instant completedAt;

    @Column(name = "notes", length = 2000)
    public String notes;

    @ElementCollection
    @CollectionTable(name = "approval_approvers", joinColumns = @JoinColumn(name = "approval_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "userId", column = @Column(name = "user_id", columnDefinition = "UUID")),
        @AttributeOverride(name = "userName", column = @Column(name = "user_name", length = 100)),
        @AttributeOverride(name = "role", column = @Column(name = "role", length = 50)),
        @AttributeOverride(name = "order", column = @Column(name = "approval_order")),
        @AttributeOverride(name = "approved", column = @Column(name = "is_approved")),
        @AttributeOverride(name = "comments", column = @Column(name = "comments", length = 1000)),
        @AttributeOverride(name = "approvedAt", column = @Column(name = "approved_at"))
    })
    public List<ApproverEntity> approvers = new ArrayList<>();

    /**
     * Approver entity embedded.
     */
    @Embeddable
    public static class ApproverEntity {
        public UUID userId;
        public String userName;
        public String role;
        public int order;
        public boolean approved;
        public String comments;
        public Instant approvedAt;
    }
}