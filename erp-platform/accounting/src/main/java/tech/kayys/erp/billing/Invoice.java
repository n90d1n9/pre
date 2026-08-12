package tech.kayys.erp.billing;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Invoice aggregate root.
 * Represents a bill or invoice sent to a customer.
 */
public final class Invoice extends AggregateRoot<InvoiceId> {
    
    private static final long serialVersionUID = 1L;
    
    private CustomerId customerId;
    private String invoiceNumber;
    private Instant invoiceDate;
    private Instant dueDate;
    private List<InvoiceLine> lines;
    private Money subtotal;
    private Money taxTotal;
    private Money discountTotal;
    private Money total;
    private Money paidAmount;
    private Money balance;
    private InvoiceStatus status;
    private String customerNotes;
    private String internalNotes;
    private String purchaseOrderNumber;
    private PaymentMethod paymentMethod;
    private JournalEntryId journalEntryId;
    private List<Payment> payments;

    private Invoice(InvoiceId id) {
        super(id);
        this.lines = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.status = InvoiceStatus.DRAFT;
        this.paidAmount = Money.zero("USD");
        this.balance = Money.zero("USD");
        this.invoiceDate = Instant.now();
    }

    private Invoice() {
        super();
    }

    /**
     * Factory method to create a new invoice.
     */
    public static Invoice create(
            InvoiceId id,
            CustomerId customerId,
            String invoiceNumber,
            Instant dueDate) {
        Invoice invoice = new Invoice(id);
        invoice.customerId = customerId;
        invoice.invoiceNumber = invoiceNumber;
        invoice.dueDate = dueDate;
        return invoice;
    }

    /**
     * Adds a line to the invoice.
     */
    public void addLine(InvoiceLine line) {
        if (status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify invoice in status: " + status);
        }
        lines.add(line);
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a line from the invoice.
     */
    public void removeLine(int index) {
        if (status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify invoice in status: " + status);
        }
        if (index < 0 || index >= lines.size()) {
            throw new IllegalArgumentException("Invalid line index: " + index);
        }
        lines.remove(index);
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sends the invoice to the customer.
     */
    public void send() {
        if (status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Cannot send invoice in status: " + status);
        }
        if (lines.isEmpty()) {
            throw new IllegalStateException("Invoice has no lines");
        }
        this.status = InvoiceStatus.SENT;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records a payment against the invoice.
     */
    public void recordPayment(Money amount, PaymentMethod method, String reference) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot pay invoice in status: " + status);
        }

        Money remaining = getRemainingBalance();
        if (amount.isGreaterThan(remaining)) {
            throw new IllegalArgumentException("Payment amount exceeds remaining balance");
        }

        Payment payment = new Payment(
            UUID.randomUUID().toString(),
            amount,
            method,
            reference,
            Instant.now()
        );
        payments.add(payment);
        
        this.paidAmount = paidAmount.add(amount);
        this.balance = total.subtract(paidAmount);

        if (balance.isZero()) {
            this.status = InvoiceStatus.PAID;
        } else if (paidAmount.isPositive()) {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        }

        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the invoice.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot cancel invoice in status: " + status);
        }
        if (status == InvoiceStatus.PAID || status == InvoiceStatus.PARTIALLY_PAID) {
            throw new IllegalStateException("Cannot cancel partially or fully paid invoice");
        }
        this.status = InvoiceStatus.CANCELLED;
        this.internalNotes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Refunds the invoice.
     */
    public void refund(String reason) {
        if (status != InvoiceStatus.PAID && status != InvoiceStatus.PARTIALLY_PAID) {
            throw new IllegalStateException("Cannot refund invoice in status: " + status);
        }
        this.status = InvoiceStatus.REFUNDED;
        this.internalNotes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the invoice as overdue.
     */
    public void markOverdue() {
        if (status == InvoiceStatus.SENT || status == InvoiceStatus.VIEWED || 
            status == InvoiceStatus.PARTIALLY_PAID) {
            if (Instant.now().isAfter(dueDate)) {
                this.status = InvoiceStatus.OVERDUE;
                setUpdatedAt(Instant.now());
                incrementVersion();
            }
        }
    }

    private void recalculateTotals() {
        this.subtotal = lines.stream()
            .map(InvoiceLine::getLineTotal)
            .reduce(Money.zero("USD"), Money::add);

        // Calculate tax (simplified)
        // In a real system, use a tax calculation service
        this.taxTotal = subtotal.percentage(10);
        
        this.discountTotal = Money.zero(subtotal.getCurrency().getCurrencyCode());
        this.total = subtotal.add(taxTotal).subtract(discountTotal);
        this.balance = total.subtract(paidAmount);
    }

    // Getters
    public CustomerId getCustomerId() { return customerId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public Instant getInvoiceDate() { return invoiceDate; }
    public Instant getDueDate() { return dueDate; }
    public List<InvoiceLine> getLines() { return Collections.unmodifiableList(lines); }
    public Money getSubtotal() { return subtotal; }
    public Money getTaxTotal() { return taxTotal; }
    public Money getDiscountTotal() { return discountTotal; }
    public Money getTotal() { return total; }
    public Money getPaidAmount() { return paidAmount; }
    public Money getBalance() { return balance; }
    public Money getRemainingBalance() { return total.subtract(paidAmount); }
    public InvoiceStatus getStatus() { return status; }
    public String getCustomerNotes() { return customerNotes; }
    public String getInternalNotes() { return internalNotes; }
    public String getPurchaseOrderNumber() { return purchaseOrderNumber; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public JournalEntryId getJournalEntryId() { return journalEntryId; }
    public List<Payment> getPayments() { return Collections.unmodifiableList(payments); }

    public void setCustomerNotes(String customerNotes) {
        if (status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify invoice in status: " + status);
        }
        this.customerNotes = customerNotes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        if (status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify invoice in status: " + status);
        }
        this.purchaseOrderNumber = purchaseOrderNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setJournalEntryId(JournalEntryId journalEntryId) {
        this.journalEntryId = journalEntryId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Invoice line value object.
     */
    public static final class InvoiceLine implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final UUID productId;
        private final String description;
        private final int quantity;
        private final Money unitPrice;
        private final Money lineTotal;
        private final Money taxAmount;
        private final Money discountAmount;

        public InvoiceLine(
                UUID productId,
                String description,
                int quantity,
                Money unitPrice,
                Money taxAmount,
                Money discountAmount) {
            this.productId = productId;
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.taxAmount = taxAmount != null ? taxAmount : Money.zero(unitPrice.getCurrency().getCurrencyCode());
            this.discountAmount = discountAmount != null ? discountAmount : Money.zero(unitPrice.getCurrency().getCurrencyCode());
            this.lineTotal = calculateTotal();
            validate();
        }

        @Override
        public void validate() {
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("Description cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice == null || unitPrice.isNegative()) {
                throw new IllegalArgumentException("Unit price must be positive");
            }
        }

        private Money calculateTotal() {
            Money subtotal = unitPrice.multiply(quantity);
            Money subtotalAfterDiscount = subtotal.subtract(discountAmount);
            return subtotalAfterDiscount.add(taxAmount);
        }

        public UUID getProductId() { return productId; }
        public String getDescription() { return description; }
        public int getQuantity() { return quantity; }
        public Money getUnitPrice() { return unitPrice; }
        public Money getLineTotal() { return lineTotal; }
        public Money getTaxAmount() { return taxAmount; }
        public Money getDiscountAmount() { return discountAmount; }
        public Money getSubtotal() { return unitPrice.multiply(quantity); }

        @Override
        public String toString() {
            return "InvoiceLine{" +
                    "description='" + description + '\'' +
                    ", quantity=" + quantity +
                    ", lineTotal=" + lineTotal +
                    '}';
        }
    }

    /**
     * Payment record.
     */
    public static final class Payment implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String transactionId;
        private final Money amount;
        private final PaymentMethod method;
        private final String reference;
        private final Instant date;

        public Payment(String transactionId, Money amount, PaymentMethod method, String reference, Instant date) {
            this.transactionId = transactionId;
            this.amount = amount;
            this.method = method;
            this.reference = reference;
            this.date = date;
            validate();
        }

        @Override
        public void validate() {
            if (transactionId == null || transactionId.trim().isEmpty()) {
                throw new IllegalArgumentException("Transaction ID cannot be empty");
            }
            if (amount == null || amount.isZero()) {
                throw new IllegalArgumentException("Amount must be non-zero");
            }
            if (method == null) {
                throw new IllegalArgumentException("Payment method cannot be null");
            }
        }

        public String getTransactionId() { return transactionId; }
        public Money getAmount() { return amount; }
        public PaymentMethod getMethod() { return method; }
        public String getReference() { return reference; }
        public Instant getDate() { return date; }

        @Override
        public String toString() {
            return "Payment{" +
                    "transactionId='" + transactionId + '\'' +
                    ", amount=" + amount +
                    ", method=" + method +
                    '}';
        }
    }
}