package tech.kayys.erp.transaction.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.transaction.domain.identifier.TransactionId;
import tech.kayys.erp.transaction.domain.model.Transaction;
import tech.kayys.erp.transaction.domain.valueobject.TransactionStatus;
import tech.kayys.erp.transaction.domain.valueobject.TransactionType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Transaction aggregates.
 */
public interface TransactionRepository extends Repository<Transaction, TransactionId> {

    /**
     * Finds transactions by order ID.
     */
    CompletionStage<List<Transaction>> findByOrderId(UUID orderId);

    /**
     * Finds transactions by customer ID.
     */
    CompletionStage<List<Transaction>> findByCustomerId(String customerId);

    /**
     * Finds transactions by reference.
     */
    CompletionStage<Transaction> findByReference(String reference);

    /**
     * Finds transactions by status.
     */
    CompletionStage<List<Transaction>> findByStatus(TransactionStatus status);

    /**
     * Finds transactions by type.
     */
    CompletionStage<List<Transaction>> findByType(TransactionType type);

    /**
     * Finds transactions by processor transaction ID.
     */
    CompletionStage<Transaction> findByProcessorTransactionId(String processorTransactionId);

    /**
     * Finds transactions by date range.
     */
    CompletionStage<List<Transaction>> findByDateRange(Instant start, Instant end);

    /**
     * Finds transactions requiring settlement.
     */
    CompletionStage<List<Transaction>> findTransactionsForSettlement(Instant cutoffDate);

    /**
     * Finds transactions for reconciliation.
     */
    CompletionStage<List<Transaction>> findTransactionsForReconciliation(
        Instant start,
        Instant end,
        String merchantId
    );

    /**
     * Finds refundable transactions.
     */
    CompletionStage<List<Transaction>> findRefundableTransactions(String customerId);

    /**
     * Finds transactions by batch ID.
     */
    CompletionStage<List<Transaction>> findByBatchId(String batchId);

    /**
     * Gets transaction totals for a period.
     */
    CompletionStage<TransactionTotals> getTransactionTotals(Instant start, Instant end, String merchantId);

    /**
     * Transaction totals record.
     */
    record TransactionTotals(
        long totalCount,
        Money totalAmount,
        Money totalTaxAmount,
        Money totalTipAmount,
        Money totalFeeAmount,
        Money totalNetAmount,
        int pendingCount,
        int authorizedCount,
        int capturedCount,
        int settledCount,
        int completedCount,
        int failedCount,
        int refundedCount
    ) {}
}