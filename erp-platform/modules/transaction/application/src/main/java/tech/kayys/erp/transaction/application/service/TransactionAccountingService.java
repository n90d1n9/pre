package tech.kayys.erp.transaction.application.service;

import io.smallrye.mutiny.Uni;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.transaction.application.port.AccountingPort;
import tech.kayys.erp.transaction.application.port.TransactionEventPublisher;
import tech.kayys.erp.transaction.domain.model.Transaction;
import tech.kayys.erp.transaction.domain.repository.TransactionRepository;
import tech.kayys.erp.transaction.domain.valueobject.TransactionStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Transaction service with automatic accounting integration.
 */
@Singleton
@UseCase("Transaction with accounting integration")
public class TransactionAccountingService {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    AccountingPort accountingPort;

    @Inject
    TransactionEventPublisher eventPublisher;

    /**
     * Processes a transaction with automatic accounting entries.
     */
    public CompletionStage<TransactionResult> processTransaction(Transaction transaction) {
        // 1. Save the transaction
        return transactionRepository.save(transaction)
            .thenCompose(saved -> {
                // 2. Create accounting entries
                return accountingPort.createJournalEntry(saved)
                    .thenCompose(accountingResult -> {
                        if (!accountingResult.success()) {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Accounting failed: " + accountingResult.message())
                            );
                        }

                        // 3. Update transaction with accounting reference
                        saved.setAccountingReference(accountingResult.journalEntryId());
                        return transactionRepository.save(saved)
                            .thenCompose(updated -> {
                                // 4. Publish events
                                return eventPublisher.publishTransactionEvent(updated)
                                    .thenApply(v -> {
                                        // 5. Return result
                                        return new TransactionResult(
                                            updated.getId(),
                                            updated.getTransactionReference(),
                                            updated.getStatus(),
                                            accountingResult.journalEntryId(),
                                            true,
                                            "Transaction processed successfully"
                                        );
                                    });
                            });
                    });
            });
    }

    /**
     * Processes a refund with accounting integration.
     */
    public CompletionStage<TransactionResult> processRefund(
            String originalTransactionId,
            Money refundAmount,
            String reason) {
        
        return transactionRepository.findByProcessorTransactionId(originalTransactionId)
            .thenCompose(originalTransaction -> {
                if (originalTransaction == null) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Original transaction not found")
                    );
                }

                // Create refund transaction
                Transaction refund = Transaction.create(
                    TransactionId.generate(),
                    "REF-" + originalTransaction.getTransactionReference(),
                    originalTransaction.getOrderId(),
                    originalTransaction.getCustomerId(),
                    TransactionType.REFUND,
                    refundAmount,
                    originalTransaction.getPaymentInstrument(),
                    originalTransaction.getCurrencyCode(),
                    originalTransaction.getMerchantId()
                );
                refund.setRefundReference(originalTransaction.getId().toString());
                refund.setReason(reason);

                // Process refund
                return processTransaction(refund);
            });
    }

    /**
     * Transaction result record.
     */
    public record TransactionResult(
        TransactionId transactionId,
        String reference,
        TransactionStatus status,
        String journalEntryId,
        boolean success,
        String message
    ) {}
}