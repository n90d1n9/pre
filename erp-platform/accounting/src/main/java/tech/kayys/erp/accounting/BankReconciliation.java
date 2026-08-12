
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bank Reconciliation aggregate root.
 * Represents the reconciliation of a bank account with company records.
 */
public final class BankReconciliation extends AggregateRoot<ReconciliationId> {
    
    private static final long serialVersionUID = 1L;
    
    private AccountId accountId;
    private String accountNumber;
    private String bankName;
    private Instant statementDate;
    private Money statementBalance;
    private Money ledgerBalance;
    private Money difference;
    private ReconciliationStatus status;
    private List<ReconciliationItem> items;
    private String preparedBy;
    private String approvedBy;
    private Instant approvedAt;
    private String notes;
    private boolean completed;

    private BankReconciliation(ReconciliationId id) {
        super(id);
        this.items = new ArrayList<>();
        this.status = ReconciliationStatus.DRAFT;
        this.completed = false;
        this.difference = Money.zero("USD");
    }

    private BankReconciliation() {
        super();
    }

    /**
     * Factory method to create a new bank reconciliation.
     */
    public static BankReconciliation create(
            ReconciliationId id,
            AccountId accountId,
            String accountNumber,
            String bankName,
            Instant statementDate,
            Money statementBalance,
            Money ledgerBalance) {
        BankReconciliation reconciliation = new BankReconciliation(id);
        reconciliation.accountId = accountId;
        reconciliation.accountNumber = accountNumber;
        reconciliation.bankName = bankName;
        reconciliation.statementDate = statementDate;
        reconciliation.statementBalance = statementBalance;
        reconciliation.ledgerBalance = ledgerBalance;
        reconciliation.difference = statementBalance.subtract(ledgerBalance);
        return reconciliation;
    }

    /**
     * Adds a reconciliation item.
     */
    public void addItem(ReconciliationItem item) {
        if (status == ReconciliationStatus.COMPLETED) {
            throw new IllegalStateException("Cannot modify completed reconciliation");
        }
        items.add(item);
        recalculate();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a reconciliation item.
     */
    public void removeItem(int index) {
        if (status == ReconciliationStatus.COMPLETED) {
            throw new IllegalStateException("Cannot modify completed reconciliation");
        }
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("Invalid item index");
        }
        items.remove(index);
        recalculate();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void recalculate() {
        Money adjustedLedger = ledgerBalance;
        Money adjustedStatement = statementBalance;
        
        for (ReconciliationItem item : items) {
            if (item.getType() == ReconciliationItemType.ADD_TO_LEDGER) {
                adjustedLedger = adjustedLedger.add(item.getAmount());
            } else if (item.getType() == ReconciliationItemType.SUBTRACT_FROM_LEDGER) {
                adjustedLedger = adjustedLedger.subtract(item.getAmount());
            } else if (item.getType() == ReconciliationItemType.ADD_TO_STATEMENT) {
                adjustedStatement = adjustedStatement.add(item.getAmount());
            } else if (item.getType() == ReconciliationItemType.SUBTRACT_FROM_STATEMENT) {
                adjustedStatement = adjustedStatement.subtract(item.getAmount());
            }
        }
        
        this.difference = adjustedStatement.subtract(adjustedLedger);
        
        // Auto-complete if difference is zero
        if (difference.isZero() && status != ReconciliationStatus.COMPLETED) {
            this.status = ReconciliationStatus.BALANCED;
        }
    }

    /**
     * Completes the reconciliation.
     */
    public void complete(String approvedBy) {
        if (status == ReconciliationStatus.COMPLETED) {
            return;
        }
        if (!difference.isZero()) {
            throw new IllegalStateException("Cannot complete reconciliation with difference: " + difference);
        }
        
        this.status = ReconciliationStatus.COMPLETED;
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        this.completed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the reconciliation is balanced.
     */
    public boolean isBalanced() {
        return difference.isZero();
    }

    // Getters
    public AccountId getAccountId() { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public String getBankName() { return bankName; }
    public Instant getStatementDate() { return statementDate; }
    public Money getStatementBalance() { return statementBalance; }
    public Money getLedgerBalance() { return ledgerBalance; }
    public Money getDifference() { return difference; }
    public ReconciliationStatus getStatus() { return status; }
    public List<ReconciliationItem> getItems() { return Collections.unmodifiableList(items); }
    public String getPreparedBy() { return preparedBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getNotes() { return notes; }
    public boolean isCompleted() { return completed; }

    public void setPreparedBy(String preparedBy) {
        this.preparedBy = preparedBy;
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
        return "BankReconciliation{" +
                "id=" + getId() +
                ", accountNumber='" + accountNumber + '\'' +
                ", statementDate=" + statementDate +
                ", status=" + status +
                ", difference=" + difference +
                '}';
    }

    /**
     * Reconciliation status enum.
     */
    public enum ReconciliationStatus {
        DRAFT("Draft - in progress"),
        BALANCED("Balanced - ready for review"),
        COMPLETED("Completed - finalized"),
        DISCREPANCY("Discrepancy - needs investigation");

        private final String description;

        ReconciliationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Reconciliation item type enum.
     */
    public enum ReconciliationItemType {
        ADD_TO_LEDGER("Add to ledger balance"),
        SUBTRACT_FROM_LEDGER("Subtract from ledger balance"),
        ADD_TO_STATEMENT("Add to statement balance"),
        SUBTRACT_FROM_STATEMENT("Subtract from statement balance");

        private final String description;

        ReconciliationItemType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Reconciliation item value object.
     */
    public static final class ReconciliationItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String reference;
        private final String description;
        private final ReconciliationItemType type;
        private final Money amount;
        private final Instant date;

        public ReconciliationItem(
                String reference,
                String description,
                ReconciliationItemType type,
                Money amount,
                Instant date) {
            this.reference = reference;
            this.description = description;
            this.type = type;
            this.amount = amount;
            this.date = date;
            validate();
        }

        @Override
        public void validate() {
            if (type == null) {
                throw new IllegalArgumentException("Item type cannot be null");
            }
            if (amount == null || amount.isZero()) {
                throw new IllegalArgumentException("Amount must be non-zero");
            }
            if (date == null) {
                throw new IllegalArgumentException("Date cannot be null");
            }
        }

        public String getReference() { return reference; }
        public String getDescription() { return description; }
        public ReconciliationItemType getType() { return type; }
        public Money getAmount() { return amount; }
        public Instant getDate() { return date; }

        @Override
        public String toString() {
            return "ReconciliationItem{" +
                    "reference='" + reference + '\'' +
                    ", type=" + type +
                    ", amount=" + amount +
                    '}';
        }
    }
}