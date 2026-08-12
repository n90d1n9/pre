package tech.kayys.erp.subscription.application.port;

import tech.kayys.erp.subscription.domain.identifier.InvoiceId;
import tech.kayys.erp.subscription.domain.model.Subscription;
import tech.kayys.erp.subscription.domain.valueobject.Money;

import java.util.concurrent.CompletionStage;

/**
 * Port for generating invoices in Accounting context.
 */
public interface InvoiceGenerationPort {

    /**
     * Generates an invoice for a subscription renewal.
     */
    CompletionStage<InvoiceId> generateInvoice(Subscription subscription, Money amount);

    /**
     * Generates a pro-rated invoice for a plan change.
     */
    CompletionStage<InvoiceId> generateProRatedInvoice(
        Subscription subscription, 
        Money amount,
        String reason
    );
}