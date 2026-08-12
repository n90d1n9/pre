package tech.kayys.erp.accounting.repository;


import java.util.List;
import java.util.concurrent.CompletionStage;

import tech.kayys.erp.accounting.model.Account;

/**
 * Repository for Account aggregates.
 */
public interface AccountRepository extends Repository<Account, AccountId> {

    /**
     * Finds an account by account number.
     */
    CompletionStage<Account> findByAccountNumber(String accountNumber);

    /**
     * Finds accounts by type.
     */
    CompletionStage<List<Account>> findByType(AccountType type);

    /**
     * Finds active accounts.
     */
    CompletionStage<List<Account>> findActiveAccounts();

    /**
     * Finds accounts by parent.
     */
    CompletionStage<List<Account>> findByParentId(AccountId parentId);

    /**
     * Finds root accounts (no parent).
     */
    CompletionStage<List<Account>> findRootAccounts();

    /**
     * Gets the account hierarchy for reporting.
     */
    CompletionStage<List<Account>> getAccountHierarchy();

    /**
     * Checks if an account number is unique.
     */
    CompletionStage<Boolean> isAccountNumberUnique(String accountNumber);
}