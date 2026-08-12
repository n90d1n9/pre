
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Financial Statement DTO.
 */
public final class FinancialStatement {
    
    private final String statementType; // BALANCE_SHEET, INCOME_STATEMENT, CASH_FLOW
    private final String companyId;
    private final String companyName;
    private final Instant statementDate;
    private final Instant periodStart;
    private final Instant periodEnd;
    private final List<FinancialLineItem> lineItems;
    private final Money totalAssets;
    private final Money totalLiabilities;
    private final Money totalEquity;
    private final Money totalRevenue;
    private final Money totalExpenses;
    private final Money netIncome;
    private final Money operatingCashFlow;
    private final Money investingCashFlow;
    private final Money financingCashFlow;
    private final Money netCashFlow;

    private FinancialStatement(Builder builder) {
        this.statementType = builder.statementType;
        this.companyId = builder.companyId;
        this.companyName = builder.companyName;
        this.statementDate = builder.statementDate;
        this.periodStart = builder.periodStart;
        this.periodEnd = builder.periodEnd;
        this.lineItems = Collections.unmodifiableList(builder.lineItems);
        this.totalAssets = builder.totalAssets;
        this.totalLiabilities = builder.totalLiabilities;
        this.totalEquity = builder.totalEquity;
        this.totalRevenue = builder.totalRevenue;
        this.totalExpenses = builder.totalExpenses;
        this.netIncome = builder.netIncome;
        this.operatingCashFlow = builder.operatingCashFlow;
        this.investingCashFlow = builder.investingCashFlow;
        this.financingCashFlow = builder.financingCashFlow;
        this.netCashFlow = builder.netCashFlow;
    }

    // Getters
    public String getStatementType() { return statementType; }
    public String getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public Instant getStatementDate() { return statementDate; }
    public Instant getPeriodStart() { return periodStart; }
    public Instant getPeriodEnd() { return periodEnd; }
    public List<FinancialLineItem> getLineItems() { return lineItems; }
    public Money getTotalAssets() { return totalAssets; }
    public Money getTotalLiabilities() { return totalLiabilities; }
    public Money getTotalEquity() { return totalEquity; }
    public Money getTotalRevenue() { return totalRevenue; }
    public Money getTotalExpenses() { return totalExpenses; }
    public Money getNetIncome() { return netIncome; }
    public Money getOperatingCashFlow() { return operatingCashFlow; }
    public Money getInvestingCashFlow() { return investingCashFlow; }
    public Money getFinancingCashFlow() { return financingCashFlow; }
    public Money getNetCashFlow() { return netCashFlow; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String statementType;
        private String companyId;
        private String companyName;
        private Instant statementDate;
        private Instant periodStart;
        private Instant periodEnd;
        private List<FinancialLineItem> lineItems = new ArrayList<>();
        private Money totalAssets = Money.zero("USD");
        private Money totalLiabilities = Money.zero("USD");
        private Money totalEquity = Money.zero("USD");
        private Money totalRevenue = Money.zero("USD");
        private Money totalExpenses = Money.zero("USD");
        private Money netIncome = Money.zero("USD");
        private Money operatingCashFlow = Money.zero("USD");
        private Money investingCashFlow = Money.zero("USD");
        private Money financingCashFlow = Money.zero("USD");
        private Money netCashFlow = Money.zero("USD");

        public Builder statementType(String statementType) {
            this.statementType = statementType;
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

        public Builder statementDate(Instant statementDate) {
            this.statementDate = statementDate;
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

        public Builder lineItems(List<FinancialLineItem> lineItems) {
            this.lineItems = new ArrayList<>(lineItems);
            return this;
        }

        public Builder addLineItem(FinancialLineItem item) {
            this.lineItems.add(item);
            return this;
        }

        public Builder totalAssets(Money totalAssets) {
            this.totalAssets = totalAssets;
            return this;
        }

        public Builder totalLiabilities(Money totalLiabilities) {
            this.totalLiabilities = totalLiabilities;
            return this;
        }

        public Builder totalEquity(Money totalEquity) {
            this.totalEquity = totalEquity;
            return this;
        }

        public Builder totalRevenue(Money totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }

        public Builder totalExpenses(Money totalExpenses) {
            this.totalExpenses = totalExpenses;
            return this;
        }

        public Builder netIncome(Money netIncome) {
            this.netIncome = netIncome;
            return this;
        }

        public Builder operatingCashFlow(Money operatingCashFlow) {
            this.operatingCashFlow = operatingCashFlow;
            return this;
        }

        public Builder investingCashFlow(Money investingCashFlow) {
            this.investingCashFlow = investingCashFlow;
            return this;
        }

        public Builder financingCashFlow(Money financingCashFlow) {
            this.financingCashFlow = financingCashFlow;
            return this;
        }

        public Builder netCashFlow(Money netCashFlow) {
            this.netCashFlow = netCashFlow;
            return this;
        }

        public FinancialStatement build() {
            return new FinancialStatement(this);
        }
    }

    /**
     * Financial line item.
     */
    public static final class FinancialLineItem {
        private final String accountId;
        private final String accountNumber;
        private final String accountName;
        private final Money amount;
        private final int depth;
        private final String category;

        public FinancialLineItem(
                String accountId,
                String accountNumber,
                String accountName,
                Money amount,
                int depth,
                String category) {
            this.accountId = accountId;
            this.accountNumber = accountNumber;
            this.accountName = accountName;
            this.amount = amount;
            this.depth = depth;
            this.category = category;
        }

        // Getters
        public String getAccountId() { return accountId; }
        public String getAccountNumber() { return accountNumber; }
        public String getAccountName() { return accountName; }
        public Money getAmount() { return amount; }
        public int getDepth() { return depth; }
        public String getCategory() { return category; }

        public String getIndentedName() {
            return "  ".repeat(depth) + accountName;
        }
    }
}