package tech.kayys.erp.accounting.model;

import tech.kayys.erp.accounting.model.identifier.AccountId;
import tech.kayys.erp.accounting.model.valueobject.AccountStatus;
import tech.kayys.erp.accounting.model.valueobject.AccountType;
import tech.kayys.erp.foundation.domain.AggregateRoot;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Account aggregate root in the chart of accounts.
 * Represents a financial account for tracking monetary transactions.
 */
public final class Account extends AggregateRoot<AccountId> {
    
    private static final long serialVersionUID = 1L;
    
    private String accountNumber;
    private String name;
    private String description;
    private AccountType accountType;
    private AccountStatus status;
    private BigDecimal openingBalance;
    private BigDecimal currentBalance;
    private String currencyCode;
    private AccountId parentAccountId;
    private boolean isReconcilable;
    private boolean isActive;
    private String notes;

    private Account(AccountId id) {
        super(id);
        this.status = AccountStatus.ACTIVE;
        this.openingBalance = BigDecimal.ZERO;
        this.currentBalance = BigDecimal.ZERO;
        this.isReconcilable = true;
        this.isActive = true;
    }

    private Account() {
        super();
    }

    /**
     * Factory method to create a new account.
     */
    public static Account create(
            AccountId id,
            String accountNumber,
            String name,
            AccountType accountType,
            String currencyCode) {
        Account account = new Account(id);
        account.accountNumber = accountNumber;
        account.name = name;
        account.accountType = accountType;
        account.currencyCode = currencyCode;
        account.status = AccountStatus.ACTIVE;
        return account;
    }

    /**
     * Posts a debit transaction to this account.
     */
    public void debit(Money amount) {
        if (!isActive || status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        if (accountType.getNormalBalance() != AccountType.NormalBalance.DEBIT) {
            // Debit decreases credit-natural accounts
            currentBalance = currentBalance.subtract(amount.getAmount());
        } else {
            // Debit increases debit-natural accounts
            currentBalance = currentBalance.add(amount.getAmount());
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Posts a credit transaction to this account.
     */
    public void credit(Money amount) {
        if (!isActive || status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        if (accountType.getNormalBalance() != AccountType.NormalBalance.CREDIT) {
            // Credit decreases debit-natural accounts
            currentBalance = currentBalance.subtract(amount.getAmount());
        } else {
            // Credit increases credit-natural accounts
            currentBalance = currentBalance.add(amount.getAmount());
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the current balance as Money.
     */
    public Money getBalance() {
        return Money.of(currentBalance, currencyCode);
    }

    /**
     * Opens the account with a balance.
     */
    public void openWithBalance(Money balance) {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot open account in status: " + status);
        }
        this.openingBalance = balance.getAmount();
        this.currentBalance = balance.getAmount();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Closes the account.
     */
    public void close() {
        if (!currentBalance.equals(BigDecimal.ZERO)) {
            throw new IllegalStateException("Account has non-zero balance: " + currentBalance);
        }
        this.status = AccountStatus.CLOSED;
        this.isActive = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the parent account.
     */
    public void setParentAccount(AccountId parentAccountId) {
        if (this.id.equals(parentAccountId)) {
            throw new IllegalArgumentException("Cannot set self as parent");
        }
        this.parentAccountId = parentAccountId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public AccountType getAccountType() { return accountType; }
    public AccountStatus getStatus() { return status; }
    public BigDecimal getOpeningBalance() { return openingBalance; }
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public String getCurrencyCode() { return currencyCode; }
    public AccountId getParentAccountId() { return parentAccountId; }
    public boolean isReconcilable() { return isReconcilable; }
    public boolean isActive() { return isActive; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setReconcilable(boolean reconcilable) {
        this.isReconcilable = reconcilable;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void activate() {
        this.isActive = true;
        this.status = AccountStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void deactivate() {
        this.isActive = false;
        this.status = AccountStatus.INACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + getId() +
                ", accountNumber='" + accountNumber + '\'' +
                ", name='" + name + '\'' +
                ", accountType=" + accountType +
                ", balance=" + currentBalance +
                '}';
    }
}