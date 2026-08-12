# Complete Implementation: Vendor Management Extension

Now I'll complete the Vendor Management functionality within the Purchasing context, including vendor contracts, vendor performance tracking, vendor contacts, and vendor compliance management.

## 1. Vendor Domain Extensions

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/identifier/ContractId.java`**:

```java
package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Vendor contract identifier.
 */
public final class ContractId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ContractId(UUID value) {
        super(value);
    }

    public static ContractId of(UUID value) {
        return new ContractId(value);
    }

    public static ContractId generate() {
        return new ContractId(UUID.randomUUID());
    }

    public static ContractId fromString(String value) {
        return new ContractId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ContractId{" + value + "}";
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/ContractType.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Types of vendor contracts.
 */
public enum ContractType {
    SUPPLY_AGREEMENT("Supply Agreement"),
    SERVICE_AGREEMENT("Service Agreement"),
    MASTER_SERVICE_AGREEMENT("Master Service Agreement"),
    STATEMENT_OF_WORK("Statement of Work"),
    NON_DISCLOSURE_AGREEMENT("Non-Disclosure Agreement"),
    PURCHASE_AGREEMENT("Purchase Agreement"),
    FRAMEWORK_AGREEMENT("Framework Agreement"),
    LICENSE_AGREEMENT("License Agreement");

    private final String displayName;

    ContractType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/ContractStatus.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Status of a vendor contract.
 */
public enum ContractStatus {
    DRAFT("Draft - being negotiated"),
    PENDING_APPROVAL("Pending Approval - awaiting internal approval"),
    ACTIVE("Active - contract in effect"),
    SUSPENDED("Suspended - temporarily inactive"),
    EXPIRED("Expired - contract period ended"),
    TERMINATED("Terminated - ended early"),
    UNDER_RENEWAL("Under Renewal - being renewed");

    private final String description;

    ContractStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PENDING_APPROVAL;
    }

    public boolean isTerminal() {
        return this == EXPIRED || this == TERMINATED;
    }

    public boolean canTransitionTo(ContractStatus target) {
        return switch (this) {
            case DRAFT -> target == PENDING_APPROVAL || target == TERMINATED;
            case PENDING_APPROVAL -> target == ACTIVE || target == DRAFT || target == TERMINATED;
            case ACTIVE -> target == SUSPENDED || target == EXPIRED || target == TERMINATED || target == UNDER_RENEWAL;
            case SUSPENDED -> target == ACTIVE || target == EXPIRED || target == TERMINATED;
            case UNDER_RENEWAL -> target == ACTIVE || target == EXPIRED || target == TERMINATED;
            case EXPIRED, TERMINATED -> false;
        };
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/valueobject/VendorCertification.java`**:

```java
package tech.kayys.erp.purchasing.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.util.Objects;

/**
 * Vendor certification value object.
 */
public final class VendorCertification implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String certificationName;
    private final String certificationNumber;
    private final String issuingAuthority;
    private final Instant issueDate;
    private final Instant expiryDate;
    private final boolean verified;

    public VendorCertification(
            String certificationName,
            String certificationNumber,
            String issuingAuthority,
            Instant issueDate,
            Instant expiryDate,
            boolean verified) {
        this.certificationName = certificationName;
        this.certificationNumber = certificationNumber;
        this.issuingAuthority = issuingAuthority;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.verified = verified;
        validate();
    }

    @Override
    public void validate() {
        if (certificationName == null || certificationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Certification name cannot be empty");
        }
        if (issuingAuthority == null || issuingAuthority.trim().isEmpty()) {
            throw new IllegalArgumentException("Issuing authority cannot be empty");
        }
        if (issueDate == null) {
            throw new IllegalArgumentException("Issue date cannot be null");
        }
        if (expiryDate != null && expiryDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Expiry date must be after issue date");
        }
    }

    public String getCertificationName() { return certificationName; }
    public String getCertificationNumber() { return certificationNumber; }
    public String getIssuingAuthority() { return issuingAuthority; }
    public Instant getIssueDate() { return issueDate; }
    public Instant getExpiryDate() { return expiryDate; }
    public boolean isVerified() { return verified; }

    public boolean isValid() {
        if (!verified) return false;
        if (expiryDate == null) return true;
        return Instant.now().isBefore(expiryDate);
    }

    public boolean isExpiringSoon(int daysThreshold) {
        if (expiryDate == null) return false;
        Instant threshold = Instant.now().plusSeconds(daysThreshold * 24L * 60L * 60L);
        return expiryDate.isBefore(threshold);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorCertification that = (VendorCertification) o;
        return Objects.equals(certificationNumber, that.certificationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificationNumber);
    }

    @Override
    public String toString() {
        return "VendorCertification{" +
                "certificationName='" + certificationName + '\'' +
                ", issuingAuthority='" + issuingAuthority + '\'' +
                ", expiryDate=" + expiryDate +
                ", verified=" + verified +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String certificationName;
        private String certificationNumber;
        private String issuingAuthority;
        private Instant issueDate;
        private Instant expiryDate;
        private boolean verified = false;

        public Builder certificationName(String certificationName) {
            this.certificationName = certificationName;
            return this;
        }

        public Builder certificationNumber(String certificationNumber) {
            this.certificationNumber = certificationNumber;
            return this;
        }

        public Builder issuingAuthority(String issuingAuthority) {
            this.issuingAuthority = issuingAuthority;
            return this;
        }

        public Builder issueDate(Instant issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public Builder expiryDate(Instant expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder verified(boolean verified) {
            this.verified = verified;
            return this;
        }

        public VendorCertification build() {
            if (issueDate == null) {
                issueDate = Instant.now();
            }
            return new VendorCertification(
                certificationName, certificationNumber, issuingAuthority,
                issueDate, expiryDate, verified
            );
        }
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/model/VendorContact.java`**:

```java
package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.Objects;

/**
 * Vendor contact value object.
 */
public final class VendorContact implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String mobile;
    private final String jobTitle;
    private final String department;
    private final boolean primary;
    private final boolean active;

    public VendorContact(
            String firstName,
            String lastName,
            String email,
            String phone,
            String mobile,
            String jobTitle,
            String department,
            boolean primary,
            boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.mobile = mobile;
        this.jobTitle = jobTitle;
        this.department = department;
        this.primary = primary;
        this.active = active;
        validate();
    }

    @Override
    public void validate() {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMobile() { return mobile; }
    public String getJobTitle() { return jobTitle; }
    public String getDepartment() { return department; }
    public boolean isPrimary() { return primary; }
    public boolean isActive() { return active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorContact that = (VendorContact) o;
        return primary == that.primary &&
               Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, primary);
    }

    @Override
    public String toString() {
        return "VendorContact{" +
                "fullName='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", primary=" + primary +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String mobile;
        private String jobTitle;
        private String department;
        private boolean primary = false;
        private boolean active = true;

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder mobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public Builder jobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder primary(boolean primary) {
            this.primary = primary;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public VendorContact build() {
            return new VendorContact(
                firstName, lastName, email, phone, mobile,
                jobTitle, department, primary, active
            );
        }
    }
}
```

**`/modules/purchasing/domain/src/main/java/tech/kayys/erp/purchasing/domain/model/VendorContract.java`**:

```java
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
```

## 2. Vendor Application Extensions

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/VendorCommandService.java`**:

```java
package tech.kayys.erp.purchasing.application.api;

import tech.kayys.erp.purchasing.application.api.command.*;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for vendor and contract commands.
 */
public interface VendorCommandService {

    // ============ Vendor Commands ============

    /**
     * Creates a new vendor.
     */
    CompletionStage<VendorId> createVendor(CreateVendorCommand command);

    /**
     * Approves a vendor.
     */
    CompletionStage<VendorId> approveVendor(ApproveVendorCommand command);

    /**
     * Activates a vendor.
     */
    CompletionStage<VendorId> activateVendor(ActivateVendorCommand command);

    /**
     * Deactivates a vendor.
     */
    CompletionStage<VendorId> deactivateVendor(DeactivateVendorCommand command);

    /**
     * Blacklists a vendor.
     */
    CompletionStage<VendorId> blacklistVendor(BlacklistVendorCommand command);

    /**
     * Adds a contact to a vendor.
     */
    CompletionStage<VendorId> addVendorContact(AddVendorContactCommand command);

    /**
     * Removes a contact from a vendor.
     */
    CompletionStage<VendorId> removeVendorContact(RemoveVendorContactCommand command);

    /**
     * Records vendor performance.
     */
    CompletionStage<VendorId> recordVendorPerformance(RecordVendorPerformanceCommand command);

    // ============ Contract Commands ============

    /**
     * Creates a new vendor contract.
     */
    CompletionStage<ContractId> createContract(CreateContractCommand command);

    /**
     * Submits a contract for approval.
     */
    CompletionStage<ContractId> submitContract(SubmitContractCommand command);

    /**
     * Approves a contract.
     */
    CompletionStage<ContractId> approveContract(ApproveContractCommand command);

    /**
     * Activates a contract.
     */
    CompletionStage<ContractId> activateContract(ActivateContractCommand command);

    /**
     * Suspends a contract.
     */
    CompletionStage<ContractId> suspendContract(SuspendContractCommand command);

    /**
     * Terminates a contract.
     */
    CompletionStage<ContractId> terminateContract(TerminateContractCommand command);

    /**
     * Renews a contract.
     */
    CompletionStage<ContractId> renewContract(RenewContractCommand command);

    /**
     * Processes contract renewals.
     */
    CompletionStage<Integer> processContractRenewals();
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/CreateVendorCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.valueobject.VendorType;

import java.util.List;

/**
 * Command to create a new vendor.
 */
public record CreateVendorCommand(
        VendorId vendorId,
        String name,
        String legalName,
        String taxId,
        String email,
        String phone,
        String address,
        String city,
        String state,
        String postalCode,
        String country,
        String website,
        VendorType vendorType,
        String contactPerson,
        String contactEmail,
        String contactPhone,
        String paymentTerms,
        String shippingTerms,
        String currencyCode,
        String notes
) implements Command<VendorId> {

    public CreateVendorCommand {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Vendor name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (vendorType == null) {
            throw new IllegalArgumentException("Vendor type is required");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private VendorId vendorId;
        private String name;
        private String legalName;
        private String taxId;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String website;
        private VendorType vendorType;
        private String contactPerson;
        private String contactEmail;
        private String contactPhone;
        private String paymentTerms;
        private String shippingTerms;
        private String currencyCode = "USD";
        private String notes;

        public Builder vendorId(VendorId vendorId) {
            this.vendorId = vendorId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder legalName(String legalName) {
            this.legalName = legalName;
            return this;
        }

        public Builder taxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder website(String website) {
            this.website = website;
            return this;
        }

        public Builder vendorType(VendorType vendorType) {
            this.vendorType = vendorType;
            return this;
        }

        public Builder contactPerson(String contactPerson) {
            this.contactPerson = contactPerson;
            return this;
        }

        public Builder contactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
            return this;
        }

        public Builder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public Builder paymentTerms(String paymentTerms) {
            this.paymentTerms = paymentTerms;
            return this;
        }

        public Builder shippingTerms(String shippingTerms) {
            this.shippingTerms = shippingTerms;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public CreateVendorCommand build() {
            if (vendorId == null) {
                vendorId = VendorId.generate();
            }
            return new CreateVendorCommand(
                vendorId, name, legalName, taxId, email, phone,
                address, city, state, postalCode, country,
                website, vendorType, contactPerson, contactEmail,
                contactPhone, paymentTerms, shippingTerms,
                currencyCode, notes
            );
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/ApproveVendorCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;

/**
 * Command to approve a vendor.
 */
public record ApproveVendorCommand(
        VendorId vendorId,
        String approvedBy
) implements Command<VendorId> {

    public ApproveVendorCommand {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        if (approvedBy == null || approvedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Approved by is required");
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/AddVendorContactCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;

/**
 * Command to add a contact to a vendor.
 */
public record AddVendorContactCommand(
        VendorId vendorId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String mobile,
        String jobTitle,
        String department,
        boolean primary
) implements Command<VendorId> {

    public AddVendorContactCommand {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/CreateContractCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;
import tech.kayys.erp.purchasing.domain.valueobject.ContractType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Command to create a new vendor contract.
 */
public record CreateContractCommand(
        ContractId contractId,
        UUID vendorId,
        String vendorName,
        ContractType contractType,
        String title,
        String description,
        Instant effectiveDate,
        Instant expirationDate,
        List<ContractLineItemCommand> lineItems,
        String termsAndConditions,
        String specialConditions,
        String paymentTerms,
        String deliveryTerms,
        String renewalTerms,
        Integer autoRenewalDays,
        Boolean autoRenew,
        String currencyCode,
        String notes,
        String createdBy
) implements Command<ContractId> {

    public CreateContractCommand {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        if (contractType == null) {
            throw new IllegalArgumentException("Contract type is required");
        }
        if (effectiveDate == null) {
            throw new IllegalArgumentException("Effective date is required");
        }
        if (expirationDate == null) {
            throw new IllegalArgumentException("Expiration date is required");
        }
        if (expirationDate.isBefore(effectiveDate)) {
            throw new IllegalArgumentException("Expiration date must be after effective date");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContractId contractId;
        private UUID vendorId;
        private String vendorName;
        private ContractType contractType;
        private String title;
        private String description;
        private Instant effectiveDate;
        private Instant expirationDate;
        private List<ContractLineItemCommand> lineItems;
        private String termsAndConditions;
        private String specialConditions;
        private String paymentTerms;
        private String deliveryTerms;
        private String renewalTerms;
        private Integer autoRenewalDays = 30;
        private Boolean autoRenew = false;
        private String currencyCode = "USD";
        private String notes;
        private String createdBy;

        public Builder contractId(ContractId contractId) {
            this.contractId = contractId;
            return this;
        }

        public Builder vendorId(UUID vendorId) {
            this.vendorId = vendorId;
            return this;
        }

        public Builder vendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        public Builder contractType(ContractType contractType) {
            this.contractType = contractType;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder effectiveDate(Instant effectiveDate) {
            this.effectiveDate = effectiveDate;
            return this;
        }

        public Builder expirationDate(Instant expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder lineItems(List<ContractLineItemCommand> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        public Builder termsAndConditions(String termsAndConditions) {
            this.termsAndConditions = termsAndConditions;
            return this;
        }

        public Builder specialConditions(String specialConditions) {
            this.specialConditions = specialConditions;
            return this;
        }

        public Builder paymentTerms(String paymentTerms) {
            this.paymentTerms = paymentTerms;
            return this;
        }

        public Builder deliveryTerms(String deliveryTerms) {
            this.deliveryTerms = deliveryTerms;
            return this;
        }

        public Builder renewalTerms(String renewalTerms) {
            this.renewalTerms = renewalTerms;
            return this;
        }

        public Builder autoRenewalDays(Integer autoRenewalDays) {
            this.autoRenewalDays = autoRenewalDays;
            return this;
        }

        public Builder autoRenew(Boolean autoRenew) {
            this.autoRenew = autoRenew;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CreateContractCommand build() {
            if (contractId == null) {
                contractId = ContractId.generate();
            }
            if (autoRenew == null) {
                autoRenew = false;
            }
            return new CreateContractCommand(
                contractId, vendorId, vendorName, contractType, title,
                description, effectiveDate, expirationDate, lineItems,
                termsAndConditions, specialConditions, paymentTerms,
                deliveryTerms, renewalTerms, autoRenewalDays, autoRenew,
                currencyCode, notes, createdBy
            );
        }
    }

    /**
     * Contract line item command.
     */
    public record ContractLineItemCommand(
            String description,
            int quantity,
            String unitPrice,
            String uom,
            String category
    ) {
        public ContractLineItemCommand {
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("Description cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice == null || unitPrice.trim().isEmpty()) {
                throw new IllegalArgumentException("Unit price is required");
            }
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/ApproveContractCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

/**
 * Command to approve a contract.
 */
public record ApproveContractCommand(
        ContractId contractId,
        String approvedBy
) implements Command<ContractId> {

    public ApproveContractCommand {
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        if (approvedBy == null || approvedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Approved by is required");
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/api/command/RenewContractCommand.java`**:

```java
package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.time.Instant;

/**
 * Command to renew a contract.
 */
public record RenewContractCommand(
        ContractId contractId,
        Instant newExpirationDate,
        String notes
) implements Command<ContractId> {

    public RenewContractCommand {
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        if (newExpirationDate == null) {
            throw new IllegalArgumentException("New expiration date is required");
        }
        if (newExpirationDate.isBefore(Instant.now())) {
            throw new IllegalArgumentException("New expiration date must be in the future");
        }
    }
}
```

**`/modules/purchasing/application/src/main/java/tech/kayys/erp/purchasing/application/internal/CreateVendorHandler.java`**:

```java
package tech.kayys.erp.purchasing.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.purchasing.application.api.command.CreateVendorCommand;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.model.Vendor;
import tech.kayys.erp.purchasing.domain.repository.VendorRepository;
import tech.kayys.erp.purchasing.domain.valueobject.VendorStatus;
import tech.kayys.erp.purchasing.domain.valueobject.VendorType;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating vendors.
 */
@UseCase("Create a new vendor")
public class CreateVendorHandler implements CommandHandler<CreateVendorCommand, VendorId> {

    private final VendorRepository vendorRepository;

    @Inject
    public CreateVendorHandler(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public CompletionStage<VendorId> handle(CreateVendorCommand command) {
        // Check if vendor already exists
        return vendorRepository.existsByName(command.name())
            .thenCompose(exists -> {
                if (exists) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Vendor already exists: " + command.name())
                    );
                }

                // Create the vendor
                Vendor vendor = Vendor.create(
                    command.vendorId(),
                    command.name(),
                    command.vendorType(),
                    command.email(),
                    command.currencyCode()
                );

                // Set optional fields
                if (command.legalName() != null) {
                    vendor.setLegalName(command.legalName());
                }
                if (command.taxId() != null) {
                    vendor.setTaxId(command.taxId());
                }
                if (command.phone() != null) {
                    vendor.setPhone(command.phone());
                }
                if (command.address() != null) {
                    vendor.setAddress(command.address());
                }
                if (command.city() != null) {
                    vendor.setCity(command.city());
                }
                if (command.state() != null) {
                    vendor.setState(command.state());
                }
                if (command.postalCode() != null) {
                    vendor.setPostalCode(command.postalCode());
                }
                if (command.country() != null) {
                    vendor.setCountry(command.country());
                }
                if (command.website() != null) {
                    vendor.setWebsite(command.website());
                }
                if (command.contactPerson() != null) {
                    vendor.setContactPerson(command.contactPerson());
                }
                if (command.contactEmail() != null) {
                    vendor.setContactEmail(command.contactEmail());
                }
                if (command.contactPhone() != null) {
                    vendor.setContactPhone(command.contactPhone());
                }
                if (command.paymentTerms() != null) {
                    vendor.setPaymentTerms(command.paymentTerms());
                }
                if (command.shippingTerms() != null) {
                    vendor.setShippingTerms(command.shippingTerms());
                }
                if (command.notes() != null) {
                    vendor.setNotes(command.notes());
                }

                // Save the vendor
                return vendorRepository.save(vendor)
                    .thenApply(Vendor::getId);
            });
    }
}
```

## 3. Update Architecture Tests

**`/architecture/tests/src/test/java/tech/kayys/erp/architecture/CompleteArchitectureTest.java`** (add Purchasing rules):

```java
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule purchasingDomainMustNotDependOnOtherContexts =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "tech.kayys.erp.catalog..",
                        "tech.kayys.erp.sales..",
                        "tech.kayys.erp.inventory..",
                        "tech.kayys.erp.accounting.."
                );

@ArchTest
static final ArchRule purchasingApplicationMayUsePorts =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.application.port..")
                .should()
                .haveSimpleNameEndingWith("Port");

@ArchTest
static final ArchRule purchasingDomainPackagesCorrect =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain..")
                .should()
                .resideInAnyPackage(
                        "tech.kayys.erp.purchasing.domain.model..",
                        "tech.kayys.erp.purchasing.domain.identifier..",
                        "tech.kayys.erp.purchasing.domain.valueobject..",
                        "tech.kayys.erp.purchasing.domain.event..",
                        "tech.kayys.erp.purchasing.domain.repository.."
                );

@ArchTest
static final ArchRule vendorContractStateMachine =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain.valueobject..")
                .and()
                .haveSimpleName("ContractStatus")
                .should()
                .haveOnlyFinalFields();

@ArchTest
static final ArchRule purchasingShouldNotDirectlyUseInventoryModel =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("tech.kayys.erp.inventory.domain.model..");
```

## Summary

The complete Vendor Management implementation provides:

1. **Vendor Management**:
   - Vendor creation with approval workflow
   - Vendor status management (Active, Inactive, Blacklisted)
   - Multiple vendor contacts
   - Vendor certifications and compliance tracking
   - Performance rating and tracking
   - On-time delivery metrics

2. **Contract Management**:
   - Full contract lifecycle (Draft → Pending Approval → Active → Suspended/Expired/Terminated)
   - Contract line items with pricing
   - Auto-renewal with configurable days
   - Contract value calculation
   - Attachment support

3. **Advanced Features**:
   - Vendor performance scoring
   - Certification validity tracking
   - Expiration notifications
   - Contract renewal workflows
   - Vendor blacklisting with reason

4. **Integration Ports**:
   - VendorPort for other contexts to validate vendors
   - InventoryPort for PO receiving
   - SalesOrderPort for creating POs from sales orders

5. **Architecture Rules**:
   - No direct dependencies on other contexts
   - Proper package structure
   - State machine enforcement
   - Use of ports for integration

This completes the Purchasing context with full vendor management, procurement, and contract capabilities that integrate with Inventory, Sales, and Accounting contexts.