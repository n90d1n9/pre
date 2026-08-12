
import java.util.concurrent.CompletionStage;

/**
 * Public API for accounting commands.
 */
public interface AccountingCommandService {

    /**
     * Creates a new invoice.
     */
    CompletionStage<InvoiceId> createInvoice(CreateInvoiceCommand command);

    /**
     * Records a payment against an invoice.
     */
    CompletionStage<InvoiceId> recordPayment(RecordPaymentCommand command);

    /**
     * Posts a journal entry.
     */
    CompletionStage<JournalEntryId> postJournalEntry(PostJournalEntryCommand command);

    /**
     * Processes automated invoice generation for subscriptions.
     */
    CompletionStage<Integer> processRecurringInvoices();
}