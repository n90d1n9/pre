package tech.kayys.erp.accounting.model;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Journal entry aggregate root.
 * Represents a double-entry accounting transaction.
 */
public final class JournalEntry extends AggregateRoot<JournalEntryId> {
    
    private static final long serialVersionUID = 1L;
    
    private String description;
    private Instant entryDate;
    private String referenceNumber;
    private List<JournalLine> lines;
    private boolean posted;
    private Instant postedDate;
    private String postedBy;
    private String sourceType; // e.g., "INVOICE", "PAYMENT", "SUBSCRIPTION"
    private String sourceId;

    private JournalEntry(JournalEntryId id) {
        super(id);
        this.lines = new ArrayList<>();
        this.entryDate = Instant.now();
        this.posted = false;
    }

    private JournalEntry() {
        super();
    }

    /**
     * Factory method to create a new journal entry.
     */
    public static JournalEntry create(
            JournalEntryId id,
            String description,
            String referenceNumber) {
        JournalEntry entry = new JournalEntry(id);
        entry.description = description;
        entry.referenceNumber = referenceNumber;
        return entry;
    }

    /**
     * Adds a line to the journal entry.
     */
    public void addLine(JournalLine line) {
        if (posted) {
            throw new IllegalStateException("Cannot modify posted journal entry");
        }
        lines.add(line);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Validates that the journal entry balances.
     * Total debits must equal total credits.
     */
    public boolean isBalanced() {
        Money totalDebits = lines.stream()
            .filter(line -> line.getType() == JournalLine.LineType.DEBIT)
            .map(JournalLine::getAmount)
            .reduce(Money.zero("USD"), Money::add);

        Money totalCredits = lines.stream()
            .filter(line -> line.getType() == JournalLine.LineType.CREDIT)
            .map(JournalLine::getAmount)
            .reduce(Money.zero("USD"), Money::add);

        return totalDebits.equals(totalCredits);
    }

    /**
     * Posts the journal entry to the ledger.
     */
    public void post(String postedBy) {
        if (posted) {
            throw new IllegalStateException("Journal entry already posted");
        }
        if (!isBalanced()) {
            throw new IllegalStateException("Journal entry is not balanced");
        }
        if (lines.isEmpty()) {
            throw new IllegalStateException("Journal entry has no lines");
        }

        this.posted = true;
        this.postedDate = Instant.now();
        this.postedBy = postedBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Calculates the total amount of the journal entry.
     */
    public Money getTotalAmount() {
        return lines.stream()
            .map(JournalLine::getAmount)
            .reduce(Money.zero("USD"), Money::add);
    }

    // Getters
    public String getDescription() { return description; }
    public Instant getEntryDate() { return entryDate; }
    public String getReferenceNumber() { return referenceNumber; }
    public List<JournalLine> getLines() { return Collections.unmodifiableList(lines); }
    public boolean isPosted() { return posted; }
    public Instant getPostedDate() { return postedDate; }
    public String getPostedBy() { return postedBy; }
    public String getSourceType() { return sourceType; }
    public String getSourceId() { return sourceId; }

    public void setSource(String sourceType, String sourceId) {
        if (posted) {
            throw new IllegalStateException("Cannot modify posted journal entry");
        }
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Journal line value object.
     */
    public static final class JournalLine implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final AccountId accountId;
        private final LineType type;
        private final Money amount;
        private final String description;

        public JournalLine(AccountId accountId, LineType type, Money amount, String description) {
            this.accountId = accountId;
            this.type = type;
            this.amount = amount;
            this.description = description;
            validate();
        }

        @Override
        public void validate() {
            if (accountId == null) {
                throw new IllegalArgumentException("Account ID cannot be null");
            }
            if (type == null) {
                throw new IllegalArgumentException("Line type cannot be null");
            }
            if (amount == null || amount.isZero()) {
                throw new IllegalArgumentException("Amount must be non-zero");
            }
        }

        public AccountId getAccountId() { return accountId; }
        public LineType getType() { return type; }
        public Money getAmount() { return amount; }
        public String getDescription() { return description; }

        @Override
        public String toString() {
            return "JournalLine{" +
                    "accountId=" + accountId +
                    ", type=" + type +
                    ", amount=" + amount +
                    '}';
        }

        public enum LineType {
            DEBIT, CREDIT
        }
    }
}
