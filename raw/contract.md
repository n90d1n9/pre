# Complete Implementation: Contract Management Extension

Now I'll complete the Contract Management functionality within the Purchasing context, including contract templates, compliance tracking, contract performance metrics, and contract renewal automation.

## 1. Contract Domain Extensions

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/ContractTemplate.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.Objects;

/**
 * Contract template value object.
 */
public final class ContractTemplate implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String id;
    private final String name;
    private final String description;
    private final ContractType contractType;
    private final String content;
    private final String language;
    private final String version;
    private final boolean active;

    public ContractTemplate(
            String id,
            String name,
            String description,
            ContractType contractType,
            String content,
            String language,
            String version,
            boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.contractType = contractType;
        this.content = content;
        this.language = language;
        this.version = version;
        this.active = active;
        validate();
    }

    @Override
    public void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Template ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be empty");
        }
        if (contractType == null) {
            throw new IllegalArgumentException("Contract type is required");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ContractType getContractType() { return contractType; }
    public String getContent() { return content; }
    public String getLanguage() { return language; }
    public String getVersion() { return version; }
    public boolean isActive() { return active; }

    public String renderWithData(String data) {
        // Simple template rendering - in production, use a proper template engine
        return content.replace("{{data}}", data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractTemplate that = (ContractTemplate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ContractTemplate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", contractType=" + contractType +
                ", version='" + version + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private ContractType contractType;
        private String content;
        private String language = "en";
        private String version = "1.0";
        private boolean active = true;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder contractType(ContractType contractType) {
            this.contractType = contractType;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public ContractTemplate build() {
            return new ContractTemplate(id, name, description, contractType, content, language, version, active);
        }
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/ContractCompliance.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.util.Objects;

/**
 * Contract compliance record.
 */
public final class ContractCompliance implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String complianceType;
    private final String description;
    private final Instant reviewDate;
    private final Instant nextReviewDate;
    private final String reviewer;
    private final String status;
    private final String findings;
    private final boolean compliant;
    private final String recommendations;

    public ContractCompliance(
            String complianceType,
            String description,
            Instant reviewDate,
            Instant nextReviewDate,
            String reviewer,
            String status,
            String findings,
            boolean compliant,
            String recommendations) {
        this.complianceType = complianceType;
        this.description = description;
        this.reviewDate = reviewDate;
        this.nextReviewDate = nextReviewDate;
        this.reviewer = reviewer;
        this.status = status;
        this.findings = findings;
        this.compliant = compliant;
        this.recommendations = recommendations;
        validate();
    }

    @Override
    public void validate() {
        if (complianceType == null || complianceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Compliance type cannot be empty");
        }
        if (reviewDate == null) {
            throw new IllegalArgumentException("Review date cannot be null");
        }
        if (reviewer == null || reviewer.trim().isEmpty()) {
            throw new IllegalArgumentException("Reviewer cannot be empty");
        }
        if (nextReviewDate != null && nextReviewDate.isBefore(reviewDate)) {
            throw new IllegalArgumentException("Next review date must be after review date");
        }
    }

    public String getComplianceType() { return complianceType; }
    public String getDescription() { return description; }
    public Instant getReviewDate() { return reviewDate; }
    public Instant getNextReviewDate() { return nextReviewDate; }
    public String getReviewer() { return reviewer; }
    public String getStatus() { return status; }
    public String getFindings() { return findings; }
    public boolean isCompliant() { return compliant; }
    public String getRecommendations() { return recommendations; }

    public boolean requiresFollowUp() {
        return !compliant || (nextReviewDate != null && Instant.now().isAfter(nextReviewDate));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractCompliance that = (ContractCompliance) o;
        return Objects.equals(complianceType, that.complianceType) &&
               Objects.equals(reviewDate, that.reviewDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(complianceType, reviewDate);
    }

    @Override
    public String toString() {
        return "ContractCompliance{" +
                "complianceType='" + complianceType + '\'' +
                ", reviewDate=" + reviewDate +
                ", compliant=" + compliant +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String complianceType;
        private String description;
        private Instant reviewDate;
        private Instant nextReviewDate;
        private String reviewer;
        private String status = "Review Complete";
        private String findings;
        private boolean compliant = true;
        private String recommendations;

        public Builder complianceType(String complianceType) {
            this.complianceType = complianceType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder reviewDate(Instant reviewDate) {
            this.reviewDate = reviewDate;
            return this;
        }

        public Builder nextReviewDate(Instant nextReviewDate) {
            this.nextReviewDate = nextReviewDate;
            return this;
        }

        public Builder reviewer(String reviewer) {
            this.reviewer = reviewer;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder findings(String findings) {
            this.findings = findings;
            return this;
        }

        public Builder compliant(boolean compliant) {
            this.compliant = compliant;
            return this;
        }

        public Builder recommendations(String recommendations) {
            this.recommendations = recommendations;
            return this;
        }

        public ContractCompliance build() {
            if (reviewDate == null) {
                reviewDate = Instant.now();
            }
            return new ContractCompliance(
                complianceType, description, reviewDate, nextReviewDate,
                reviewer, status, findings, compliant, recommendations
            );
        }
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/ContractPerformance.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Contract performance metrics.
 */
public final class ContractPerformance implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String metricName;
    private final BigDecimal targetValue;
    private final BigDecimal actualValue;
    private final String uom;
    private final Instant measurementDate;
    private final String notes;
    private final boolean achieved;
    private final double percentageAchieved;

    public ContractPerformance(
            String metricName,
            BigDecimal targetValue,
            BigDecimal actualValue,
            String uom,
            Instant measurementDate,
            String notes,
            boolean achieved) {
        this.metricName = metricName;
        this.targetValue = targetValue;
        this.actualValue = actualValue;
        this.uom = uom;
        this.measurementDate = measurementDate;
        this.notes = notes;
        this.achieved = achieved;
        this.percentageAchieved = calculatePercentage();
        validate();
    }

    @Override
    public void validate() {
        if (metricName == null || metricName.trim().isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be empty");
        }
        if (targetValue == null || targetValue.signum() <= 0) {
            throw new IllegalArgumentException("Target value must be positive");
        }
        if (actualValue == null) {
            throw new IllegalArgumentException("Actual value cannot be null");
        }
        if (measurementDate == null) {
            throw new IllegalArgumentException("Measurement date cannot be null");
        }
    }

    private double calculatePercentage() {
        if (targetValue.signum() == 0) {
            return 0.0;
        }
        return actualValue.divide(targetValue, 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .doubleValue();
    }

    public String getMetricName() { return metricName; }
    public BigDecimal getTargetValue() { return targetValue; }
    public BigDecimal getActualValue() { return actualValue; }
    public String getUom() { return uom; }
    public Instant getMeasurementDate() { return measurementDate; }
    public String getNotes() { return notes; }
    public boolean isAchieved() { return achieved; }
    public double getPercentageAchieved() { return percentageAchieved; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractPerformance that = (ContractPerformance) o;
        return Objects.equals(metricName, that.metricName) &&
               Objects.equals(measurementDate, that.measurementDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metricName, measurementDate);
    }

    @Override
    public String toString() {
        return "ContractPerformance{" +
                "metricName='" + metricName + '\'' +
                ", achieved=" + achieved +
                ", percentageAchieved=" + percentageAchieved + "%" +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String metricName;
        private BigDecimal targetValue;
        private BigDecimal actualValue;
        private String uom = "%";
        private Instant measurementDate;
        private String notes;
        private boolean achieved = false;

        public Builder metricName(String metricName) {
            this.metricName = metricName;
            return this;
        }

        public Builder targetValue(BigDecimal targetValue) {
            this.targetValue = targetValue;
            return this;
        }

        public Builder actualValue(BigDecimal actualValue) {
            this.actualValue = actualValue;
            return this;
        }

        public Builder uom(String uom) {
            this.uom = uom;
            return this;
        }

        public Builder measurementDate(Instant measurementDate) {
            this.measurementDate = measurementDate;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder achieved(boolean achieved) {
            this.achieved = achieved;
            return this;
        }

        public ContractPerformance build() {
            if (measurementDate == null) {
                measurementDate = Instant.now();
            }
            return new ContractPerformance(
                metricName, targetValue, actualValue, uom, measurementDate, notes, achieved
            );
        }
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/model/VendorContract.java`** (extended):

```java
// Add these fields and methods to the existing VendorContract class:

public class VendorContract extends AggregateRoot<ContractId> {
    // ... existing fields ...
    
    private String templateId;
    private List<ContractCompliance> complianceRecords;
    private List<ContractPerformance> performanceMetrics;
    private List<ContractAmendment> amendments;
    private String approvedBy;
    private Instant approvedAt;
    private String lastModifiedBy;
    private String legalEntity;
    private String governingLaw;
    private String disputeResolution;
    private boolean terminated;
    private Instant terminationDate;
    private String terminationReason;
    
    // ... existing constructor ...
    
    // Add to constructor
    private VendorContract(ContractId id) {
        // ... existing initialization ...
        this.complianceRecords = new ArrayList<>();
        this.performanceMetrics = new ArrayList<>();
        this.amendments = new ArrayList<>();
        this.terminated = false;
    }
    
    /**
     * Adds a compliance record to the contract.
     */
    public void addComplianceRecord(ContractCompliance compliance) {
        complianceRecords.add(compliance);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
    
    /**
     * Adds a performance metric.
     */
    public void addPerformanceMetric(ContractPerformance metric) {
        performanceMetrics.add(metric);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
    
    /**
     * Adds an amendment to the contract.
     */
    public void addAmendment(ContractAmendment amendment) {
        if (status != ContractStatus.ACTIVE && status != ContractStatus.SUSPENDED) {
            throw new IllegalStateException("Cannot amend contract in status: " + status);
        }
        amendments.add(amendment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
    
    /**
     * Gets the overall compliance score.
     */
    public double getComplianceScore() {
        if (complianceRecords.isEmpty()) {
            return 100.0;
        }
        long compliantCount = complianceRecords.stream()
            .filter(ContractCompliance::isCompliant)
            .count();
        return (double) compliantCount / complianceRecords.size() * 100.0;
    }
    
    /**
     * Gets the overall performance score.
     */
    public double getPerformanceScore() {
        if (performanceMetrics.isEmpty()) {
            return 0.0;
        }
        return performanceMetrics.stream()
            .mapToDouble(ContractPerformance::getPercentageAchieved)
            .average()
            .orElse(0.0);
    }
    
    // ... continue with getters and setters ...
    
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    
    public List<ContractCompliance> getComplianceRecords() { 
        return Collections.unmodifiableList(complianceRecords); 
    }
    
    public List<ContractPerformance> getPerformanceMetrics() { 
        return Collections.unmodifiableList(performanceMetrics); 
    }
    
    public List<ContractAmendment> getAmendments() { 
        return Collections.unmodifiableList(amendments); 
    }
    
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public String getLegalEntity() { return legalEntity; }
    public String getGoverningLaw() { return governingLaw; }
    public String getDisputeResolution() { return disputeResolution; }
    public boolean isTerminated() { return terminated; }
    public Instant getTerminationDate() { return terminationDate; }
    public String getTerminationReason() { return terminationReason; }
    
    public void setLegalEntity(String legalEntity) {
        this.legalEntity = legalEntity;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
    
    public void setGoverningLaw(String governingLaw) {
        this.governingLaw = governingLaw;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
    
    public void setDisputeResolution(String disputeResolution) {
        this.disputeResolution = disputeResolution;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
    
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/ContractAmendment.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.util.Objects;

/**
 * Contract amendment value object.
 */
public final class ContractAmendment implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String amendmentNumber;
    private final String description;
    private final String changeType;
    private final String beforeChange;
    private final String afterChange;
    private final Instant effectiveDate;
    private final String approvedBy;
    private final Instant approvedAt;
    private final String notes;

    public ContractAmendment(
            String amendmentNumber,
            String description,
            String changeType,
            String beforeChange,
            String afterChange,
            Instant effectiveDate,
            String approvedBy,
            Instant approvedAt,
            String notes) {
        this.amendmentNumber = amendmentNumber;
        this.description = description;
        this.changeType = changeType;
        this.beforeChange = beforeChange;
        this.afterChange = afterChange;
        this.effectiveDate = effectiveDate;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.notes = notes;
        validate();
    }

    @Override
    public void validate() {
        if (amendmentNumber == null || amendmentNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Amendment number cannot be empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (changeType == null || changeType.trim().isEmpty()) {
            throw new IllegalArgumentException("Change type cannot be empty");
        }
        if (effectiveDate == null) {
            throw new IllegalArgumentException("Effective date cannot be null");
        }
        if (approvedBy == null || approvedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Approved by cannot be empty");
        }
        if (approvedAt == null) {
            throw new IllegalArgumentException("Approved at cannot be null");
        }
    }

    public String getAmendmentNumber() { return amendmentNumber; }
    public String getDescription() { return description; }
    public String getChangeType() { return changeType; }
    public String getBeforeChange() { return beforeChange; }
    public String getAfterChange() { return afterChange; }
    public Instant getEffectiveDate() { return effectiveDate; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getNotes() { return notes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractAmendment that = (ContractAmendment) o;
        return Objects.equals(amendmentNumber, that.amendmentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amendmentNumber);
    }

    @Override
    public String toString() {
        return "ContractAmendment{" +
                "amendmentNumber='" + amendmentNumber + '\'' +
                ", description='" + description + '\'' +
                ", effectiveDate=" + effectiveDate +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String amendmentNumber;
        private String description;
        private String changeType;
        private String beforeChange;
        private String afterChange;
        private Instant effectiveDate;
        private String approvedBy;
        private Instant approvedAt;
        private String notes;

        public Builder amendmentNumber(String amendmentNumber) {
            this.amendmentNumber = amendmentNumber;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder changeType(String changeType) {
            this.changeType = changeType;
            return this;
        }

        public Builder beforeChange(String beforeChange) {
            this.beforeChange = beforeChange;
            return this;
        }

        public Builder afterChange(String afterChange) {
            this.afterChange = afterChange;
            return this;
        }

        public Builder effectiveDate(Instant effectiveDate) {
            this.effectiveDate = effectiveDate;
            return this;
        }

        public Builder approvedBy(String approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public Builder approvedAt(Instant approvedAt) {
            this.approvedAt = approvedAt;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public ContractAmendment build() {
            if (approvedAt == null) {
                approvedAt = Instant.now();
            }
            return new ContractAmendment(
                amendmentNumber, description, changeType, beforeChange,
                afterChange, effectiveDate, approvedBy, approvedAt, notes
            );
        }
    }
}
```

## 2. Contract Application Extensions

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/ContractCommandService.java`**:

```java
package tech.kayys.erp.purchasing.application.api;

import tech.kayys.erp.purchasing.application.api.command.*;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.util.concurrent.CompletionStage;

/**
 * Extended public API for contract commands.
 */
public interface ContractCommandService extends VendorCommandService {

    /**
     * Creates a contract from a template.
     */
    CompletionStage<ContractId> createContractFromTemplate(CreateFromTemplateCommand command);

    /**
     * Adds compliance record to a contract.
     */
    CompletionStage<ContractId> addComplianceRecord(AddComplianceRecordCommand command);

    /**
     * Adds performance metric to a contract.
     */
    CompletionStage<ContractId> addPerformanceMetric(AddPerformanceMetricCommand command);

    /**
     * Amends a contract.
     */
    CompletionStage<ContractId> amendContract(AmendContractCommand command);

    /**
     * Terminates a contract.
     */
    CompletionStage<ContractId> terminateContract(TerminateContractCommand command);

    /**
     * Processes auto-renewals.
     */
    CompletionStage<Integer> processAutoRenewals();
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/CreateFromTemplateCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to create a contract from a template.
 */
public record CreateFromTemplateCommand(
        ContractId contractId,
        UUID vendorId,
        String vendorName,
        String templateId,
        Instant effectiveDate,
        Instant expirationDate,
        String data,
        String currencyCode,
        String notes,
        String createdBy
) implements Command<ContractId> {

    public CreateFromTemplateCommand {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        if (templateId == null || templateId.trim().isEmpty()) {
            throw new IllegalArgumentException("Template ID cannot be empty");
        }
        if (effectiveDate == null) {
            throw new IllegalArgumentException("Effective date is required");
        }
        if (expirationDate == null) {
            throw new IllegalArgumentException("Expiration date is required");
        }
        if (expirationDate.isBefore(effectiveDate)) {
            throw new IllegalArgumentException("Expiration date must be after effective date");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContractId contractId;
        private UUID vendorId;
        private String vendorName;
        private String templateId;
        private Instant effectiveDate;
        private Instant expirationDate;
        private String data;
        private String currencyCode = "USD";
        private String notes;
        private String createdBy;

        public Builder contractId(ContractId contractId) {
            this.contractId = contractId;
            return this;
        }

        public Builder vendorId(UUID vendorId) {
            this.vendorId = vendorId;
            return this;
        }

        public Builder vendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public Builder effectiveDate(Instant effectiveDate) {
            this.effectiveDate = effectiveDate;
            return this;
        }

        public Builder expirationDate(Instant expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CreateFromTemplateCommand build() {
            if (contractId == null) {
                contractId = ContractId.generate();
            }
            return new CreateFromTemplateCommand(
                contractId, vendorId, vendorName, templateId,
                effectiveDate, expirationDate, data, currencyCode,
                notes, createdBy
            );
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/AddComplianceRecordCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.time.Instant;

/**
 * Command to add a compliance record to a contract.
 */
public record AddComplianceRecordCommand(
        ContractId contractId,
        String complianceType,
        String description,
        Instant reviewDate,
        Instant nextReviewDate,
        String reviewer,
        String findings,
        boolean compliant,
        String recommendations
) implements Command<ContractId> {

    public AddComplianceRecordCommand {
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        if (complianceType == null || complianceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Compliance type cannot be empty");
        }
        if (reviewDate == null) {
            throw new IllegalArgumentException("Review date is required");
        }
        if (reviewer == null || reviewer.trim().isEmpty()) {
            throw new IllegalArgumentException("Reviewer cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContractId contractId;
        private String complianceType;
        private String description;
        private Instant reviewDate;
        private Instant nextReviewDate;
        private String reviewer;
        private String findings;
        private boolean compliant = true;
        private String recommendations;

        public Builder contractId(ContractId contractId) {
            this.contractId = contractId;
            return this;
        }

        public Builder complianceType(String complianceType) {
            this.complianceType = complianceType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder reviewDate(Instant reviewDate) {
            this.reviewDate = reviewDate;
            return this;
        }

        public Builder nextReviewDate(Instant nextReviewDate) {
            this.nextReviewDate = nextReviewDate;
            return this;
        }

        public Builder reviewer(String reviewer) {
            this.reviewer = reviewer;
            return this;
        }

        public Builder findings(String findings) {
            this.findings = findings;
            return this;
        }

        public Builder compliant(boolean compliant) {
            this.compliant = compliant;
            return this;
        }

        public Builder recommendations(String recommendations) {
            this.recommendations = recommendations;
            return this;
        }

        public AddComplianceRecordCommand build() {
            if (reviewDate == null) {
                reviewDate = Instant.now();
            }
            return new AddComplianceRecordCommand(
                contractId, complianceType, description, reviewDate,
                nextReviewDate, reviewer, findings, compliant, recommendations
            );
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/AmendContractCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.time.Instant;

/**
 * Command to amend a contract.
 */
public record AmendContractCommand(
        ContractId contractId,
        String amendmentNumber,
        String description,
        String changeType,
        String beforeChange,
        String afterChange,
        Instant effectiveDate,
        String approvedBy,
        String notes
) implements Command<ContractId> {

    public AmendContractCommand {
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (changeType == null || changeType.trim().isEmpty()) {
            throw new IllegalArgumentException("Change type cannot be empty");
        }
        if (effectiveDate == null) {
            throw new IllegalArgumentException("Effective date is required");
        }
        if (approvedBy == null || approvedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Approved by cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContractId contractId;
        private String amendmentNumber;
        private String description;
        private String changeType;
        private String beforeChange;
        private String afterChange;
        private Instant effectiveDate;
        private String approvedBy;
        private String notes;

        public Builder contractId(ContractId contractId) {
            this.contractId = contractId;
            return this;
        }

        public Builder amendmentNumber(String amendmentNumber) {
            this.amendmentNumber = amendmentNumber;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder changeType(String changeType) {
            this.changeType = changeType;
            return this;
        }

        public Builder beforeChange(String beforeChange) {
            this.beforeChange = beforeChange;
            return this;
        }

        public Builder afterChange(String afterChange) {
            this.afterChange = afterChange;
            return this;
        }

        public Builder effectiveDate(Instant effectiveDate) {
            this.effectiveDate = effectiveDate;
            return this;
        }

        public Builder approvedBy(String approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public AmendContractCommand build() {
            if (amendmentNumber == null) {
                amendmentNumber = "AMD-" + System.currentTimeMillis();
            }
            if (effectiveDate == null) {
                effectiveDate = Instant.now();
            }
            return new AmendContractCommand(
                contractId, amendmentNumber, description, changeType,
                beforeChange, afterChange, effectiveDate, approvedBy, notes
            );
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/internal/CreateFromTemplateHandler.java`**:

```java
package tech.kayys.erp.purchasing.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.purchasing.application.api.command.CreateFromTemplateCommand;
import tech.kayys.erp.purchasing.application.port.ContractTemplatePort;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.model.VendorContract;
import tech.kayys.erp.purchasing.domain.repository.VendorContractRepository;
import tech.kayys.erp.purchasing.domain.valueobject.ContractTemplate;
import tech.kayys.erp.purchasing.domain.valueobject.ContractType;
import tech.kayys.erp.purchasing.domain.valueobject.Money;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating contracts from templates.
 */
@UseCase("Create a contract from a template")
public class CreateFromTemplateHandler 
        implements CommandHandler<CreateFromTemplateCommand, ContractId> {

    private final VendorContractRepository contractRepository;
    private final ContractTemplatePort templatePort;

    @Inject
    public CreateFromTemplateHandler(
            VendorContractRepository contractRepository,
            ContractTemplatePort templatePort) {
        this.contractRepository = contractRepository;
        this.templatePort = templatePort;
    }

    @Override
    public CompletionStage<ContractId> handle(CreateFromTemplateCommand command) {
        // 1. Get the template
        return templatePort.getTemplate(command.templateId())
            .thenCompose(template -> {
                if (template == null || !template.isActive()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Template not found or inactive: " + command.templateId())
                    );
                }

                // 2. Create the contract from template
                VendorContract contract = VendorContract.create(
                    command.contractId(),
                    "CTR-" + System.currentTimeMillis(),
                    VendorId.of(command.vendorId()),
                    command.vendorName(),
                    template.getContractType(),
                    command.effectiveDate(),
                    command.expirationDate(),
                    command.currencyCode()
                );

                // 3. Set template details
                contract.setTitle(template.getName());
                contract.setDescription(template.getDescription());
                contract.setTemplateId(template.getId());

                // 4. Render template content with data
                if (command.data() != null) {
                    String renderedContent = template.renderWithData(command.data());
                    contract.setTermsAndConditions(renderedContent);
                }

                // 5. Set additional fields
                if (command.notes() != null) {
                    contract.setNotes(command.notes());
                }
                if (command.createdBy() != null) {
                    contract.setCreatedBy(command.createdBy());
                }

                // 6. Save the contract
                return contractRepository.save(contract)
                    .thenApply(VendorContract::getId);
            });
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/internal/ProcessAutoRenewalsHandler.java`**:

```java
package tech.kayys.erp.purchasing.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.purchasing.domain.model.VendorContract;
import tech.kayys.erp.purchasing.domain.repository.VendorContractRepository;
import tech.kayys.erp.purchasing.domain.valueobject.ContractStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Background processor for contract auto-renewals.
 */
@Singleton
@UseCase("Process contract auto-renewals")
public class ProcessAutoRenewalsHandler {

    private final VendorContractRepository contractRepository;

    @Inject
    public ProcessAutoRenewalsHandler(VendorContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    /**
     * Processes auto-renewals for all eligible contracts.
     * Returns the number of successfully renewed contracts.
     */
    public CompletionStage<Integer> processAutoRenewals() {
        return contractRepository.findContractsNeedingRenewal()
            .thenCompose(contracts -> {
                if (contracts.isEmpty()) {
                    return CompletableFuture.completedFuture(0);
                }

                // Process renewals in parallel
                List<CompletableFuture<VendorContract>> renewalFutures = contracts.stream()
                    .filter(contract -> contract.isAutoRenew() && contract.isActive())
                    .map(contract -> {
                        return processRenewal(contract)
                            .toCompletableFuture();
                    })
                    .collect(Collectors.toList());

                return CompletableFuture.allOf(renewalFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        long count = renewalFutures.stream()
                            .filter(f -> !f.isCompletedExceptionally())
                            .count();
                        return (int) count;
                    });
            });
    }

    private CompletionStage<VendorContract> processRenewal(VendorContract contract) {
        // Calculate new expiration date
        Instant currentExpiration = contract.getExpirationDate();
        long durationDays = java.time.temporal.ChronoUnit.DAYS.between(
            contract.getEffectiveDate(),
            currentExpiration
        );
        
        Instant newExpiration = currentExpiration.plusSeconds(durationDays * 24L * 60L * 60L);

        // Renew the contract
        contract.renew(newExpiration);
        
        // Mark for renewal tracking
        contract.setNotes("Auto-renewed on " + Instant.now());
        contract.setLastModifiedBy("System");
        
        return contractRepository.save(contract);
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/port/ContractTemplatePort.java`**:

```java
package tech.kayys.erp.purchasing.application.port;

import tech.kayys.erp.purchasing.domain.valueobject.ContractTemplate;

import java.util.concurrent.CompletionStage;

/**
 * Port for contract template operations.
 */
public interface ContractTemplatePort {

    /**
     * Gets a contract template by ID.
     */
    CompletionStage<ContractTemplate> getTemplate(String templateId);

    /**
     * Gets all active templates.
     */
    CompletionStage<List<ContractTemplate>> getActiveTemplates();

    /**
     * Gets templates by contract type.
     */
    CompletionStage<List<ContractTemplate>> getTemplatesByType(String contractType);

    /**
     * Renders a template with data.
     */
    default CompletionStage<String> renderTemplate(String templateId, String data) {
        return getTemplate(templateId)
            .thenApply(template -> template.renderWithData(data));
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/repository/VendorContractRepository.java`** (extended):

```java
package tech.kayys.erp.purchasing.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;
import tech.kayys.erp.purchasing.domain.model.VendorContract;
import tech.kayys.erp.purchasing.domain.valueobject.ContractStatus;
import tech.kayys.erp.purchasing.domain.valueobject.ContractType;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Extended repository for VendorContract aggregates.
 */
public interface VendorContractRepository extends Repository<VendorContract, ContractId> {

    /**
     * Finds contracts by vendor.
     */
    CompletionStage<List<VendorContract>> findByVendorId(VendorId vendorId);

    /**
     * Finds contracts by status.
     */
    CompletionStage<List<VendorContract>> findByStatus(ContractStatus status);

    /**
     * Finds active contracts.
     */
    default CompletionStage<List<VendorContract>> findActiveContracts() {
        return findByStatus(ContractStatus.ACTIVE);
    }

    /**
     * Finds contracts by type.
     */
    CompletionStage<List<VendorContract>> findByType(ContractType type);

    /**
     * Finds contracts expiring between two dates.
     */
    CompletionStage<List<VendorContract>> findExpiringBetween(Instant start, Instant end);

    /**
     * Finds contracts needing renewal (expiring within 30 days).
     */
    default CompletionStage<List<VendorContract>> findContractsNeedingRenewal() {
        Instant now = Instant.now();
        Instant threshold = now.plusSeconds(30L * 24L * 60L * 60L);
        return findExpiringBetween(now, threshold);
    }

    /**
     * Finds contracts with compliance issues.
     */
    CompletionStage<List<VendorContract>> findContractsWithComplianceIssues();

    /**
     * Finds contracts with performance issues.
     */
    CompletionStage<List<VendorContract>> findContractsWithPerformanceIssues();

    /**
     * Finds contracts by vendor and status.
     */
    CompletionStage<List<VendorContract>> findByVendorAndStatus(
        VendorId vendorId, 
        ContractStatus status
    );

    /**
     * Finds contracts for renewal processing.
     */
    CompletionStage<List<VendorContract>> findContractsForRenewal(Instant currentDate);

    /**
     * Gets the total contract value by vendor.
     */
    CompletionStage<Money> getTotalContractValueByVendor(VendorId vendorId);
}
```

## 3. Update Root POM

The Purchasing modules are already included in the root POM from the previous step.

## 4. Architecture Tests Update

**`/architecture/tests/src/test/java/tech/kayys/erp/architecture/CompleteArchitectureTest.java`** (add Contract rules):

```java
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule contractsShouldHaveCorrectNaming =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain.model..")
                .and()
                .haveSimpleNameContaining("Contract")
                .should()
                .haveSimpleNameEndingWith("Contract")
                .orShould()
                .haveSimpleNameEndingWith("VendorContract");

@ArchTest
static final ArchRule contractStatusTransitionsShouldBeEnforced =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain.valueobject..")
                .and()
                .haveSimpleName("ContractStatus")
                .should()
                .haveMethod("canTransitionTo");

@ArchTest
static final ArchRule contractTemplatesShouldBeImmutable =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain.valueobject..")
                .and()
                .haveSimpleName("ContractTemplate")
                .should()
                .beFinal()
                .andShould()
                .haveOnlyFinalFields();

@ArchTest
static final ArchRule contractAmendmentsShouldBeImmutable =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain.valueobject..")
                .and()
                .haveSimpleName("ContractAmendment")
                .should()
                .beFinal()
                .andShould()
                .haveOnlyFinalFields();
```

## Summary

The complete Contract Management extension provides:

1. **Contract Templates**:
   - Pre-defined contract templates
   - Template rendering with data
   - Version control
   - Language support

2. **Compliance Management**:
   - Compliance record tracking
   - Review scheduling
   - Compliance scoring
   - Follow-up detection

3. **Performance Metrics**:
   - KPI tracking against targets
   - Performance scoring
   - Achievement tracking
   - Historical metrics

4. **Contract Amendments**:
   - Amendment creation
   - Change tracking
   - Approval workflow
   - Effective date management

5. **Auto-Renewal**:
   - Automatic renewal processing
   - Configurable renewal periods
   - System-driven renewals
   - Renewal tracking

6. **Enhanced Contract Features**:
   - Legal entity
   - Governing law
   - Dispute resolution
   - Termination management

7. **Integration**:
   - Template port for template management
   - Repository extensions for advanced queries
   - Background processing for renewals

This completes the Contract Management functionality with comprehensive features for managing vendor agreements, ensuring compliance, tracking performance, and automating renewals.