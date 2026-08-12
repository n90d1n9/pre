package tech.kayys.erp.accounting.application.internal;

import tech.kayys.erp.accounting.application.port.PdfGeneratorPort;
import tech.kayys.erp.accounting.domain.model.Invoice;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * PDF generator for invoices.
 * In a real implementation, this would use a PDF library like iText or JasperReports.
 */
@ApplicationScoped
public class InvoicePdfGenerator implements PdfGeneratorPort {

    @Override
    public CompletionStage<byte[]> generateInvoicePdf(Invoice invoice, String templateId) {
        // This is a placeholder - in production, use actual PDF generation
        // with proper formatting, fonts, logos, etc.
        return CompletableFuture.supplyAsync(() -> {
            // Simulated PDF generation
            String pdfContent = generatePdfContent(invoice);
            return pdfContent.getBytes();
        });
    }

    private String generatePdfContent(Invoice invoice) {
        StringBuilder sb = new StringBuilder();
        sb.append("INVOICE\n");
        sb.append("=".repeat(50)).append("\n");
        sb.append("Invoice Number: ").append(invoice.getInvoiceNumber()).append("\n");
        sb.append("Invoice Date: ").append(invoice.getInvoiceDate()).append("\n");
        sb.append("Due Date: ").append(invoice.getDueDate()).append("\n");
        sb.append("\n");
        
        sb.append("Items:\n");
        for (Invoice.InvoiceLine line : invoice.getLines()) {
            sb.append("  - ")
              .append(line.getDescription())
              .append(" x")
              .append(line.getQuantity())
              .append(": ")
              .append(line.getLineTotal().getAmount())
              .append(" ")
              .append(line.getLineTotal().getCurrency().getCurrencyCode())
              .append("\n");
        }
        
        sb.append("\n");
        sb.append("Subtotal: ").append(invoice.getSubtotal().getAmount())
          .append(" ").append(invoice.getSubtotal().getCurrency().getCurrencyCode()).append("\n");
        sb.append("Tax: ").append(invoice.getTaxTotal().getAmount())
          .append(" ").append(invoice.getTaxTotal().getCurrency().getCurrencyCode()).append("\n");
        sb.append("Total: ").append(invoice.getTotal().getAmount())
          .append(" ").append(invoice.getTotal().getCurrency().getCurrencyCode()).append("\n");
        
        return sb.toString();
    }
}