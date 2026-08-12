
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Budget aggregate root.
 * Represents a financial budget for a period.
 */
public final class Budget extends AggregateRoot<BudgetId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private String fiscalYear;
    private BudgetType budgetType;
    private BudgetStatus status;
    private List<BudgetLineItem> lineItems;
    private Money totalBudgeted;
    private Money totalActual;
    private Money totalVariance;
    private double variancePercentage;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private boolean active;

    private Budget(BudgetId id) {
        super(id);
        this.lineItems = new ArrayList<>();
        this.status = BudgetStatus.DRAFT;
        this.active = true;
        this.totalBudgeted = Money.zero("USD");
        this.totalActual = Money.zero("USD");
        this.totalVariance = Money.zero("USD");
    }

    private Budget() {
        super();
    }

    /**
     * Factory method to create a new budget.
     */
    public static Budget create(
            BudgetId id,
            String name,
            String fiscalYear,
            BudgetType budgetType) {
        Budget budget = new Budget(id);
        budget.name = name;
        budget.fiscalYear = fiscalYear;
        budget.budgetType = budgetType;
        return budget;
    }

    /**
     * Adds a budget line item.
     */
    public void addLineItem(BudgetLineItem item) {
        if (status == BudgetStatus.APPROVED || status == BudgetStatus.FINALIZED) {
            throw new IllegalStateException("Cannot modify approved or finalized budget");
        }
        lineItems.add(item);
        recalculate();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates actual amounts for budget items.
     */
    public void updateActuals(List<BudgetActual> actuals) {
        if (status == BudgetStatus.FINALIZED) {
            throw new IllegalStateException("Cannot update finalized budget");
        }
        
        for (BudgetActual actual : actuals) {
            for (BudgetLineItem item : lineItems) {
                if (item.getAccountId().equals(actual.accountId())) {
                    item.setActualAmount(actual.amount());
                    break;
                }
            }
        }
        recalculate();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the budget.
     */
    public void approve(String approvedBy) {
        if (status != BudgetStatus.DRAFT && status != BudgetStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Cannot approve budget in status: " + status);
        }
        if (lineItems.isEmpty()) {
            throw new IllegalStateException("Budget must have at least one line item");
        }
        
        this.status = BudgetStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits budget for review.
     */
    public void submitForReview() {
        if (status != BudgetStatus.DRAFT) {
            throw new IllegalStateException("Cannot submit budget in status: " + status);
        }
        this.status = BudgetStatus.UNDER_REVIEW;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Finalizes the budget.
     */
    public void finalizeBudget() {
        if (status != BudgetStatus.APPROVED) {
            throw new IllegalStateException("Cannot finalize budget in status: " + status);
        }
        this.status = BudgetStatus.FINALIZED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void recalculate() {
        this.totalBudgeted = lineItems.stream()
            .map(BudgetLineItem::getBudgetedAmount)
            .reduce(Money.zero("USD"), Money::add);
        
        this.totalActual = lineItems.stream()
            .map(BudgetLineItem::getActualAmount)
            .reduce(Money.zero("USD"), Money::add);
        
        this.totalVariance = totalBudgeted.subtract(totalActual);
        
        if (!totalBudgeted.isZero()) {
            this.variancePercentage = totalVariance.getAmount()
                .divide(totalBudgeted.getAmount(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(java.math.BigDecimal.valueOf(100))
                .doubleValue();
        }
    }

    /**
     * Gets the variance analysis.
     */
    public VarianceAnalysis getVarianceAnalysis() {
        return new VarianceAnalysis(totalBudgeted, totalActual, totalVariance, variancePercentage);
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getFiscalYear() { return fiscalYear; }
    public BudgetType getBudgetType() { return budgetType; }
    public BudgetStatus getStatus() { return status; }
    public List<BudgetLineItem> getLineItems() { return Collections.unmodifiableList(lineItems); }
    public Money getTotalBudgeted() { return totalBudgeted; }
    public Money getTotalActual() { return totalActual; }
    public Money getTotalVariance() { return totalVariance; }
    public double getVariancePercentage() { return variancePercentage; }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public boolean isActive() { return active; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", fiscalYear='" + fiscalYear + '\'' +
                ", status=" + status +
                ", totalBudgeted=" + totalBudgeted +
                '}';
    }

    /**
     * Budget type enum.
     */
    public enum BudgetType {
        OPERATING("Operating Budget"),
        CAPITAL("Capital Budget"),
        CASH_FLOW("Cash Flow Budget"),
        ROLLING("Rolling Forecast"),
        ZERO_BASED("Zero-Based Budget"),
        FIXED("Fixed Budget"),
        FLEXIBLE("Flexible Budget");

        private final String displayName;

        BudgetType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Budget status enum.
     */
    public enum BudgetStatus {
        DRAFT("Draft - being created"),
        UNDER_REVIEW("Under Review - being evaluated"),
        APPROVED("Approved - ready for use"),
        FINALIZED("Finalized - completed"),
        REJECTED("Rejected - not approved");

        private final String description;

        BudgetStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Budget line item.
     */
    public static final class BudgetLineItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final AccountId accountId;
        private final String accountName;
        private final String accountNumber;
        private final Money budgetedAmount;
        private Money actualAmount;
        private final String category;
        private final String notes;

        public BudgetLineItem(
                AccountId accountId,
                String accountName,
                String accountNumber,
                Money budgetedAmount,
                String category,
                String notes) {
            this.accountId = accountId;
            this.accountName = accountName;
            this.accountNumber = accountNumber;
            this.budgetedAmount = budgetedAmount;
            this.actualAmount = Money.zero(budgetedAmount.getCurrency().getCurrencyCode());
            this.category = category;
            this.notes = notes;
            validate();
        }

        @Override
        public void validate() {
            if (accountId == null) {
                throw new IllegalArgumentException("Account ID cannot be null");
            }
            if (budgetedAmount == null) {
                throw new IllegalArgumentException("Budgeted amount cannot be null");
            }
        }

        public AccountId getAccountId() { return accountId; }
        public String getAccountName() { return accountName; }
        public String getAccountNumber() { return accountNumber; }
        public Money getBudgetedAmount() { return budgetedAmount; }
        public Money getActualAmount() { return actualAmount; }
        public String getCategory() { return category; }
        public String getNotes() { return notes; }

        public void setActualAmount(Money actualAmount) {
            this.actualAmount = actualAmount;
        }

        public Money getVariance() {
            return budgetedAmount.subtract(actualAmount);
        }

        public double getVariancePercentage() {
            if (budgetedAmount.isZero()) {
                return 0.0;
            }
            return getVariance().getAmount()
                .divide(budgetedAmount.getAmount(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(java.math.BigDecimal.valueOf(100))
                .doubleValue();
        }
    }

    /**
     * Budget actual record.
     */
    public record BudgetActual(
            AccountId accountId,
            Money amount,
            Instant period
    ) {}

    /**
     * Variance analysis record.
     */
    public record VarianceAnalysis(
            Money budgeted,
            Money actual,
            Money variance,
            double variancePercentage
    ) {}
}