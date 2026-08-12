
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for recording payments.
 */
@UseCase("Record a payment against an invoice")
public class RecordPaymentHandler implements CommandHandler<RecordPaymentCommand, InvoiceId> {

    private final InvoiceRepository invoiceRepository;
    private final JournalEntryPort journalEntryPort;

    @Inject
    public RecordPaymentHandler(InvoiceRepository invoiceRepository, JournalEntryPort journalEntryPort) {
        this.invoiceRepository = invoiceRepository;
        this.journalEntryPort = journalEntryPort;
    }

    @Override
    public CompletionStage<InvoiceId> handle(RecordPaymentCommand command) {
        return invoiceRepository.findById(command.invoiceId())
            .thenCompose(invoiceOpt -> {
                if (invoiceOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Invoice not found: " + command.invoiceId())
                    );
                }

                Invoice invoice = invoiceOpt.get();
                Money paymentAmount = Money.of(
                    new BigDecimal(command.amount()),
                    command.currencyCode()
                );

                // Record the payment
                invoice.recordPayment(
                    paymentAmount,
                    command.paymentMethod(),
                    command.reference()
                );

                // Create journal entry for the payment
                return journalEntryPort.createPaymentJournalEntry(
                    invoice.getCustomerId(),
                    invoice.getId(),
                    paymentAmount,
                    command.paymentMethod()
                ).thenCompose(journalEntryId -> {
                    // Save the updated invoice
                    return invoiceRepository.save(invoice)
                        .thenApply(Invoice::getId);
                });
            });
    }
}