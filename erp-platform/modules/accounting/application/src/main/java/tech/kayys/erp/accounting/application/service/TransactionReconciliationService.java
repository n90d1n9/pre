package tech.kayys.erp.accounting.application.service;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.accounting.application.port.TransactionPort;
import tech.kayys.erp.accounting.domain.model.JournalEntry;
import tech.kayys.erp.accounting.domain.repository.JournalEntryRepository;
import tech.kayys.erp.accounting.domain.valueobject.Money;
import tech.kayys.erp.transaction.application.port.TransactionDetails;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Reconciliation service between transactions and accounting entries.
 */
@Singleton
@UseCase("Transaction-Accounting Reconciliation")
public class TransactionReconciliationService {

    @Inject
    TransactionPort transactionPort;

    @Inject
    JournalEntryRepository journalEntryRepository;

    /**
     * Performs reconciliation for a date range.
     */
    public CompletionStage<ReconciliationResult> reconcile(Instant fromDate, Instant toDate) {
        // 1. Get all transactions
        return transactionPort.getTransactionsForReconciliation(fromDate, toDate, null)
            .thenCompose(transactions -> {
                // 2. Get all accounting entries
                return journalEntryRepository.findByDateRange(fromDate, toDate)
                    .thenApply(journalEntries -> {
                        List<ReconciliationItem> items = new ArrayList<>();
                        long matchedCount = 0;
                        long unmatchedCount = 0;
                        Money totalMatched = Money.zero("USD");
                        Money totalUnmatched = Money.zero("USD");

                        // Match transactions with journal entries
                        for (TransactionDetails transaction : transactions) {
                            boolean matched = journalEntries.stream()
                                .anyMatch(entry -> 
                                    entry.getSourceId() != null && 
                                    entry.getSourceId().equals(transaction.transactionId())
                                );

                            ReconciliationItem item = new ReconciliationItem(
                                transaction.transactionId(),
                                transaction.reference(),
                                transaction.amount(),
                                transaction.currencyCode(),
                                matched ? "MATCHED" : "UNMATCHED",
                                matched ? "Found matching journal entry" : "No matching journal entry"
                            );
                            items.add(item);

                            if (matched) {
                                matchedCount++;
                                totalMatched = totalMatched.add(transaction.amount());
                            } else {
                                unmatchedCount++;
                                totalUnmatched = totalUnmatched.add(transaction.amount());
                            }
                        }

                        boolean reconciled = unmatchedCount == 0;
                        Money discrepancy = totalMatched.subtract(totalUnmatched);

                        return new ReconciliationResult(
                            fromDate,
                            toDate,
                            items,
                            matchedCount,
                            unmatchedCount,
                            totalMatched,
                            totalUnmatched,
                            discrepancy,
                            reconciled,
                            reconciled ? "All transactions reconciled" : "Unmatched transactions found",
                            Instant.now()
                        );
                    });
            });
    }

    /**
     * Reconciliation result record.
     */
    public record ReconciliationResult(
        Instant fromDate,
        Instant toDate,
        List<ReconciliationItem> items,
        long matchedCount,
        long unmatchedCount,
        Money totalMatched,
        Money totalUnmatched,
        Money discrepancy,
        boolean reconciled,
        String notes,
        Instant processedAt
    ) {}

    /**
     * Reconciliation item record.
     */
    public record ReconciliationItem(
        String transactionId,
        String reference,
        Money amount,
        String currencyCode,
        String status,
        String notes
    ) {}
}