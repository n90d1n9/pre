package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to create a contract from a template.
 */
public record CreateFromTemplateCommand(
        ContractId contractId,
        UUID vendorId,
        String vendorName,
        String templateId,
        Instant effectiveDate,
        Instant expirationDate,
        String data,
        String currencyCode,
        String notes,
        String createdBy
) implements Command<ContractId> {

    public CreateFromTemplateCommand {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        if (templateId == null || templateId.trim().isEmpty()) {
            throw new IllegalArgumentException("Template ID cannot be empty");
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
        private String templateId;
        private Instant effectiveDate;
        private Instant expirationDate;
        private String data;
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

        public Builder templateId(String templateId) {
            this.templateId = templateId;
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

        public Builder data(String data) {
            this.data = data;
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

        public CreateFromTemplateCommand build() {
            if (contractId == null) {
                contractId = ContractId.generate();
            }
            return new CreateFromTemplateCommand(
                contractId, vendorId, vendorName, templateId,
                effectiveDate, expirationDate, data, currencyCode,
                notes, createdBy
            );
        }
    }
}