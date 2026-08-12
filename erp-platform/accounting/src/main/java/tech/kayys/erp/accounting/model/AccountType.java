package tech.kayys.erp.accounting.model;


/**
 * Types of accounts in the chart of accounts.
 */
public enum AccountType {
    ASSET("Asset"),
    LIABILITY("Liability"),
    EQUITY("Equity"),
    REVENUE("Revenue"),
    EXPENSE("Expense"),
    COGS("Cost of Goods Sold"),
    OTHER_INCOME("Other Income"),
    OTHER_EXPENSE("Other Expense");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the normal balance type for this account type.
     */
    public NormalBalance getNormalBalance() {
        return switch (this) {
            case ASSET, EXPENSE, COGS, OTHER_EXPENSE -> NormalBalance.DEBIT;
            case LIABILITY, EQUITY, REVENUE, OTHER_INCOME -> NormalBalance.CREDIT;
        };
    }

    /**
     * Checks if this account type appears on the balance sheet.
     */
    public boolean isBalanceSheetAccount() {
        return this == ASSET || this == LIABILITY || this == EQUITY;
    }

    /**
     * Checks if this account type appears on the income statement.
     */
    public boolean isIncomeStatementAccount() {
        return this == REVENUE || this == EXPENSE || this == COGS ||
               this == OTHER_INCOME || this == OTHER_EXPENSE;
    }

    public enum NormalBalance {
        DEBIT, CREDIT
    }
}