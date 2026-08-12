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