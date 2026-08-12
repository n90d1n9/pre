
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tax report for compliance.
 */
public final class TaxReport {
    
    private final String taxType; // VAT, GST, SALES_TAX
    private final String companyId;
    private final String companyName;
    private final String taxId;
    private final Instant periodStart;
    private final Instant periodEnd;
    private final List<TaxLineItem> lineItems;
    private final Money totalSales;
    private final Money totalPurchases;
    private final Money taxCollected;
    private final Money taxPaid;
    private final Money netTaxPayable;
    private final String taxJurisdiction;
    private final String currencyCode;

    public TaxReport(Builder builder) {
        this.taxType = builder.taxType;
        this.companyId = builder.companyId;
        this.companyName = builder.companyName;
        this.taxId = builder.taxId;
        this.periodStart = builder.periodStart;
        this.periodEnd = builder.periodEnd;
        this.lineItems = Collections.unmodifiableList(builder.lineItems);
        this.totalSales = builder.totalSales;
        this.totalPurchases = builder.totalPurchases;
        this.taxCollected = builder.taxCollected;
        this.taxPaid = builder.taxPaid;
        this.netTaxPayable = builder.netTaxPayable;
        this.taxJurisdiction = builder.taxJurisdiction;
        this.currencyCode = builder.currencyCode;
    }

    // Getters
    public String getTaxType() { return taxType; }
    public String getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public String getTaxId() { return taxId; }
    public Instant getPeriodStart() { return periodStart; }
    public Instant getPeriodEnd() { return periodEnd; }
    public List<TaxLineItem> getLineItems() { return lineItems; }
    public Money getTotalSales() { return totalSales; }
    public Money getTotalPurchases() { return totalPurchases; }
    public Money getTaxCollected() { return taxCollected; }
    public Money getTaxPaid() { return taxPaid; }
    public Money getNetTaxPayable() { return netTaxPayable; }
    public String getTaxJurisdiction() { return taxJurisdiction; }
    public String getCurrencyCode() { return currencyCode; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String taxType = "VAT";
        private String companyId;
        private String companyName;
        private String taxId;
        private Instant periodStart;
        private Instant periodEnd;
        private List<TaxLineItem> lineItems = new ArrayList<>();
        private Money totalSales = Money.zero("USD");
        private Money totalPurchases = Money.zero("USD");
        private Money taxCollected = Money.zero("USD");
        private Money taxPaid = Money.zero("USD");
        private Money netTaxPayable = Money.zero("USD");
        private String taxJurisdiction;
        private String currencyCode = "USD";

        public Builder taxType(String taxType) {
            this.taxType = taxType;
            return this;
        }

        public Builder companyId(String companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder taxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public Builder periodStart(Instant periodStart) {
            this.periodStart = periodStart;
            return this;
        }

        public Builder periodEnd(Instant periodEnd) {
            this.periodEnd = periodEnd;
            return this;
        }

        public Builder lineItems(List<TaxLineItem> lineItems) {
            this.lineItems = new ArrayList<>(lineItems);
            recalculate();
            return this;
        }

        public Builder addLineItem(TaxLineItem item) {
            this.lineItems.add(item);
            recalculate();
            return this;
        }

        public Builder taxJurisdiction(String taxJurisdiction) {
            this.taxJurisdiction = taxJurisdiction;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        private void recalculate() {
            totalSales = Money.zero(currencyCode);
            totalPurchases = Money.zero(currencyCode);
            taxCollected = Money.zero(currencyCode);
            taxPaid = Money.zero(currencyCode);

            for (TaxLineItem item : lineItems) {
                totalSales = totalSales.add(item.getTaxableSales());
                totalPurchases = totalPurchases.add(item.getTaxablePurchases());
                taxCollected = taxCollected.add(item.getTaxCollected());
                taxPaid = taxPaid.add(item.getTaxPaid());
            }
            
            netTaxPayable = taxCollected.subtract(taxPaid);
        }

        public TaxReport build() {
            return new TaxReport(this);
        }
    }

    /**
     * Tax line item.
     */
    public static final class TaxLineItem {
        private final String transactionId;
        private final String description;
        private final String transactionType;
        private final Instant transactionDate;
        private final Money taxableSales;
        private final Money taxablePurchases;
        private final Money taxCollected;
        private final Money taxPaid;
        private final String taxCode;
        private final BigDecimal taxRate;

        public TaxLineItem(
                String transactionId,
                String description,
                String transactionType,
                Instant transactionDate,
                Money taxableSales,
                Money taxablePurchases,
                Money taxCollected,
                Money taxPaid,
                String taxCode,
                BigDecimal taxRate) {
            this.transactionId = transactionId;
            this.description = description;
            this.transactionType = transactionType;
            this.transactionDate = transactionDate;
            this.taxableSales = taxableSales;
            this.taxablePurchases = taxablePurchases;
            this.taxCollected = taxCollected;
            this.taxPaid = taxPaid;
            this.taxCode = taxCode;
            this.taxRate = taxRate;
        }

        // Getters
        public String getTransactionId() { return transactionId; }
        public String getDescription() { return description; }
        public String getTransactionType() { return transactionType; }
        public Instant getTransactionDate() { return transactionDate; }
        public Money getTaxableSales() { return taxableSales; }
        public Money getTaxablePurchases() { return taxablePurchases; }
        public Money getTaxCollected() { return taxCollected; }
        public Money getTaxPaid() { return taxPaid; }
        public String getTaxCode() { return taxCode; }
        public BigDecimal getTaxRate() { return taxRate; }
    }
}