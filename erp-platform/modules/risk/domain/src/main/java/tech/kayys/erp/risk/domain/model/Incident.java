package tech.kayys.erp.risk.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.risk.domain.identifier.IncidentId;
import tech.kayys.erp.risk.domain.identifier.RiskId;
import tech.kayys.erp.risk.domain.valueobject.IncidentSeverity;
import tech.kayys.erp.risk.domain.valueobject.IncidentStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Incident aggregate root.
 * Represents an incident or event that occurred.
 */
public final class Incident extends AggregateRoot<IncidentId> {
    
    private static final long serialVersionUID = 1L;
    
    private String incidentNumber;
    private String title;
    private String description;
    private IncidentSeverity severity;
    private IncidentStatus status;
    private String type;
    private RiskId riskId;
    private String reportedBy;
    private Instant reportedAt;
    private String assignedTo;
    private Instant assignedAt;
    private String escalatedTo;
    private Instant escalatedAt;
    private List<IncidentComment> comments;
    private List<IncidentAttachment> attachments;
    private String rootCause;
    private String resolution;
    private Instant resolvedAt;
    private String closedBy;
    private Instant closedAt;
    private String notes;
    private boolean active;

    private Incident(IncidentId id) {
        super(id);
        this.comments = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.status = IncidentStatus.REPORTED;
        this.active = true;
        this.reportedAt = Instant.now();
    }

    private Incident() {
        super();
    }

    /**
     * Factory method to create a new incident.
     */
    public static Incident create(
            IncidentId id,
            String incidentNumber,
            String title,
            String description,
            IncidentSeverity severity,
            String type,
            String reportedBy) {
        Incident incident = new Incident(id);
        incident.incidentNumber = incidentNumber;
        incident.title = title;
        incident.description = description;
        incident.severity = severity;
        incident.type = type;
        incident.reportedBy = reportedBy;
        return incident;
    }

    /**
     * Assigns the incident to someone.
     */
    public void assign(String assignedTo) {
        if (status == IncidentStatus.CLOSED || status == IncidentStatus.RESOLVED) {
            throw new IllegalStateException("Cannot assign closed incident");
        }
        this.assignedTo = assignedTo;
        this.assignedAt = Instant.now();
        this.status = IncidentStatus.UNDER_INVESTIGATION;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Escalates the incident.
     */
    public void escalate(String escalatedTo, String reason) {
        if (status == IncidentStatus.CLOSED) {
            throw new IllegalStateException("Cannot escalate closed incident");
        }
        this.escalatedTo = escalatedTo;
        this.escalatedAt = Instant.now();
        this.status = IncidentStatus.ESCALATED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a comment to the incident.
     */
    public void addComment(IncidentComment comment) {
        comments.add(comment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an attachment to the incident.
     */
    public void addAttachment(IncidentAttachment attachment) {
        attachments.add(attachment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Mitigates the incident.
     */
    public void mitigate(String rootCause) {
        if (status == IncidentStatus.CLOSED) {
            throw new IllegalStateException("Cannot mitigate closed incident");
        }
        this.rootCause = rootCause;
        this.status = IncidentStatus.MITIGATED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Resolves the incident.
     */
    public void resolve(String resolution) {
        if (status != IncidentStatus.MITIGATED) {
            throw new IllegalStateException("Cannot resolve incident in status: " + status);
        }
        this.resolution = resolution;
        this.resolvedAt = Instant.now();
        this.status = IncidentStatus.RESOLVED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Closes the incident.
     */
    public void close(String closedBy) {
        if (status != IncidentStatus.RESOLVED) {
            throw new IllegalStateException("Cannot close incident in status: " + status);
        }
        this.closedBy = closedBy;
        this.closedAt = Instant.now();
        this.status = IncidentStatus.CLOSED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the severity.
     */
    public void updateSeverity(IncidentSeverity severity) {
        this.severity = severity;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Links to a risk.
     */
    public void linkRisk(RiskId riskId) {
        this.riskId = riskId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the incident age in hours.
     */
    public long getAgeHours() {
        if (closedAt != null) {
            return java.time.Duration.between(reportedAt, closedAt).toHours();
        }
        return java.time.Duration.between(reportedAt, Instant.now()).toHours();
    }

    /**
     * Checks if the incident is overdue.
     */
    public boolean isOverdue() {
        if (status == IncidentStatus.CLOSED) {
            return false;
        }
        // Simple SLA: 24 hours for critical, 48 hours for major, 7 days for others
        long maxHours = switch (severity) {
            case CRITICAL -> 24;
            case MAJOR -> 48;
            case MODERATE -> 72;
            case MINOR -> 168;
            case INSIGNIFICANT -> 336;
        };
        return getAgeHours() > maxHours;
    }

    // Getters
    public String getIncidentNumber() { return incidentNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public IncidentSeverity getSeverity() { return severity; }
    public IncidentStatus getStatus() { return status; }
    public String getType() { return type; }
    public RiskId getRiskId() { return riskId; }
    public String getReportedBy() { return reportedBy; }
    public Instant getReportedAt() { return reportedAt; }
    public String getAssignedTo() { return assignedTo; }
    public Instant getAssignedAt() { return assignedAt; }
    public String getEscalatedTo() { return escalatedTo; }
    public Instant getEscalatedAt() { return escalatedAt; }
    public List<IncidentComment> getComments() { return Collections.unmodifiableList(comments); }
    public List<IncidentAttachment> getAttachments() { return Collections.unmodifiableList(attachments); }
    public String getRootCause() { return rootCause; }
    public String getResolution() { return resolution; }
    public Instant getResolvedAt() { return resolvedAt; }
    public String getClosedBy() { return closedBy; }
    public Instant getClosedAt() { return closedAt; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Incident{" +
                "id=" + getId() +
                ", incidentNumber='" + incidentNumber + '\'' +
                ", title='" + title + '\'' +
                ", severity=" + severity +
                ", status=" + status +
                ", age=" + getAgeHours() + "h" +
                '}';
    }

    /**
     * Incident comment value object.
     */
    public static final class IncidentComment implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String author;
        private final String content;
        private final Instant timestamp;
        private final boolean internal;

        public IncidentComment(String id, String author, String content, boolean internal) {
            this.id = id;
            this.author = author;
            this.content = content;
            this.internal = internal;
            this.timestamp = Instant.now();
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
        public Instant getTimestamp() { return timestamp; }
        public boolean isInternal() { return internal; }

        @Override
        public String toString() {
            return "IncidentComment{" +
                    "id='" + id + '\'' +
                    ", author='" + author + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    /**
     * Incident attachment value object.
     */
    public static final class IncidentAttachment implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String fileName;
        private final String fileType;
        private final long fileSize;
        private final String fileUrl;
        private final Instant uploadedAt;

        public IncidentAttachment(String id, String fileName, String fileType, long fileSize, String fileUrl) {
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
            return "IncidentAttachment{" +
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

    <module>modules/tenant/domain</module>
    <module>modules/tenant/application</module>
    <module>modules/tenant/infrastructure</module>
    <module>modules/tenant/interfaces</module>

    <module>modules/compliance/domain</module>
    <module>modules/compliance/application</module>
    <module>modules/compliance/infrastructure</module>
    <module>modules/compliance/interfaces</module>

    <module>modules/communication/domain</module>
    <module>modules/communication/application</module>
    <module>modules/communication/infrastructure</module>
    <module>modules/communication/interfaces</module>

    <module>modules/asset/domain</module>
    <module>modules/asset/application</module>
    <module>modules/asset/infrastructure</module>
    <module>modules/asset/interfaces</module>

    <module>modules/workforce/domain</module>
    <module>modules/workforce/application</module>
    <module>modules/workforce/infrastructure</module>
    <module>modules/workforce/interfaces</module>

    <module>modules/risk/domain</module>
    <module>modules/risk/application</module>
    <module>modules/risk/infrastructure</module>
    <module>modules/risk/interfaces</module>
</modules>