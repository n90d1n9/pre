

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating invoices.
 */
@UseCase("Create a new invoice")
public class CreateInvoiceHandler implements CommandHandler<CreateInvoiceCommand, InvoiceId> {

    private final InvoiceRepository invoiceRepository;
    private final CustomerPort customerPort;
    private final ProductCatalogPort productCatalogPort;

    @Inject
    public CreateInvoiceHandler(
            InvoiceRepository invoiceRepository,
            CustomerPort customerPort,
            ProductCatalogPort productCatalogPort) {
        this.invoiceRepository = invoiceRepository;
        this.customerPort = customerPort;
        this.productCatalogPort = productCatalogPort;
    }

    @Override
    public CompletionStage<InvoiceId> handle(CreateInvoiceCommand command) {
        // 1. Validate customer
        return customerPort.validateCustomer(command.customerId())
            .thenCompose(valid -> {
                if (!valid) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Customer not found: " + command.customerId())
                    );
                }

                // 2. Get customer billing details
                return customerPort.getCustomerBillingDetails(command.customerId())
                    .thenCompose(customer -> {
                        // 3. Create the invoice
                        Invoice invoice = Invoice.create(
                            command.invoiceId(),
                            CustomerId.of(command.customerId()),
                            command.invoiceNumber() != null ? command.invoiceNumber() : generateInvoiceNumber(),
                            command.dueDate()
                        );

                        // 4. Add lines
                        for (CreateInvoiceCommand.InvoiceLineCommand lineCommand : command.lines()) {
                            // Get product details if productId is provided
                            if (lineCommand.productId() != null) {
                                return productCatalogPort.getProductDetails(lineCommand.productId())
                                    .thenApply(product -> {
                                        Money unitPrice = Money.of(lineCommand.unitPrice(), command.currencyCode());
                                        Money taxAmount = Money.zero(command.currencyCode());
                                        Money discountAmount = Money.zero(command.currencyCode());

                                        Invoice.InvoiceLine line = new Invoice.InvoiceLine(
                                            lineCommand.productId(),
                                            lineCommand.description() != null ? 
                                                lineCommand.description() : product.name(),
                                            lineCommand.quantity(),
                                            unitPrice,
                                            taxAmount,
                                            discountAmount
                                        );
                                        invoice.addLine(line);
                                        return invoice;
                                    });
                            } else {
                                // Add line without product
                                Money unitPrice = Money.of(lineCommand.unitPrice(), command.currencyCode());
                                Money taxAmount = Money.zero(command.currencyCode());
                                Money discountAmount = Money.zero(command.currencyCode());

                                Invoice.InvoiceLine line = new Invoice.InvoiceLine(
                                    null,
                                    lineCommand.description(),
                                    lineCommand.quantity(),
                                    unitPrice,
                                    taxAmount,
                                    discountAmount
                                );
                                invoice.addLine(line);
                                return CompletableFuture.completedFuture(invoice);
                            }
                        }

                        // 5. Set additional fields
                        if (command.customerNotes() != null) {
                            invoice.setCustomerNotes(command.customerNotes());
                        }
                        if (command.purchaseOrderNumber() != null) {
                            invoice.setPurchaseOrderNumber(command.purchaseOrderNumber());
                        }

                        // 6. Save the invoice
                        return invoiceRepository.save(invoice)
                            .thenApply(Invoice::getId);
                    });
            });
    }

    private String generateInvoiceNumber() {
        // In a real system, this would be generated from a sequence
        return "INV-" + System.currentTimeMillis();
    }
}
