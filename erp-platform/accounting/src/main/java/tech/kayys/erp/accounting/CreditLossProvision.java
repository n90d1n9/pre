
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Credit Loss Provision aggregate root.
 * Implements PSAK 71 expected credit loss calculation.
 */
public final class CreditLossProvision extends AggregateRoot<CreditLossProvisionId> {
    
    private static final long serialVersionUID = 1L;
    
    private String customerId;
    private String invoiceId;
    private String transactionId;
    private Money exposureAmount;
    private String currencyCode;
    private Instant invoiceDate;
    private Instant dueDate;
    private int daysPastDue;
    private ProvisionStage stage; // Stage 1, 2, or 3
    private BigDecimal probabilityOfDefault;
    private BigDecimal lossGivenDefault;
    private Money expectedCreditLoss;
    private Money provisionBalance;
    private Money writeOffAmount;
    private ProvisionStatus status;
    private List<ProvisionHistory> history;
    private String reviewedBy;
    private Instant reviewedAt;
    private String notes;
    private boolean active;

    private CreditLossProvision(CreditLossProvisionId id) {
        super(id);
        this.history = new ArrayList<>();
        this.status = ProvisionStatus.ACTIVE;
        this.active = true;
        this.stage = ProvisionStage.STAGE_1;
        this.probabilityOfDefault = BigDecimal.valueOf(0.01); // 1% baseline
        this.lossGivenDefault = BigDecimal.valueOf(0.5); // 50% LGD
        this.provisionBalance = Money.zero("IDR");
    }

    private CreditLossProvision() {
        super();
    }

    /**
     * Factory method to create a new credit loss provision.
     */
    public static CreditLossProvision create(
            CreditLossProvisionId id,
            String customerId,
            String invoiceId,
            String transactionId,
            Money exposureAmount,
            Instant invoiceDate,
            Instant dueDate,
            String currencyCode) {
        CreditLossProvision provision = new CreditLossProvision(id);
        provision.customerId = customerId;
        provision.invoiceId = invoiceId;
        provision.transactionId = transactionId;
        provision.exposureAmount = exposureAmount;
        provision.currencyCode = currencyCode;
        provision.invoiceDate = invoiceDate;
        provision.dueDate = dueDate;
        provision.daysPastDue = calculateDaysPastDue(dueDate);
        return provision;
    }

    /**
     * Calculates the expected credit loss based on PSAK 71 methodology.
     */
    public void calculateECL() {
        // Determine stage based on credit risk deterioration
        determineStage();
        
        // Calculate PD based on stage and historical data
        this.probabilityOfDefault = calculateProbabilityOfDefault();
        
        // LGD is typically based on historical recovery rates
        this.lossGivenDefault = calculateLossGivenDefault();
        
        // Calculate ECL = PD × LGD × EAD
        BigDecimal pd = probabilityOfDefault;
        BigDecimal lgd = lossGivenDefault;
        BigDecimal ead = exposureAmount.getAmount();
        
        BigDecimal eclAmount = ead
            .multiply(pd)
            .multiply(lgd)
            .setScale(2, RoundingMode.HALF_UP);
        
        this.expectedCreditLoss = Money.of(eclAmount, currencyCode);
        
        // Update provision balance
        this.provisionBalance = this.expectedCreditLoss;
        
        // Record history
        addHistory(
            "ECL Calculation",
            "ECL calculated at " + expectedCreditLoss + " (" + stage.name() + ")"
        );
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void determineStage() {
        // Stage 1: Low credit risk (performing)
        // Stage 2: Significant increase in credit risk (underperforming)
        // Stage 3: Credit-impaired (non-performing)
        
        if (daysPastDue > 90) {
            this.stage = ProvisionStage.STAGE_3;
        } else if (daysPastDue > 30) {
            this.stage = ProvisionStage.STAGE_2;
        } else {
            this.stage = ProvisionStage.STAGE_1;
        }
    }

    private BigDecimal calculateProbabilityOfDefault() {
        // In production, this would use:
        // 1. Historical default rates by segment
        // 2. Credit scores from rating agencies
        // 3. Macroeconomic factors
        
        // Simplified calculation based on days past due
        if (daysPastDue <= 0) {
            return BigDecimal.valueOf(0.01); // 1% for current
        } else if (daysPastDue <= 30) {
            return BigDecimal.valueOf(0.05); // 5% for 1-30 days
        } else if (daysPastDue <= 60) {
            return BigDecimal.valueOf(0.15); // 15% for 31-60 days
        } else if (daysPastDue <= 90) {
            return BigDecimal.valueOf(0.30); // 30% for 61-90 days
        } else {
            return BigDecimal.valueOf(0.60); // 60% for >90 days
        }
    }

    private BigDecimal calculateLossGivenDefault() {
        // LGD is typically based on:
        // 1. Collateral coverage
        // 2. Seniority of debt
        // 3. Historical recovery rates
        
        // Simplified: 50% LGD for unsecured
        return BigDecimal.valueOf(0.5);
    }

    private static int calculateDaysPastDue(Instant dueDate) {
        if (dueDate == null) {
            return 0;
        }
        LocalDate due = dueDate.atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        return (int) ChronoUnit.DAYS.between(due, now);
    }

    /**
     * Adds a history entry.
     */
    private void addHistory(String action, String details) {
        ProvisionHistory historyEntry = new ProvisionHistory(
            java.util.UUID.randomUUID().toString(),
            action,
            details,
            Instant.now()
        );
        history.add(historyEntry);
    }

    /**
     * Reviews the provision.
     */
    public void review(String reviewer, String notes) {
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Writes off the provision.
     */
    public void writeOff(Money amount, String reason) {
        if (amount.isGreaterThan(provisionBalance)) {
            throw new IllegalArgumentException("Write-off amount exceeds provision balance");
        }
        this.writeOffAmount = amount;
        this.provisionBalance = provisionBalance.subtract(amount);
        this.status = ProvisionStatus.WRITTEN_OFF;
        addHistory("Write Off", "Written off " + amount + " - " + reason);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Reverses a write-off.
     */
    public void reverseWriteOff(String reason) {
        if (status != ProvisionStatus.WRITTEN_OFF) {
            throw new IllegalStateException("Only written-off provisions can be reversed");
        }
        this.status = ProvisionStatus.REVERSED;
        this.writeOffAmount = Money.zero(currencyCode);
        this.provisionBalance = expectedCreditLoss;
        addHistory("Write Off Reversed", reason);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getInvoiceId() { return invoiceId; }
    public String getTransactionId() { return transactionId; }
    public Money getExposureAmount() { return exposureAmount; }
    public String getCurrencyCode() { return currencyCode; }
    public Instant getInvoiceDate() { return invoiceDate; }
    public Instant getDueDate() { return dueDate; }
    public int getDaysPastDue() { return daysPastDue; }
    public ProvisionStage getStage() { return stage; }
    public BigDecimal getProbabilityOfDefault() { return probabilityOfDefault; }
    public BigDecimal getLossGivenDefault() { return lossGivenDefault; }
    public Money getExpectedCreditLoss() { return expectedCreditLoss; }
    public Money getProvisionBalance() { return provisionBalance; }
    public Money getWriteOffAmount() { return writeOffAmount; }
    public ProvisionStatus getStatus() { return status; }
    public List<ProvisionHistory> getHistory() { return Collections.unmodifiableList(history); }
    public String getReviewedBy() { return reviewedBy; }
    public Instant getReviewedAt() { return reviewedAt; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    @Override
    public String toString() {
        return "CreditLossProvision{" +
                "id=" + getId() +
                ", invoiceId='" + invoiceId + '\'' +
                ", stage=" + stage +
                ", expectedCreditLoss=" + expectedCreditLoss +
                ", provisionBalance=" + provisionBalance +
                '}';
    }

    /**
     * Provision stage enum (PSAK 71).
     */
    public enum ProvisionStage {
        STAGE_1("Stage 1 - Low credit risk (performing)"),
        STAGE_2("Stage 2 - Significant increase in credit risk"),
        STAGE_3("Stage 3 - Credit-impaired");

        private final String description;

        ProvisionStage(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Provision status enum.
     */
    public enum ProvisionStatus {
        ACTIVE("Active - Provision in effect"),
        WRITTEN_OFF("Written Off - Provision utilized"),
        REVERSED("Reversed - Write-off reversed");

        private final String description;

        ProvisionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Provision history record.
     */
    public static final class ProvisionHistory {
        private final String historyId;
        private final String action;
        private final String details;
        private final Instant timestamp;

        public ProvisionHistory(String historyId, String action, String details, Instant timestamp) {
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