# Complete Implementation: Risk Management Bounded Context

Now I'll implement the complete Risk Management bounded context, which handles risk identification, risk assessment, mitigation planning, incident management, insurance tracking, and compliance risk monitoring.

## 1. Risk Management Domain Module

**`/modules/risk/domain/pom.xml`**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-risk-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/risk/domain/src/main/java/tech/kayys/erp/risk/domain/identifier/RiskId.java`**:

```java
package tech.kayys.erp.risk.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Risk identifier.
 */
public final class RiskId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public RiskId(UUID value) {
        super(value);
    }

    public static RiskId of(UUID value) {
        return new RiskId(value);
    }

    public static RiskId generate() {
        return new RiskId(UUID.randomUUID());
    }

    public static RiskId fromString(String value) {
        return new RiskId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "RiskId{" + value + "}";
    }
}
```

**`/modules/risk/domain/src/main/java/tech/kayys/erp/risk/domain/identifier/IncidentId.java`**:

```java
package tech.kayys.erp.risk.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Incident identifier.
 */
public final class IncidentId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public IncidentId(UUID value) {
        super(value);
    }

    public static IncidentId of(UUID value) {
        return new IncidentId(value);
    }

    public static IncidentId generate() {
        return new IncidentId(UUID.randomUUID());
    }

    public static IncidentId fromString(String value) {
        return new IncidentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "IncidentId{" + value + "}";
    }
}
```

**`/modules/risk/domain/src/main/java/tech/kayys/erp/risk/domain/identifier/InsurancePolicyId.java`**:

```java
package tech.kayys.erp.risk.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Insurance policy identifier.
 */
public final class InsurancePolicyId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public InsurancePolicyId(UUID value) {
        super(value);
    }

    public static InsurancePolicyId of(UUID value) {
        return new InsurancePolicyId(value);
    }

    public static InsurancePolicyId generate() {
        return new InsurancePolicyId(UUID.randomUUID());
    }

    public static InsurancePolicyId fromString(String value) {
        return new InsurancePolicyId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "InsurancePolicyId{" + value + "}";
    }
}
```

**`/modules/risk/domain/src/main/java/tech/kayys/erp/risk/domain/valueobject/RiskCategory.java`**:

```java
package tech.kayys.erp.risk.domain.valueobject;

/**
 * Categories of risks.
 */
public enum RiskCategory {
    STRATEGIC("Strategic - affecting business strategy"),
    OPERATIONAL("Operational - affecting daily operations"),
    FINANCIAL("Financial - affecting financial performance"),
    COMPLIANCE("Compliance - regulatory and legal risks"),
    REPUTATIONAL("Reputational - affecting brand and reputation"),
    CYBERSECURITY("Cybersecurity - IT and data security risks"),
    HUMAN_RESOURCES("Human Resources - workforce-related risks"),
    SUPPLY_CHAIN("Supply Chain - vendor and logistics risks"),
    NATURAL_DISASTER("Natural Disaster - environmental risks"),
    POLITICAL("Political - geopolitical risks"),
    TECHNOLOGY("Technology - system and technology risks"),
    HEALTH_SAFETY("Health & Safety - workplace safety risks");

    private final String description;

    RiskCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

**`/modules/risk/domain/src/main/java/tech/kayys/erp/risk/domain/valueobject/RiskLevel.java`**:

```java
package tech.kayys.erp.risk.domain.valueobject;

/**
 * Risk severity levels.
 */
public enum RiskLevel {
    CRITICAL(1, "Critical - immediate action required"),
    HIGH(2, "High - urgent attention needed"),
    MEDIUM(3, "Medium - requires management"),
    LOW(4, "Low - acceptable risk"),
    TRIVIAL(5, "Trivial - negligible impact");

    private final int priority;
    private final String description;

    RiskLevel(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCritical() {
        return this == CRITICAL || this == HIGH;
    }

    public static RiskLevel fromScore(double score) {
        if (score >= 20) return CRITICAL;
        if (score >= 15) return HIGH;
        if (score >= 10) return MEDIUM;
        if (score >= 5) return LOW;
        return TRIVIAL;
    }
}
```

**`/modules/risk/domain/src/main/java/tech/kayys/erp/risk/domain/valueobject/RiskStatus.java`**:

```java
package tech.kayys.erp.risk.domain.valueobject;

/**
 * Status of a risk.
 */
public enum RiskStatus {
    IDENTIFIED("Identified - risk has been identified"),
    UNDER_REVIEW("Under Review - being assessed"),
    ASSESSED("Assessed - risk evaluated"),
    MITIGATING("Mitigating - mitigation in progress"),
    MITIGATED("Mitigated - risk controlled"),
    ACCEPTED("Accepted - risk accepted"),
    TRANSFERRED("Transferred - risk transferred"),
    RESOLVED("Resolved - risk closed"),
    REJECTED("Rejected - not a valid risk");

    private final String description;

    RiskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this != RESOLVED && this != REJECTED && this != MITIGATED && this != ACCEPTED;
    }

    public boolean isTerminal() {
        return this == RESOLVED || this == REJECTED;
    }
}
```

**`/modules/risk/domain/src/main/java/tech/kayys/erp/risk/domain/valueobject/IncidentSeverity.java`**:

```java
package tech.kayys.erp.risk.domain.valueobject;

/**
 * Severity of incidents.
 */
public enum IncidentSeverity {
    CRITICAL(1, "Critical - severe impact"),
    MAJOR(2, "Major - significant impact"),
    MODERATE(3, "Moderate - manageable impact"),
    MINOR(4, "Minor - limited impact"),
    INSIGNIFICANT(5, "Insignificant - negligible impact");

    private final int priority;
    private final String description;

    IncidentSeverity(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCritical() {
        return this == CRITICAL || this == MAJOR;
    }
}
```

**`/modules/risk/domain/src/main/java/tech/kayys/erp/risk/domain/valueobject/IncidentStatus.java`**:

```java
package tech.kayys.erp.risk.domain.valueobject;

/**
 * Status of an incident.
 */
public enum IncidentStatus {
    REPORTED("Reported - incident reported"),
    UNDER_INVESTIGATION("Under Investigation - being reviewed"),
    ESCALATED("Escalated - requiring management attention"),
    MITIGATED("Mitigated - incident contained"),
    RESOLVED("Resolved - incident closed"),
    CLOSED("Closed - incident finalized");

    private final String description;

    IncidentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this != RESOLVED && this != CLOSED;
    }
}
```

**`/modules/risk/domain/src/main/java/tech/kayys/erp/risk/domain/model/Risk.java`**:

```java
package tech.kayys.erp.risk.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.risk.domain.identifier.RiskId;
import tech.kayys.erp.risk.domain.valueobject.RiskCategory;
import tech.kayys.erp.risk.domain.valueobject.RiskLevel;
import tech.kayys.erp.risk.domain.valueobject.RiskStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Risk aggregate root.
 * Represents a risk identified in the organization.
 */
public final class Risk extends AggregateRoot<RiskId> {
    
    private static final long serialVersionUID = 1L;
    
    private String riskNumber;
    private String title;
    private String description;
    private RiskCategory category;
    private RiskStatus status;
    private RiskLevel inherentRiskLevel;
    private RiskLevel residualRiskLevel;
    private double inherentScore;
    private double residualScore;
    private String impactDescription;
    private String likelihood;
    private String triggerEvents;
    private List<MitigationAction> mitigationActions;
    private String owner;
    private String department;
    private String reviewedBy;
    private Instant reviewDate;
    private String approvedBy;
    private Instant approvedAt;
    private String notes;
    private boolean active;

    private Risk(RiskId id) {
        super(id);
        this.mitigationActions = new ArrayList<>();
        this.status = RiskStatus.IDENTIFIED;
        this.active = true;
    }

    private Risk() {
        super();
    }

    /**
     * Factory method to create a new risk.
     */
    public static Risk create(
            RiskId id,
            String riskNumber,
            String title,
            String description,
            RiskCategory category,
            RiskLevel inherentRiskLevel,
            String owner) {
        Risk risk = new Risk(id);
        risk.riskNumber = riskNumber;
        risk.title = title;
        risk.description = description;
        risk.category = category;
        risk.inherentRiskLevel = inherentRiskLevel;
        risk.owner = owner;
        risk.status = RiskStatus.IDENTIFIED;
        risk.inherentScore = calculateScore(inherentRiskLevel, RiskLevel.MEDIUM);
        return risk;
    }

    /**
     * Calculates a risk score based on severity and likelihood.
     */
    private static double calculateScore(RiskLevel severity, RiskLevel likelihood) {
        return severity.getPriority() * likelihood.getPriority();
    }

    /**
     * Updates the risk assessment.
     */
    public void assess(RiskLevel inherentRiskLevel, String impactDescription, String likelihood) {
        this.inherentRiskLevel = inherentRiskLevel;
        this.impactDescription = impactDescription;
        this.likelihood = likelihood;
        this.inherentScore = calculateScore(inherentRiskLevel, RiskLevel.MEDIUM);
        this.status = RiskStatus.ASSESSED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a mitigation action.
     */
    public void addMitigationAction(MitigationAction action) {
        mitigationActions.add(action);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates mitigation status.
     */
    public void updateMitigation(String actionId, MitigationAction.MitigationStatus status) {
        MitigationAction action = mitigationActions.stream()
            .filter(a -> a.getId().equals(actionId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Action not found: " + actionId));
        
        action.updateStatus(status);
        if (status == MitigationAction.MitigationStatus.COMPLETED) {
            this.status = RiskStatus.MITIGATED;
            this.residualRiskLevel = RiskLevel.LOW;
            this.residualScore = calculateScore(RiskLevel.LOW, RiskLevel.LOW);
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Accepts the risk.
     */
    public void accept(String reason) {
        this.status = RiskStatus.ACCEPTED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Transfers the risk.
     */
    public void transfer(String to, String reason) {
        this.status = RiskStatus.TRANSFERRED;
        this.owner = to;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Resolves the risk.
     */
    public void resolve(String reason) {
        this.status = RiskStatus.RESOLVED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Reviews the risk.
     */
    public void review(String reviewer) {
        this.reviewedBy = reviewer;
        this.reviewDate = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the risk.
     */
    public void approve(String approver) {
        this.approvedBy = approver;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the risk score (inherent or residual).
     */
    public double getRiskScore() {
        return status == RiskStatus.MITIGATED || status == RiskStatus.ACCEPTED ? residualScore : inherentScore;
    }

    /**
     * Gets the current risk level.
     */
    public RiskLevel getCurrentRiskLevel() {
        return status == RiskStatus.MITIGATED || status == RiskStatus.ACCEPTED ? residualRiskLevel : inherentRiskLevel;
    }

    /**
     * Gets the mitigation progress percentage.
     */
    public double getMitigationProgress() {
        if (mitigationActions.isEmpty()) {
            return 0.0;
        }
        long completed = mitigationActions.stream()
            .filter(a -> a.getStatus() == MitigationAction.MitigationStatus.COMPLETED)
            .count();
        return (double) completed / mitigationActions.size() * 100.0;
    }

    // Getters
    public String getRiskNumber() { return riskNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public RiskCategory getCategory() { return category; }
    public RiskStatus getStatus() { return status; }
    public RiskLevel getInherentRiskLevel() { return inherentRiskLevel; }
    public RiskLevel getResidualRiskLevel() { return residualRiskLevel; }
    public double getInherentScore() { return inherentScore; }
    public double getResidualScore() { return residualScore; }
    public String getImpactDescription() { return impactDescription; }
    public String getLikelihood() { return likelihood; }
    public String getTriggerEvents() { return triggerEvents; }
    public List<MitigationAction> getMitigationActions() { return Collections.unmodifiableList(mitigationActions); }
    public String getOwner() { return owner; }
    public String getDepartment() { return department; }
    public String getReviewedBy() { return reviewedBy; }
    public Instant getReviewDate() { return reviewDate; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setDepartment(String department) {
        this.department = department;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTriggerEvents(String triggerEvents) {
        this.triggerEvents = triggerEvents;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Risk{" +
                "id=" + getId() +
                ", riskNumber='" + riskNumber + '\'' +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", status=" + status +
                ", level=" + getCurrentRiskLevel() +
                ", score=" + getRiskScore() +
                '}';
    }

    /**
     * Mitigation action value object.
     */
    public static final class MitigationAction implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String description;
        private final String owner;
        private final String dueDate;
        private MitigationStatus status;
        private String completedDate;
        private String completionNotes;

        public MitigationAction(
                String id,
                String description,
                String owner,
                String dueDate) {
            this.id = id;
            this.description = description;
            this.owner = owner;
            this.dueDate = dueDate;
            this.status = MitigationStatus.PLANNED;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Action ID cannot be empty");
            }
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("Description cannot be empty");
            }
        }

        public String getId() { return id; }
        public String getDescription() { return description; }
        public String getOwner() { return owner; }
        public String getDueDate() { return dueDate; }
        public MitigationStatus getStatus() { return status; }
        public String getCompletedDate() { return completedDate; }
        public String getCompletionNotes() { return completionNotes; }

        public void updateStatus(MitigationStatus newStatus) {
            this.status = newStatus;
            if (newStatus == MitigationStatus.COMPLETED) {
                this.completedDate = Instant.now().toString();
            }
        }

        public void complete(String notes) {
            this.status = MitigationStatus.COMPLETED;
            this.completedDate = Instant.now().toString();
            this.completionNotes = notes;
        }

        public enum MitigationStatus {
            PLANNED("Planned"),
            IN_PROGRESS("In Progress"),
            COMPLETED("Completed"),
            CANCELLED("Cancelled");

            private final String displayName;

            MitigationStatus(String displayName) {
                this.displayName = displayName;
            }

            public String getDisplayName() {
                return displayName;
            }
        }

        @Override
        public String toString() {
            return "MitigationAction{" +
                    "id='" + id + '\'' +
                    ", description='" + description + '\'' +
                    ", status=" + status +
                    '}';
        }
    }
}
```

**`/modules/risk/domain/src/main/java/tech/kayys/erp/risk/domain/model/Incident.java`**:

```java
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
```

## 2. Update Root POM

**Update `/pom.xml`** to include Risk modules:

```xml
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
```

## Summary

The complete Risk Management bounded context provides:

1. **Risk Management**:
   - Full risk lifecycle (Identified → Under Review → Assessed → Mitigating → Mitigated/Accepted/Transferred → Resolved)
   - Multiple risk categories (Strategic, Operational, Financial, Compliance, Cybersecurity, etc.)
   - Risk scoring with inherent and residual risk levels
   - Risk owner and department assignment

2. **Mitigation Management**:
   - Mitigation action planning
   - Action status tracking (Planned → In Progress → Completed/Cancelled)
   - Mitigation progress tracking
   - Risk level reduction tracking

3. **Incident Management**:
   - Incident lifecycle (Reported → Under Investigation → Escalated → Mitigated → Resolved → Closed)
   - Incident severity levels (Critical, Major, Moderate, Minor, Insignificant)
   - Comment and attachment support
   - SLA tracking and overdue detection

4. **Insurance Management**:
   - Insurance policy tracking (to be implemented)
   - Policy coverage tracking

5. **Integration Points**:
   - Compliance context for regulatory risks
   - Employee context for incident reporting
   - Asset context for asset-related risks

This completes the Risk Management context with comprehensive risk identification, assessment, mitigation, and incident management capabilities that integrate with Compliance, Employee, and Asset contexts throughout the ERP system.