package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseRequisitionId;
import tech.kayys.erp.purchasing.domain.valueobject.Money;
import tech.kayys.erp.purchasing.domain.valueobject.RequisitionStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Purchase Requisition aggregate root.
 * Represents an internal request to purchase goods or services.
 */
public final class PurchaseRequisition extends AggregateRoot<PurchaseRequisitionId> {
    
    private static final long serialVersionUID = 1L;
    
    private String requisitionNumber;
    private String departmentId;
    private String departmentName;
    private String requestedBy;
    private String requestedByName;
    private String costCenter;
    private String projectCode;
    private String budgetCode;
    private List<RequisitionItem> items;
    private Money totalAmount;
    private String currencyCode;
    private String justification;
    private String deliveryLocation;
    private Instant requiredDate;
    private Instant createdDate;
    private RequisitionStatus status;
    private String approvedBy;
    private Instant approvedAt;
    private String rejectedBy;
    private String rejectionReason;
    private Instant rejectedAt;
    private String purchaseOrderId;
    private List<RequisitionHistory> history;
    private String notes;
    private boolean active;

    private PurchaseRequisition(PurchaseRequisitionId id) {
        super(id);
        this.items = new ArrayList<>();
        this.history = new ArrayList<>();
        this.status = RequisitionStatus.DRAFT;
        this.active = true;
        this.createdDate = Instant.now();
        this.totalAmount = Money.zero("USD");
    }

    private PurchaseRequisition() {
        super();
    }

    /**
     * Factory method to create a new purchase requisition.
     */
    public static PurchaseRequisition create(
            PurchaseRequisitionId id,
            String requisitionNumber,
            String departmentId,
            String requestedBy,
            String costCenter,
            String currencyCode) {
        PurchaseRequisition req = new PurchaseRequisition(id);
        req.requisitionNumber = requisitionNumber;
        req.departmentId = departmentId;
        req.requestedBy = requestedBy;
        req.costCenter = costCenter;
        req.currencyCode = currencyCode;
        return req;
    }

    /**
     * Adds an item to the requisition.
     */
    public void addItem(RequisitionItem item) {
        if (status != RequisitionStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify requisition in status: " + status);
        }
        items.add(item);
        recalculateTotal();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes an item from the requisition.
     */
    public void removeItem(int index) {
        if (status != RequisitionStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify requisition in status: " + status);
        }
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("Invalid item index");
        }
        items.remove(index);
        recalculateTotal();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits the requisition for approval.
     */
    public void submit() {
        if (status != RequisitionStatus.DRAFT) {
            throw new IllegalStateException("Cannot submit requisition in status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Requisition must have at least one item");
        }
        this.status = RequisitionStatus.SUBMITTED;
        addHistory("SUBMITTED", "Requisition submitted for approval");
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the requisition.
     */
    public void approve(String approvedBy) {
        if (status != RequisitionStatus.SUBMITTED && status != RequisitionStatus.IN_REVIEW) {
            throw new IllegalStateException("Cannot approve requisition in status: " + status);
        }
        this.status = RequisitionStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        addHistory("APPROVED", "Approved by: " + approvedBy);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the requisition.
     */
    public void reject(String rejectedBy, String reason) {
        if (status != RequisitionStatus.SUBMITTED && status != RequisitionStatus.IN_REVIEW) {
            throw new IllegalStateException("Cannot reject requisition in status: " + status);
        }
        this.status = RequisitionStatus.REJECTED;
        this.rejectedBy = rejectedBy;
        this.rejectionReason = reason;
        this.rejectedAt = Instant.now();
        addHistory("REJECTED", "Rejected by: " + rejectedBy + " - " + reason);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Converts the requisition to a purchase order.
     */
    public void convertToPurchaseOrder(String purchaseOrderId) {
        if (status != RequisitionStatus.APPROVED) {
            throw new IllegalStateException("Cannot convert requisition in status: " + status);
        }
        this.purchaseOrderId = purchaseOrderId;
        this.status = RequisitionStatus.CONVERTED;
        addHistory("CONVERTED", "Converted to purchase order: " + purchaseOrderId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
            .map(RequisitionItem::getTotalAmount)
            .reduce(Money.zero(currencyCode), Money::add);
    }

    private void addHistory(String action, String details) {
        RequisitionHistory entry = new RequisitionHistory(
            java.util.UUID.randomUUID().toString(),
            action,
            details,
            Instant.now()
        );
        history.add(entry);
    }

    // Getters
    public String getRequisitionNumber() { return requisitionNumber; }
    public String getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public String getRequestedBy() { return requestedBy; }
    public String getRequestedByName() { return requestedByName; }
    public String getCostCenter() { return costCenter; }
    public String getProjectCode() { return projectCode; }
    public String getBudgetCode() { return budgetCode; }
    public List<RequisitionItem> getItems() { return Collections.unmodifiableList(items); }
    public Money getTotalAmount() { return totalAmount; }
    public String getCurrencyCode() { return currencyCode; }
    public String getJustification() { return justification; }
    public String getDeliveryLocation() { return deliveryLocation; }
    public Instant getRequiredDate() { return requiredDate; }
    public Instant getCreatedDate() { return createdDate; }
    public RequisitionStatus getStatus() { return status; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getRejectedBy() { return rejectedBy; }
    public String getRejectionReason() { return rejectionReason; }
    public Instant getRejectedAt() { return rejectedAt; }
    public String getPurchaseOrderId() { return purchaseOrderId; }
    public List<RequisitionHistory> getHistory() { return Collections.unmodifiableList(history); }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBudgetCode(String budgetCode) {
        this.budgetCode = budgetCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setJustification(String justification) {
        this.justification = justification;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRequiredDate(Instant requiredDate) {
        this.requiredDate = requiredDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "PurchaseRequisition{" +
                "id=" + getId() +
                ", requisitionNumber='" + requisitionNumber + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                '}';
    }

    /**
     * Requisition item value object.
     */
    public static final class RequisitionItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String productId;
        private final String productName;
        private final String sku;
        private final int quantity;
        private final Money unitPrice;
        private final Money totalAmount;
        private final String uom;
        private final String requiredDate;
        private final String notes;

        public RequisitionItem(
                String productId,
                String productName,
                String sku,
                int quantity,
                Money unitPrice,
                String uom,
                String requiredDate,
                String notes) {
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.uom = uom;
            this.requiredDate = requiredDate;
            this.notes = notes;
            this.totalAmount = unitPrice.multiply(quantity);
            validate();
        }

        @Override
        public void validate() {
            if (productName == null || productName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice == null || unitPrice.isNegative()) {
                throw new IllegalArgumentException("Unit price must be positive");
            }
        }

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public Money getUnitPrice() { return unitPrice; }
        public Money getTotalAmount() { return totalAmount; }
        public String getUom() { return uom; }
        public String getRequiredDate() { return requiredDate; }
        public String getNotes() { return notes; }
    }

    /**
     * Requisition history record.
     */
    public static final class RequisitionHistory {
        private final String historyId;
        private final String action;
        private final String details;
        private final Instant timestamp;

        public RequisitionHistory(String historyId, String action, String details, Instant timestamp) {
            this.historyId = historyId;
            this.action = action;
            this.details = details;
            this.timestamp = timestamp;
        }

        public String getHistoryId() { return historyId; }
        public String getAction() { return action; }
        public String getDetails() { return details; }
        public Instant getTimestamp() { return timestamp; }
    }
}