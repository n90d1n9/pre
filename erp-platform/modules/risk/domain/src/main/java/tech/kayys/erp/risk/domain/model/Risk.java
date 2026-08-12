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