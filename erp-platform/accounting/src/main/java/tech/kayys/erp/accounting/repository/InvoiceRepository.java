package tech.kayys.erp.accounting.repository;


import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Invoice aggregates.
 */
public interface InvoiceRepository extends Repository<Invoice, InvoiceId> {

    /**
     * Finds invoices by customer.
     */
    CompletionStage<List<Invoice>> findByCustomerId(CustomerId customerId);

    /**
     * Finds invoices by status.
     */
    CompletionStage<List<Invoice>> findByStatus(InvoiceStatus status);

    /**
     * Finds outstanding invoices for a customer.
     */
    default CompletionStage<List<Invoice>> findOutstandingByCustomer(CustomerId customerId) {
        return findByCustomerId(customerId)
            .thenApply(invoices -> invoices.stream()
                .filter(invoice -> invoice.getStatus().isOutstanding())
                .toList()
            );
    }

    /**
     * Finds overdue invoices.
     */
    CompletionStage<List<Invoice>> findOverdueInvoices();

    /**
     * Finds invoices by date range.
     */
    CompletionStage<List<Invoice>> findByDateRange(Instant start, Instant end);

    /**
     * Finds invoices with payments due soon (within 5 days).
     */
    CompletionStage<List<Invoice>> findInvoicesDueSoon();

    /**
     * Gets the total amount of outstanding invoices for a customer.
     */
    default CompletionStage<Money> getTotalOutstandingByCustomer(CustomerId customerId) {
        return findOutstandingByCustomer(customerId)
            .thenApply(invoices -> invoices.stream()
                .map(Invoice::getRemainingBalance)
                .reduce(Money.zero("USD"), Money::add)
            );
    }

    /**
     * Generates a unique invoice number.
     */
    CompletionStage<String> generateInvoiceNumber();
}