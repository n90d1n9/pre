package tech.kayys.erp.accounting.application.port;

import tech.kayys.erp.accounting.domain.model.Invoice;

import java.util.concurrent.CompletionStage;

/**
 * Port for generating PDF documents.
 */
public interface PdfGeneratorPort {

    /**
     * Generates a PDF for an invoice.
     */
    CompletionStage<byte[]> generateInvoicePdf(Invoice invoice, String templateId);

    /**
     * Generates a PDF for an invoice with a specific language.
     */
    default CompletionStage<byte[]> generateInvoicePdf(Invoice invoice, String templateId, String language) {
        return generateInvoicePdf(invoice, templateId);
    }
}