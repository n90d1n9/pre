
import java.util.concurrent.CompletionStage;

/**
 * Port for creating journal entries.
 * This is used internally by the Accounting context to record transactions.
 */
public interface JournalEntryPort {

    /**
     * Creates a journal entry for an invoice.
     */
    CompletionStage<JournalEntryId> createInvoiceJournalEntry(
        InvoiceId invoiceId,
        CustomerId customerId,
        Money total,
        Money tax
    );

    /**
     * Creates a journal entry for a payment.
     */
    CompletionStage<JournalEntryId> createPaymentJournalEntry(
        CustomerId customerId,
        InvoiceId invoiceId,
        Money amount,
        PaymentMethod paymentMethod
    );

    /**
     * Creates a journal entry for a refund.
     */
    CompletionStage<JournalEntryId> createRefundJournalEntry(
        CustomerId customerId,
        InvoiceId invoiceId,
        Money amount
    );
}