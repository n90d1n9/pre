package tech.kayys.erp.transaction.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.transaction.domain.identifier.DisputeId;
import tech.kayys.erp.transaction.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dispute aggregate root.
 * Manages chargebacks and disputes.
 */
public final class Dispute extends AggregateRoot<DisputeId> {
    
    private static final long serialVersionUID = 1L;
    
    private String transactionId;
    private String customerId;
    private String orderId;
    private Money amount;
    private String currencyCode;
    private DisputeType type;
    private DisputeStatus status;
    private String reasonCode;
    private String reasonDescription;
    private Instant disputeDate;
    private String evidenceId;
    private List<DisputeEvidence> evidence;
    private String responseDueDate;
    private String response;
    private Instant respondedAt;
    private String resolvedBy;
    private Instant resolvedAt;
    private String resolutionNotes;
    private boolean customerNotified;
    private boolean fundsWithheld;
    private String internalNotes;

    private Dispute(DisputeId id) {
        super(id);
        this.evidence = new ArrayList<>();
        this.status = DisputeStatus.OPEN;
        this.disputeDate = Instant.now();
        this.customerNotified = false;
        this.fundsWithheld = true;
    }

    private Dispute() {
        super();
    }

    /**
     * Factory method to create a new dispute.
     */
    public static Dispute create(
            DisputeId id,
            String transactionId,
            String customerId,
            String orderId,
            Money amount,
            DisputeType type,
            String reasonCode,
            String reasonDescription) {
        Dispute dispute = new Dispute(id);
        dispute.transactionId = transactionId;
        dispute.customerId = customerId;
        dispute.orderId = orderId;
        dispute.amount = amount;
        dispute.currencyCode = amount.getCurrency().getCurrencyCode();
        dispute.type = type;
        dispute.reasonCode = reasonCode;
        dispute.reasonDescription = reasonDescription;
        return dispute;
    }

    /**
     * Adds evidence to the dispute.
     */
    public void addEvidence(DisputeEvidence evidence) {
        this.evidence.add(evidence);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits the dispute response.
     */
    public void submitResponse(String response) {
        if (status != DisputeStatus.OPEN && status != DisputeStatus.EVIDENCE_REQUESTED) {
            throw new IllegalStateException("Cannot submit response in status: " + status);
        }
        this.response = response;
        this.respondedAt = Instant.now();
        this.status = DisputeStatus.RESPONDED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Resolves the dispute in favor of the customer.
     */
    public void resolveForCustomer(String resolvedBy, String notes) {
        if (status == DisputeStatus.RESOLVED) {
            return;
        }
        this.status = DisputeStatus.RESOLVED_FOR_CUSTOMER;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = Instant.now();
        this.resolutionNotes = notes;
        this.fundsWithheld = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Resolves the dispute in favor of the merchant.
     */
    public void resolveForMerchant(String resolvedBy, String notes) {
        if (status == DisputeStatus.RESOLVED) {
            return;
        }
        this.status = DisputeStatus.RESOLVED_FOR_MERCHANT;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = Instant.now();
        this.resolutionNotes = notes;
        this.fundsWithheld = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Closes the dispute.
     */
    public void close(String notes) {
        if (status != DisputeStatus.RESOLVED_FOR_CUSTOMER && 
            status != DisputeStatus.RESOLVED_FOR_MERCHANT) {
            throw new IllegalStateException("Cannot close unresolved dispute");
        }
        this.status = DisputeStatus.CLOSED;
        this.resolutionNotes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Requests additional evidence.
     */
    public void requestEvidence(String requestDetails) {
        this.status = DisputeStatus.EVIDENCE_REQUESTED;
        this.internalNotes = requestDetails;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getCustomerId() { return customerId; }
    public String getOrderId() { return orderId; }
    public Money getAmount() { return amount; }
    public String getCurrencyCode() { return currencyCode; }
    public DisputeType getType() { return type; }
    public DisputeStatus getStatus() { return status; }
    public String getReasonCode() { return reasonCode; }
    public String getReasonDescription() { return reasonDescription; }
    public Instant getDisputeDate() { return disputeDate; }
    public String getEvidenceId() { return evidenceId; }
    public List<DisputeEvidence> getEvidence() { return Collections.unmodifiableList(evidence); }
    public String getResponseDueDate() { return responseDueDate; }
    public String getResponse() { return response; }
    public Instant getRespondedAt() { return respondedAt; }
    public String getResolvedBy() { return resolvedBy; }
    public Instant getResolvedAt() { return resolvedAt; }
    public String getResolutionNotes() { return resolutionNotes; }
    public boolean isCustomerNotified() { return customerNotified; }
    public boolean isFundsWithheld() { return fundsWithheld; }
    public String getInternalNotes() { return internalNotes; }

    public void setEvidenceId(String evidenceId) {
        this.evidenceId = evidenceId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setResponseDueDate(String responseDueDate) {
        this.responseDueDate = responseDueDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCustomerNotified(boolean customerNotified) {
        this.customerNotified = customerNotified;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Dispute{" +
                "id=" + getId() +
                ", transactionId='" + transactionId + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", amount=" + amount +
                '}';
    }

    /**
     * Dispute type enum.
     */
    public enum DisputeType {
        FRAUDULENT("Fraudulent transaction"),
        DUPLICATE("Duplicate charge"),
        NOT_RECEIVED("Goods not received"),
        DEFECTIVE("Defective product"),
        UNAUTHORIZED("Unauthorized transaction"),
        INCORRECT_AMOUNT("Incorrect amount"),
        CUSTOMER_REVERSAL("Customer reversal");

        private final String description;

        DisputeType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Dispute status enum.
     */
    public enum DisputeStatus {
        OPEN("Open - Awaiting response"),
        EVIDENCE_REQUESTED("Evidence Requested - Additional evidence needed"),
        RESPONDED("Responded - Response submitted"),
        UNDER_REVIEW("Under Review - Being reviewed"),
        RESOLVED_FOR_CUSTOMER("Resolved - Customer won"),
        RESOLVED_FOR_MERCHANT("Resolved - Merchant won"),
        CLOSED("Closed - Dispute finalized");

        private final String description;

        DisputeStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public boolean isResolved() {
            return this == RESOLVED_FOR_CUSTOMER || this == RESOLVED_FOR_MERCHANT || this == CLOSED;
        }
    }

    /**
     * Dispute evidence value object.
     */
    public static final class DisputeEvidence {
        private final String evidenceId;
        private final String type; // DOCUMENT, EMAIL, SCREENSHOT, RECEIPT
        private final String fileName;
        private final String fileUrl;
        private final String description;
        private final Instant uploadedAt;
        private final String uploadedBy;

        public DisputeEvidence(
                String evidenceId,
                String type,
                String fileName,
                String fileUrl,
                String description,
                Instant uploadedAt,
                String uploadedBy) {
            this.evidenceId = evidenceId;
            this.type = type;
            this.fileName = fileName;
            this.fileUrl = fileUrl;
            this.description = description;
            this.uploadedAt = uploadedAt != null ? uploadedAt : Instant.now();
            this.uploadedBy = uploadedBy;
        }

        public String getEvidenceId() { return evidenceId; }
        public String getType() { return type; }
        public String getFileName() { return fileName; }
        public String getFileUrl() { return fileUrl; }
        public String getDescription() { return description; }
        public Instant getUploadedAt() { return uploadedAt; }
        public String getUploadedBy() { return uploadedBy; }
    }
}