package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.identifier.BudgetCheckId;
import tech.kayys.erp.purchasing.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Budget Check aggregate root.
 * Performs budget availability checking and encumbrance.
 */
public final class BudgetCheck extends AggregateRoot<BudgetCheckId> {
    
    private static final long serialVersionUID = 1L;
    
    private String budgetCode;
    private String costCenter;
    private String projectCode;
    private Money requestedAmount;
    private Money availableBudget;
    private Money encumberedAmount;
    private Money remainingBudget;
    private BudgetStatus status;
    private String purchaseOrderId;
    private String requisitionId;
    private List<BudgetCheckDetail> details;
    private String checkedBy;
    private Instant checkedAt;
    private String approvedBy;
    private Instant approvedAt;
    private String notes;
    private boolean active;

    private BudgetCheck(BudgetCheckId id) {
        super(id);
        this.details = new ArrayList<>();
        this.status = BudgetStatus.PENDING;
        this.active = true;
    }

    private BudgetCheck() {
        super();
    }

    /**
     * Factory method to create a new budget check.
     */
    public static BudgetCheck create(
            BudgetCheckId id,
            String budgetCode,
            String costCenter,
            Money requestedAmount,
            String purchaseOrderId) {
        BudgetCheck check = new BudgetCheck(id);
        check.budgetCode = budgetCode;
        check.costCenter = costCenter;
        check.requestedAmount = requestedAmount;
        check.purchaseOrderId = purchaseOrderId;
        return check;
    }

    /**
     * Performs the budget check.
     */
    public void performCheck(Money availableBudget, Money encumberedAmount) {
        this.availableBudget = availableBudget;
        this.encumberedAmount = encumberedAmount;
        this.remainingBudget = availableBudget.subtract(encumberedAmount);
        
        if (requestedAmount.isGreaterThan(remainingBudget)) {
            this.status = BudgetStatus.INSUFFICIENT;
            addDetail("INSUFFICIENT_BUDGET", 
                "Requested: " + requestedAmount + ", Available: " + remainingBudget);
        } else {
            this.status = BudgetStatus.AVAILABLE;
            addDetail("BUDGET_AVAILABLE", 
                "Requested: " + requestedAmount + ", Available: " + remainingBudget);
        }
        
        this.checkedBy = "SYSTEM";
        this.checkedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Encumbers the budget.
     */
    public void encumber() {
        if (status != BudgetStatus.AVAILABLE) {
            throw new IllegalStateException("Cannot encumber budget in status: " + status);
        }
        this.status = BudgetStatus.ENCUMBERED;
        this.remainingBudget = remainingBudget.subtract(requestedAmount);
        addDetail("ENCUMBERED", "Budget encumbered for PO: " + purchaseOrderId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Releases the encumbrance.
     */
    public void releaseEncumbrance(String reason) {
        if (status != BudgetStatus.ENCUMBERED) {
            throw new IllegalStateException("Cannot release encumbrance in status: " + status);
        }
        this.status = BudgetStatus.RELEASED;
        this.remainingBudget = remainingBudget.add(requestedAmount);
        addDetail("RELEASED", "Encumbrance released: " + reason);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records actual spending against the budget.
     */
    public void recordSpend(Money spendAmount) {
        if (status == BudgetStatus.ENCUMBERED) {
            this.remainingBudget = remainingBudget.add(requestedAmount).subtract(spendAmount);
            this.status = BudgetStatus.SPENT;
            addDetail("SPENT", "Actual spend: " + spendAmount);
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void addDetail(String type, String description) {
        BudgetCheckDetail detail = new BudgetCheckDetail(
            java.util.UUID.randomUUID().toString(),
            type,
            description,
            Instant.now()
        );
        details.add(detail);
    }

    // Getters
    public String getBudgetCode() { return budgetCode; }
    public String getCostCenter() { return costCenter; }
    public String getProjectCode() { return projectCode; }
    public Money getRequestedAmount() { return requestedAmount; }
    public Money getAvailableBudget() { return availableBudget; }
    public Money getEncumberedAmount() { return encumberedAmount; }
    public Money getRemainingBudget() { return remainingBudget; }
    public BudgetStatus getStatus() { return status; }
    public String getPurchaseOrderId() { return purchaseOrderId; }
    public String getRequisitionId() { return requisitionId; }
    public List<BudgetCheckDetail> getDetails() { return Collections.unmodifiableList(details); }
    public String getCheckedBy() { return checkedBy; }
    public Instant getCheckedAt() { return checkedAt; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRequisitionId(String requisitionId) {
        this.requisitionId = requisitionId;
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
        return "BudgetCheck{" +
                "id=" + getId() +
                ", budgetCode='" + budgetCode + '\'' +
                ", requested=" + requestedAmount +
                ", available=" + remainingBudget +
                ", status=" + status +
                '}';
    }

    /**
     * Budget status enum.
     */
    public enum BudgetStatus {
        PENDING("Pending"),
        AVAILABLE("Available"),
        INSUFFICIENT("Insufficient"),
        ENCUMBERED("Encumbered"),
        RELEASED("Released"),
        SPENT("Spent");

        private final String description;

        BudgetStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Budget check detail record.
     */
    public static final class BudgetCheckDetail {
        private final String detailId;
        private final String type;
        private final String description;
        private final Instant timestamp;

        public BudgetCheckDetail(String detailId, String type, String description, Instant timestamp) {
            this.detailId = detailId;
            this.type = type;
            this.description = description;
            this.timestamp = timestamp;
        }

        public String getDetailId() { return detailId; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public Instant getTimestamp() { return timestamp; }
    }
}