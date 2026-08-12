package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.valueobject.ContractStatus;
import tech.kayys.erp.purchasing.domain.valueobject.ContractType;
import tech.kayys.erp.purchasing.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Vendor contract aggregate root.
 * Represents a formal agreement with a vendor.
 */
public final class VendorContract extends AggregateRoot<ContractId> {
    
    private static final long serialVersionUID = 1L;
    
    private String contractNumber;
    private VendorId vendorId;
    private String vendorName;
    private ContractType contractType;
    private ContractStatus status;
    private String title;
    private String description;
    private Instant effectiveDate;
    private Instant expirationDate;
    private Money contractValue;
    private String currencyCode;
    private String termsAndConditions;
    private String specialConditions;
    private String paymentTerms;
    private String deliveryTerms;
    private String renewalTerms;
    private int autoRenewalDays;
    private boolean autoRenew;
    private List<ContractLineItem> lineItems;
    private List<String> attachments;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private String notes;

    private VendorContract(ContractId id) {
        super(id);
        this.lineItems = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.status = ContractStatus.DRAFT;
        this.autoRenew = false;
        this.autoRenewalDays = 30;
    }

    private VendorContract() {
        super();
    }

    /**
     * Factory method to create a new vendor contract.
     */
    public static VendorContract create(
            ContractId id,
            String contractNumber,
            VendorId vendorId,
            String vendorName,
            ContractType contractType,
            Instant effectiveDate,
            Instant expirationDate,
            String currencyCode) {
        VendorContract contract = new VendorContract(id);
        contract.contractNumber = contractNumber;
        contract.vendorId = vendorId;
        contract.vendorName = vendorName;
        contract.contractType = contractType;
        contract.effectiveDate = effectiveDate;
        contract.expirationDate = expirationDate;
        contract.currencyCode = currencyCode;
        contract.contractValue = Money.zero(currencyCode);
        return contract;
    }

    /**
     * Adds a line item to the contract.
     */
    public void addLineItem(ContractLineItem item) {
        if (status != ContractStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify contract in status: " + status);
        }
        lineItems.add(item);
        recalculateValue();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits the contract for approval.
     */
    public void submitForApproval() {
        if (status != ContractStatus.DRAFT) {
            throw new IllegalStateException("Cannot submit contract in status: " + status);
        }
        if (lineItems.isEmpty()) {
            throw new IllegalStateException("Contract must have at least one line item");
        }
        
        this.status = ContractStatus.PENDING_APPROVAL;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the contract.
     */
    public void approve(String approvedBy) {
        if (status != ContractStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Cannot approve contract in status: " + status);
        }
        
        this.status = ContractStatus.ACTIVE;
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the contract.
     */
    public void activate() {
        if (status != ContractStatus.SUSPENDED && status != ContractStatus.DRAFT) {
            throw new IllegalStateException("Cannot activate contract in status: " + status);
        }
        
        this.status = ContractStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Suspends the contract.
     */
    public void suspend(String reason) {
        if (status != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Cannot suspend contract in status: " + status);
        }
        
        this.status = ContractStatus.SUSPENDED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Terminates the contract.
     */
    public void terminate(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Contract is already terminated or expired");
        }
        
        this.status = ContractStatus.TERMINATED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Renews the contract.
     */
    public void renew(Instant newExpirationDate) {
        if (status != ContractStatus.ACTIVE && status != ContractStatus.UNDER_RENEWAL) {
            throw new IllegalStateException("Cannot renew contract in status: " + status);
        }
        if (newExpirationDate.isBefore(Instant.now())) {
            throw new IllegalArgumentException("New expiration date must be in the future");
        }
        
        this.expirationDate = newExpirationDate;
        this.status = ContractStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks contract for renewal.
     */
    public void markForRenewal() {
        if (status != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Cannot renew contract in status: " + status);
        }
        
        this.status = ContractStatus.UNDER_RENEWAL;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Expires the contract.
     */
    public void expire() {
        if (status != ContractStatus.ACTIVE && status != ContractStatus.SUSPENDED) {
            throw new IllegalStateException("Cannot expire contract in status: " + status);
        }
        
        this.status = ContractStatus.EXPIRED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void recalculateValue() {
        this.contractValue = lineItems.stream()
            .map(ContractLineItem::getTotalValue)
            .reduce(Money.zero(currencyCode), Money::add);
    }

    /**
     * Checks if the contract is active.
     */
    public boolean isActive() {
        return status.isActive() && 
               (expirationDate == null || Instant.now().isBefore(expirationDate));
    }

    /**
     * Checks if the contract is expiring soon.
     */
    public boolean isExpiringSoon(int daysThreshold) {
        if (expirationDate == null) return false;
        Instant threshold = Instant.now().plusSeconds(daysThreshold * 24L * 60L * 60L);
        return expirationDate.isBefore(threshold);
    }

    /**
     * Gets the days until expiration.
     */
    public long getDaysUntilExpiration() {
        if (expirationDate == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(Instant.now(), expirationDate);
    }

    // Getters
    public String getContractNumber() { return contractNumber; }
    public VendorId getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public ContractType getContractType() { return contractType; }
    public ContractStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Instant getEffectiveDate() { return effectiveDate; }
    public Instant getExpirationDate() { return expirationDate; }
    public Money getContractValue() { return contractValue; }
    public String getCurrencyCode() { return currencyCode; }
    public String getTermsAndConditions() { return termsAndConditions; }
    public String getSpecialConditions() { return specialConditions; }
    public String getPaymentTerms() { return paymentTerms; }
    public String getDeliveryTerms() { return deliveryTerms; }
    public String getRenewalTerms() { return renewalTerms; }
    public int getAutoRenewalDays() { return autoRenewalDays; }
    public boolean isAutoRenew() { return autoRenew; }
    public List<ContractLineItem> getLineItems() { return Collections.unmodifiableList(lineItems); }
    public List<String> getAttachments() { return Collections.unmodifiableList(attachments); }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getNotes() { return notes; }

    public void setTitle(String title) {
        this.title = title;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSpecialConditions(String specialConditions) {
        this.specialConditions = specialConditions;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDeliveryTerms(String deliveryTerms) {
        this.deliveryTerms = deliveryTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRenewalTerms(String renewalTerms) {
        this.renewalTerms = renewalTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setAutoRenewalDays(int autoRenewalDays) {
        this.autoRenewalDays = autoRenewalDays;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void addAttachment(String attachment) {
        this.attachments.add(attachment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "VendorContract{" +
                "id=" + getId() +
                ", contractNumber='" + contractNumber + '\'' +
                ", vendorId=" + vendorId +
                ", contractType=" + contractType +
                ", status=" + status +
                ", contractValue=" + contractValue +
                '}';
    }

    /**
     * Contract line item.
     */
    public static final class ContractLineItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String description;
        private final int quantity;
        private final Money unitPrice;
        private final Money totalValue;
        private final String uom;
        private final String category;

        public ContractLineItem(
                String description,
                int quantity,
                Money unitPrice,
                String uom,
                String category) {
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.uom = uom;
            this.category = category;
            this.totalValue = unitPrice.multiply(quantity);
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

        public String getDescription() { return description; }
        public int getQuantity() { return quantity; }
        public Money getUnitPrice() { return unitPrice; }
        public Money getTotalValue() { return totalValue; }
        public String getUom() { return uom; }
        public String getCategory() { return category; }

        @Override
        public String toString() {
            return "ContractLineItem{" +
                    "description='" + description + '\'' +
                    ", quantity=" + quantity +
                    ", totalValue=" + totalValue +
                    '}';
        }
    }
}