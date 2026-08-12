package tech.kayys.erp.transaction.application.api;

import tech.kayys.erp.transaction.application.api.command.*;
import tech.kayys.erp.transaction.application.api.query.*;
import tech.kayys.erp.transaction.domain.identifier.TransactionId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for transaction processing.
 */
public interface TransactionService {

    /**
     * Processes a payment transaction.
     */
    CompletionStage<TransactionResult> processPayment(ProcessPaymentCommand command);

    /**
     * Authorizes a payment.
     */
    CompletionStage<TransactionResult> authorizePayment(AuthorizePaymentCommand command);

    /**
     * Captures an authorized payment.
     */
    CompletionStage<TransactionResult> capturePayment(CapturePaymentCommand command);

    /**
     * Processes a refund.
     */
    CompletionStage<TransactionResult> refundPayment(RefundPaymentCommand command);

    /**
     * Voids a transaction.
     */
    CompletionStage<TransactionResult> voidTransaction(VoidTransactionCommand command);

    /**
     * Gets transaction details.
     */
    CompletionStage<TransactionView> getTransaction(GetTransactionQuery query);

    /**
     * Gets transaction by reference.
     */
    CompletionStage<TransactionView> getTransactionByReference(String reference);

    /**
     * Searches transactions.
     */
    CompletionStage<TransactionSearchResult> searchTransactions(SearchTransactionsQuery query);

    /**
     * Gets transaction statistics.
     */
    CompletionStage<TransactionStatistics> getTransactionStatistics(
        TransactionStatisticsQuery query
    );

    /**
     * Processes batch settlement.
     */
    CompletionStage<BatchSettlementResult> processBatchSettlement(BatchSettlementCommand command);

    /**
     * Gets transaction summary for reconciliation.
     */
    CompletionStage<ReconciliationSummary> getReconciliationSummary(
        ReconciliationSummaryQuery query
    );
}