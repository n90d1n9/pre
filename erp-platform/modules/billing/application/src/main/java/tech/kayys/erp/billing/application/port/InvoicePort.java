package tech.kayys.erp.billing.application.port;

import tech.kayys.erp.billing.domain.valueobject.Money;

import java.util.concurrent.CompletionStage;

/**
 * Port for invoice generation.
 */
public interface InvoicePort {

    /**
     * Generates an invoice for billing.
     */
    CompletionStage<String> generateInvoice(
        String customerId,
        Money amount,
        String currencyCode,
        String description
    );

    /**
     * Gets invoice details.
     */
    CompletionStage<InvoiceDetails> getInvoice(String invoiceId);

    record InvoiceDetails(
        String invoiceId,
        String customerId,
        Money amount,
        String currencyCode,
        String status,
        Instant createdAt
    ) {}
}