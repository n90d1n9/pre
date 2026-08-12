package tech.kayys.erp.transaction.application.port;

import tech.kayys.erp.transaction.domain.model.Transaction;
import tech.kayys.erp.transaction.domain.valueobject.PaymentInstrument;

import java.util.concurrent.CompletionStage;

/**
 * Port for payment gateway integration.
 * Abstracts external payment providers (Stripe, Adyen, Square, etc.)
 */
public interface PaymentGatewayPort {

    /**
     * Authorizes a payment.
     */
    CompletionStage<GatewayResponse> authorize(Transaction transaction);

    /**
     * Captures an authorized payment.
     */
    CompletionStage<GatewayResponse> capture(Transaction transaction);

    /**
     * Processes a refund.
     */
    CompletionStage<GatewayResponse> refund(Transaction transaction);

    /**
     * Voids a transaction.
     */
    CompletionStage<GatewayResponse> voidTransaction(Transaction transaction);

    /**
     * Processes a sale (authorize + capture).
     */
    CompletionStage<GatewayResponse> sale(Transaction transaction);

    /**
     * Processes a tokenized payment.
     */
    CompletionStage<GatewayResponse> tokenizedPayment(Transaction transaction, String token);

    /**
     * Gets transaction status from the gateway.
     */
    CompletionStage<GatewayResponse> getStatus(String processorTransactionId);

    /**
     * Processes a batch settlement.
     */
    CompletionStage<BatchSettlementResponse> settleBatch(List<Transaction> transactions);

    /**
     * Gateway response wrapper.
     */
    record GatewayResponse(
        boolean success,
        String processorTransactionId,
        String authorizationCode,
        String responseCode,
        String responseMessage,
        String status,
        String rawResponse,
        Instant processedAt
    ) {}

    /**
     * Batch settlement response.
     */
    record BatchSettlementResponse(
        boolean success,
        String batchId,
        int totalTransactions,
        Money totalAmount,
        String settlementStatus,
        String rawResponse
    ) {}
}