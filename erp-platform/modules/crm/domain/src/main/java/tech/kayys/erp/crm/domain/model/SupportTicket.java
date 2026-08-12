package tech.kayys.erp.crm.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.crm.domain.identifier.CustomerId;
import tech.kayys.erp.crm.domain.identifier.TicketId;
import tech.kayys.erp.crm.domain.valueobject.TicketPriority;
import tech.kayys.erp.crm.domain.valueobject.TicketStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Support ticket aggregate root.
 * Represents a customer support request.
 */
public final class SupportTicket extends AggregateRoot<TicketId> {
    
    private static final long serialVersionUID = 1L;
    
    private String ticketNumber;
    private CustomerId customerId;
    private String customerName;
    private String subject;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private String category;
    private String subCategory;
    private String assignedTo;
    private Instant assignedAt;
    private Instant resolvedAt;
    private Instant closedAt;
    private List<TicketComment> comments;
    private List<TicketAttachment> attachments;
    private String resolution;
    private String escalatedTo;
    private String notes;
    private boolean active;

    private SupportTicket(TicketId id) {
        super(id);
        this.comments = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.status = TicketStatus.NEW;
        this.priority = TicketPriority.MEDIUM;
        this.active = true;
    }

    private SupportTicket() {
        super();
    }

    /**
     * Factory method to create a new support ticket.
     */
    public static SupportTicket create(
            TicketId id,
            String ticketNumber,
            CustomerId customerId,
            String customerName,
            String subject,
            String description,
            TicketPriority priority,
            String category) {
        SupportTicket ticket = new SupportTicket(id);
        ticket.ticketNumber = ticketNumber;
        ticket.customerId = customerId;
        ticket.customerName = customerName;
        ticket.subject = subject;
        ticket.description = description;
        ticket.priority = priority;
        ticket.category = category;
        return ticket;
    }

    /**
     * Assigns the ticket to an agent.
     */
    public void assign(String assignedTo) {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Cannot assign closed ticket");
        }
        this.assignedTo = assignedTo;
        this.assignedAt = Instant.now();
        this.status = TicketStatus.ASSIGNED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts working on the ticket.
     */
    public void startWork() {
        if (status != TicketStatus.ASSIGNED && status != TicketStatus.NEW) {
            throw new IllegalStateException("Cannot start work on ticket in status: " + status);
        }
        this.status = TicketStatus.IN_PROGRESS;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a comment to the ticket.
     */
    public void addComment(TicketComment comment) {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Cannot add comment to closed ticket");
        }
        comments.add(comment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Resolves the ticket.
     */
    public void resolve(String resolution) {
        if (status != TicketStatus.IN_PROGRESS && status != TicketStatus.PENDING_CUSTOMER) {
            throw new IllegalStateException("Cannot resolve ticket in status: " + status);
        }
        this.resolution = resolution;
        this.status = TicketStatus.RESOLVED;
        this.resolvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Closes the ticket.
     */
    public void close() {
        if (status != TicketStatus.RESOLVED) {
            throw new IllegalStateException("Cannot close ticket in status: " + status);
        }
        this.status = TicketStatus.CLOSED;
        this.closedAt = Instant.now();
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Reopens the ticket.
     */
    public void reopen(String reason) {
        if (status != TicketStatus.RESOLVED && status != TicketStatus.CLOSED) {
            throw new IllegalStateException("Cannot reopen ticket in status: " + status);
        }
        this.status = TicketStatus.REOPENED;
        this.active = true;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Escalates the ticket.
     */
    public void escalate(String escalatedTo, String reason) {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Cannot escalate closed ticket");
        }
        this.status = TicketStatus.ESCALATED;
        this.escalatedTo = escalatedTo;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the ticket on hold.
     */
    public void putOnHold(String reason) {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Cannot put closed ticket on hold");
        }
        this.status = TicketStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets as pending customer.
     */
    public void pendingCustomer() {
        if (status != TicketStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot set pending customer in status: " + status);
        }
        this.status = TicketStatus.PENDING_CUSTOMER;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the priority.
     */
    public void updatePriority(TicketPriority priority) {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Cannot update closed ticket priority");
        }
        this.priority = priority;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the time the ticket has been open.
     */
    public long getOpenTimeSeconds() {
        if (createdAt == null) {
            return 0;
        }
        Instant end = status == TicketStatus.CLOSED ? closedAt : Instant.now();
        return java.time.Duration.between(createdAt, end).getSeconds();
    }

    /**
     * Checks if the ticket is overdue.
     */
    public boolean isOverdue() {
        if (status == TicketStatus.CLOSED || status == TicketStatus.RESOLVED) {
            return false;
        }
        // Simple SLA check: 1 hour for critical, 4 hours for high, etc.
        int maxHours = switch (priority) {
            case CRITICAL -> 1;
            case HIGH -> 4;
            case MEDIUM -> 8;
            case LOW -> 24;
            case TRIVIAL -> 48;
        };
        return getOpenTimeSeconds() > maxHours * 3600;
    }

    // Getters
    public String getTicketNumber() { return ticketNumber; }
    public CustomerId getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public String getCategory() { return category; }
    public String getSubCategory() { return subCategory; }
    public String getAssignedTo() { return assignedTo; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public Instant getClosedAt() { return closedAt; }
    public List<TicketComment> getComments() { return Collections.unmodifiableList(comments); }
    public List<TicketAttachment> getAttachments() { return Collections.unmodifiableList(attachments); }
    public String getResolution() { return resolution; }
    public String getEscalatedTo() { return escalatedTo; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void addAttachment(TicketAttachment attachment) {
        attachments.add(attachment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "SupportTicket{" +
                "id=" + getId() +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", subject='" + subject + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", customerName='" + customerName + '\'' +
                '}';
    }

    /**
     * Ticket comment value object.
     */
    public static final class TicketComment implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String author;
        private final String content;
        private final boolean internal;
        private final Instant createdAt;

        public TicketComment(String id, String author, String content, boolean internal) {
            this.id = id;
            this.author = author;
            this.content = content;
            this.internal = internal;
            this.createdAt = Instant.now();
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Comment ID cannot be empty");
            }
            if (author == null || author.trim().isEmpty()) {
                throw new IllegalArgumentException("Author cannot be empty");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Content cannot be empty");
            }
        }

        public String getId() { return id; }
        public String getAuthor() { return author; }
        public String getContent() { return content; }
        public boolean isInternal() { return internal; }
        public Instant getCreatedAt() { return createdAt; }

        @Override
        public String toString() {
            return "TicketComment{" +
                    "id='" + id + '\'' +
                    ", author='" + author + '\'' +
                    ", internal=" + internal +
                    '}';
        }
    }

    /**
     * Ticket attachment value object.
     */
    public static final class TicketAttachment implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String fileName;
        private final String fileType;
        private final long fileSize;
        private final String fileUrl;
        private final Instant uploadedAt;

        public TicketAttachment(String id, String fileName, String fileType, long fileSize, String fileUrl) {
            this.id = id;
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileSize = fileSize;
            this.fileUrl = fileUrl;
            this.uploadedAt = Instant.now();
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Attachment ID cannot be empty");
            }
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalArgumentException("File name cannot be empty");
            }
            if (fileSize < 0) {
                throw new IllegalArgumentException("File size cannot be negative");
            }
        }

        public String getId() { return id; }
        public String getFileName() { return fileName; }
        public String getFileType() { return fileType; }
        public long getFileSize() { return fileSize; }
        public String getFileUrl() { return fileUrl; }
        public Instant getUploadedAt() { return uploadedAt; }

        @Override
        public String toString() {
            return "TicketAttachment{" +
                    "id='" + id + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", fileSize=" + fileSize +
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
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>

    <module>modules/payroll/domain</module>
    <module>modules/payroll/application</module>
    <module>modules/payroll/infrastructure</module>
    <module>modules/payroll/interfaces</module>

    <module>modules/hris/domain</module>
    <module>modules/hris/application</module>
    <module>modules/hris/infrastructure</module>
    <module>modules/hris/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/stockopname/domain</module>
    <module>modules/stockopname/application</module>
    <module>modules/stockopname/infrastructure</module>
    <module>modules/stockopname/interfaces</module>

    <module>modules/warehouse/domain</module>
    <module>modules/warehouse/application</module>
    <module>modules/warehouse/infrastructure</module>
    <module>modules/warehouse/interfaces</module>

    <module>modules/crm/domain</module>
    <module>modules/crm/application</module>
    <module>modules/crm/infrastructure</module>
    <module>modules/crm/interfaces</module>
</modules>