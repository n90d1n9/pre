package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.identifier.InvoiceMatchId;
import tech.kayys.erp.purchasing.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Invoice Match aggregate root.
 * Implements 3-way matching: Purchase Order → Receiving → Vendor Invoice.
 */
public final class InvoiceMatch extends AggregateRoot<InvoiceMatchId> {
    
    private static final long serialVersionUID = 1L;
    
    private String purchaseOrderId;
    private String receivingRecordId;
    private String vendorInvoiceId;
    private String vendorInvoiceNumber;
    private Money invoiceAmount;
    private Money poAmount;
    private Money receivedAmount;
    private MatchStatus status;
    private List<MatchLine> lines;
    private List<MatchDiscrepancy> discrepancies;
    private String matchedBy;
    private Instant matchedAt;
    private String approvedBy;
    private Instant approvedAt;
    private boolean active;

    private InvoiceMatch(InvoiceMatchId id) {
        super(id);
        this.lines = new ArrayList<>();
        this.discrepancies = new ArrayList<>();
        this.status = MatchStatus.PENDING;
        this.active = true;
    }

    private InvoiceMatch() {
        super();
    }

    /**
     * Factory method to create a new invoice match.
     */
    public static InvoiceMatch create(
            InvoiceMatchId id,
            String purchaseOrderId,
            String receivingRecordId,
            String vendorInvoiceId,
            String vendorInvoiceNumber,
            Money invoiceAmount) {
        InvoiceMatch match = new InvoiceMatch(id);
        match.purchaseOrderId = purchaseOrderId;
        match.receivingRecordId = receivingRecordId;
        match.vendorInvoiceId = vendorInvoiceId;
        match.vendorInvoiceNumber = vendorInvoiceNumber;
        match.invoiceAmount = invoiceAmount;
        return match;
    }

    /**
     * Performs the 3-way match.
     */
    public void performMatch(Money poAmount, Money receivedAmount) {
        this.poAmount = poAmount;
        this.receivedAmount = receivedAmount;
        
        // Check quantity match
        boolean quantityMatch = checkQuantityMatch();
        // Check price match
        boolean priceMatch = checkPriceMatch();
        // Check amount match
        boolean amountMatch = invoiceAmount.equals(poAmount) && invoiceAmount.equals(receivedAmount);
        
        if (quantityMatch && priceMatch && amountMatch) {
            this.status = MatchStatus.MATCHED;
        } else {
            this.status = MatchStatus.DISCREPANCY;
            if (!quantityMatch) {
                discrepancies.add(new MatchDiscrepancy(
                    "QUANTITY",
                    "Quantity mismatch between PO and receipt",
                    "Resolve quantity variance"
                ));
            }
            if (!priceMatch) {
                discrepancies.add(new MatchDiscrepancy(
                    "PRICE",
                    "Price mismatch between PO and invoice",
                    "Resolve price variance"
                ));
            }
            if (!amountMatch) {
                discrepancies.add(new MatchDiscrepancy(
                    "AMOUNT",
                    "Amount mismatch between documents",
                    "Resolve amount variance"
                ));
            }
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private boolean checkQuantityMatch() {
        // In production, compare line quantities from PO, receipt, and invoice
        return true;
    }

    private boolean checkPriceMatch() {
        // In production, compare unit prices from PO and invoice
        return true;
    }

    /**
     * Approves the match.
     */
    public void approve(String approvedBy) {
        if (status == MatchStatus.DISCREPANCY) {
            throw new IllegalStateException("Cannot approve match with discrepancies");
        }
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        this.status = MatchStatus.APPROVED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the match.
     */
    public void reject(String reason) {
        this.status = MatchStatus.REJECTED;
        discrepancies.add(new MatchDiscrepancy(
            "REJECTION",
            "Match rejected: " + reason,
            "Review and correct documents"
        ));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getPurchaseOrderId() { return purchaseOrderId; }
    public String getReceivingRecordId() { return receivingRecordId; }
    public String getVendorInvoiceId() { return vendorInvoiceId; }
    public String getVendorInvoiceNumber() { return vendorInvoiceNumber; }
    public Money getInvoiceAmount() { return invoiceAmount; }
    public Money getPoAmount() { return poAmount; }
    public Money getReceivedAmount() { return receivedAmount; }
    public MatchStatus getStatus() { return status; }
    public List<MatchLine> getLines() { return Collections.unmodifiableList(lines); }
    public List<MatchDiscrepancy> getDiscrepancies() { return Collections.unmodifiableList(discrepancies); }
    public String getMatchedBy() { return matchedBy; }
    public Instant getMatchedAt() { return matchedAt; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public boolean isActive() { return active; }

    @Override
    public String toString() {
        return "InvoiceMatch{" +
                "id=" + getId() +
                ", purchaseOrderId='" + purchaseOrderId + '\'' +
                ", vendorInvoiceNumber='" + vendorInvoiceNumber + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * Match status enum.
     */
    public enum MatchStatus {
        PENDING("Pending"),
        MATCHED("Matched"),
        DISCREPANCY("Discrepancy"),
        APPROVED("Approved"),
        REJECTED("Rejected");

        private final String description;

        MatchStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Match line value object.
     */
    public static final class MatchLine implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final int lineNumber;
        private final String productId;
        private final String productName;
        private final String sku;
        private final int poQuantity;
        private final int receivedQuantity;
        private final int invoiceQuantity;
        private final Money poPrice;
        private final Money invoicePrice;
        private final Money poAmount;
        private final Money invoiceAmount;
        private final boolean quantityMatch;
        private final boolean priceMatch;

        public MatchLine(
                int lineNumber,
                String productId,
                String productName,
                String sku,
                int poQuantity,
                int receivedQuantity,
                int invoiceQuantity,
                Money poPrice,
                Money invoicePrice,
                Money poAmount,
                Money invoiceAmount) {
            this.lineNumber = lineNumber;
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.poQuantity = poQuantity;
            this.receivedQuantity = receivedQuantity;
            this.invoiceQuantity = invoiceQuantity;
            this.poPrice = poPrice;
            this.invoicePrice = invoicePrice;
            this.poAmount = poAmount;
            this.invoiceAmount = invoiceAmount;
            this.quantityMatch = poQuantity == receivedQuantity && receivedQuantity == invoiceQuantity;
            this.priceMatch = poPrice.equals(invoicePrice);
        }

        public int getLineNumber() { return lineNumber; }
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public int getPoQuantity() { return poQuantity; }
        public int getReceivedQuantity() { return receivedQuantity; }
        public int getInvoiceQuantity() { return invoiceQuantity; }
        public Money getPoPrice() { return poPrice; }
        public Money getInvoicePrice() { return invoicePrice; }
        public Money getPoAmount() { return poAmount; }
        public Money getInvoiceAmount() { return invoiceAmount; }
        public boolean isQuantityMatch() { return quantityMatch; }
        public boolean isPriceMatch() { return priceMatch; }
    }

    /**
     * Match discrepancy value object.
     */
    public static final class MatchDiscrepancy {
        private final String type;
        private final String description;
        private final String resolution;

        public MatchDiscrepancy(String type, String description, String resolution) {
            this.type = type;
            this.description = description;
            this.resolution = resolution;
        }

        public String getType() { return type; }
        public String getDescription() { return description; }
        public String getResolution() { return resolution; }
    }
}