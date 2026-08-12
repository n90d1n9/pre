package tech.kayys.erp.crm.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;
import tech.kayys.erp.crm.domain.valueobject.TicketPriority;
import tech.kayys.erp.crm.domain.valueobject.TicketStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Support ticket entity for persistence.
 */
@Entity
@Table(name = "crm_tickets", indexes = {
    @Index(name = "idx_ticket_number", columnList = "ticket_number"),
    @Index(name = "idx_ticket_customer", columnList = "customer_id"),
    @Index(name = "idx_ticket_status", columnList = "status"),
    @Index(name = "idx_ticket_assigned", columnList = "assigned_to")
})
public class SupportTicketEntity extends BaseEntity {

    @Column(name = "ticket_number", unique = true, nullable = false, length = 50)
    public String ticketNumber;

    @Column(name = "customer_id", columnDefinition = "UUID")
    public UUID customerId;

    @Column(name = "customer_name", length = 100)
    public String customerName;

    @Column(name = "subject", nullable = false, length = 255)
    public String subject;

    @Column(name = "description", length = 2000)
    public String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    public TicketStatus status;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    public TicketPriority priority;

    @Column(name = "category", length = 50)
    public String category;

    @Column(name = "sub_category", length = 50)
    public String subCategory;

    @Column(name = "assigned_to", length = 100)
    public String assignedTo;

    @Column(name = "assigned_at")
    public Instant assignedAt;

    @Column(name = "resolved_at")
    public Instant resolvedAt;

    @Column(name = "closed_at")
    public Instant closedAt;

    @Column(name = "resolution", length = 2000)
    public String resolution;

    @Column(name = "escalated_to", length = 100)
    public String escalatedTo;

    @Column(name = "notes", length = 2000)
    public String notes;

    @ElementCollection
    @CollectionTable(name = "crm_ticket_comments", joinColumns = @JoinColumn(name = "ticket_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "comment_id")),
        @AttributeOverride(name = "author", column = @Column(name = "author", length = 100)),
        @AttributeOverride(name = "content", column = @Column(name = "content", length = 2000)),
        @AttributeOverride(name = "internal", column = @Column(name = "is_internal"))
    })
    public List<TicketCommentEntity> comments = new ArrayList<>();

    @Embeddable
    public static class TicketCommentEntity {
        public String id;
        public String author;
        public String content;
        public boolean internal;
        public Instant createdAt;
    }
}