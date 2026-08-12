package tech.kayys.erp.transaction.application.port;

import tech.kayys.erp.transaction.domain.model.Transaction;

import java.util.concurrent.CompletionStage;

/**
 * Port for accounting integration.
 * Transaction context calls this to create accounting entries.
 */
public interface AccountingPort {

    /**
     * Creates journal entries for a transaction.
     */
    CompletionStage<AccountingResult> createJournalEntry(Transaction transaction);

    /**
     * Records a payment in the accounting system.
     */
    CompletionStage<AccountingResult> recordPayment(
        String transactionId,
        String customerId,
        Money amount,
        String currencyCode,
        String paymentMethod
    );

    /**
     * Records a refund in the accounting system.
     */
    CompletionStage<AccountingResult> recordRefund(
        String transactionId,
        String customerId,
        Money amount,
        String currencyCode,
        String originalTransactionId
    );

    /**
     * Records a chargeback in the accounting system.
     */
    CompletionStage<AccountingResult> recordChargeback(
        String transactionId,
        String customerId,
        Money amount,
        String currencyCode,
        String reason
    );

    /**
     * Gets accounting status for a transaction.
     */
    CompletionStage<AccountingStatus> getAccountingStatus(String transactionId);

    /**
     * Accounting result record.
     */
    record AccountingResult(
        boolean success,
        String journalEntryId,
        String message,
        Instant processedAt
    ) {}

    /**
     * Accounting status record.
     */
    record AccountingStatus(
        String transactionId,
        boolean posted,
        String journalEntryId,
        String status,
        Instant postedAt
    ) {}
}