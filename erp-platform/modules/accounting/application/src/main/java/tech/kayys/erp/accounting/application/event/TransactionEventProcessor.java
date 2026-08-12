package tech.kayys.erp.accounting.application.event;

import io.smallrye.mutiny.Uni;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.transaction.domain.events.*;
import tech.kayys.erp.accounting.domain.model.Account;
import tech.kayys.erp.accounting.domain.model.JournalEntry;
import tech.kayys.erp.accounting.domain.repository.AccountRepository;
import tech.kayys.erp.accounting.domain.repository.JournalEntryRepository;
import tech.kayys.erp.accounting.domain.valueobject.Money;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Event processor that listens to transaction events and creates accounting entries.
 */
@ApplicationScoped
@UseCase("Process transaction events to accounting")
public class TransactionEventProcessor {

    @Inject
    AccountRepository accountRepository;

    @Inject
    JournalEntryRepository journalEntryRepository;

    /**
     * Handles payment authorized event.
     */
    public Uni<Void> onPaymentAuthorized(@ObservesAsync PaymentAuthorizedEvent event) {
        return Uni.createFrom()
            .completionStage(createAuthorizationEntry(event))
            .onItem()
            .transformToUni(result -> Uni.createFrom().voidItem());
    }

    /**
     * Handles payment captured event.
     */
    public Uni<Void> onPaymentCaptured(@ObservesAsync PaymentCapturedEvent event) {
        return Uni.createFrom()
            .completionStage(createCaptureEntry(event))
            .onItem()
            .transformToUni(result -> Uni.createFrom().voidItem());
    }

    /**
     * Handles payment settled event.
     */
    public Uni<Void> onPaymentSettled(@ObservesAsync PaymentSettledEvent event) {
        return Uni.createFrom()
            .completionStage(createSettlementEntry(event))
            .onItem()
            .transformToUni(result -> Uni.createFrom().voidItem());
    }

    /**
     * Handles refund processed event.
     */
    public Uni<Void> onRefundProcessed(@ObservesAsync RefundProcessedEvent event) {
        return Uni.createFrom()
            .completionStage(createRefundEntry(event))
            .onItem()
            .transformToUni(result -> Uni.createFrom().voidItem());
    }

    /**
     * Handles chargeback received event.
     */
    public Uni<Void> onChargebackReceived(@ObservesAsync ChargebackReceivedEvent event) {
        return Uni.createFrom()
            .completionStage(createChargebackEntry(event))
            .onItem()
            .transformToUni(result -> Uni.createFrom().voidItem());
    }

    /**
     * Creates journal entry for authorization.
     */
    private CompletionStage<JournalEntry> createAuthorizationEntry(PaymentAuthorizedEvent event) {
        // When a payment is authorized, we create a "receivable" entry
        // Debit: Accounts Receivable
        // Credit: Sales Revenue
        
        return accountRepository.findByAccountNumber("AR-001") // Accounts Receivable
            .thenCompose(arAccount -> {
                if (arAccount == null) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Accounts Receivable account not found")
                    );
                }
                
                return accountRepository.findByAccountNumber("REV-001") // Revenue
                    .thenCompose(revenueAccount -> {
                        if (revenueAccount == null) {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Revenue account not found")
                            );
                        }

                        Money amount = Money.of(event.getAmount(), event.getCurrencyCode());

                        JournalEntry entry = JournalEntry.create(
                            JournalEntryId.generate(),
                            "Payment authorization - " + event.getTransactionId(),
                            event.getTransactionId()
                        );
                        entry.setSource("TRANSACTION", event.getTransactionId());

                        // Debit: Accounts Receivable
                        JournalEntry.JournalLine debitLine = new JournalEntry.JournalLine(
                            arAccount.getId(),
                            JournalEntry.JournalLine.LineType.DEBIT,
                            amount,
                            "Payment authorized for transaction: " + event.getTransactionId()
                        );
                        entry.addLine(debitLine);

                        // Credit: Revenue
                        JournalEntry.JournalLine creditLine = new JournalEntry.JournalLine(
                            revenueAccount.getId(),
                            JournalEntry.JournalLine.LineType.CREDIT,
                            amount,
                            "Revenue from transaction: " + event.getTransactionId()
                        );
                        entry.addLine(creditLine);

                        // Post the entry
                        entry.post("SYSTEM");

                        return journalEntryRepository.save(entry);
                    });
            });
    }

    /**
     * Creates journal entry for capture.
     */
    private CompletionStage<JournalEntry> createCaptureEntry(PaymentCapturedEvent event) {
        // When payment is captured, we update the entry
        // No new journal entry needed if we already created one for authorization
        // But we might want to record the capture
        
        return accountRepository.findByAccountNumber("AR-001")
            .thenCompose(arAccount -> {
                if (arAccount == null) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Accounts Receivable account not found")
                    );
                }

                Money amount = Money.of(event.getAmount(), event.getCurrencyCode());

                // Update AR balance (already updated in authorization)
                // In production, we would link to the existing entry
                
                return CompletableFuture.completedFuture(null);
            });
    }

    /**
     * Creates journal entry for settlement.
     */
    private CompletionStage<JournalEntry> createSettlementEntry(PaymentSettledEvent event) {
        // When funds are settled, we move from AR to Cash
        // Debit: Cash
        // Credit: Accounts Receivable
        
        return accountRepository.findByAccountNumber("CASH-001")
            .thenCompose(cashAccount -> {
                if (cashAccount == null) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Cash account not found")
                    );
                }

                return accountRepository.findByAccountNumber("AR-001")
                    .thenCompose(arAccount -> {
                        if (arAccount == null) {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Accounts Receivable account not found")
                            );
                        }

                        Money amount = Money.of(event.getAmount(), event.getCurrencyCode());

                        JournalEntry entry = JournalEntry.create(
                            JournalEntryId.generate(),
                            "Payment settlement - " + event.getTransactionId(),
                            event.getTransactionId()
                        );
                        entry.setSource("TRANSACTION", event.getTransactionId());

                        // Debit: Cash
                        JournalEntry.JournalLine debitLine = new JournalEntry.JournalLine(
                            cashAccount.getId(),
                            JournalEntry.JournalLine.LineType.DEBIT,
                            amount,
                            "Funds settled for transaction: " + event.getTransactionId()
                        );
                        entry.addLine(debitLine);

                        // Credit: Accounts Receivable
                        JournalEntry.JournalLine creditLine = new JournalEntry.JournalLine(
                            arAccount.getId(),
                            JournalEntry.JournalLine.LineType.CREDIT,
                            amount,
                            "AR cleared for transaction: " + event.getTransactionId()
                        );
                        entry.addLine(creditLine);

                        entry.post("SYSTEM");

                        return journalEntryRepository.save(entry);
                    });
            });
    }

    /**
     * Creates journal entry for refund.
     */
    private CompletionStage<JournalEntry> createRefundEntry(RefundProcessedEvent event) {
        // Refund reverses the original payment
        // Debit: Revenue (if refund of revenue)
        // Credit: Cash/AR
        
        return accountRepository.findByAccountNumber("REV-001")
            .thenCompose(revenueAccount -> {
                if (revenueAccount == null) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Revenue account not found")
                    );
                }

                return accountRepository.findByAccountNumber("CASH-001")
                    .thenCompose(cashAccount -> {
                        if (cashAccount == null) {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Cash account not found")
                            );
                        }

                        Money amount = Money.of(event.getAmount(), event.getCurrencyCode());

                        JournalEntry entry = JournalEntry.create(
                            JournalEntryId.generate(),
                            "Refund - " + event.getTransactionId(),
                            event.getTransactionId()
                        );
                        entry.setSource("TRANSACTION", event.getTransactionId());

                        // Debit: Revenue
                        JournalEntry.JournalLine debitLine = new JournalEntry.JournalLine(
                            revenueAccount.getId(),
                            JournalEntry.JournalLine.LineType.DEBIT,
                            amount,
                            "Refund for transaction: " + event.getTransactionId()
                        );
                        entry.addLine(debitLine);

                        // Credit: Cash
                        JournalEntry.JournalLine creditLine = new JournalEntry.JournalLine(
                            cashAccount.getId(),
                            JournalEntry.JournalLine.LineType.CREDIT,
                            amount,
                            "Cash refund for transaction: " + event.getTransactionId()
                        );
                        entry.addLine(creditLine);

                        entry.post("SYSTEM");

                        return journalEntryRepository.save(entry);
                    });
            });
    }

    /**
     * Creates journal entry for chargeback.
     */
    private CompletionStage<JournalEntry> createChargebackEntry(ChargebackReceivedEvent event) {
        // Chargeback reverses the payment and creates a liability
        // Debit: Chargeback Expense
        // Credit: Accounts Receivable
        
        return accountRepository.findByAccountNumber("EXP-CHARGEBACK")
            .thenCompose(expenseAccount -> {
                if (expenseAccount == null) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Chargeback expense account not found")
                    );
                }

                return accountRepository.findByAccountNumber("AR-001")
                    .thenCompose(arAccount -> {
                        if (arAccount == null) {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Accounts Receivable account not found")
                            );
                        }

                        Money amount = Money.of(event.getAmount(), event.getCurrencyCode());

                        JournalEntry entry = JournalEntry.create(
                            JournalEntryId.generate(),
                            "Chargeback - " + event.getTransactionId(),
                            event.getTransactionId()
                        );
                        entry.setSource("TRANSACTION", event.getTransactionId());

                        // Debit: Chargeback Expense
                        JournalEntry.JournalLine debitLine = new JournalEntry.JournalLine(
                            expenseAccount.getId(),
                            JournalEntry.JournalLine.LineType.DEBIT,
                            amount,
                            "Chargeback expense for transaction: " + event.getTransactionId()
                        );
                        entry.addLine(debitLine);

                        // Credit: Accounts Receivable
                        JournalEntry.JournalLine creditLine = new JournalEntry.JournalLine(
                            arAccount.getId(),
                            JournalEntry.JournalLine.LineType.CREDIT,
                            amount,
                            "AR reversal for chargeback: " + event.getTransactionId()
                        );
                        entry.addLine(creditLine);

                        entry.post("SYSTEM");

                        return journalEntryRepository.save(entry);
                    });
            });
    }
}