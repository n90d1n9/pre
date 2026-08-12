package tech.kayys.erp.transaction.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.transaction.domain.identifier.TransactionId;
import tech.kayys.erp.transaction.domain.valueobject.PaymentInstrument;
import tech.kayys.erp.transaction.domain.valueobject.TransactionStatus;
import tech.kayys.erp.transaction.domain.valueobject.TransactionType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Transaction aggregate root.
 * Represents a financial transaction with payment processing.
 */
public final class Transaction extends AggregateRoot<TransactionId> {
    
    private static final long serialVersionUID = 1L;
    
    private String transactionReference;
    private UUID orderId;
    private String orderNumber;
    private String customerId;
    private String customerEmail;
    private TransactionType type;
    private TransactionStatus status;
    private PaymentInstrument paymentInstrument;
    private Money amount;
    private Money taxAmount;
    private Money tipAmount;
    private Money feeAmount;
    private Money conversionRate;
    private String currencyCode;
    private String merchantId;
    private String terminalId;
    private String channelId;
    private String channelType; // POS, KIOSK, ECOM, MOBILE
    private String processorTransactionId;
    private String authorizationCode;
    private String responseCode;
    private String responseMessage;
    private String batchId;
    private Instant authorizationAt;
    private Instant settlementAt;
    private Instant completedAt;
    private List<TransactionEvent> events;
    private List<TransactionSplit> splits;
    private String refundReference;
    private int retryCount;
    private boolean isTestMode;
    private String metadataJson;
    private String errorCode;
    private String errorMessage;
    private String createdBy;

    private Transaction(TransactionId id) {
        super(id);
        this.events = new ArrayList<>();
        this.splits = new ArrayList<>();
        this.status = TransactionStatus.PENDING;
        this.retryCount = 0;
        this.isTestMode = false;
    }

    private Transaction() {
        super();
    }

    /**
     * Factory method to create a new transaction.
     */
    public static Transaction create(
            TransactionId id,
            String transactionReference,
            UUID orderId,
            String customerId,
            TransactionType type,
            Money amount,
            PaymentInstrument paymentInstrument,
            String currencyCode,
            String merchantId) {
        Transaction transaction = new Transaction(id);
        transaction.transactionReference = transactionReference;
        transaction.orderId = orderId;
        transaction.customerId = customerId;
        transaction.type = type;
        transaction.amount = amount;
        transaction.paymentInstrument = paymentInstrument;
        transaction.currencyCode = currencyCode;
        transaction.merchantId = merchantId;
        transaction.status = TransactionStatus.PENDING;
        return transaction;
    }

    /**
     * Authorizes the transaction.
     */
    public void authorize(String processorTransactionId, String authorizationCode) {
        if (status != TransactionStatus.PENDING) {
            throw new IllegalStateException("Cannot authorize transaction in status: " + status);
        }
        if (type != TransactionType.SALE && type != TransactionType.AUTHORIZATION) {
            throw new IllegalStateException("Only sale and authorization transactions can be authorized");
        }

        this.processorTransactionId = processorTransactionId;
        this.authorizationCode = authorizationCode;
        this.status = TransactionStatus.AUTHORIZED;
        this.authorizationAt = Instant.now();
        addEvent("Authorization", "Transaction authorized with code: " + authorizationCode);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Captures the transaction.
     */
    public void capture(Money captureAmount) {
        if (status != TransactionStatus.AUTHORIZED) {
            throw new IllegalStateException("Cannot capture transaction in status: " + status);
        }
        if (type != TransactionType.SALE && type != TransactionType.CAPTURE) {
            throw new IllegalStateException("Only sale and capture transactions can be captured");
        }
        if (captureAmount.isGreaterThan(amount)) {
            throw new IllegalArgumentException("Capture amount exceeds transaction amount");
        }

        this.amount = captureAmount;
        this.status = TransactionStatus.CAPTURED;
        addEvent("Capture", "Transaction captured for amount: " + captureAmount);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Settles the transaction.
     */
    public void settle(String batchId) {
        if (status != TransactionStatus.CAPTURED && status != TransactionStatus.AUTHORIZED) {
            throw new IllegalStateException("Cannot settle transaction in status: " + status);
        }

        this.batchId = batchId;
        this.status = TransactionStatus.SETTLED;
        this.settlementAt = Instant.now();
        addEvent("Settlement", "Transaction settled in batch: " + batchId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes the transaction.
     */
    public void complete() {
        if (status != TransactionStatus.SETTLED && status != TransactionStatus.AUTHORIZED) {
            throw new IllegalStateException("Cannot complete transaction in status: " + status);
        }

        this.status = TransactionStatus.COMPLETED;
        this.completedAt = Instant.now();
        addEvent("Completion", "Transaction completed successfully");
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Processes a refund.
     */
    public Transaction refund(Money refundAmount, String reason) {
        if (status != TransactionStatus.COMPLETED && status != TransactionStatus.SETTLED && 
            status != TransactionStatus.CAPTURED) {
            throw new IllegalStateException("Cannot refund transaction in status: " + status);
        }
        if (refundAmount.isGreaterThan(amount)) {
            throw new IllegalArgumentException("Refund amount exceeds transaction amount");
        }

        // Create refund transaction
        Transaction refund = Transaction.create(
            TransactionId.generate(),
            "REF-" + this.transactionReference,
            this.orderId,
            this.customerId,
            TransactionType.REFUND,
            refundAmount,
            this.paymentInstrument,
            this.currencyCode,
            this.merchantId
        );
        refund.refundReference = this.id.toString();
        refund.status = TransactionStatus.REFUNDED;
        refund.addEvent("Refund", "Refund processed: " + reason);

        // Update original transaction
        this.status = refundAmount.equals(amount) ? 
            TransactionStatus.REFUNDED : TransactionStatus.PARTIALLY_REFUNDED;
        this.addEvent("Refund", "Partial refund processed for: " + refundAmount);

        setUpdatedAt(Instant.now());
        incrementVersion();

        return refund;
    }

    /**
     * Voids the transaction.
     */
    public void voidTransaction(String reason) {
        if (status == TransactionStatus.COMPLETED || status == TransactionStatus.SETTLED) {
            throw new IllegalStateException("Cannot void settled or completed transaction");
        }
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot void transaction in terminal status: " + status);
        }

        this.status = TransactionStatus.CANCELLED;
        addEvent("Void", "Transaction voided: " + reason);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a split to the transaction.
     */
    public void addSplit(TransactionSplit split) {
        if (status != TransactionStatus.PENDING) {
            throw new IllegalStateException("Cannot add splits to transaction in status: " + status);
        }
        splits.add(split);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records a failed attempt.
     */
    public void recordFailure(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.retryCount++;
        this.status = TransactionStatus.FAILED;
        addEvent("Failure", "Transaction failed: " + errorMessage);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Retries the transaction.
     */
    public void retry() {
        if (status != TransactionStatus.FAILED && status != TransactionStatus.DECLINED) {
            throw new IllegalStateException("Cannot retry transaction in status: " + status);
        }
        if (retryCount >= 3) {
            throw new IllegalStateException("Maximum retry attempts exceeded");
        }

        this.status = TransactionStatus.PENDING;
        this.errorCode = null;
        this.errorMessage = null;
        addEvent("Retry", "Transaction retry attempt #" + (retryCount + 1));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the total amount including tax and tip.
     */
    public Money getTotalAmount() {
        Money total = amount;
        if (taxAmount != null) {
            total = total.add(taxAmount);
        }
        if (tipAmount != null) {
            total = total.add(tipAmount);
        }
        return total;
    }

    /**
     * Gets the net amount after fees.
     */
    public Money getNetAmount() {
        Money total = getTotalAmount();
        if (feeAmount != null) {
            total = total.subtract(feeAmount);
        }
        return total;
    }

    private void addEvent(String action, String details) {
        TransactionEvent event = new TransactionEvent(
            UUID.randomUUID().toString(),
            action,
            details,
            Instant.now()
        );
        events.add(event);
    }

    // Getters
    public String getTransactionReference() { return transactionReference; }
    public UUID getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public String getCustomerId() { return customerId; }
    public String getCustomerEmail() { return customerEmail; }
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public PaymentInstrument getPaymentInstrument() { return paymentInstrument; }
    public Money getAmount() { return amount; }
    public Money getTaxAmount() { return taxAmount; }
    public Money getTipAmount() { return tipAmount; }
    public Money getFeeAmount() { return feeAmount; }
    public Money getConversionRate() { return conversionRate; }
    public String getCurrencyCode() { return currencyCode; }
    public String getMerchantId() { return merchantId; }
    public String getTerminalId() { return terminalId; }
    public String getChannelId() { return channelId; }
    public String getChannelType() { return channelType; }
    public String getProcessorTransactionId() { return processorTransactionId; }
    public String getAuthorizationCode() { return authorizationCode; }
    public String getResponseCode() { return responseCode; }
    public String getResponseMessage() { return responseMessage; }
    public String getBatchId() { return batchId; }
    public Instant getAuthorizationAt() { return authorizationAt; }
    public Instant getSettlementAt() { return settlementAt; }
    public Instant getCompletedAt() { return completedAt; }
    public List<TransactionEvent> getEvents() { return Collections.unmodifiableList(events); }
    public List<TransactionSplit> getSplits() { return Collections.unmodifiableList(splits); }
    public String getRefundReference() { return refundReference; }
    public int getRetryCount() { return retryCount; }
    public boolean isTestMode() { return isTestMode; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public String getCreatedBy() { return createdBy; }

    // Setters
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTaxAmount(Money taxAmount) {
        this.taxAmount = taxAmount;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTipAmount(Money tipAmount) {
        this.tipAmount = tipAmount;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setFeeAmount(Money feeAmount) {
        this.feeAmount = feeAmount;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setConversionRate(Money conversionRate) {
        this.conversionRate = conversionRate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTestMode(boolean testMode) {
        isTestMode = testMode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + getId() +
                ", reference='" + transactionReference + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", amount=" + amount +
                ", customerId='" + customerId + '\'' +
                '}';
    }

    /**
     * Transaction event record.
     */
    public static final class TransactionEvent implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String eventId;
        private final String action;
        private final String details;
        private final Instant timestamp;

        public TransactionEvent(String eventId, String action, String details, Instant timestamp) {
            this.eventId = eventId;
            this.action = action;
            this.details = details;
            this.timestamp = timestamp;
            validate();
        }

        @Override
        public void validate() {
            if (eventId == null || eventId.trim().isEmpty()) {
                throw new IllegalArgumentException("Event ID cannot be empty");
            }
            if (action == null || action.trim().isEmpty()) {
                throw new IllegalArgumentException("Action cannot be empty");
            }
        }

        public String getEventId() { return eventId; }
        public String getAction() { return action; }
        public String getDetails() { return details; }
        public Instant getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return "TransactionEvent{" +
                    "action='" + action + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    /**
     * Transaction split for multiple payment methods.
     */
    public static final class TransactionSplit implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String splitId;
        private final PaymentInstrument instrument;
        private final Money amount;
        private final String splitType; // PRIMARY, SECONDARY
        private final TransactionStatus status;

        public TransactionSplit(
                String splitId,
                PaymentInstrument instrument,
                Money amount,
                String splitType,
                TransactionStatus status) {
            this.splitId = splitId;
            this.instrument = instrument;
            this.amount = amount;
            this.splitType = splitType;
            this.status = status;
            validate();
        }

        @Override
        public void validate() {
            if (splitId == null || splitId.trim().isEmpty()) {
                throw new IllegalArgumentException("Split ID cannot be empty");
            }
            if (instrument == null) {
                throw new IllegalArgumentException("Payment instrument cannot be null");
            }
            if (amount == null || amount.isZero()) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            if (splitType == null || splitType.trim().isEmpty()) {
                throw new IllegalArgumentException("Split type cannot be empty");
            }
        }

        public String getSplitId() { return splitId; }
        public PaymentInstrument getInstrument() { return instrument; }
        public Money getAmount() { return amount; }
        public String getSplitType() { return splitType; }
        public TransactionStatus getStatus() { return status; }

        @Override
        public String toString() {
            return "TransactionSplit{" +
                    "instrument=" + instrument.getMaskedDisplay() +
                    ", amount=" + amount +
                    ", splitType='" + splitType + '\'' +
                    '}';
        }

        public static TransactionSplit create(
                PaymentInstrument instrument,
                Money amount,
                String splitType) {
            return new TransactionSplit(
                UUID.randomUUID().toString(),
                instrument,
                amount,
                splitType,
                TransactionStatus.PENDING
            );
        }
    }
}