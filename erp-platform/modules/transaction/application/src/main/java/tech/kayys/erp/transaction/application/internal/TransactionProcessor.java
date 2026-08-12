package tech.kayys.erp.transaction.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.transaction.application.api.TransactionService;
import tech.kayys.erp.transaction.application.api.command.*;
import tech.kayys.erp.transaction.application.api.query.*;
import tech.kayys.erp.transaction.application.port.PaymentGatewayPort;
import tech.kayys.erp.transaction.domain.identifier.TransactionId;
import tech.kayys.erp.transaction.domain.model.Transaction;
import tech.kayys.erp.transaction.domain.repository.TransactionRepository;
import tech.kayys.erp.transaction.domain.valueobject.PaymentInstrument;
import tech.kayys.erp.transaction.domain.valueobject.TransactionStatus;
import tech.kayys.erp.transaction.domain.valueobject.TransactionType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Core transaction processing engine.
 */
@Singleton
@UseCase("Transaction processing engine")
public class TransactionProcessor implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentGatewayPort gatewayPort;
    private final AuditService auditService;
    private final EventPublisher eventPublisher;

    @Inject
    public TransactionProcessor(
            TransactionRepository transactionRepository,
            PaymentGatewayPort gatewayPort,
            AuditService auditService,
            EventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.gatewayPort = gatewayPort;
        this.auditService = auditService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletionStage<TransactionResult> processPayment(ProcessPaymentCommand command) {
        // 1. Create payment instrument
        PaymentInstrument instrument = buildPaymentInstrument(command);

        // 2. Create transaction
        Transaction transaction = Transaction.create(
            TransactionId.generate(),
            generateTransactionReference(),
            command.orderId(),
            command.customerId(),
            TransactionType.SALE,
            Money.of(command.amount(), command.currencyCode()),
            instrument,
            command.currencyCode(),
            command.merchantId()
        );

        // 3. Set additional fields
        transaction.setTerminalId(command.terminalId());
        transaction.setChannelId(command.channelId());
        transaction.setChannelType(command.channelType());
        transaction.setOrderNumber(command.orderNumber());
        transaction.setCustomerEmail(command.customerEmail());
        
        if (command.taxAmount() != null) {
            transaction.setTaxAmount(Money.of(command.taxAmount(), command.currencyCode()));
        }
        if (command.tipAmount() != null) {
            transaction.setTipAmount(Money.of(command.tipAmount(), command.currencyCode()));
        }

        // 4. Save transaction
        return transactionRepository.save(transaction)
            .thenCompose(saved -> {
                // 5. Process through gateway
                return gatewayPort.sale(saved)
                    .thenCompose(gatewayResponse -> {
                        if (gatewayResponse.success()) {
                            // 6. Update with gateway response
                            saved.authorize(
                                gatewayResponse.processorTransactionId(),
                                gatewayResponse.authorizationCode()
                            );
                            saved.capture(Money.of(command.amount(), command.currencyCode()));
                            saved.complete();

                            // 7. Save updated transaction
                            return transactionRepository.save(saved)
                                .thenApply(updated -> {
                                    // 8. Audit and publish events
                                    auditService.recordTransaction(updated);
                                    eventPublisher.publishTransactionEvent(updated);
                                    
                                    return toTransactionResult(updated);
                                });
                        } else {
                            // 9. Handle gateway failure
                            saved.recordFailure(
                                gatewayResponse.responseCode(),
                                gatewayResponse.responseMessage()
                            );
                            return transactionRepository.save(saved)
                                .thenApply(failed -> {
                                    auditService.recordTransaction(failed);
                                    return toTransactionResult(failed);
                                });
                        }
                    });
            });
    }

    @Override
    public CompletionStage<TransactionResult> authorizePayment(AuthorizePaymentCommand command) {
        PaymentInstrument instrument = buildPaymentInstrument(command);
        
        Transaction transaction = Transaction.create(
            TransactionId.generate(),
            generateTransactionReference(),
            command.orderId(),
            command.customerId(),
            TransactionType.AUTHORIZATION,
            Money.of(command.amount(), command.currencyCode()),
            instrument,
            command.currencyCode(),
            command.merchantId()
        );

        transaction.setTerminalId(command.terminalId());
        transaction.setChannelId(command.channelId());
        transaction.setChannelType(command.channelType());

        return transactionRepository.save(transaction)
            .thenCompose(saved -> {
                return gatewayPort.authorize(saved)
                    .thenCompose(gatewayResponse -> {
                        if (gatewayResponse.success()) {
                            saved.authorize(
                                gatewayResponse.processorTransactionId(),
                                gatewayResponse.authorizationCode()
                            );
                            return transactionRepository.save(saved)
                                .thenApply(updated -> toTransactionResult(updated));
                        } else {
                            saved.recordFailure(
                                gatewayResponse.responseCode(),
                                gatewayResponse.responseMessage()
                            );
                            return transactionRepository.save(saved)
                                .thenApply(failed -> toTransactionResult(failed));
                        }
                    });
            });
    }

    @Override
    public CompletionStage<TransactionResult> capturePayment(CapturePaymentCommand command) {
        return transactionRepository.findByProcessorTransactionId(command.processorTransactionId())
            .thenCompose(transaction -> {
                if (transaction == null) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Transaction not found: " + command.processorTransactionId())
                    );
                }

                if (transaction.getStatus() != TransactionStatus.AUTHORIZED) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Transaction is not authorized: " + transaction.getStatus())
                    );
                }

                Money captureAmount = Money.of(command.amount(), command.currencyCode());
                
                return gatewayPort.capture(transaction)
                    .thenCompose(gatewayResponse -> {
                        if (gatewayResponse.success()) {
                            transaction.capture(captureAmount);
                            transaction.complete();
                            return transactionRepository.save(transaction)
                                .thenApply(updated -> toTransactionResult(updated));
                        } else {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Capture failed: " + gatewayResponse.responseMessage())
                            );
                        }
                    });
            });
    }

    @Override
    public CompletionStage<TransactionResult> refundPayment(RefundPaymentCommand command) {
        return transactionRepository.findByProcessorTransactionId(command.processorTransactionId())
            .thenCompose(transaction -> {
                if (transaction == null) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Transaction not found: " + command.processorTransactionId())
                    );
                }

                Money refundAmount = Money.of(command.amount(), command.currencyCode());
                
                return gatewayPort.refund(transaction)
                    .thenCompose(gatewayResponse -> {
                        if (gatewayResponse.success()) {
                            Transaction refundTransaction = transaction.refund(refundAmount, command.reason());
                            return transactionRepository.save(refundTransaction)
                                .thenApply(updated -> toTransactionResult(updated));
                        } else {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Refund failed: " + gatewayResponse.responseMessage())
                            );
                        }
                    });
            });
    }

    @Override
    public CompletionStage<TransactionResult> voidTransaction(VoidTransactionCommand command) {
        return transactionRepository.findByProcessorTransactionId(command.processorTransactionId())
            .thenCompose(transaction -> {
                if (transaction == null) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Transaction not found: " + command.processorTransactionId())
                    );
                }

                return gatewayPort.voidTransaction(transaction)
                    .thenCompose(gatewayResponse -> {
                        if (gatewayResponse.success()) {
                            transaction.voidTransaction(command.reason());
                            return transactionRepository.save(transaction)
                                .thenApply(updated -> toTransactionResult(updated));
                        } else {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Void failed: " + gatewayResponse.responseMessage())
                            );
                        }
                    });
            });
    }

    @Override
    public CompletionStage<TransactionView> getTransaction(GetTransactionQuery query) {
        return transactionRepository.findById(query.transactionId())
            .thenApply(transactionOpt -> 
                transactionOpt.map(TransactionView::fromDomain)
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found: " + query.transactionId()
                    ))
            );
    }

    @Override
    public CompletionStage<TransactionView> getTransactionByReference(String reference) {
        return transactionRepository.findByReference(reference)
            .thenApply(transaction -> 
                transaction != null ? TransactionView.fromDomain(transaction) : null
            );
    }

    @Override
    public CompletionStage<TransactionSearchResult> searchTransactions(SearchTransactionsQuery query) {
        return transactionRepository.findByDateRange(query.fromDate(), query.toDate())
            .thenApply(transactions -> {
                List<TransactionView> views = transactions.stream()
                    .map(TransactionView::fromDomain)
                    .collect(Collectors.toList());
                return TransactionSearchResult.of(views, views.size(), query.page(), query.size());
            });
    }

    @Override
    public CompletionStage<TransactionStatistics> getTransactionStatistics(TransactionStatisticsQuery query) {
        return transactionRepository.getTransactionTotals(
            query.fromDate(),
            query.toDate(),
            query.merchantId()
        ).thenApply(statistics -> {
            return new TransactionStatistics(
                query.fromDate(),
                query.toDate(),
                query.merchantId(),
                statistics.totalCount(),
                statistics.totalAmount(),
                statistics.totalTaxAmount(),
                statistics.totalTipAmount(),
                statistics.totalFeeAmount(),
                statistics.totalNetAmount(),
                statistics.pendingCount(),
                statistics.authorizedCount(),
                statistics.capturedCount(),
                statistics.settledCount(),
                statistics.completedCount(),
                statistics.failedCount(),
                statistics.refundedCount(),
                Instant.now()
            );
        });
    }

    @Override
    public CompletionStage<BatchSettlementResult> processBatchSettlement(BatchSettlementCommand command) {
        return transactionRepository.findTransactionsForSettlement(Instant.now().minusSeconds(86400))
            .thenCompose(transactions -> {
                if (transactions.isEmpty()) {
                    return CompletableFuture.completedFuture(
                        new BatchSettlementResult(false, null, 0, Money.zero("USD"), "No transactions to settle")
                    );
                }

                return gatewayPort.settleBatch(transactions)
                    .thenApply(batchResponse -> {
                        if (batchResponse.success()) {
                            // Update all transactions with batch ID
                            for (Transaction t : transactions) {
                                t.settle(batchResponse.batchId());
                            }
                            // Save all updated transactions
                            // In production, this would be done in batch
                            return new BatchSettlementResult(
                                true,
                                batchResponse.batchId(),
                                batchResponse.totalTransactions(),
                                batchResponse.totalAmount(),
                                batchResponse.settlementStatus()
                            );
                        } else {
                            return new BatchSettlementResult(
                                false,
                                null,
                                0,
                                Money.zero("USD"),
                                batchResponse.rawResponse()
                            );
                        }
                    });
            });
    }

    @Override
    public CompletionStage<ReconciliationSummary> getReconciliationSummary(ReconciliationSummaryQuery query) {
        return transactionRepository.findTransactionsForReconciliation(
            query.fromDate(),
            query.toDate(),
            query.merchantId()
        ).thenApply(transactions -> {
            long totalCount = transactions.size();
            Money totalAmount = transactions.stream()
                .map(Transaction::getTotalAmount)
                .reduce(Money.zero("USD"), Money::add);
            
            long settledCount = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SETTLED)
                .count();
            long unsettledCount = totalCount - settledCount;
            
            return new ReconciliationSummary(
                query.fromDate(),
                query.toDate(),
                query.merchantId(),
                totalCount,
                totalAmount,
                settledCount,
                unsettledCount,
                Money.zero("USD"), // totalDiscrepancy would be calculated
                transactions.stream()
                    .limit(10)
                    .map(TransactionView::fromDomain)
                    .collect(Collectors.toList()),
                Instant.now()
            );
        });
    }

    private PaymentInstrument buildPaymentInstrument(ProcessPaymentCommand command) {
        return PaymentInstrument.builder()
            .method(command.paymentMethod())
            .lastFourDigits(command.lastFourDigits())
            .cardType(command.cardType())
            .token(command.token())
            .expiryMonth(command.expiryMonth())
            .expiryYear(command.expiryYear())
            .cardholderName(command.cardholderName())
            .fingerprint(command.fingerprint())
            .isTokenized(command.isTokenized())
            .build();
    }

    private PaymentInstrument buildPaymentInstrument(AuthorizePaymentCommand command) {
        return PaymentInstrument.builder()
            .method(command.paymentMethod())
            .lastFourDigits(command.lastFourDigits())
            .cardType(command.cardType())
            .token(command.token())
            .expiryMonth(command.expiryMonth())
            .expiryYear(command.expiryYear())
            .cardholderName(command.cardholderName())
            .fingerprint(command.fingerprint())
            .isTokenized(command.isTokenized())
            .build();
    }

    private String generateTransactionReference() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private TransactionResult toTransactionResult(Transaction transaction) {
        return new TransactionResult(
            transaction.getId(),
            transaction.getTransactionReference(),
            transaction.getStatus().name(),
            transaction.getAmount(),
            transaction.getProcessorTransactionId(),
            transaction.getAuthorizationCode(),
            transaction.getResponseCode(),
            transaction.getResponseMessage(),
            transaction.getErrorCode(),
            transaction.getErrorMessage(),
            transaction.getCreatedAt()
        );
    }

    /**
     * Audit service interface for recording transactions.
     */
    public interface AuditService {
        void recordTransaction(Transaction transaction);
    }

    /**
     * Event publisher for transaction events.
     */
    public interface EventPublisher {
        void publishTransactionEvent(Transaction transaction);
    }
}