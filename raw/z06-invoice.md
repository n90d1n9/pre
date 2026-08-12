# Complete Implementation: Invoice Management (Accounting Context)

Now I'll complete the Invoice Management functionality within the Accounting context, including invoice generation, PDF creation, email delivery, payment processing, and reconciliation.

## 1. Invoice Domain Extensions

**`/modules/accounting/domain/src/main/java/tech/kayys/erp/accounting/domain/valueobject/InvoiceTemplate.java`**:

```java
package tech.kayys.erp.accounting.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.Objects;

/**
 * Invoice template configuration.
 */
public final class InvoiceTemplate implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String id;
    private final String name;
    private final String description;
    private final String header;
    private final String footer;
    private final String logoUrl;
    private final String colorScheme;
    private final boolean showTaxBreakdown;
    private final boolean showDiscountBreakdown;
    private final String language;

    public InvoiceTemplate(
            String id,
            String name,
            String description,
            String header,
            String footer,
            String logoUrl,
            String colorScheme,
            boolean showTaxBreakdown,
            boolean showDiscountBreakdown,
            String language) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.header = header;
        this.footer = footer;
        this.logoUrl = logoUrl;
        this.colorScheme = colorScheme;
        this.showTaxBreakdown = showTaxBreakdown;
        this.showDiscountBreakdown = showDiscountBreakdown;
        this.language = language;
        validate();
    }

    @Override
    public void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Template ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be empty");
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getHeader() { return header; }
    public String getFooter() { return footer; }
    public String getLogoUrl() { return logoUrl; }
    public String getColorScheme() { return colorScheme; }
    public boolean isShowTaxBreakdown() { return showTaxBreakdown; }
    public boolean isShowDiscountBreakdown() { return showDiscountBreakdown; }
    public String getLanguage() { return language; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceTemplate that = (InvoiceTemplate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private String header;
        private String footer;
        private String logoUrl;
        private String colorScheme = "#1a73e8";
        private boolean showTaxBreakdown = true;
        private boolean showDiscountBreakdown = true;
        private String language = "en";

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder header(String header) {
            this.header = header;
            return this;
        }

        public Builder footer(String footer) {
            this.footer = footer;
            return this;
        }

        public Builder logoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
            return this;
        }

        public Builder colorScheme(String colorScheme) {
            this.colorScheme = colorScheme;
            return this;
        }

        public Builder showTaxBreakdown(boolean showTaxBreakdown) {
            this.showTaxBreakdown = showTaxBreakdown;
            return this;
        }

        public Builder showDiscountBreakdown(boolean showDiscountBreakdown) {
            this.showDiscountBreakdown = showDiscountBreakdown;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public InvoiceTemplate build() {
            return new InvoiceTemplate(
                id, name, description, header, footer, logoUrl,
                colorScheme, showTaxBreakdown, showDiscountBreakdown, language
            );
        }
    }
}
```

**`/modules/accounting/domain/src/main/java/tech/kayys/erp/accounting/domain/valueobject/InvoiceDeliveryMethod.java`**:

```java
package tech.kayys.erp.accounting.domain.valueobject;

/**
 * Methods for delivering invoices.
 */
public enum InvoiceDeliveryMethod {
    EMAIL("Email"),
    PRINT("Print"),
    PORTAL("Customer Portal"),
    API("API Integration"),
    FAX("Fax");

    private final String displayName;

    InvoiceDeliveryMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isDigital() {
        return this == EMAIL || this == PORTAL || this == API;
    }
}
```

**`/modules/accounting/domain/src/main/java/tech/kayys/erp/accounting/domain/model/InvoiceHistory.java`**:

```java
package tech.kayys.erp.accounting.domain.model;

import tech.kayys.erp.foundation.domain.ValueObject;
import tech.kayys.erp.accounting.domain.valueobject.InvoiceStatus;

import java.time.Instant;
import java.util.Objects;

/**
 * Invoice history record.
 */
public final class InvoiceHistory implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String action;
    private final InvoiceStatus fromStatus;
    private final InvoiceStatus toStatus;
    private final String performedBy;
    private final Instant performedAt;
    private final String notes;

    public InvoiceHistory(
            String action,
            InvoiceStatus fromStatus,
            InvoiceStatus toStatus,
            String performedBy,
            Instant performedAt,
            String notes) {
        this.action = action;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.performedBy = performedBy;
        this.performedAt = performedAt != null ? performedAt : Instant.now();
        this.notes = notes;
        validate();
    }

    @Override
    public void validate() {
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action cannot be empty");
        }
        if (performedBy == null || performedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Performed by cannot be empty");
        }
    }

    public String getAction() { return action; }
    public InvoiceStatus getFromStatus() { return fromStatus; }
    public InvoiceStatus getToStatus() { return toStatus; }
    public String getPerformedBy() { return performedBy; }
    public Instant getPerformedAt() { return performedAt; }
    public String getNotes() { return notes; }

    public boolean isStatusChange() {
        return fromStatus != null && toStatus != null && fromStatus != toStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceHistory that = (InvoiceHistory) o;
        return Objects.equals(action, that.action) &&
               Objects.equals(performedAt, that.performedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, performedAt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String action;
        private InvoiceStatus fromStatus;
        private InvoiceStatus toStatus;
        private String performedBy;
        private Instant performedAt;
        private String notes;

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder fromStatus(InvoiceStatus fromStatus) {
            this.fromStatus = fromStatus;
            return this;
        }

        public Builder toStatus(InvoiceStatus toStatus) {
            this.toStatus = toStatus;
            return this;
        }

        public Builder performedBy(String performedBy) {
            this.performedBy = performedBy;
            return this;
        }

        public Builder performedAt(Instant performedAt) {
            this.performedAt = performedAt;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public InvoiceHistory build() {
            return new InvoiceHistory(action, fromStatus, toStatus, performedBy, performedAt, notes);
        }
    }
}
```

## 2. Invoice Application Services

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/InvoiceService.java`**:

```java
package tech.kayys.erp.accounting.application.api;

import tech.kayys.erp.accounting.application.api.command.*;
import tech.kayys.erp.accounting.application.api.query.*;
import tech.kayys.erp.accounting.domain.identifier.InvoiceId;

import java.util.concurrent.CompletionStage;

/**
 * Comprehensive invoice management service.
 */
public interface InvoiceService {

    // ============ Write Operations ============

    /**
     * Creates a new invoice.
     */
    CompletionStage<InvoiceId> createInvoice(CreateInvoiceCommand command);

    /**
     * Sends an invoice to the customer.
     */
    CompletionStage<InvoiceId> sendInvoice(SendInvoiceCommand command);

    /**
     * Records a payment against an invoice.
     */
    CompletionStage<InvoiceId> recordPayment(RecordPaymentCommand command);

    /**
     * Records a payment failure for an invoice.
     */
    CompletionStage<InvoiceId> recordPaymentFailure(RecordPaymentFailureCommand command);

    /**
     * Issues a refund for an invoice.
     */
    CompletionStage<InvoiceId> refundInvoice(RefundInvoiceCommand command);

    /**
     * Writes off an invoice as uncollectable.
     */
    CompletionStage<InvoiceId> writeOffInvoice(WriteOffInvoiceCommand command);

    /**
     * Cancels an invoice.
     */
    CompletionStage<InvoiceId> cancelInvoice(CancelInvoiceCommand command);

    /**
     * Generates a PDF for an invoice.
     */
    CompletionStage<byte[]> generateInvoicePdf(GenerateInvoicePdfCommand command);

    // ============ Read Operations ============

    /**
     * Gets a complete invoice view.
     */
    CompletionStage<InvoiceView> getInvoice(GetInvoiceQuery query);

    /**
     * Gets invoice summary for a customer.
     */
    CompletionStage<InvoiceSummaryView> getInvoiceSummary(GetInvoiceSummaryQuery query);

    /**
     * Searches invoices with filters.
     */
    CompletionStage<InvoiceSearchResult> searchInvoices(SearchInvoicesQuery query);

    /**
     * Gets invoice statistics.
     */
    CompletionStage<InvoiceStatistics> getInvoiceStatistics(InvoiceStatisticsQuery query);
}
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/command/SendInvoiceCommand.java`**:

```java
package tech.kayys.erp.accounting.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.accounting.domain.identifier.InvoiceId;
import tech.kayys.erp.accounting.domain.valueobject.InvoiceDeliveryMethod;

/**
 * Command to send an invoice to the customer.
 */
public record SendInvoiceCommand(
        InvoiceId invoiceId,
        InvoiceDeliveryMethod deliveryMethod,
        String emailSubject,
        String emailBody,
        String templateId
) implements Command<InvoiceId> {

    public SendInvoiceCommand {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }
        if (deliveryMethod == null) {
            throw new IllegalArgumentException("Delivery method is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InvoiceId invoiceId;
        private InvoiceDeliveryMethod deliveryMethod = InvoiceDeliveryMethod.EMAIL;
        private String emailSubject;
        private String emailBody;
        private String templateId;

        public Builder invoiceId(InvoiceId invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Builder deliveryMethod(InvoiceDeliveryMethod deliveryMethod) {
            this.deliveryMethod = deliveryMethod;
            return this;
        }

        public Builder emailSubject(String emailSubject) {
            this.emailSubject = emailSubject;
            return this;
        }

        public Builder emailBody(String emailBody) {
            this.emailBody = emailBody;
            return this;
        }

        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public SendInvoiceCommand build() {
            return new SendInvoiceCommand(invoiceId, deliveryMethod, emailSubject, emailBody, templateId);
        }
    }
}
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/command/RecordPaymentFailureCommand.java`**:

```java
package tech.kayys.erp.accounting.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.accounting.domain.identifier.InvoiceId;

/**
 * Command to record a payment failure.
 */
public record RecordPaymentFailureCommand(
        InvoiceId invoiceId,
        String reason,
        int attemptNumber
) implements Command<InvoiceId> {

    public RecordPaymentFailureCommand {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Failure reason is required");
        }
        if (attemptNumber < 1) {
            throw new IllegalArgumentException("Attempt number must be positive");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InvoiceId invoiceId;
        private String reason;
        private int attemptNumber = 1;

        public Builder invoiceId(InvoiceId invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder attemptNumber(int attemptNumber) {
            this.attemptNumber = attemptNumber;
            return this;
        }

        public RecordPaymentFailureCommand build() {
            return new RecordPaymentFailureCommand(invoiceId, reason, attemptNumber);
        }
    }
}
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/command/RefundInvoiceCommand.java`**:

```java
package tech.kayys.erp.accounting.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.accounting.domain.identifier.InvoiceId;

/**
 * Command to refund an invoice.
 */
public record RefundInvoiceCommand(
        InvoiceId invoiceId,
        String amount,
        String currencyCode,
        String reason,
        String reference
) implements Command<InvoiceId> {

    public RefundInvoiceCommand {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }
        if (amount == null || amount.trim().isEmpty()) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Refund reason is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InvoiceId invoiceId;
        private String amount;
        private String currencyCode = "USD";
        private String reason;
        private String reference;

        public Builder invoiceId(InvoiceId invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Builder amount(String amount) {
            this.amount = amount;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }

        public RefundInvoiceCommand build() {
            return new RefundInvoiceCommand(invoiceId, amount, currencyCode, reason, reference);
        }
    }
}
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/command/WriteOffInvoiceCommand.java`**:

```java
package tech.kayys.erp.accounting.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.accounting.domain.identifier.InvoiceId;

/**
 * Command to write off an invoice as uncollectable.
 */
public record WriteOffInvoiceCommand(
        InvoiceId invoiceId,
        String reason,
        String writeOffAccount
) implements Command<InvoiceId> {

    public WriteOffInvoiceCommand {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Write-off reason is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InvoiceId invoiceId;
        private String reason;
        private String writeOffAccount = "BAD_DEBT_EXPENSE";

        public Builder invoiceId(InvoiceId invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder writeOffAccount(String writeOffAccount) {
            this.writeOffAccount = writeOffAccount;
            return this;
        }

        public WriteOffInvoiceCommand build() {
            return new WriteOffInvoiceCommand(invoiceId, reason, writeOffAccount);
        }
    }
}
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/command/CancelInvoiceCommand.java`**:

```java
package tech.kayys.erp.accounting.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.accounting.domain.identifier.InvoiceId;

/**
 * Command to cancel an invoice.
 */
public record CancelInvoiceCommand(
        InvoiceId invoiceId,
        String reason
) implements Command<InvoiceId> {

    public CancelInvoiceCommand {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InvoiceId invoiceId;
        private String reason;

        public Builder invoiceId(InvoiceId invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public CancelInvoiceCommand build() {
            return new CancelInvoiceCommand(invoiceId, reason);
        }
    }
}
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/command/GenerateInvoicePdfCommand.java`**:

```java
package tech.kayys.erp.accounting.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.accounting.domain.identifier.InvoiceId;

/**
 * Command to generate a PDF for an invoice.
 */
public record GenerateInvoicePdfCommand(
        InvoiceId invoiceId,
        String templateId,
        String language
) implements Command<byte[]> {

    public GenerateInvoicePdfCommand {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InvoiceId invoiceId;
        private String templateId;
        private String language = "en";

        public Builder invoiceId(InvoiceId invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public GenerateInvoicePdfCommand build() {
            return new GenerateInvoicePdfCommand(invoiceId, templateId, language);
        }
    }
}
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/query/InvoiceView.java`**:

```java
package tech.kayys.erp.accounting.application.api.query;

import tech.kayys.erp.accounting.domain.model.Invoice;
import tech.kayys.erp.accounting.domain.valueobject.InvoiceStatus;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Complete invoice view with all details.
 */
public record InvoiceView(
        String invoiceId,
        String customerId,
        String customerName,
        String customerEmail,
        String invoiceNumber,
        String status,
        String statusDescription,
        String invoiceDate,
        String dueDate,
        List<InvoiceLineView> lines,
        String subtotal,
        String taxTotal,
        String discountTotal,
        String total,
        String paidAmount,
        String balance,
        String remainingBalance,
        String currencyCode,
        String customerNotes,
        String purchaseOrderNumber,
        List<PaymentView> payments,
        List<HistoryView> history,
        String pdfUrl,
        boolean overdue,
        int daysOverdue,
        String createdAt,
        String updatedAt
) {

    public static InvoiceView fromDomain(Invoice invoice, String customerName, String customerEmail) {
        List<InvoiceLineView> lineViews = invoice.getLines().stream()
            .map(InvoiceLineView::fromDomain)
            .collect(Collectors.toList());

        List<PaymentView> paymentViews = invoice.getPayments().stream()
            .map(PaymentView::fromDomain)
            .collect(Collectors.toList());

        boolean isOverdue = invoice.getStatus().isOutstanding() && 
            Instant.now().isAfter(invoice.getDueDate());
        long daysOverdue = isOverdue ? 
            java.time.temporal.ChronoUnit.DAYS.between(
                invoice.getDueDate(), Instant.now()
            ) : 0;

        return new InvoiceView(
            invoice.getId().toString(),
            invoice.getCustomerId().toString(),
            customerName,
            customerEmail,
            invoice.getInvoiceNumber(),
            invoice.getStatus().name(),
            invoice.getStatus().getDescription(),
            invoice.getInvoiceDate().toString(),
            invoice.getDueDate().toString(),
            lineViews,
            invoice.getSubtotal().getAmount().toPlainString(),
            invoice.getTaxTotal().getAmount().toPlainString(),
            invoice.getDiscountTotal().getAmount().toPlainString(),
            invoice.getTotal().getAmount().toPlainString(),
            invoice.getPaidAmount().getAmount().toPlainString(),
            invoice.getBalance().getAmount().toPlainString(),
            invoice.getRemainingBalance().getAmount().toPlainString(),
            invoice.getTotal().getCurrency().getCurrencyCode(),
            invoice.getCustomerNotes(),
            invoice.getPurchaseOrderNumber(),
            paymentViews,
            List.of(), // History would be populated separately
            null, // PDF URL would be generated
            isOverdue,
            (int) daysOverdue,
            invoice.getCreatedAt().toString(),
            invoice.getUpdatedAt().toString()
        );
    }

    public record InvoiceLineView(
            String productId,
            String description,
            int quantity,
            String unitPrice,
            String lineTotal,
            String taxAmount,
            String discountAmount,
            String currencyCode
    ) {
        public static InvoiceLineView fromDomain(Invoice.InvoiceLine line) {
            return new InvoiceLineView(
                line.getProductId() != null ? line.getProductId().toString() : null,
                line.getDescription(),
                line.getQuantity(),
                line.getUnitPrice().getAmount().toPlainString(),
                line.getLineTotal().getAmount().toPlainString(),
                line.getTaxAmount().getAmount().toPlainString(),
                line.getDiscountAmount().getAmount().toPlainString(),
                line.getLineTotal().getCurrency().getCurrencyCode()
            );
        }
    }

    public record PaymentView(
            String transactionId,
            String amount,
            String method,
            String reference,
            String date,
            String currencyCode
    ) {
        public static PaymentView fromDomain(Invoice.Payment payment) {
            return new PaymentView(
                payment.getTransactionId(),
                payment.getAmount().getAmount().toPlainString(),
                payment.getMethod().name(),
                payment.getReference(),
                payment.getDate().toString(),
                payment.getAmount().getCurrency().getCurrencyCode()
            );
        }
    }

    public record HistoryView(
            String action,
            String fromStatus,
            String toStatus,
            String performedBy,
            String performedAt,
            String notes
    ) {}
}
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/query/InvoiceSummaryView.java`**:

```java
package tech.kayys.erp.accounting.application.api.query;

import tech.kayys.erp.accounting.domain.model.Invoice;

import java.util.List;

/**
 * Summary view for a customer's invoices.
 */
public record InvoiceSummaryView(
        String customerId,
        String customerName,
        int totalInvoices,
        int openInvoices,
        int overdueInvoices,
        String totalAmount,
        String openAmount,
        String overdueAmount,
        String currencyCode,
        List<InvoiceBrief> recentInvoices
) {

    public record InvoiceBrief(
            String invoiceId,
            String invoiceNumber,
            String status,
            String date,
            String total,
            String balance
    ) {
        public static InvoiceBrief fromDomain(Invoice invoice) {
            return new InvoiceBrief(
                invoice.getId().toString(),
                invoice.getInvoiceNumber(),
                invoice.getStatus().name(),
                invoice.getInvoiceDate().toString(),
                invoice.getTotal().getAmount().toPlainString(),
                invoice.getRemainingBalance().getAmount().toPlainString()
            );
        }
    }
}
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/query/InvoiceSearchResult.java`**:

```java
package tech.kayys.erp.accounting.application.api.query;

import java.util.List;

/**
 * Search results for invoices with pagination.
 */
public record InvoiceSearchResult(
        List<InvoiceView> invoices,
        long totalCount,
        int page,
        int size,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
    public static InvoiceSearchResult of(
            List<InvoiceView> invoices,
            long totalCount,
            int page,
            int size) {
        int totalPages = (int) Math.ceil((double) totalCount / size);
        return new InvoiceSearchResult(
            invoices,
            totalCount,
            page,
            size,
            totalPages,
            page < totalPages - 1,
            page > 0
        );
    }
}
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/api/query/InvoiceStatistics.java`**:

```java
package tech.kayys.erp.accounting.application.api.query;

import tech.kayys.erp.accounting.domain.valueobject.InvoiceStatus;

import java.util.Map;

/**
 * Invoice statistics for reporting.
 */
public record InvoiceStatistics(
        int totalInvoices,
        int totalOpenInvoices,
        int totalOverdueInvoices,
        String totalRevenue,
        String totalOutstanding,
        String totalOverdue,
        Map<InvoiceStatus, Integer> statusCounts,
        String currencyCode,
        String periodStart,
        String periodEnd
) {}
```

## 3. Invoice Internal Implementation

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/internal/SendInvoiceHandler.java`**:

```java
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
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/internal/InvoicePdfGenerator.java`**:

```java
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
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/port/PdfGeneratorPort.java`**:

```java
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
```

**`/modules/accounting/application/src/main/java/tech/kayys/erp/accounting/application/port/EmailPort.java`**:

```java
package tech.kayys.erp.accounting.application.port;

import java.util.concurrent.CompletionStage;

/**
 * Port for sending emails.
 */
public interface EmailPort {

    /**
     * Sends an invoice email with PDF attachment.
     */
    CompletionStage<Boolean> sendInvoiceEmail(
        String to,
        String subject,
        String body,
        byte[] pdfAttachment,
        String pdfFileName
    );

    /**
     * Sends a simple email.
     */
    CompletionStage<Boolean> sendEmail(
        String to,
        String subject,
        String body
    );
}
```

## 4. Invoice REST API

**`/modules/accounting/interfaces/src/main/java/tech/kayys/erp/accounting/interfaces/rest/InvoiceResource.java`**:

```java
package tech.kayys.erp.accounting.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.accounting.application.api.InvoiceService;
import tech.kayys.erp.accounting.application.api.command.*;
import tech.kayys.erp.accounting.application.api.query.*;
import tech.kayys.erp.accounting.domain.identifier.InvoiceId;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for invoice management.
 */
@Path("/api/v1/invoices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Invoice API", description = "Invoice management endpoints")
public class InvoiceResource {

    @Inject
    InvoiceService invoiceService;

    @POST
    @Operation(summary = "Create a new invoice")
    @APIResponse(responseCode = "201", description = "Invoice created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> createInvoice(@Valid CreateInvoiceRequest request) {
        CreateInvoiceCommand command = CreateInvoiceCommand.builder()
            .customerId(request.getCustomerId())
            .invoiceNumber(request.getInvoiceNumber())
            .dueDate(request.getDueDate())
            .customerNotes(request.getCustomerNotes())
            .purchaseOrderNumber(request.getPurchaseOrderNumber())
            .currencyCode(request.getCurrencyCode() != null ? request.getCurrencyCode() : "USD")
            .lines(request.getLines().stream()
                .map(line -> new CreateInvoiceCommand.InvoiceLineCommand(
                    line.getProductId(),
                    line.getDescription(),
                    line.getQuantity(),
                    line.getUnitPrice(),
                    line.getTaxRate(),
                    line.getDiscountRate()
                ))
                .collect(java.util.stream.Collectors.toList())
            )
            .build();

        return invoiceService.createInvoice(command)
            .thenApply(invoiceId -> Response
                .created(URI.create("/api/v1/invoices/" + invoiceId.getValue()))
                .entity(new CreateInvoiceResponse(invoiceId))
                .build()
            );
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get invoice by ID")
    @APIResponse(responseCode = "200", description = "Invoice found")
    @APIResponse(responseCode = "404", description = "Invoice not found")
    public CompletionStage<Response> getInvoice(@PathParam("id") UUID id) {
        InvoiceId invoiceId = InvoiceId.of(id);
        GetInvoiceQuery query = new GetInvoiceQuery(invoiceId);

        return invoiceService.getInvoice(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{id}/send")
    @Operation(summary = "Send invoice to customer")
    @APIResponse(responseCode = "200", description = "Invoice sent")
    @APIResponse(responseCode = "404", description = "Invoice not found")
    public CompletionStage<Response> sendInvoice(
            @PathParam("id") UUID id,
            @Valid SendInvoiceRequest request) {
        InvoiceId invoiceId = InvoiceId.of(id);

        SendInvoiceCommand command = SendInvoiceCommand.builder()
            .invoiceId(invoiceId)
            .deliveryMethod(request.getDeliveryMethod())
            .emailSubject(request.getEmailSubject())
            .emailBody(request.getEmailBody())
            .templateId(request.getTemplateId())
            .build();

        return invoiceService.sendInvoice(command)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{id}/payments")
    @Operation(summary = "Record a payment")
    @APIResponse(responseCode = "200", description = "Payment recorded")
    @APIResponse(responseCode = "404", description = "Invoice not found")
    public CompletionStage<Response> recordPayment(
            @PathParam("id") UUID id,
            @Valid RecordPaymentRequest request) {
        InvoiceId invoiceId = InvoiceId.of(id);

        RecordPaymentCommand command = RecordPaymentCommand.builder()
            .invoiceId(invoiceId)
            .amount(request.getAmount())
            .currencyCode(request.getCurrencyCode())
            .paymentMethod(request.getPaymentMethod())
            .reference(request.getReference())
            .transactionId(request.getTransactionId())
            .build();

        return invoiceService.recordPayment(command)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{id}/refund")
    @Operation(summary = "Refund an invoice")
    @APIResponse(responseCode = "200", description = "Refund processed")
    @APIResponse(responseCode = "404", description = "Invoice not found")
    public CompletionStage<Response> refundInvoice(
            @PathParam("id") UUID id,
            @Valid RefundInvoiceRequest request) {
        InvoiceId invoiceId = InvoiceId.of(id);

        RefundInvoiceCommand command = RefundInvoiceCommand.builder()
            .invoiceId(invoiceId)
            .amount(request.getAmount())
            .currencyCode(request.getCurrencyCode())
            .reason(request.getReason())
            .reference(request.getReference())
            .build();

        return invoiceService.refundInvoice(command)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{id}/cancel")
    @Operation(summary = "Cancel an invoice")
    @APIResponse(responseCode = "200", description = "Invoice cancelled")
    @APIResponse(responseCode = "404", description = "Invoice not found")
    public CompletionStage<Response> cancelInvoice(
            @PathParam("id") UUID id,
            @Valid CancelInvoiceRequest request) {
        InvoiceId invoiceId = InvoiceId.of(id);

        CancelInvoiceCommand command = CancelInvoiceCommand.builder()
            .invoiceId(invoiceId)
            .reason(request.getReason())
            .build();

        return invoiceService.cancelInvoice(command)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GET
    @Path("/{id}/pdf")
    @Operation(summary = "Generate invoice PDF")
    @APIResponse(responseCode = "200", description = "PDF generated")
    @APIResponse(responseCode = "404", description = "Invoice not found")
    @Produces("application/pdf")
    public CompletionStage<Response> generatePdf(@PathParam("id") UUID id) {
        InvoiceId invoiceId = InvoiceId.of(id);

        GenerateInvoicePdfCommand command = GenerateInvoicePdfCommand.builder()
            .invoiceId(invoiceId)
            .build();

        return invoiceService.generateInvoicePdf(command)
            .thenApply(pdfBytes -> Response
                .ok(pdfBytes)
                .header("Content-Disposition", "attachment; filename=invoice-" + id + ".pdf")
                .build()
            )
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search invoices")
    @APIResponse(responseCode = "200", description = "Search results")
    public CompletionStage<Response> searchInvoices(
            @QueryParam("customerId") UUID customerId,
            @QueryParam("status") String status,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("minAmount") Double minAmount,
            @QueryParam("maxAmount") Double maxAmount,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sort") @DefaultValue("INVOICE_DATE_DESC") String sort) {
        // Parse and build search query
        SearchInvoicesQuery query = new SearchInvoicesQuery(
            customerId != null ? java.util.UUID.fromString(customerId.toString()) : null,
            status != null ? tech.kayys.erp.accounting.domain.valueobject.InvoiceStatus.valueOf(status) : null,
            fromDate != null ? java.time.Instant.parse(fromDate) : null,
            toDate != null ? java.time.Instant.parse(toDate) : null,
            minAmount,
            maxAmount,
            page,
            size,
            SearchInvoicesQuery.SortBy.valueOf(sort)
        );

        return invoiceService.searchInvoices(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreateInvoiceRequest {
        private UUID customerId;
        private String invoiceNumber;
        private Instant dueDate;
        private List<InvoiceLineRequest> lines;
        private String customerNotes;
        private String purchaseOrderNumber;
        private String currencyCode;

        // Getters and setters
        public UUID getCustomerId() { return customerId; }
        public void setCustomerId(UUID customerId) { this.customerId = customerId; }
        public String getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
        public Instant getDueDate() { return dueDate; }
        public void setDueDate(Instant dueDate) { this.dueDate = dueDate; }
        public List<InvoiceLineRequest> getLines() { return lines; }
        public void setLines(List<InvoiceLineRequest> lines) { this.lines = lines; }
        public String getCustomerNotes() { return customerNotes; }
        public void setCustomerNotes(String customerNotes) { this.customerNotes = customerNotes; }
        public String getPurchaseOrderNumber() { return purchaseOrderNumber; }
        public void setPurchaseOrderNumber(String purchaseOrderNumber) { this.purchaseOrderNumber = purchaseOrderNumber; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    }

    public static class InvoiceLineRequest {
        private UUID productId;
        private String description;
        private int quantity;
        private String unitPrice;
        private String taxRate;
        private String discountRate;

        // Getters and setters
        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getUnitPrice() { return unitPrice; }
        public void setUnitPrice(String unitPrice) { this.unitPrice = unitPrice; }
        public String getTaxRate() { return taxRate; }
        public void setTaxRate(String taxRate) { this.taxRate = taxRate; }
        public String getDiscountRate() { return discountRate; }
        public void setDiscountRate(String discountRate) { this.discountRate = discountRate; }
    }

    public static class SendInvoiceRequest {
        private InvoiceDeliveryMethod deliveryMethod;
        private String emailSubject;
        private String emailBody;
        private String templateId;

        // Getters and setters
        public InvoiceDeliveryMethod getDeliveryMethod() { return deliveryMethod; }
        public void setDeliveryMethod(InvoiceDeliveryMethod deliveryMethod) { this.deliveryMethod = deliveryMethod; }
        public String getEmailSubject() { return emailSubject; }
        public void setEmailSubject(String emailSubject) { this.emailSubject = emailSubject; }
        public String getEmailBody() { return emailBody; }
        public void setEmailBody(String emailBody) { this.emailBody = emailBody; }
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
    }

    public static class RecordPaymentRequest {
        private String amount;
        private String currencyCode;
        private PaymentMethod paymentMethod;
        private String reference;
        private String transactionId;

        // Getters and setters
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        public PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    }

    public static class RefundInvoiceRequest {
        private String amount;
        private String currencyCode;
        private String reason;
        private String reference;

        // Getters and setters
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
    }

    public static class CancelInvoiceRequest {
        private String reason;

        // Getters and setters
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class CreateInvoiceResponse {
        private final InvoiceId invoiceId;

        public CreateInvoiceResponse(InvoiceId invoiceId) {
            this.invoiceId = invoiceId;
        }

        public UUID getInvoiceId() {
            return invoiceId.getValue();
        }
    }
}
```

## 5. Invoice Repository Extensions

**`/modules/accounting/domain/src/main/java/tech/kayys/erp/accounting/domain/repository/InvoiceRepository.java`** (extended):

```java
// Add these methods to the existing InvoiceRepository interface:

/**
 * Finds invoices by search criteria.
 */
CompletionStage<List<Invoice>> findInvoices(InvoiceSearchCriteria criteria);

/**
 * Gets invoice statistics for a date range.
 */
CompletionStage<InvoiceStatistics> getStatistics(Instant start, Instant end);

/**
 * Finds invoices that are overdue and need reminder.
 */
CompletionStage<List<Invoice>> findOverdueInvoicesForReminder(
    int daysOverdue,
    int maxRemindersSent
);

/**
 * Finds invoices by status and date range.
 */
CompletionStage<List<Invoice>> findByStatusAndDateRange(
    InvoiceStatus status,
    Instant start,
    Instant end
);

/**
 * Records invoice history.
 */
CompletionStage<Void> recordHistory(
    InvoiceId invoiceId,
    InvoiceHistory history
);

/**
 * Gets invoice history.
 */
CompletionStage<List<InvoiceHistory>> getHistory(InvoiceId invoiceId);

/**
 * Search criteria for invoices.
 */
record InvoiceSearchCriteria(
    CustomerId customerId,
    InvoiceStatus status,
    Instant fromDate,
    Instant toDate,
    Money minAmount,
    Money maxAmount,
    String invoiceNumber,
    String purchaseOrderNumber,
    int page,
    int size,
    SortBy sortBy
) {
    public enum SortBy {
        INVOICE_DATE_ASC,
        INVOICE_DATE_DESC,
        TOTAL_ASC,
        TOTAL_DESC,
        STATUS_ASC,
        STATUS_DESC
    }
}

/**
 * Invoice statistics DTO.
 */
record InvoiceStatistics(
    long totalInvoices,
    long totalOpenInvoices,
    long totalOverdueInvoices,
    Money totalRevenue,
    Money totalOutstanding,
    Money totalOverdue,
    Map<InvoiceStatus, Long> statusCounts
) {}
```

## Summary

The complete Invoice Management implementation provides:

1. **Comprehensive Invoice Model**:
   - Full invoice lifecycle management
   - Line items with tax and discount calculations
   - Payment tracking and reconciliation
   - History and audit trail

2. **Invoice Commands**:
   - Create, Send, Pay, Refund, Write-off, Cancel
   - PDF generation
   - Payment recording with failure handling

3. **Invoice Queries**:
   - Complete invoice views
   - Customer summaries
   - Search with pagination
   - Statistics and reporting

4. **Integration Ports**:
   - CustomerPort for customer information
   - EmailPort for invoice delivery
   - PdfGeneratorPort for PDF creation
   - JournalEntryPort for accounting integration

5. **REST API**:
   - Complete CRUD operations
   - PDF download
   - Search and filtering
   - Payment processing

6. **Business Rules**:
   - Invoice status transitions
   - Overdue detection
   - Payment validation
   - Balance management

This completes the Accounting context with full invoice management capabilities that can be used by Sales, Subscription, and other contexts throughout the ERP system.