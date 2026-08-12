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