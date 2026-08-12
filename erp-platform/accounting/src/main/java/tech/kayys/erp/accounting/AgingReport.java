
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aging report for Accounts Receivable.
 */
public final class AgingReport {
    
    private final String reportType; // AR or AP
    private final String companyId;
    private final String companyName;
    private final Instant asOfDate;
    private final List<AgingBucket> buckets;
    private final Money totalCurrent;
    private final Money total30Days;
    private final Money total60Days;
    private final Money total90Days;
    private final Money total90PlusDays;
    private final Money grandTotal;

    public AgingReport(Builder builder) {
        this.reportType = builder.reportType;
        this.companyId = builder.companyId;
        this.companyName = builder.companyName;
        this.asOfDate = builder.asOfDate;
        this.buckets = Collections.unmodifiableList(builder.buckets);
        this.totalCurrent = builder.totalCurrent;
        this.total30Days = builder.total30Days;
        this.total60Days = builder.total60Days;
        this.total90Days = builder.total90Days;
        this.total90PlusDays = builder.total90PlusDays;
        this.grandTotal = builder.grandTotal;
    }

    // Getters
    public String getReportType() { return reportType; }
    public String getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public Instant getAsOfDate() { return asOfDate; }
    public List<AgingBucket> getBuckets() { return buckets; }
    public Money getTotalCurrent() { return totalCurrent; }
    public Money getTotal30Days() { return total30Days; }
    public Money getTotal60Days() { return total60Days; }
    public Money getTotal90Days() { return total90Days; }
    public Money getTotal90PlusDays() { return total90PlusDays; }
    public Money getGrandTotal() { return grandTotal; }

    /**
     * Gets the percentage of total for a bucket.
     */
    public double getPercentage(Money amount) {
        if (grandTotal.isZero()) {
            return 0.0;
        }
        return amount.getAmount()
            .divide(grandTotal.getAmount(), 4, java.math.RoundingMode.HALF_UP)
            .multiply(java.math.BigDecimal.valueOf(100))
            .doubleValue();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String reportType = "AR";
        private String companyId;
        private String companyName;
        private Instant asOfDate = Instant.now();
        private List<AgingBucket> buckets = new ArrayList<>();
        private Money totalCurrent = Money.zero("USD");
        private Money total30Days = Money.zero("USD");
        private Money total60Days = Money.zero("USD");
        private Money total90Days = Money.zero("USD");
        private Money total90PlusDays = Money.zero("USD");
        private Money grandTotal = Money.zero("USD");

        public Builder reportType(String reportType) {
            this.reportType = reportType;
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

        public Builder asOfDate(Instant asOfDate) {
            this.asOfDate = asOfDate;
            return this;
        }

        public Builder buckets(List<AgingBucket> buckets) {
            this.buckets = new ArrayList<>(buckets);
            recalculate();
            return this;
        }

        public Builder addBucket(AgingBucket bucket) {
            this.buckets.add(bucket);
            recalculate();
            return this;
        }

        private void recalculate() {
            totalCurrent = Money.zero("USD");
            total30Days = Money.zero("USD");
            total60Days = Money.zero("USD");
            total90Days = Money.zero("USD");
            total90PlusDays = Money.zero("USD");
            grandTotal = Money.zero("USD");

            for (AgingBucket bucket : buckets) {
                totalCurrent = totalCurrent.add(bucket.current);
                total30Days = total30Days.add(bucket.thirtyDays);
                total60Days = total60Days.add(bucket.sixtyDays);
                total90Days = total90Days.add(bucket.ninetyDays);
                total90PlusDays = total90PlusDays.add(bucket.ninetyPlusDays);
                grandTotal = grandTotal.add(bucket.total);
            }
        }

        public AgingReport build() {
            return new AgingReport(this);
        }
    }

    /**
     * Aging bucket for a customer.
     */
    public static final class AgingBucket {
        private final String customerId;
        private final String customerName;
        private final Money current;
        private final Money thirtyDays;
        private final Money sixtyDays;
        private final Money ninetyDays;
        private final Money ninetyPlusDays;
        private final Money total;

        public AgingBucket(
                String customerId,
                String customerName,
                Money current,
                Money thirtyDays,
                Money sixtyDays,
                Money ninetyDays,
                Money ninetyPlusDays) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.current = current;
            this.thirtyDays = thirtyDays;
            this.sixtyDays = sixtyDays;
            this.ninetyDays = ninetyDays;
            this.ninetyPlusDays = ninetyPlusDays;
            this.total = calculateTotal();
        }

        private Money calculateTotal() {
            Money currency = current != null ? current : Money.zero("USD");
            return currency
                .add(thirtyDays)
                .add(sixtyDays)
                .add(ninetyDays)
                .add(ninetyPlusDays);
        }

        // Getters
        public String getCustomerId() { return customerId; }
        public String getCustomerName() { return customerName; }
        public Money getCurrent() { return current; }
        public Money getThirtyDays() { return thirtyDays; }
        public Money getSixtyDays() { return sixtyDays; }
        public Money getNinetyDays() { return ninetyDays; }
        public Money getNinetyPlusDays() { return ninetyPlusDays; }
        public Money getTotal() { return total; }
    }
}