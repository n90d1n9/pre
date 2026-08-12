package tech.kayys.erp.transaction.domain.events;

import tech.kayys.erp.transaction.domain.model.Transaction;

import java.time.Instant;

/**
 * Domain events for transaction state changes.
 * These events are consumed by the Accounting context.
 */
public sealed interface TransactionEvent permits 
    PaymentAuthorizedEvent,
    PaymentCapturedEvent,
    PaymentSettledEvent,
    PaymentCompletedEvent,
    RefundProcessedEvent,
    ChargebackReceivedEvent,
    TransactionFailedEvent {

    String getTransactionId();
    String getEventType();
    Instant getOccurredAt();
}

/**
 * Event fired when a payment is authorized.
 */
public final record PaymentAuthorizedEvent(
    String transactionId,
    String orderId,
    String customerId,
    String amount,
    String currencyCode,
    String authorizationCode,
    Instant occurredAt
) implements TransactionEvent {
    
    public PaymentAuthorizedEvent(Transaction transaction) {
        this(
            transaction.getId().toString(),
            transaction.getOrderId().toString(),
            transaction.getCustomerId(),
            transaction.getAmount().getAmount().toPlainString(),
            transaction.getCurrencyCode(),
            transaction.getAuthorizationCode(),
            Instant.now()
        );
    }

    @Override
    public String getEventType() {
        return "PAYMENT_AUTHORIZED";
    }
}

/**
 * Event fired when a payment is captured.
 */
public final record PaymentCapturedEvent(
    String transactionId,
    String orderId,
    String amount,
    String currencyCode,
    String captureId,
    Instant occurredAt
) implements TransactionEvent {
    
    public PaymentCapturedEvent(Transaction transaction) {
        this(
            transaction.getId().toString(),
            transaction.getOrderId().toString(),
            transaction.getAmount().getAmount().toPlainString(),
            transaction.getCurrencyCode(),
            transaction.getProcessorTransactionId(),
            Instant.now()
        );
    }

    @Override
    public String getEventType() {
        return "PAYMENT_CAPTURED";
    }
}

/**
 * Event fired when a payment is settled.
 */
public final record PaymentSettledEvent(
    String transactionId,
    String orderId,
    String batchId,
    String amount,
    String currencyCode,
    String settlementDate,
    Instant occurredAt
) implements TransactionEvent {
    
    public PaymentSettledEvent(Transaction transaction, String batchId) {
        this(
            transaction.getId().toString(),
            transaction.getOrderId().toString(),
            batchId,
            transaction.getAmount().getAmount().toPlainString(),
            transaction.getCurrencyCode(),
            transaction.getSettlementAt().toString(),
            Instant.now()
        );
    }

    @Override
    public String getEventType() {
        return "PAYMENT_SETTLED";
    }
}

/**
 * Event fired when a refund is processed.
 */
public final record RefundProcessedEvent(
    String transactionId,
    String refundTransactionId,
    String originalOrderId,
    String amount,
    String currencyCode,
    String reason,
    Instant occurredAt
) implements TransactionEvent {
    
    public RefundProcessedEvent(Transaction originalTransaction, Transaction refundTransaction) {
        this(
            originalTransaction.getId().toString(),
            refundTransaction.getId().toString(),
            originalTransaction.getOrderId().toString(),
            refundTransaction.getAmount().getAmount().toPlainString(),
            refundTransaction.getCurrencyCode(),
            "Customer refund",
            Instant.now()
        );
    }

    @Override
    public String getEventType() {
        return "REFUND_PROCESSED";
    }
}

/**
 * Event fired when a chargeback is received.
 */
public final record ChargebackReceivedEvent(
    String transactionId,
    String chargebackId,
    String orderId,
    String amount,
    String currencyCode,
    String reason,
    String disputeStatus,
    Instant occurredAt
) implements TransactionEvent {
    
    public ChargebackReceivedEvent(
            String transactionId,
            String chargebackId,
            String orderId,
            String amount,
            String currencyCode,
            String reason) {
        this(
            transactionId,
            chargebackId,
            orderId,
            amount,
            currencyCode,
            reason,
            "OPEN",
            Instant.now()
        );
    }

    @Override
    public String getEventType() {
        return "CHARGEBACK_RECEIVED";
    }
}

/**
 * Event fired when a payment is completed.
 */
public final record PaymentCompletedEvent(
    String transactionId,
    String orderId,
    String amount,
    String currencyCode,
    String status,
    Instant occurredAt
) implements TransactionEvent {
    
    public PaymentCompletedEvent(Transaction transaction) {
        this(
            transaction.getId().toString(),
            transaction.getOrderId().toString(),
            transaction.getAmount().getAmount().toPlainString(),
            transaction.getCurrencyCode(),
            transaction.getStatus().name(),
            Instant.now()
        );
    }

    @Override
    public String getEventType() {
        return "PAYMENT_COMPLETED";
    }
}

/**
 * Event fired when a transaction fails.
 */
public final record TransactionFailedEvent(
    String transactionId,
    String orderId,
    String errorCode,
    String errorMessage,
    Instant occurredAt
) implements TransactionEvent {
    
    public TransactionFailedEvent(Transaction transaction) {
        this(
            transaction.getId().toString(),
            transaction.getOrderId().toString(),
            transaction.getErrorCode(),
            transaction.getErrorMessage(),
            Instant.now()
        );
    }

    @Override
    public String getEventType() {
        return "TRANSACTION_FAILED";
    }
}