
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Withholding Tax aggregate root (PPh 23 and PPh 26).
 */
public final class WithholdingTax extends AggregateRoot<WithholdingTaxId> {
    
    private static final long serialVersionUID = 1L;
    
    private String transactionId;
    private String customerId;
    private String customerNPWP; // Tax ID
    private String customerName;
    private WithholdingType type; // PPH_23, PPH_26
    private Money grossAmount;
    private Money withholdingAmount;
    private String currencyCode;
    private BigDecimal taxRate;
    private String taxRateDescription;
    private String taxCode; // 23-100-01 for royalties, 23-104-01 for services
    private String taxObjectType; // ROYALTY, SERVICE, INTEREST, DIVIDEND, etc.
    private Instant transactionDate;
    private Instant dueDate;
    private String status; // DRAFT, CALCULATED, REPORTED, PAID
    private String taxPeriod; // e.g., 2024-01
    private String invoiceId;
    private String approvalCode;
    private String rejectionReason;
    private String notes;
    private String createdBy;
    private boolean active;

    private WithholdingTax(WithholdingTaxId id) {
        super(id);
        this.status = "DRAFT";
        this.active = true;
    }

    private WithholdingTax() {
        super();
    }

    /**
     * Factory method to create a new withholding tax record.
     */
    public static WithholdingTax create(
            WithholdingTaxId id,
            String transactionId,
            String customerId,
            String customerNPWP,
            String customerName,
            WithholdingType type,
            Money grossAmount,
            String currencyCode,
            String taxObjectType) {
        WithholdingTax tax = new WithholdingTax(id);
        tax.transactionId = transactionId;
        tax.customerId = customerId;
        tax.customerNPWP = customerNPWP;
        tax.customerName = customerName;
        tax.type = type;
        tax.grossAmount = grossAmount;
        tax.currencyCode = currencyCode;
        tax.taxObjectType = taxObjectType;
        tax.transactionDate = Instant.now();
        return tax;
    }

    /**
     * Calculates the withholding tax amount.
     */
    public void calculateTax() {
        // PPh 23: 15% on royalties, 2% on services
        // PPh 26: 20% on payments to foreign entities
        
        double rate = 0.0;
        String rateDesc = "";
        
        if (type == WithholdingType.PPH_23) {
            if ("ROYALTY".equals(taxObjectType)) {
                rate = 0.15;
                rateDesc = "15% (PPh 23 - Royalties)";
                taxCode = "23-100-01";
            } else if ("SERVICE".equals(taxObjectType)) {
                rate = 0.02;
                rateDesc = "2% (PPh 23 - Services)";
                taxCode = "23-104-01";
            } else if ("INTEREST".equals(taxObjectType)) {
                rate = 0.15;
                rateDesc = "15% (PPh 23 - Interest)";
                taxCode = "23-100-02";
            } else if ("DIVIDEND".equals(taxObjectType)) {
                rate = 0.10;
                rateDesc = "10% (PPh 23 - Dividends)";
                taxCode = "23-100-03";
            }
        } else if (type == WithholdingType.PPH_26) {
            // Check if tax treaty applies
            if (hasTaxTreaty()) {
                // Apply treaty rate (e.g., 10% for Singapore)
                rate = 0.10;
                rateDesc = "10% (PPh 26 - Tax Treaty)";
            } else {
                rate = 0.20;
                rateDesc = "20% (PPh 26 - Standard)";
            }
            taxCode = "26-100-01";
        }

        this.taxRate = BigDecimal.valueOf(rate);
        this.taxRateDescription = rateDesc;
        this.withholdingAmount = grossAmount.multiply(taxRate);
        this.status = "CALCULATED";
        
        // Set tax period (YYYY-MM)
        this.taxPeriod = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")
            .format(java.time.LocalDate.now());
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private boolean hasTaxTreaty() {
        // In production, check if country has a tax treaty with Indonesia
        // and rate is applicable
        return false;
    }

    /**
     * Reports the withholding tax.
     */
    public void report(String approvalCode) {
        if (!status.equals("CALCULATED")) {
            throw new IllegalStateException("Tax must be calculated before reporting");
        }
        this.approvalCode = approvalCode;
        this.status = "REPORTED";
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the tax as paid.
     */
    public void pay() {
        if (!status.equals("REPORTED")) {
            throw new IllegalStateException("Tax must be reported before payment");
        }
        this.status = "PAID";
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the tax report.
     */
    public void reject(String reason) {
        this.rejectionReason = reason;
        this.status = "DRAFT"; // Allow recalculation
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerNPWP() { return customerNPWP; }
    public String getCustomerName() { return customerName; }
    public WithholdingType getType() { return type; }
    public Money getGrossAmount() { return grossAmount; }
    public Money getWithholdingAmount() { return withholdingAmount; }
    public String getCurrencyCode() { return currencyCode; }
    public BigDecimal getTaxRate() { return taxRate; }
    public String getTaxRateDescription() { return taxRateDescription; }
    public String getTaxCode() { return taxCode; }
    public String getTaxObjectType() { return taxObjectType; }
    public Instant getTransactionDate() { return transactionDate; }
    public Instant getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public String getTaxPeriod() { return taxPeriod; }
    public String getInvoiceId() { return invoiceId; }
    public String getApprovalCode() { return approvalCode; }
    public String getRejectionReason() { return rejectionReason; }
    public String getNotes() { return notes; }
    public String getCreatedBy() { return createdBy; }
    public boolean isActive() { return active; }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        return "WithholdingTax{" +
                "id=" + getId() +
                ", customerName='" + customerName + '\'' +
                ", type=" + type +
                ", taxCode='" + taxCode + '\'' +
                ", withholdingAmount=" + withholdingAmount +
                '}';
    }

    /**
     * Withholding tax type enum.
     */
    public enum WithholdingType {
        PPH_23("PPh 23 - Domestic income tax"),
        PPH_26("PPh 26 - Foreign income tax");

        private final String description;

        WithholdingType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}