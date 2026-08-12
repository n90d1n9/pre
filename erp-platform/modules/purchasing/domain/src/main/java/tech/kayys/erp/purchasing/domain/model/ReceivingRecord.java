package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.identifier.ReceivingRecordId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Receiving Record aggregate root.
 * Tracks the receipt and inspection of purchase order items.
 */
public final class ReceivingRecord extends AggregateRoot<ReceivingRecordId> {
    
    private static final long serialVersionUID = 1L;
    
    private String purchaseOrderId;
    private String vendorId;
    private String receivingNumber;
    private Instant receivingDate;
    private List<ReceivedItem> items;
    private InspectionStatus inspectionStatus;
    private List<InspectionRecord> inspections;
    private String receivedBy;
    private String approvedBy;
    private Instant approvedAt;
    private String notes;
    private boolean active;

    private ReceivingRecord(ReceivingRecordId id) {
        super(id);
        this.items = new ArrayList<>();
        this.inspections = new ArrayList<>();
        this.receivingDate = Instant.now();
        this.inspectionStatus = InspectionStatus.PENDING;
        this.active = true;
    }

    private ReceivingRecord() {
        super();
    }

    /**
     * Factory method to create a new receiving record.
     */
    public static ReceivingRecord create(
            ReceivingRecordId id,
            String purchaseOrderId,
            String vendorId,
            String receivingNumber,
            String receivedBy) {
        ReceivingRecord record = new ReceivingRecord(id);
        record.purchaseOrderId = purchaseOrderId;
        record.vendorId = vendorId;
        record.receivingNumber = receivingNumber;
        record.receivedBy = receivedBy;
        return record;
    }

    /**
     * Adds a received item.
     */
    public void addItem(ReceivedItem item) {
        items.add(item);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records inspection results.
     */
    public void recordInspection(InspectionRecord inspection) {
        inspections.add(inspection);
        
        // Update inspection status
        boolean allPassed = inspections.stream().allMatch(InspectionRecord::isPassed);
        if (allPassed) {
            this.inspectionStatus = InspectionStatus.PASSED;
        } else {
            this.inspectionStatus = InspectionStatus.FAILED;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the receiving record.
     */
    public void approve(String approvedBy) {
        if (inspectionStatus == InspectionStatus.PENDING) {
            throw new IllegalStateException("Cannot approve pending inspection");
        }
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getPurchaseOrderId() { return purchaseOrderId; }
    public String getVendorId() { return vendorId; }
    public String getReceivingNumber() { return receivingNumber; }
    public Instant getReceivingDate() { return receivingDate; }
    public List<ReceivedItem> getItems() { return Collections.unmodifiableList(items); }
    public InspectionStatus getInspectionStatus() { return inspectionStatus; }
    public List<InspectionRecord> getInspections() { return Collections.unmodifiableList(inspections); }
    public String getReceivedBy() { return receivedBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "ReceivingRecord{" +
                "id=" + getId() +
                ", purchaseOrderId='" + purchaseOrderId + '\'' +
                ", receivingNumber='" + receivingNumber + '\'' +
                ", inspectionStatus=" + inspectionStatus +
                '}';
    }

    /**
     * Inspection status enum.
     */
    public enum InspectionStatus {
        PENDING("Pending"),
        PASSED("Passed"),
        FAILED("Failed"),
        PARTIAL("Partial");

        private final String description;

        InspectionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Received item value object.
     */
    public static final class ReceivedItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final int lineNumber;
        private final String productId;
        private final String productName;
        private final String sku;
        private final int orderedQuantity;
        private final int receivedQuantity;
        private final int rejectedQuantity;
        private final String uom;
        private final String condition; // GOOD, DAMAGED, SHORT
        private final String notes;

        public ReceivedItem(
                int lineNumber,
                String productId,
                String productName,
                String sku,
                int orderedQuantity,
                int receivedQuantity,
                int rejectedQuantity,
                String uom,
                String condition,
                String notes) {
            this.lineNumber = lineNumber;
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.orderedQuantity = orderedQuantity;
            this.receivedQuantity = receivedQuantity;
            this.rejectedQuantity = rejectedQuantity;
            this.uom = uom;
            this.condition = condition;
            this.notes = notes;
            validate();
        }

        @Override
        public void validate() {
            if (receivedQuantity < 0) {
                throw new IllegalArgumentException("Received quantity cannot be negative");
            }
            if (rejectedQuantity < 0) {
                throw new IllegalArgumentException("Rejected quantity cannot be negative");
            }
            if (receivedQuantity + rejectedQuantity > orderedQuantity) {
                throw new IllegalArgumentException("Received + rejected cannot exceed ordered quantity");
            }
        }

        public int getLineNumber() { return lineNumber; }
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public int getOrderedQuantity() { return orderedQuantity; }
        public int getReceivedQuantity() { return receivedQuantity; }
        public int getRejectedQuantity() { return rejectedQuantity; }
        public int getAcceptedQuantity() { return receivedQuantity - rejectedQuantity; }
        public String getUom() { return uom; }
        public String getCondition() { return condition; }
        public String getNotes() { return notes; }
    }

    /**
     * Inspection record value object.
     */
    public static final class InspectionRecord {
        private final String inspectorId;
        private final String inspectorName;
        private final int itemIndex;
        private final boolean passed;
        private final String defectType;
        private final String defectDescription;
        private final Instant inspectionDate;
        private final String notes;

        public InspectionRecord(
                String inspectorId,
                String inspectorName,
                int itemIndex,
                boolean passed,
                String defectType,
                String defectDescription,
                Instant inspectionDate,
                String notes) {
            this.inspectorId = inspectorId;
            this.inspectorName = inspectorName;
            this.itemIndex = itemIndex;
            this.passed = passed;
            this.defectType = defectType;
            this.defectDescription = defectDescription;
            this.inspectionDate = inspectionDate;
            this.notes = notes;
        }

        public String getInspectorId() { return inspectorId; }
        public String getInspectorName() { return inspectorName; }
        public int getItemIndex() { return itemIndex; }
        public boolean isPassed() { return passed; }
        public String getDefectType() { return defectType; }
        public String getDefectDescription() { return defectDescription; }
        public Instant getInspectionDate() { return inspectionDate; }
        public String getNotes() { return notes; }
    }
}