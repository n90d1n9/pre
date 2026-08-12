package tech.kayys.erp.transaction.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.transaction.domain.identifier.FraudCheckId;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Fraud Check aggregate root.
 * Performs fraud detection on transactions.
 */
public final class FraudCheck extends AggregateRoot<FraudCheckId> {
    
    private static final long serialVersionUID = 1L;
    
    private String transactionId;
    private String customerId;
    private String ipAddress;
    private String userAgent;
    private String deviceFingerprint;
    private double riskScore; // 0-100
    private FraudLevel fraudLevel;
    private FraudStatus status;
    private Map<String, Object> checkResults;
    private String ruleSetId;
    private String recommendedAction; // ALLOW, REVIEW, BLOCK
    private String reviewedBy;
    private Instant reviewedAt;
    private String reviewNotes;
    private boolean flagged;
    private String flagReason;

    private FraudCheck(FraudCheckId id) {
        super(id);
        this.checkResults = new HashMap<>();
        this.status = FraudStatus.PENDING;
        this.fraudLevel = FraudLevel.LOW;
        this.flagged = false;
    }

    private FraudCheck() {
        super();
    }

    /**
     * Factory method to create a new fraud check.
     */
    public static FraudCheck create(
            FraudCheckId id,
            String transactionId,
            String customerId,
            String ipAddress,
            String userAgent) {
        FraudCheck check = new FraudCheck(id);
        check.transactionId = transactionId;
        check.customerId = customerId;
        check.ipAddress = ipAddress;
        check.userAgent = userAgent;
        return check;
    }

    /**
     * Performs fraud analysis on the transaction.
     */
    public void analyze() {
        // In production, this would integrate with a fraud detection service
        // like Riskified, Sift, or custom ML models
        
        double score = calculateRiskScore();
        this.riskScore = score;
        this.fraudLevel = determineFraudLevel(score);
        this.status = FraudStatus.COMPLETED;
        
        if (score > 70) {
            this.flagged = true;
            this.flagReason = "High risk score: " + score;
            this.recommendedAction = "BLOCK";
        } else if (score > 40) {
            this.flagged = true;
            this.flagReason = "Medium risk score: " + score;
            this.recommendedAction = "REVIEW";
        } else {
            this.recommendedAction = "ALLOW";
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private double calculateRiskScore() {
        // Simplified risk scoring - in production, this would be more sophisticated
        double score = 0.0;
        
        // Check IP address reputation
        if (isSuspiciousIp(ipAddress)) {
            score += 20;
        }
        
        // Check velocity
        if (isHighVelocity()) {
            score += 15;
        }
        
        // Check amount
        if (isHighAmount()) {
            score += 10;
        }
        
        // Check device fingerprint
        if (deviceFingerprint == null) {
            score += 10;
        }
        
        return Math.min(score, 100);
    }

    private boolean isSuspiciousIp(String ip) {
        // In production, this would check against a IP reputation service
        return false;
    }

    private boolean isHighVelocity() {
        // In production, this would check transaction velocity for the customer
        return false;
    }

    private boolean isHighAmount() {
        // In production, this would check amount thresholds
        return false;
    }

    private FraudLevel determineFraudLevel(double score) {
        if (score > 70) return FraudLevel.CRITICAL;
        if (score > 50) return FraudLevel.HIGH;
        if (score > 30) return FraudLevel.MEDIUM;
        if (score > 10) return FraudLevel.LOW;
        return FraudLevel.MINIMAL;
    }

    /**
     * Approves the transaction after manual review.
     */
    public void approve(String reviewer, String notes) {
        if (status == FraudStatus.COMPLETED) {
            this.recommendedAction = "ALLOW";
            this.reviewedBy = reviewer;
            this.reviewedAt = Instant.now();
            this.reviewNotes = notes;
            this.flagged = false;
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Rejects the transaction after manual review.
     */
    public void reject(String reviewer, String notes) {
        if (status == FraudStatus.COMPLETED) {
            this.recommendedAction = "BLOCK";
            this.reviewedBy = reviewer;
            this.reviewedAt = Instant.now();
            this.reviewNotes = notes;
            this.flagged = true;
            this.flagReason = "Rejected by reviewer: " + notes;
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getCustomerId() { return customerId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getDeviceFingerprint() { return deviceFingerprint; }
    public double getRiskScore() { return riskScore; }
    public FraudLevel getFraudLevel() { return fraudLevel; }
    public FraudStatus getStatus() { return status; }
    public Map<String, Object> getCheckResults() { return checkResults; }
    public String getRuleSetId() { return ruleSetId; }
    public String getRecommendedAction() { return recommendedAction; }
    public String getReviewedBy() { return reviewedBy; }
    public Instant getReviewedAt() { return reviewedAt; }
    public String getReviewNotes() { return reviewNotes; }
    public boolean isFlagged() { return flagged; }
    public String getFlagReason() { return flagReason; }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCheckResult(String key, Object value) {
        this.checkResults.put(key, value);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRuleSetId(String ruleSetId) {
        this.ruleSetId = ruleSetId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "FraudCheck{" +
                "id=" + getId() +
                ", transactionId='" + transactionId + '\'' +
                ", riskScore=" + riskScore +
                ", fraudLevel=" + fraudLevel +
                ", recommendedAction='" + recommendedAction + '\'' +
                '}';
    }

    /**
     * Fraud level enum.
     */
    public enum FraudLevel {
        MINIMAL("Minimal - Very low risk"),
        LOW("Low - Low risk"),
        MEDIUM("Medium - Moderate risk"),
        HIGH("High - High risk"),
        CRITICAL("Critical - Very high risk");

        private final String description;

        FraudLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Fraud status enum.
     */
    public enum FraudStatus {
        PENDING("Pending - Awaiting analysis"),
        ANALYZING("Analyzing - In progress"),
        COMPLETED("Completed - Analysis done"),
        MANUAL_REVIEW("Manual Review - Requires human review");

        private final String description;

        FraudStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}