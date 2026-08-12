package tech.kayys.erp.accounting.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.accounting.application.api.command.SendInvoiceCommand;
import tech.kayys.erp.accounting.application.port.EmailPort;
import tech.kayys.erp.accounting.application.port.CustomerPort;
import tech.kayys.erp.accounting.application.port.PdfGeneratorPort;
import tech.kayys.erp.accounting.domain.identifier.InvoiceId;
import tech.kayys.erp.accounting.domain.model.Invoice;
import tech.kayys.erp.accounting.domain.repository.InvoiceRepository;
import tech.kayys.erp.accounting.domain.valueobject.InvoiceStatus;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for sending invoices.
 */
@UseCase("Send an invoice to the customer")
public class SendInvoiceHandler implements CommandHandler<SendInvoiceCommand, InvoiceId> {

    private final InvoiceRepository invoiceRepository;
    private final CustomerPort customerPort;
    private final EmailPort emailPort;
    private final PdfGeneratorPort pdfGeneratorPort;

    @Inject
    public SendInvoiceHandler(
            InvoiceRepository invoiceRepository,
            CustomerPort customerPort,
            EmailPort emailPort,
            PdfGeneratorPort pdfGeneratorPort) {
        this.invoiceRepository = invoiceRepository;
        this.customerPort = customerPort;
        this.emailPort = emailPort;
        this.pdfGeneratorPort = pdfGeneratorPort;
    }

    @Override
    public CompletionStage<InvoiceId> handle(SendInvoiceCommand command) {
        return invoiceRepository.findById(command.invoiceId())
            .thenCompose(invoiceOpt -> {
                if (invoiceOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Invoice not found: " + command.invoiceId())
                    );
                }

                Invoice invoice = invoiceOpt.get();
                
                // Validate invoice can be sent
                if (invoice.getStatus() != InvoiceStatus.DRAFT) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Invoice cannot be sent in status: " + invoice.getStatus())
                    );
                }

                // Get customer details
                return customerPort.getCustomerBillingDetails(
                        invoice.getCustomerId().getValue()
                    )
                    .thenCompose(customer -> {
                        // Generate PDF
                        return pdfGeneratorPort.generateInvoicePdf(invoice, command.templateId())
                            .thenCompose(pdfBytes -> {
                                // Send email with PDF attachment
                                String subject = command.emailSubject() != null ? 
                                    command.emailSubject() : 
                                    "Invoice " + invoice.getInvoiceNumber();
                                
                                String body = command.emailBody() != null ?
                                    command.emailBody() :
                                    generateDefaultEmailBody(invoice, customer);

                                return emailPort.sendInvoiceEmail(
                                    customer.email(),
                                    subject,
                                    body,
                                    pdfBytes,
                                    "Invoice-" + invoice.getInvoiceNumber() + ".pdf"
                                ).thenCompose(sent -> {
                                    // Update invoice status
                                    invoice.send();
                                    return invoiceRepository.save(invoice)
                                        .thenApply(Invoice::getId);
                                });
                            });
                    });
            });
    }

    private String generateDefaultEmailBody(Invoice invoice, CustomerPort.CustomerBillingDetails customer) {
        return String.format("""
            Dear %s,
            
            Please find attached invoice %s for your records.
            
            Invoice Details:
            - Invoice Number: %s
            - Invoice Date: %s
            - Due Date: %s
            - Total Amount: %s %s
            
            Thank you for your business.
            
            Best regards,
            Kayys ERP
            """,
            customer.name(),
            invoice.getInvoiceNumber(),
            invoice.getInvoiceNumber(),
            invoice.getInvoiceDate().toString(),
            invoice.getDueDate().toString(),
            invoice.getTotal().getAmount().toPlainString(),
            invoice.getTotal().getCurrency().getCurrencyCode()
        );
    }
}